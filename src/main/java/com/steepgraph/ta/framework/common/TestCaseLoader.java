package com.steepgraph.ta.framework.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.interfaces.IDBUtil;
import com.steepgraph.ta.framework.utils.pages.DBUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;

public class TestCaseLoader implements Constants {

	private IDBUtil dbUtil;

	private String suiteId;

	private ICommonUtil commonUtil;

	public TestCaseLoader(ICommonUtil commonUtil) {
		this.commonUtil = commonUtil;
	}

	/**
	 * This will start the test suite xml loading process to database
	 * 
	 * @author Steepgraph Systems
	 * @param filepath
	 * @return void
	 * @throws Exception
	 */
	public void startLoading(String suiteId, PropertyUtil propertyUtil, ICommonUtil commonUtil) throws Exception {

		LoggerUtil.debug("Started startLoading");

		this.suiteId = suiteId;
		String reRunExecution = propertyUtil.getProperty(PROPERTY_KEY_DB_RERUN_EXECUTION);
		if (reRunExecution == null || "".equals(reRunExecution))
			reRunExecution = "false";

		LoggerUtil.debug("reRunExecution : " + reRunExecution);
		if ("true".equalsIgnoreCase(reRunExecution))
			return;

		dbUtil = new DBUtil(propertyUtil);
		boolean isDBEnabled = dbUtil.configure();
		if (isDBEnabled) {
			commonUtil.showNotification("Loading Scripts to Database");

			String executionServerName = commonUtil.getExecutionServerName();

			String masterSuiteFile = propertyUtil.getProperty(PROPERTY_KEY_SUITE_FILE);
			if (masterSuiteFile == null || "".equals(masterSuiteFile))
				throw new Exception(PROPERTY_KEY_SUITE_FILE + " is not specified in configuration file");

			String objectId = "";
			List<XmlSuite> suites = new Parser(TEST_SUITES_DIR + masterSuiteFile).parseToList();
			if (!suites.isEmpty()) {
				objectId = suites.get(0).getParameter("id");
			}

			Map<String, String> rowData = new HashMap<>();
			rowData.put("suiteid", suiteId);
			rowData.put("id", objectId);
			rowData.put("suitename", executionServerName);
			rowData.put("starttime", ICommonUtil.getCurrentDateTime());
			rowData.put("status", "2");

			dbUtil.insertIntoMasterSuite(rowData);
			masterSuiteFile = "testsuites\\" + masterSuiteFile;
			LoggerUtil.debug("suiteXmlFilePath : " + masterSuiteFile);
			readSuiteXml(masterSuiteFile);
		}
		LoggerUtil.debug("End of startLoading");
	}

	/**
	 * This will be used read suite xml file
	 * 
	 * @author Steepgraph Systems
	 * @param suiteXmlFilePath
	 * @return void
	 * @throws Exception
	 */
	public void readSuiteXml(String suiteXmlFilePath) throws Exception {

		LoggerUtil.debug("Start of readSuiteXml");
		List<XmlSuite> suites = new Parser(suiteXmlFilePath).parseToList();
		for (Iterator<XmlSuite> iterator = suites.iterator(); iterator.hasNext();) {
			XmlSuite xmlSuite = iterator.next();
			String suiteName = xmlSuite.getName();
			System.out.println("suiteName: " + suiteName);
			readSuites(xmlSuite);
			readTestCase(xmlSuite);
		}
		LoggerUtil.debug("End of readSuiteXml");
	}

	/**
	 * This will be used read suite child suites of given suite
	 * 
	 * @author Steepgraph Systems
	 * @param XmlSuite
	 * @return void
	 * @throws Exception
	 */
	public void readSuites(XmlSuite xmlSuite) throws Exception {
		LoggerUtil.debug("Start of readSuites");
		List<String> suites = xmlSuite.getSuiteFiles();
		LoggerUtil.debug("Child Suite size: " + suites.size());
		for (Iterator<String> iterator2 = suites.iterator(); iterator2.hasNext();) {
			String childXmlSuitePath = iterator2.next();
			readSuiteXml(childXmlSuitePath);
		}
		LoggerUtil.debug("End of readSuites");
	}

	/**
	 * This will be used read test cases of given suite
	 * 
	 * @author Steepgraph Systems
	 * @param XmlSuite
	 * @return void
	 * @throws Exception
	 */
	public void readTestCase(XmlSuite xmlSuite) throws Exception {
		LoggerUtil.debug("Start of readTestCase");

		Map<String, String> rowData = new HashMap<>();

		String suiteName = xmlSuite.getName();
		LoggerUtil.debug("suiteName : " + suiteName);

		List<XmlTest> testcases = xmlSuite.getTests();
		for (Iterator<XmlTest> iterator2 = testcases.iterator(); iterator2.hasNext();) {
			XmlTest xmlTest = iterator2.next();
			String testCaseName = xmlTest.getName();

			LoggerUtil.debug("testCaseId_Name : " + testCaseName);

			Map<String, String> testParameterMap = xmlTest.getAllParameters();
			String testCaseXMLFilePath = testParameterMap.get("xmlFilePath");
			String inputCsvFilePath = testParameterMap.get("csvFilePath");
			String objectId = testParameterMap.get("id");

			String[] testCaseIdName = commonUtil.getTestCaseIdandName(testCaseName);

			LoggerUtil.debug("testCaseId : " + testCaseIdName[0]);
			LoggerUtil.debug("testCaseName : " + testCaseIdName[1]);

			if (commonUtil.allowTestCaseExecution(suiteName, testCaseIdName[0])) {

				// Insert record to DB
				rowData.clear();

				rowData.put("suiteid", suiteId);
				rowData.put("id", objectId);
				rowData.put("suitename", suiteName);
				rowData.put("testcaseid", testCaseIdName[0]);
				rowData.put("testcasename", testCaseIdName[1]);
				rowData.put("csvfilepath", inputCsvFilePath);
				rowData.put("xmlfilepath", testCaseXMLFilePath);
				rowData.put("status", "2");

				dbUtil.insertIntoTestCase(rowData);
			}
		}

		LoggerUtil.debug("End of readTestCase");
	}
}
