package com.steepgraph.ta.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.NoSuchWindowException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.steepgraph.ta.framework.common.AssertionException;
import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.common.pages.Handler;
import com.steepgraph.ta.framework.utils.interfaces.ICSVOutputUtil;
import com.steepgraph.ta.framework.utils.interfaces.ICSVUtil;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.CSVOutputUtil;
import com.steepgraph.ta.framework.utils.pages.CSVUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.TestCase;

/**
 * Main class from which test case execution by parsing XML and test case
 * execution begins
 * 
 * @author Steepgraph Systems
 */
public class App {

	private String testCaseName;
	
	private String testCaseId;

	private String suiteName;

	private ICommonUtil commonUtil;

	private PropertyUtil propertyUtil;

	private IHandler handler;

	protected LinkedList<Map<String, Boolean>> lnkLstOfIfStack;

	protected LinkedList<List<Boolean>> lstOfPopValue;

	private static LinkedList<Map<String, Boolean>> anotherList = null;

	private static LinkedList<List<Boolean>> tmpLstOfPopValue = null;

	private static final String terminationMessage = "User Terminated Execution";

	int poolSize = 1;

	@BeforeTest
	@Parameters({ "xmlFilePath" })
	public void beforeTest(ITestContext ctx, String xmlFilePath) throws Exception {
		this.propertyUtil = PropertyUtil.newInstance();
		String parallelExecutionPoolsize = propertyUtil.getProperty(Constants.PROPERTY_KEY_PARALLEL_EXECUTION_POOLSIZE);

		if (NumberUtils.isParsable(parallelExecutionPoolsize) && Integer.parseInt(parallelExecutionPoolsize) > 1) {
			poolSize = Integer.parseInt(parallelExecutionPoolsize);
		}
		if (poolSize == 1)
			this.handler = (IHandler) ctx.getSuite().getAttribute("handler");
		else {
			this.handler = new Handler();
		}
		ctx.setAttribute("handler", this.handler);
		this.commonUtil = this.handler.getCommonUtil();
		// this.propertyUtil = this.handler.getPropertyUtil();
		this.testCaseName = ctx.getName();

		String[] testCaseIdName = this.commonUtil.getTestCaseIdandName(testCaseName);

		this.testCaseId = testCaseIdName[0];
		LoggerUtil.debug("testCaseId :" + testCaseIdName[0]);

		this.testCaseName = testCaseIdName[1];
		LoggerUtil.debug("testCaseName :" + testCaseIdName[1]);

		this.suiteName = ctx.getCurrentXmlTest().getSuite().getName();
		LoggerUtil.debug("suiteName : " + this.suiteName);

		this.commonUtil.startVideoRecord(xmlFilePath, this.testCaseName);

	}

	private static LinkedList<List<Boolean>> deepCopyLinkedListForBoolean(LinkedList<List<Boolean>> originalList) {
		LinkedList<List<Boolean>> copiedList = new LinkedList<>();
		for (List<Boolean> list : originalList) {
			List<Boolean> copiedListInner = new ArrayList<>(list);
			copiedList.add(copiedListInner);
		}
		return copiedList;
	}

	private static LinkedList<Map<String, Boolean>> deepCopyLinkedListForMap(
			LinkedList<Map<String, Boolean>> originalList) {
		LinkedList<Map<String, Boolean>> copiedList = new LinkedList<>();
		for (Map<String, Boolean> map : originalList) {
			Map<String, Boolean> copiedMap = new HashMap<>(map);
			copiedList.add(copiedMap);
		}
		return copiedList;
	}

	/**
	 * Main method from where test case execution begins
	 * 
	 * @author Steepgraph Systems
	 * @param strXMLFile
	 * @param csvFilePath
	 * @return void
	 * @throws Exception
	 */

	@Test
	@Parameters({ "xmlFilePath", "csvFilePath", "proxy" })
	public void main(String strXMLFile, String csvFilePath, @Optional("Optional parameter") String proxy)
			throws Exception {

		LoggerUtil.debug("Processing App class.");

		LoggerUtil.debug("strXMLFile: " + strXMLFile);
		LoggerUtil.debug("csvFilePath: " + csvFilePath);

		if (!this.commonUtil.allowTestCaseExecution(this.suiteName, this.testCaseId))
			return;

		String errorMessage = Constants.STR_EMPTY_STRING;
		LinkedList<Map<String, Boolean>> lnkLstOfIfStack = new LinkedList<Map<String, Boolean>>();
		LinkedList<List<Boolean>> lstOfPopValue = new LinkedList<List<Boolean>>();

		String notificationMsg = "Suite: " + suiteName + ", Test Case: " + this.testCaseName;
		String printMsg = "Test Case XML: " + strXMLFile + ", Suite:  " + suiteName + ", Test Case: "
				+ this.testCaseName;
		ICSVUtil csvUtilObj = null;
		ICSVOutputUtil csvOutputUtilObj = new CSVOutputUtil(csvFilePath, this.testCaseName, this.propertyUtil);
		TestCase testCase = null;
		String elName = "";
		Map<String, String> parameterMap = new HashMap<String, String>();
		if (!"".equalsIgnoreCase(proxy) || proxy != null) {
			parameterMap.put("proxy", proxy);
		}

		try {
			if (this.propertyUtil.isTerminationInvoked()) {
				System.out.println(terminationMessage);
				throw new SkipException(terminationMessage);
			}
			LoggerUtil.debug("XML file parsed ");
			this.handler.initializeDriver(parameterMap);
			this.handler.setSuiteName(this.suiteName);
			this.handler.setTestCaseName(this.testCaseName);
			csvUtilObj = new CSVUtil(csvFilePath);
			String strRetryInterval = propertyUtil.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_RETRY_COUNT);
			int iRetryInterval = 0;
			boolean optionalStart = false;
			boolean validateBegin = false;
			int iCountException = 0;

			// if the execution of element failed due to unexpected behavior then retry
			// execution of same element
			if (strRetryInterval != null && !strRetryInterval.equals(""))
				iRetryInterval = Integer.parseInt(strRetryInterval);

			if (iRetryInterval < 0)
				iRetryInterval = 0;

			LoggerUtil.debug("iRetryInterval: " + iRetryInterval);

			List<String> skipErrorTagList = this.commonUtil.getSkipErrorTagList();

			Stack<TestCase> testCaseStack = new Stack<>();

			this.commonUtil.showNotification(notificationMsg + ", Status: In Progress");

			System.out.println(printMsg + ", Status: In Progress");
			int rowNo = 1;
			while (csvUtilObj.next()) {
				try {
					// TestCase object
					testCase = new TestCase();
					testCase.parseXML(strXMLFile);
					this.handler.clearStatus();
					csvOutputUtilObj.next();
					// Get next element from parsed XML file
					String element = testCase.getNextElement();

					while (element != null) {

						if ("optionalstart".equalsIgnoreCase(element)) {
							optionalStart = true;
							element = testCase.getNextElement();
						}

						if ("optionalclose".equalsIgnoreCase(element)) {
							optionalStart = false;
							element = testCase.getNextElement();
						}

						if ("validatebegin".equalsIgnoreCase(element)) {
							validateBegin = true;
						}

						if (element != null) {
							elName = element;
							if ("post".equalsIgnoreCase(element)) {
								String msg = testCase.getFileName() + "::Line-> " + testCase.getLineNumber() + " => "
										+ suiteName + " => " + testCaseName + " => <" + element + "> Processing";
								System.out.println(msg);
								LoggerUtil.debug(msg);
								LoggerUtil.debug("Starts of Post/EndPost");
								testCasePostProcessing(testCase, csvUtilObj, true, parameterMap);
								if (!testCaseStack.isEmpty()) {
									testCase = testCaseStack.pop();
									element = testCase.getNextElement();
								} else {
									break;
								}
							}

							int executionCnt = 0;

							while (executionCnt <= iRetryInterval) {
								try {
									LoggerUtil.debug("executionCnt: " + executionCnt);
									executionCnt++;
									// get included XML object for processing.
									if ("include".equalsIgnoreCase(element)) {
										String msg = testCase.getFileName() + "::Line-> " + testCase.getLineNumber()
												+ " => " + suiteName + " => " + testCaseName + " => <" + element
												+ "> Processing";
										System.out.println(msg);
										LoggerUtil.debug(msg);
										LoggerUtil.debug("Start of Include");
										LoggerUtil.debug("element: " + element);
										testCaseStack.push(testCase);
										Map<String, String> attributeMap = testCase.getAttributes();
										testCase = getIncludedXML(attributeMap);
										LoggerUtil.debug("End of Include");
									} else if (optionalStart) {
										try {
											this.handler.processElement(element, testCase, csvUtilObj, lnkLstOfIfStack,
													lstOfPopValue);
										} catch (Exception ex) {
											LoggerUtil.debug(printMsg + "  skipped. " + ex.getMessage());
											LoggerUtil.debug("Exception while processing", ex);
											break;
										}
									} else if (validateBegin) {
										try {
											this.handler.processElement(element, testCase, csvUtilObj, lnkLstOfIfStack,
													lstOfPopValue);
										} catch (Exception ex) {
											++iCountException;
											throw new Exception(printMsg + ": " + ex.getMessage());
										}
									} else {
										this.handler.processElement(element, testCase, csvUtilObj, lnkLstOfIfStack,
												lstOfPopValue);

										anotherList = deepCopyLinkedListForMap(lnkLstOfIfStack);
										tmpLstOfPopValue = deepCopyLinkedListForBoolean(lstOfPopValue);
									}
									break;
								} catch (SkipException e) {
									LoggerUtil.debug(printMsg + "  skipped. " + e.getMessage());
									break;
								} catch (AssertionException e) {
									// assertion failed tag should not be retried again.
									if (skipErrorTagList.contains(element.toLowerCase())) {
										this.handler.setInfoMessage(e.getMessage());
										break;
									} else {
										throw e;
									}
								} catch (NoSuchWindowException e) {
									LoggerUtil.debug("failed attempt: " + executionCnt);
									LoggerUtil.debug(
											"target window already closed forcefully , Browser Window is not found ",
											e);
									throw e;
								} catch (Exception e) {
									LoggerUtil.debug("failed attempt: " + executionCnt);
									LoggerUtil.debug("Exception while processing", e);

									if (executionCnt > iRetryInterval) {
										if (skipErrorTagList.contains(element.toLowerCase())) {
											this.handler.setInfoMessage(e.getMessage());
											break;
										} else {
											throw e;
										}
									}
									lnkLstOfIfStack = deepCopyLinkedListForMap(anotherList);
									lstOfPopValue = deepCopyLinkedListForBoolean(tmpLstOfPopValue);
								}
							}
						}
						if ("validateend".equalsIgnoreCase(element)) {
							validateBegin = false;
							if (iCountException > 0) {
								errorMessage = "Following number of ValidateBehaviour tag has been failed: "
										+ iCountException;
								throw new Exception();
							}
						}
						element = testCase.getNextElement();
						// get parent xml object and start processing next element
						if (element == null) {
							while (element == null && !testCaseStack.isEmpty()) {
								testCase = testCaseStack.pop();
								element = testCase.getNextElement();
							}
						}

					}
					csvOutputUtilObj.write(this.handler);
					Reporter.log(this.handler.getInfoMessage());
				} catch (Exception e) {
					errorMessage = errorMessage + "Input XML: " + strXMLFile + ", Suite:" + this.suiteName
							+ ", Test Case:" + this.testCaseName + ", Element Name: " + elName + ", AttributeMap : "
							+ testCase.getAttributes() + ", Frame Name : " + this.handler.getDriver().getCurrentFrame()
							+ " : Exception " + e.getMessage();
					errorMessage = errorMessage.replaceAll("\"", "'");
					errorMessage = errorMessage.replaceAll("\r\n", " /n ");
					errorMessage = errorMessage.replaceAll("\n", " /n ");
					LoggerUtil.debug(errorMessage, e);
					LoggerUtil.error(errorMessage, e);
					csvOutputUtilObj.write(e, this.handler);
					try {
						this.commonUtil.takeSnapShot(strXMLFile, testCaseName, rowNo, this.handler.getDriver());
					} catch (NoSuchWindowException f) {
						LoggerUtil.debug("Failure Screenshot was not captured as the window was already closed.");
					}
					Reporter.log(this.handler.getInfoMessage());
					Reporter.log("Suite: " + suiteName + ", Test Case: " + this.testCaseName + ", Status: fail. Error: "
							+ e.getMessage());
					testCasePostProcessing(testCase, csvUtilObj, false, parameterMap);
					while (!testCaseStack.isEmpty()) {
						testCase = testCaseStack.pop();
						testCasePostProcessing(testCase, csvUtilObj, false, parameterMap);
					}

				}
				rowNo++;
			}
		} catch (Exception e) {
			try {
				errorMessage = "Input XML: " + strXMLFile + ", Suite:" + this.suiteName + ", Test Case:"
						+ this.testCaseName + ", Element Name: " + elName + ", AttributeMap : "
						+ testCase.getAttributes() + ", Frame Name : " + this.handler.getDriver().getCurrentFrame()
						+ " : Exception " + e.getMessage();
				errorMessage = errorMessage.replaceAll("\"", "'");
				errorMessage = errorMessage.replaceAll("\r\n", " /n ");
				errorMessage = errorMessage.replaceAll("\n", " /n ");
			} catch (Exception ex) {
				errorMessage = "Input XML: " + strXMLFile + ", Suite:" + this.suiteName + ", Test Case:"
						+ this.testCaseName + ", : Exception " + e.getMessage();
			}
			LoggerUtil.debug(errorMessage, e);
			LoggerUtil.error(errorMessage, e);
			csvOutputUtilObj.write(e, null);
			Reporter.log("Suite: " + this.suiteName + ", Test Case: " + this.testCaseName + ", Status: fail. Error: "
					+ e.getMessage());

			testCasePostProcessing(testCase, csvUtilObj, false, parameterMap);

		} finally {
			this.handler.emptyLnkLstOfIfStack(lnkLstOfIfStack);
		}
		// Send Fail message to TestNG, if there is any failure.
		if (StringUtils.isNoneBlank(errorMessage) && !errorMessage.contains(terminationMessage)) {
			this.commonUtil.showNotification(notificationMsg + ", Status: Fail");
			System.out.println(printMsg + ", Status: Fail");
			Assert.fail(errorMessage);
		} else if (StringUtils.isNoneBlank(errorMessage) && errorMessage.contains(terminationMessage)) {
			this.commonUtil.showNotification(notificationMsg + ", Status: Aborted");
			System.out.println(printMsg + ", Status: Aborted");
			throw new SkipException(terminationMessage);
		} else {
			this.commonUtil.showNotification(notificationMsg + ", Status: Pass");
			System.out.println(printMsg + ", Status: Pass");
			Reporter.log("Suite: " + this.suiteName + ", Test Case: " + this.testCaseName + ", Status: Pass");
		}
	}

	@AfterTest
	public void afterTest(ITestContext ctx) throws Exception {
		this.commonUtil.stopVideoRecord();
		if (poolSize > 1 && handler != null) {
			handler.close();
			handler = null;
		}
	}

	public void testCasePostProcessing(TestCase xmlUtilObj, ICSVUtil csvUtilObj, boolean hasPost,
			Map<String, String> attributeMap) throws Exception {

		boolean inInclude = false;

		LoggerUtil.debug("Start of testCasePostProcessing");
		try {
			if (xmlUtilObj == null || csvUtilObj == null)
				return;

			String element = xmlUtilObj.getNextElement();
			Stack<TestCase> testCaseStack = new Stack<>();
			while (element != null) {
				if ("endpost".equalsIgnoreCase(element)) {
					String msg = xmlUtilObj.getFileName() + "::Line-> " + xmlUtilObj.getLineNumber() + " => "
							+ suiteName + " => " + testCaseName + " => <" + element + "> Processing";
					System.out.println(msg);
					LoggerUtil.debug(msg);
					LoggerUtil.debug("End of Post/EndPost");
					if (!testCaseStack.empty()) {
						xmlUtilObj = testCaseStack.pop();
					} else
						break;
				} else if ("post".equalsIgnoreCase(element)) {
					String msg = xmlUtilObj.getFileName() + "::Line-> " + xmlUtilObj.getLineNumber() + " => "
							+ suiteName + " => " + testCaseName + " => <" + element + "> Processing";
					System.out.println(msg);
					LoggerUtil.debug(msg);
					LoggerUtil.debug("Starts of Post/EndPost");
					hasPost = true;
				} else if ("include".equalsIgnoreCase(element) && hasPost) {
					inInclude = true;
					String msg = xmlUtilObj.getFileName() + "::Line-> " + xmlUtilObj.getLineNumber() + " => "
							+ suiteName + " => " + testCaseName + " => <" + element + "> Processing";
					System.out.println(msg);
					LoggerUtil.debug(msg);
					LoggerUtil.debug("Start of Include");
					LoggerUtil.debug("element: " + element);
					testCaseStack.push(xmlUtilObj);
					Map<String, String> attributeIncludeMap = xmlUtilObj.getAttributes();
					xmlUtilObj = getIncludedXML(attributeIncludeMap);
					hasPost = true;
					element = xmlUtilObj.getNextElement();

				} else if (hasPost) {
					this.handler.processElement(element, xmlUtilObj, csvUtilObj, lnkLstOfIfStack, lstOfPopValue);
				} else {
					LoggerUtil.debug(element + " Tag processing is skipped because of script failure");
				}

				if (!inInclude)
					element = xmlUtilObj.getNextElement();
				else
					inInclude = false;

				// get parent xml object and start processing next element
				if (element == null) {
					while (element == null && !testCaseStack.isEmpty()) {
						LoggerUtil.debug("End of Include");
						xmlUtilObj = testCaseStack.pop();
						element = xmlUtilObj.getNextElement();
					}
				}

			}
		} catch (Exception e) {
			LoggerUtil.debug("testCasePostProcessing failed: Exception" + e);
		}
		LoggerUtil.debug("End of testCasePostProcessing");
	}

	/**
	 * Method to get include tag file path and create XMlUtil Object
	 * 
	 * @param attributeMap
	 * @return IXMLUtil
	 * @throws Exception
	 */
	public TestCase getIncludedXML(Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Started  getIncludedXML");
		String strXmlFilepath = attributeMap.get("filepath");
		if (strXmlFilepath == null || "".equals(strXmlFilepath))
			throw new IllegalArgumentException("filepth attribute is missing for Inlcude Tag.");

		LoggerUtil.debug("strXmlFilepath: " + strXmlFilepath);

		TestCase testCase = new TestCase();
		testCase.parseXML(strXmlFilepath);
		LoggerUtil.debug("Inlcude XML file parsed ");

		LoggerUtil.debug("End of  strXmlFilepath");

		return testCase;
	}
}
