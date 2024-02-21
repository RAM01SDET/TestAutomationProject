package com.steepgraph.ta.framework.common.pages;

import static com.steepgraph.ta.framework.Constants.PROPERTY_KEY_3DSPACE_RELEASE;
import static com.steepgraph.ta.framework.Constants.PROPERTY_KEY_3DSPACE_URL;
import static com.steepgraph.ta.framework.Constants.PROPERTY_KEY_3DSPACE_URL_VERIFY;
import static com.steepgraph.ta.framework.Constants.PROPERTY_KEY_ARAS_RELEASE;
import static com.steepgraph.ta.framework.Constants.PROPERTY_KEY_BROWSER_NAME;
import static com.steepgraph.ta.framework.Constants.PROPERTY_KEY_BROWSER_NEWINSTANCEPERCASE;
import static com.steepgraph.ta.framework.Constants.PROPERTY_KEY_EXECUTION_STEP_INTERVAL;
import static com.steepgraph.ta.framework.Constants.PROPERTY_KEY_INITIAL_URL;
import static com.steepgraph.ta.framework.Constants.PROPERTY_KEY_INITIAL_URL_VERIFY;
import static com.steepgraph.ta.framework.Constants.TESTCASE_RESULT_FAIL;
import static com.steepgraph.ta.framework.Constants.TESTCASE_RESULT_PASS;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.MasterApp;
import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.common.interfaces.ILibrary;
import com.steepgraph.ta.framework.enums.ArasVersion;
import com.steepgraph.ta.framework.enums.EnoviaVersion;
import com.steepgraph.ta.framework.enums.TagElement;
import com.steepgraph.ta.framework.utils.interfaces.ICSVUtil;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.CommonUtil;
import com.steepgraph.ta.framework.utils.pages.DecryptionUtil;
import com.steepgraph.ta.framework.utils.pages.EnoviaUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;
import com.steepgraph.ta.framework.utils.pages.TestCase;

/**
 * Class which handles all the test cases
 * 
 * @author Steepgraph Systems
 */
public class Handler implements IHandler {

	protected RegisterObjectUtil registerUtil;

	protected PropertyUtil propertyUtil;

	protected CommonUtil commonUtil;

	protected String browserName;

	protected String url;

	protected boolean verifyUrl;

	protected String enoviaRelease;

	protected String arasRelease;

	protected Driver driver;

	protected ILibrary library;

	protected String testCaseName;

	String suiteName;

	long itagInterval;

	String status = TESTCASE_RESULT_PASS;

	String errorMessage = "";

	String infoMessage = "";

	boolean isEnovia = true;

	protected OutlookLibrary outlookLibrary;

	boolean isNewInstancePerCase;

	String startStep = null;

	/**
	 * Constructor for Handler that gets driver object, starts browser with given
	 * url and gets WebDriverWait object
	 * 
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	public Handler() throws Exception {
		try {
			this.propertyUtil = PropertyUtil.newInstance();
			this.registerUtil = new RegisterObjectUtil(this.propertyUtil);
			this.commonUtil = new CommonUtil(this.propertyUtil);
			if (!this.commonUtil.isLicenseValid())
				throw new Exception("License is not valid.");

			this.browserName = this.propertyUtil.getProperty(PROPERTY_KEY_BROWSER_NAME);
			LoggerUtil.debug("browser : " + browserName);

			this.enoviaRelease = this.propertyUtil.getProperty(PROPERTY_KEY_3DSPACE_RELEASE);
			LoggerUtil.debug("enoviaRelease : " + this.enoviaRelease);
			if (this.enoviaRelease == null || this.enoviaRelease.equalsIgnoreCase("")) {
				isEnovia = false;
				this.arasRelease = this.propertyUtil.getProperty(PROPERTY_KEY_ARAS_RELEASE);
				LoggerUtil.debug("arasRelease : " + this.arasRelease);
			}

			this.url = this.propertyUtil.getProperty(PROPERTY_KEY_3DSPACE_URL);
			if (StringUtils.isBlank(this.url)) {
				this.url = this.propertyUtil.getProperty(PROPERTY_KEY_INITIAL_URL);
				this.verifyUrl = Constants.CHECK_TRUE
						.equalsIgnoreCase(this.propertyUtil.getProperty(PROPERTY_KEY_INITIAL_URL_VERIFY));
			} else {
				this.verifyUrl = Constants.CHECK_TRUE
						.equalsIgnoreCase(this.propertyUtil.getProperty(PROPERTY_KEY_3DSPACE_URL_VERIFY));
			}
			LoggerUtil.debug("url : " + this.url);
			this.library = initializeLibray();

			String tagInterval = this.propertyUtil.getProperty(PROPERTY_KEY_EXECUTION_STEP_INTERVAL);
			if (tagInterval != null && !"".equals(tagInterval)) {
				this.itagInterval = Long.parseLong(tagInterval);
				this.itagInterval *= 1000;
			} else
				this.itagInterval = 0;

			String strNewInstancePerCase = this.propertyUtil.getProperty(PROPERTY_KEY_BROWSER_NEWINSTANCEPERCASE);
			isNewInstancePerCase = (strNewInstancePerCase != null && "true".equalsIgnoreCase(strNewInstancePerCase))
					? true
					: false;
			LoggerUtil.debug("itagInterval : " + this.itagInterval);
			outlookLibrary = new OutlookLibrary(propertyUtil, registerUtil, commonUtil);
		} catch (Exception e) {
			LoggerUtil.debug(e.getMessage(), e);
			LoggerUtil.error(e.getMessage(), e);
			throw e;
		}
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	@Override
	public String getTestCaseName() {
		return testCaseName;
	}

	@Override
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	@Override
	public String getSuiteName() {
		return suiteName;
	}

	@Override
	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	/**
	 * @author Steepgraph Systems
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void doLogin(Map<String, String> attributeMap) throws Exception {
		try {
			LoggerUtil.debug("Start of doLogin.");
			library.logIn(driver, attributeMap, this.propertyUtil);
			LoggerUtil.debug("End of doLogin.");
		} catch (Exception e) {
			LoggerUtil.debug(e.getMessage(), e);
			LoggerUtil.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Logout method
	 * 
	 * @author Steepgraph Systems
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void logOut(Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of logOut");
		library.logOut(driver, attributeMap);
		LoggerUtil.debug("End of logOut");
	}

	/**
	 * Method to process elements read from XML file and calls appropriate Handler
	 * method to execute test cases in sequence
	 * 
	 * @author Steepgraph Systems
	 * @param handler
	 * @param elementName
	 * @param xmlUtilOb
	 * @param csvUtilObj
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void processElement(String elementName, TestCase testCase, ICSVUtil csvUtilObj,
			LinkedList<Map<String, Boolean>> lnkLstOfIfStack, LinkedList<List<Boolean>> lstOfPopValue)
			throws Exception {

		Map<String, String> attributeMap = testCase.getAttributes();
		prepareTagAttributes(csvUtilObj, attributeMap);
		try {
			boolean ifCondition = false;
			String msg = testCase.getFileName() + "::Line-> " + testCase.getLineNumber() + " => " + suiteName + " => "
					+ testCaseName + " => <" + elementName + "> Processing";
			if ("if".equalsIgnoreCase(elementName)) {
				System.out.println(msg);
				LoggerUtil.debug(msg);
				getIFConditionCheck(ifCondition, csvUtilObj, attributeMap, lnkLstOfIfStack, lstOfPopValue);
				return;
			} else if ("elseif".equalsIgnoreCase(elementName)) {
				System.out.println(msg);
				LoggerUtil.debug(msg);

				if (lnkLstOfIfStack.isEmpty())
					throw new Exception("matching if tag is not found for elseif tag in xml.");

				getELSEIFConditionCheck(ifCondition, csvUtilObj, attributeMap, lnkLstOfIfStack, lstOfPopValue);
				return;
			} else if ("else".equalsIgnoreCase(elementName)) {
				System.out.println(msg);
				LoggerUtil.debug(msg);

				if (lnkLstOfIfStack.isEmpty())
					throw new Exception("matching if tag is not found for else tag in xml.");

				getELSEConditionCheck(lnkLstOfIfStack, lstOfPopValue);
				return;
			} else if ("endif".equalsIgnoreCase(elementName)) {
				System.out.println(msg);
				LoggerUtil.debug(msg);

				if (lnkLstOfIfStack.isEmpty())
					throw new Exception("IF tag is already closed.");

				lstOfPopValue.removeLast();
				removeLastElementFromLinkedList(lnkLstOfIfStack);
				return;
			}

			// if parent IF condition in not satisfied then don't process its child element.

			if (lnkLstOfIfStack.size() > 0) {
				Map<String, Boolean> map = lnkLstOfIfStack.getLast();
				if (map.containsKey("if") && !map.get("if")) {
					return;
				} else if (map.containsKey("elseif") && !map.get("elseif")) {
					return;
				} else if (map.containsKey("else") && !map.get("else")) {
					return;
				}
			}

			if (itagInterval > 0)
				Thread.sleep(itagInterval);

			System.out.println(msg);
			LoggerUtil.debug(msg);
			LoggerUtil.debug("attributeMap: " + attributeMap);

			if ("assert".equalsIgnoreCase(elementName)) {
				String strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.assertTag(driver, attributeMap, strInputText);
				return;
			}

			if ("step".equalsIgnoreCase(elementName)) {
				System.out.println(msg);
				startStep = elementName;
				LoggerUtil.debug(msg);
				if (attributeMap != null) {
					String stepID = attributeMap.get("id");
					if (stepID == null || "".equals(stepID))
						throw new Exception("id attribute can not be null or empty.");

					String stepTitle = attributeMap.get("title");
					if (stepTitle == null || "".equals(stepTitle))
						throw new Exception("title attribute can not be null or empty.");
					LoggerUtil.debug("Start of " + stepTitle + " Step with ID : " + stepID);
				}
				return;
			} else if ("endstep".equalsIgnoreCase(elementName)) {
				if (null == startStep) {
					LoggerUtil.debug("WARNING!!! Step is not defined for EndStep tag, Please define Step tag first.");
					return;
				} else if (startStep.equalsIgnoreCase("step")) {
					LoggerUtil.debug("End of Endstep.");
					return;
				}
			}

			if (elementName.contains("-")) {
				String[] str = elementName.split("-");
				if (str.length == 2) {
					elementName = str[1];
				} else
					LoggerUtil.debug("Invalid input");
			}
			TagElement tagElement = TagElement.valueOf(elementName.toLowerCase());

			switch (tagElement) {

			case login:
				decryptPassword(attributeMap);
				library.logIn(driver, attributeMap, this.propertyUtil);
				break;

			case logout:
				library.logOut(driver, attributeMap);
				break;

			case clickglobalactionsmenu:
				library.clickGlobalActionsMenu(driver, attributeMap);
				break;

			case switchtowindow:
				library.switchToWindow(driver, attributeMap);
				break;

			case clickoverridelink:
				library.clickOverrideLink(driver);
				break;

			case clickelement:
				library.clickElement(driver, attributeMap);
				break;

			case switchtodefaultcontent:
				library.switchToDefaultContent(driver);
				break;

			case switchtoparentwindow:
				library.switchToParentWindow(driver);
				break;

			case switchtoslideinwindow:
				library.switchToSlideInWindow(driver, attributeMap);
				break;

			case handlealert:
				library.handleAlert(driver, attributeMap);
				break;

			case clickportalcommand:
				library.clickPortalCommand(driver, attributeMap);
				break;

			case clickcategorycommand:
				library.clickCategoryCommand(driver, attributeMap);
				break;

			case switchtoframe:
				library.switchToFrame(driver, attributeMap);
				break;

			case switchtocontentframe:
				library.switchToContentFrame(driver, attributeMap);
				break;

			case switchtodetailsdisplayframe:
				library.switchToDetailsDisplayFrame(driver, attributeMap);
				break;

			case switchtoportaldisplayframe:
				library.switchToPortalDisplayFrame(driver, attributeMap);
				break;

			case selectindentedtablerow:
				String strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.selectIndentedTableRow(driver, attributeMap, strInputText);
				break;

			case inputtext:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.inputText(driver, attributeMap, strInputText);
				break;

			case selectcolor:
				library.selectColor(driver, attributeMap);
				break;

			case maximisewindow:
				library.maximiseWindow(driver);
				break;

			case clickrefreshbutton:
				library.clickRefreshButton(driver, attributeMap);
				break;

			case clickbackbutton:
				library.clickBackButton(driver);
				break;

			case clickforwardbutton:
				library.clickForwardButton(driver);
				break;

			case wait:
				library.wait(driver, attributeMap);
				break;

			case lifecycle:
				library.Lifecycle(driver, attributeMap);
				break;

			case uploadfileusingdraganddrop:
				library.uploadFileUsingDragAndDrop(driver, attributeMap);
				break;

			case uploadfileusinglocator:
				String strFilePathInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.uploadFileUsingLocator(driver, attributeMap, strFilePathInputText);
				break;

			case uploadfilefordashboard:
				library.uploadFileForDashboard(driver, attributeMap);
				break;

			case downloadfileusingcommand:
				String strInputFilePath = getAttribute(csvUtilObj, attributeMap, "id");
				library.downloadFileUsingCommand(driver, attributeMap, browserName, strInputFilePath);
				break;

			case downloadfileusingicon:
				String strInptFilePath = getAttribute(csvUtilObj, attributeMap, "id");
				library.downloadFileUsingIcon(driver, attributeMap, browserName, strInptFilePath);
				break;

			case findelement:
				library.findElement(driver, attributeMap, true);
				break;

			case globalsearch:
				String strSearchInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.globalSearch(driver, attributeMap, strSearchInputText);
				break;

			case filtersearchform:
				String strSearchFormText = getAttribute(csvUtilObj, attributeMap, "id");
				library.filterSearchForm(driver, attributeMap, strSearchFormText);
				break;

			case filtersearchperson:
				String strSearchPerson = getAttribute(csvUtilObj, attributeMap, "id");
				library.filterSearchPerson(driver, attributeMap, strSearchPerson);
				break;

			case selectelement:
				String strSelectValue = getAttribute(csvUtilObj, attributeMap, "id");
				library.selectElement(driver, attributeMap, strSelectValue);
				break;

			case closecurrentwindow:
				library.closeCurrentWindow(driver);
				break;

			case action:
				library.action(driver, attributeMap);
				break;

			case applysearch:
				library.applySearchFilter(driver, attributeMap);
				break;

			case resetsearch:
				library.resetSearchFilter(driver, attributeMap);
				break;

			case selectandsubmitsearch:
				String strSelectObjects = getAttribute(csvUtilObj, attributeMap, "id");
				library.selectAndSubmitSearch(driver, attributeMap, strSelectObjects);
				break;

			case selectandsubmit6wtagssearch:
				String selectObjects = getAttribute(csvUtilObj, attributeMap, "id");
				library.selectAndSubmit6WTagsSearch(driver, attributeMap, selectObjects);
				break;

			case opensearchresult:
				String strOpenObject = getAttribute(csvUtilObj, attributeMap, "id");
				library.openSearchResult(driver, attributeMap, strOpenObject);
				break;

			case openactiontoolbarmenu:
				library.openActionToolbarMenu(driver, attributeMap);
				break;

			case print:
				attributeMap.put("lineNumber", testCase.getLineNumber());
				attributeMap.put("fileName", testCase.getFileName());
				library.print(driver, attributeMap);
				break;

			case selectdate:
				String strDate = getAttribute(csvUtilObj, attributeMap, "id");
				library.selectDate(driver, attributeMap, strDate);
				break;

			case registerobject:
				String strKey = getAttribute(csvUtilObj, attributeMap, "id");
				library.registerObject(driver, attributeMap, strKey);
				break;

			case scrolltoelement:
				library.scrollToElement(driver, attributeMap);
				break;

			case editindentedtablerow:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.editIndentedTableRow(driver, attributeMap, strInputText);
				break;

			case checkindentedtablerow:
				library.checkIndentedTableRow(driver, attributeMap);
				break;

			case checkelement:
				library.checkElement(driver, attributeMap);
				break;

			case shownotification:
				library.showNotification(driver, attributeMap);
				break;

			case openchooser:
				library.openChooser(driver, attributeMap);
				break;

			case downloadfile:
				library.downloadFileUsingLocator(driver, attributeMap, browserName);
				break;

			case clickwindowelement:
				library.clickWindowElement(driver, attributeMap);
				break;

			case clickmydeskmenu:
				library.clickMyDeskMenu(driver, attributeMap);
				break;

			case setcontent:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.setContent(driver, attributeMap, strInputText);
				break;

			case inputwindowelement:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.inputWindowElement(driver, attributeMap, strInputText);
				break;

			case highlightelement:
				library.highLightElement(driver, attributeMap);
				break;

			case mqlassert:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.mqlAssert(driver, attributeMap, strInputText);
				break;

			case sendkeys:
				library.sendKey(driver, attributeMap);
				break;

			case assertalert:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.assertAlert(driver, attributeMap, strInputText);
				break;

			case assertdeletion:
				library.assertDeletion(driver, attributeMap);
				break;

			case switchtoparentframe:
				library.switchToParentFrame(driver, attributeMap);
				break;

			case assertlistvalues:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.assertListValues(driver, attributeMap, strInputText);
				break;

			case openurl:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.openURL(driver, attributeMap, strInputText);
				break;

			case callwebservice:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				decryptPassword(attributeMap);
				library.callWebService(driver, attributeMap, strInputText);
				break;

			case execute:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.execute(driver, attributeMap, strInputText);
				break;

			case opendbconnection:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.openDBConnection(driver, attributeMap, strInputText, this.propertyUtil);
				break;

			case closeallotherwindow:
				library.closeAllOtherWindow(driver, attributeMap);
				break;

			case assertfileexists:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.assertFileExists(driver, attributeMap, strInputText);
				break;

			case assertindentedtablerow:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.assertIndentedTableRow(driver, attributeMap, strInputText);
				break;

			case runmql:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.runMql(driver, attributeMap, strInputText);
				break;

			case clickglobaltoolsmenu:
				library.clickGlobalToolsMenu(driver, attributeMap);
				break;

			case deleteallcookies:
				library.deleteAllCookies(driver, attributeMap);
				break;

			case clickhomemenu:
				library.clickHomeMenu(driver, attributeMap);
				break;

			case selectwindowregion:
				library.selectWindowRegion(driver, attributeMap);
				break;

			case switchtostartwindow:
				library.switchToStartWindow(driver, attributeMap);
				break;

			case setstartwindow:
				library.setStartWindow(driver, attributeMap);
				break;

			case opencompassapp:
				library.openCompassApp(driver, attributeMap);
				break;
			case registerindentedtableobjects:
				library.registerIndentedTableObjects(driver, attributeMap);
				break;
			case selectoropennewindentedtablerow:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.selectOropenNewIndentedTableRow(driver, attributeMap, strInputText);
				break;
			case assertpageloadtime:
				library.assertPageLoadTime(driver, attributeMap);
				break;
			case draganddrop:
				library.dragAndDrop(driver, attributeMap, this.browserName);
				break;
			case selectsecuritycontext:
				library.selectSecurityContextFromTopBar(driver, attributeMap);
				break;
			case startperformancelogs:
				library.startPerformanceLogs(driver);
				break;
			case stopperformancelogs:
				library.stopPerformanceLogs(driver, attributeMap, msg);
				break;

			case readuserinput:
				library.readUserInput(driver);
				break;

			case getvaluefromcookies:
				library.getValueFromCookies(driver, attributeMap);
				break;

			case pinwidgettodashboard:
				library.pinWidgetToDashboard(driver, attributeMap);
				break;

			case deletecurrenttabindashboard:
				library.deleteCurrentTabInDashboard(driver, attributeMap);
				break;

			case selectcontextmenuoption:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.selectContextMenuOption(driver, attributeMap);
				break;

			case maximizeandminimize:
				library.maximizeAndMinimize(driver, attributeMap);
				break;

			case refreshbrowser:
				library.refreshBrowser(driver, attributeMap);
				break;

			case clicknavigationpanelmenu:
				library.clickNavigationPanelMenu(driver, attributeMap);
				break;

			case opennewtabindashboard:
				library.openNewTabInDashBoard(driver);
				break;

			case validatetableexport:
				library.validateTableExport(driver, attributeMap);
				break;

			case readxml:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.readXML(driver, attributeMap, strInputText);
				break;

			case assertxml:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.assertXML(driver, attributeMap, strInputText);
				break;

			case opennewtoolbarindashboard:
				library.openNewToolbarInDashboard(driver, attributeMap);
				break;

			case validatetableheader:
				library.validateTableHeader(driver, attributeMap);
				break;

			case clickdashboardcheckbox:
				library.clickDashboardCheckbox(driver, attributeMap);
				break;

			case outlooklogin:
				decryptPassword(attributeMap);
				outlookLibrary.outlookLogin(driver, attributeMap, library);
				break;

			case searchoutlookmail:
				outlookLibrary.searchOutlookMail(driver, attributeMap);
				break;

			case assertoutlookmailheader:
				outlookLibrary.assertOutlookMailHeader(driver, attributeMap);
				break;

			case outlooklogout:
				outlookLibrary.outlooklogout(driver, attributeMap, library);
				break;

			case option:
				library.option(driver, attributeMap);
				break;

			case validatebegin:
				library.validateBegin(driver, attributeMap);
				break;

			case validatebehaviour:
				library.validateBehaviour(driver, attributeMap);
				break;

			case validateend:
				library.validateEnd(driver, attributeMap);
				break;

			case createdashboard:
				library.createDashboard(driver, attributeMap);
				break;

			case deactive6wsearch:
				library.deactive6Wsearch(driver, attributeMap);
				break;

			case deletedashboard:
				library.deletedashboard(driver);
				break;

			case assertdate:
				strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				library.assertDate(csvUtilObj, driver, attributeMap, strInputText);
				break;

			default:
				throw new Exception(
						"ERROR:TAGINVALID - " + elementName + " is not a valid tag in " + testCase.getFileName());
			}
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * Method to get value of data from current row in CSV file
	 * 
	 * @author Steepgraph Systems
	 * @param csvUtil
	 * @param attributeMap
	 * @param attributeName
	 * @return String
	 * @throws Exception
	 */
	public String getAttribute(ICSVUtil csvUtil, Map<String, String> attributeMap, String attributeName)
			throws Exception {
		String strAttributeValue = "";
		if (attributeMap == null) {
			return strAttributeValue;
		}

		if (attributeMap.containsKey("id") && attributeMap.containsKey("input")) {
			LoggerUtil.debug("You are using both attribute id and input, so it will take value from ID's csv value");
		}

		if (attributeMap.containsKey(attributeName)) {
			String strAttributeKey = attributeMap.get(attributeName);
			strAttributeValue = csvUtil.getCell(strAttributeKey);
			strAttributeValue = parseREGAttribute(strAttributeValue, this.registerUtil);
		}

		if ((strAttributeValue == null || "".equalsIgnoreCase(strAttributeValue)) && attributeMap.containsKey("input"))
			strAttributeValue = attributeMap.get("input");

		if ((strAttributeValue == null || "".equalsIgnoreCase(strAttributeValue)) && attributeMap.containsKey("id")) {
			if (strAttributeValue.equals(strAttributeValue)) {
				strAttributeValue = "";
			}
		}
		return strAttributeValue;
	}

	/**
	 * This Method will be used to release resources used by framework driver
	 * 
	 * @author Steepgraph Systems *
	 * @throws Exception
	 */
	@Override
	public void close() throws Exception {
		EnoviaUtil.newInstance(propertyUtil).disconnect();
		if (driver != null)
			driver.close();
		propertyUtil = null;
		commonUtil = null;
		if (registerUtil != null)
			registerUtil.close();
		registerUtil = null;
	}

	@Override
	public Driver getDriver() {
		return driver;
	}

	@Override
	public void initializeDriver(Map<String, String> parameterMap) throws Exception {
		// Code Logic to reset the driver and open new Instance of browser - START
		if (this.driver != null && isNewInstancePerCase) {
			this.driver.close();
			this.driver = null;
		}
		// Code Logic to reset the driver and open new Instance of browser - END
		if (this.driver == null) {
			// Get driver object
			this.driver = new Driver(browserName, this.propertyUtil, parameterMap);
			LoggerUtil.debug("Driver object of " + this.browserName + " obtained.");
			if (!StringUtils.isBlank(this.url)) {
				// Start browser with given URL
				this.driver.startBrowsing(this.url, this.verifyUrl);
				LoggerUtil.debug("Browser launched.");
			}
		}
	}

	public void pushIfIntoStack(boolean input, LinkedList<Map<String, Boolean>> lnkLstOfIfStack) {
		Map<String, Boolean> map = new HashMap<>();
		map.put("if", input);
		lnkLstOfIfStack.add(map);
	}

	public void pushElseIfIntoStack(boolean input, LinkedList<Map<String, Boolean>> lnkLstOfIfStack) {
		Map<String, Boolean> map = new HashMap<>();
		map.put("elseif", input);
		lnkLstOfIfStack.add(map);
	}

	public void pushElseIntoStack(boolean input, LinkedList<Map<String, Boolean>> lnkLstOfIfStack) {
		Map<String, Boolean> map = new HashMap<>();
		map.put("else", input);
		lnkLstOfIfStack.add(map);
	}

	public boolean getLastElementFromLinkedList(LinkedList<Map<String, Boolean>> lnkLstOfIfStack) {
		Map<String, Boolean> map = lnkLstOfIfStack.getLast();
		if (map.containsKey("if"))
			return map.get("if");
		else if (map.containsKey("elseif"))
			return map.get("elseif");
		else
			return map.get("else");
	}

	public void removeLastElementFromLinkedList(LinkedList<Map<String, Boolean>> lnkLstOfIfStack) {
		lnkLstOfIfStack.removeLast();

	}

	@Override
	public ILibrary getLibrary() {
		return library;
	}

	@Override
	public void prepareTagAttributes(ICSVUtil csvUtil, Map<String, String> attributeMap) throws Exception {
		List<String> skipAttributes = new ArrayList<>();
		skipAttributes.add("locatortype");
		skipAttributes.add("id");
		skipAttributes.add("refid");
		for (String attrName : attributeMap.keySet()) {
			if (attrName != null && !"".equals(attrName)) {
				if (!skipAttributes.contains(attrName.toLowerCase())) {
					String attrValue = attributeMap.get(attrName);
					attrValue = this.parseAttribute(attrValue, csvUtil, this.registerUtil);
					attributeMap.put(attrName, attrValue);
				}
			}
		}
	}

	@Override
	public void decryptPassword(Map<String, String> attributeMap) throws Exception {

		if (null != attributeMap) {
			String key = "password";
			if (attributeMap.containsKey("Password")) {
				key = "Password";
			} else if (attributeMap.containsKey("PASSWORD")) {
				key = "PASSWORD";
			}
			attributeMap.put(key, DecryptionUtil.decrypt(attributeMap.get(key)));
		}
	}

	@Override
	public void setStatus(String status, String errorMessage, String infoMessage) {
		if (StringUtils.equalsIgnoreCase(TESTCASE_RESULT_FAIL, status)) {
			this.status = TESTCASE_RESULT_FAIL;
		}

		if (StringUtils.isNoneBlank(errorMessage)) {
			this.errorMessage += "\n" + errorMessage;
		}

		if (StringUtils.isNoneBlank(infoMessage)) {
			this.infoMessage += "\n" + infoMessage;
		}
	}

	@Override
	public void clearStatus() {
		this.status = TESTCASE_RESULT_PASS;
		this.errorMessage = "";
		this.infoMessage = "";
	}

	@Override
	public String getStatus() {
		return this.status;
	}

	@Override
	public String getErrorMessage() {
		return this.errorMessage;
	}

	@Override
	public String getInfoMessage() {
		return this.infoMessage;
	}

	@Override
	public void setInfoMessage(String infoMessage) {
		if (StringUtils.isNoneBlank(infoMessage)) {
			this.infoMessage += "\n" + infoMessage;
		}
	}

	@Override
	public ILibrary initializeLibray() throws Exception {
		LoggerUtil.debug("Start of initilaize library");
		Class<?> librayClass = null;

		if (isEnovia) {
			EnoviaVersion eVersion = EnoviaVersion.valueOf(enoviaRelease.toLowerCase());

			switch (eVersion) {

			case v6r2012x:
				librayClass = Class.forName("com.steepgraph.ta.framework.V6R2012x.pages.Library");
				break;

			case v6r2013x:
				librayClass = Class.forName("com.steepgraph.ta.framework.V6R2013x.pages.Library");
				break;

			case v6r2015x:
				librayClass = Class.forName("com.steepgraph.ta.framework.V6R2015x.pages.Library");
				break;

			case v6r2017x:
				librayClass = Class.forName("com.steepgraph.ta.framework.V6R2017x.pages.Library");
				break;

			case v6r2019x:
				librayClass = Class.forName("com.steepgraph.ta.framework.V6R2019x.pages.Library");
				break;

			case v6r2020x:
				librayClass = Class.forName("com.steepgraph.ta.framework.V6R2020x.pages.Library");
				break;

			case v6r2021x:
				librayClass = Class.forName("com.steepgraph.ta.framework.V6R2021x.pages.Library");
				break;

			case v6r2022x:
				librayClass = Class.forName("com.steepgraph.ta.framework.V6R2022x.pages.Library");
				break;

			case v6r2022xfd02:
				librayClass = Class.forName("com.steepgraph.ta.framework.V6R2022x.pages.Library");
				break;

			case v6r2022xfd06:
				librayClass = Class.forName("com.steepgraph.ta.framework.V6R2022xFD06.pages.Library");
				break;

			default:
				throw new Exception(
						PROPERTY_KEY_3DSPACE_RELEASE + " key is not defined properly in configuration file.");
			}

		} else {
			ArasVersion aVersion = ArasVersion.valueOf(arasRelease.toLowerCase());

			switch (aVersion) {
			case aras12:
				librayClass = Class.forName("com.steepgraph.ta.framework.ARAS12.pages.Library");
				break;

			default:
				throw new Exception(PROPERTY_KEY_ARAS_RELEASE + " key is not defined properly in configuration file.");
			}
		}

		LoggerUtil.debug("librayClass : " + librayClass);

		// Get Handler class's constructor for further processing
		Constructor<?> constructor = librayClass.getConstructor(IHandler.class, RegisterObjectUtil.class,
				PropertyUtil.class, ICommonUtil.class);
		// Create new instance of Handler
		ILibrary libraryInstance = (ILibrary) constructor.newInstance(this, registerUtil, propertyUtil, commonUtil);
		LoggerUtil.debug("New Libray object created for Enovia version: " + enoviaRelease + ".");
		return libraryInstance;
	}

	@Override
	public RegisterObjectUtil getRegisterUtil() {
		return registerUtil;
	}

	@Override
	public void setRegisterUtil(RegisterObjectUtil registerUtil) {
		this.registerUtil = registerUtil;
	}

	@Override
	public PropertyUtil getPropertyUtil() {
		return propertyUtil;
	}

	@Override
	public void setPropertyUtil(PropertyUtil propertyUtil) {
		this.propertyUtil = propertyUtil;
	}

	@Override
	public CommonUtil getCommonUtil() {
		return commonUtil;
	}

	@Override
	public void setCommonUtil(CommonUtil commonUtil) {
		this.commonUtil = commonUtil;
	}

	/**
	 * Method to build expression by taking values from csv and registry
	 * 
	 * @param attributeValue
	 * @param csvUtil
	 * @param registerUtil
	 * 
	 * @return parsed String
	 * @throws Exception
	 */
	@Override
	public String parseAttribute(String attributeValue, ICSVUtil csvUtil, RegisterObjectUtil registerUtil)
			throws Exception {
		return parseREGAttribute(parseCSVAttribute(attributeValue, csvUtil), registerUtil);
	}

	private String parseCSVAttribute(String attributeValue, ICSVUtil csvUtil) throws Exception {
		LoggerUtil.debug("Start of  parseCSVAttribute");
		String parsedValue = attributeValue;
		if (parsedValue == null || "".equals(parsedValue))
			return parsedValue;

		Pattern pattern = Pattern.compile("\\$csv\\{[^\\{\\}]+\\}");
		Matcher matcher = pattern.matcher(attributeValue);
		while (matcher.find()) {
			String matchedString = matcher.group(0);
			LoggerUtil.debug("csv matched String : " + matchedString);
			if (matchedString != null && !"".equals(matchedString)) {
				String csvColumnName = matchedString.replace("$csv{", "").replace("}", "");
				String csvColumnValue = csvUtil.getCell(csvColumnName);
				if (csvColumnValue != null && !"".equals(csvColumnValue))
					parsedValue = parsedValue.replace(matchedString, csvColumnValue);
				else
					parsedValue = parsedValue.replace(matchedString, "");
			}
		}
		LoggerUtil.debug("End of  parseCSVAttribute");
		return parsedValue;
	}

	public String parseREGAttribute(String attributeValue, RegisterObjectUtil registerUtil) throws Exception {
		LoggerUtil.debug("Start of  parseREGAttribute");
		String parsedValue = attributeValue;
		if (parsedValue == null || "".equals(parsedValue))
			return parsedValue;

		parsedValue = parseREGToolVariables(parsedValue);

		Pattern pattern = Pattern.compile("\\$reg\\{[^\\{\\}]+\\}");
		Matcher matcher = pattern.matcher(parsedValue);
		while (matcher.find()) {
			String matchedString = matcher.group(0);
			LoggerUtil.debug("registration matched String : " + matchedString);
			if (matchedString != null && !"".equals(matchedString)) {
				String strRegKey = matchedString.replace("$reg{", "").replace("}", "");
				String registeredValue = registerUtil.getRegisteredData(MasterApp.newInstance().getSuiteId(),
						strRegKey);
				if (registeredValue != null && !"".equals(registeredValue))
					parsedValue = parsedValue.replace(matchedString, registeredValue);
				else
					parsedValue = parsedValue.replace(matchedString, "");
			}
		}
		parsedValue = parsedValue.replaceAll("%t", CommonUtil.getCurrentTimeStampFolderName());
		LoggerUtil.debug("End of  parseREGAttribute");
		return parsedValue;
	}

	private String parseREGToolVariables(String attributeValue) throws Exception {
		LoggerUtil.debug("Start of  parseREGToolVariables");
		String parsedValue = attributeValue;
		if (parsedValue == null || "".equals(parsedValue))
			return parsedValue;

		Pattern currnetDatepattern = Pattern.compile("\\$reg\\{currentDate(##(.*)##)?\\}");
		Matcher matcher = currnetDatepattern.matcher(parsedValue);
		while (matcher.find()) {
			String matchedString = matcher.group(0);
			LoggerUtil.debug("Matched String : " + matchedString);

			String matchedFormat = matcher.group(2);

			if (null != matchedString && !"".equals(matchedString)) {
				String format = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_DATE_FORMAT);
				SimpleDateFormat df = null;
				if (null != matchedFormat && !"".equals(matchedFormat)) {
					format = matchedFormat;
				}
				try {
					df = new SimpleDateFormat(format);
				} catch (Exception e) {
					format = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_DATE_FORMAT);
					LoggerUtil.debug("Current date format is incorrect. using the default date format : " + format
							+ ". Correct format: $reg{currentDate} or $reg{currentDate##<ACTUAL DATE FORMAT>##}");
					try {
						df = new SimpleDateFormat(format);
					} catch (Exception e1) {
						format = PropertyUtil.STR_DEFAULT_DATE_FORMAT;
						LoggerUtil.debug("Current date format is incorrect. using the default date format : " + format
								+ ". Correct format: $reg{currentDate} or $reg{currentDate##<ACTUAL DATE FORMAT>##}");
						df = new SimpleDateFormat(format);
					}
				}
				parsedValue = attributeValue.replace(matchedString, df.format(Calendar.getInstance().getTime()));
				System.out.println("parsedValue :" + parsedValue);
			}
		}
		LoggerUtil.debug("End of  parseREGToolVariables");

		return parsedValue;
	}

	/**
	 * @method This method is used to add IF condition in LinkedList
	 * @param ifCondition
	 * @param csvUtilObj
	 * @param attributeMap
	 * @throws Exception
	 * @return void
	 */
	private void getIFConditionCheck(boolean ifCondition, ICSVUtil csvUtilObj, Map<String, String> attributeMap,
			LinkedList<Map<String, Boolean>> lnkLstOfIfStack, LinkedList<List<Boolean>> lstOfPopValue)
			throws Exception {

		String strInputText = getAttribute(csvUtilObj, attributeMap, "id");
		ifCondition = library.ifCondition(driver, attributeMap, strInputText);
		int iSizeStack = lnkLstOfIfStack.size();
		boolean ifFlag = false;
		List<Boolean> lstOfBoolean = new ArrayList<Boolean>();

		if (!lnkLstOfIfStack.isEmpty()) {

			for (int iTag = 0; iTag < iSizeStack; iTag++) {
				Map<String, Boolean> map = lnkLstOfIfStack.get(iTag);
				if ((map.containsKey("if") && !map.get("if")) || (map.containsKey("elseif") && !map.get("elseif"))
						|| (map.containsKey("else") && !map.get("else"))) {
					ifFlag = true;
				}
			}

			if (!ifFlag) {
				pushIfIntoStack(ifCondition, lnkLstOfIfStack);
			} else {
				pushIfIntoStack(false, lnkLstOfIfStack);
			}
		} else {
			pushIfIntoStack(ifCondition, lnkLstOfIfStack);
		}
		lstOfBoolean.add(ifCondition);
		lstOfPopValue.add(lstOfBoolean);
	}

	/**
	 * @method This method is used to add ELSEIF condition by poping the previous IF
	 *         or ELSEIF condition in LinkedList
	 * @param ifCondition
	 * @param csvUtilObj
	 * @param attributeMap
	 * @throws Exception
	 */
	private void getELSEIFConditionCheck(boolean ifCondition, ICSVUtil csvUtilObj, Map<String, String> attributeMap,
			LinkedList<Map<String, Boolean>> lnkLstOfIfStack, LinkedList<List<Boolean>> lstOfPopValue)
			throws Exception {

		boolean ifElseFlag = getLastElementFromLinkedList(lnkLstOfIfStack);
		boolean elseIfParentFlag = true;
		List<Boolean> lstOfBoolean = lstOfPopValue.removeLast();

		for (boolean blCheck : lstOfBoolean) {
			if (blCheck)
				elseIfParentFlag = false;
		}

		if (!ifElseFlag && elseIfParentFlag) {
			boolean ifFlag = getBooleanCheckForCondition(lnkLstOfIfStack);
			removeLastElementFromLinkedList(lnkLstOfIfStack);

			if (!ifFlag) {
				String strInputText = getAttribute(csvUtilObj, attributeMap, "id");
				boolean elseIfCondition = library.ifCondition(driver, attributeMap, strInputText);
				pushElseIfIntoStack(elseIfCondition, lnkLstOfIfStack);
			} else {
				pushElseIfIntoStack(false, lnkLstOfIfStack);
			}

		} else {
			removeLastElementFromLinkedList(lnkLstOfIfStack);
			pushElseIfIntoStack(false, lnkLstOfIfStack);
		}
		lstOfBoolean.add(ifElseFlag);
		lstOfPopValue.add(lstOfBoolean);
	}

	/**
	 * @method This method is used to add ELSEIF condition by poping the previous IF
	 *         or ELSEIF condition in LinkedList
	 * @return void
	 */
	private void getELSEConditionCheck(LinkedList<Map<String, Boolean>> lnkLstOfIfStack,
			LinkedList<List<Boolean>> lstOfPopValue) {

		boolean elseIfFlag = getLastElementFromLinkedList(lnkLstOfIfStack);
		boolean elseFlag = true;
		List<Boolean> lstOfBoolean = lstOfPopValue.removeLast();

		for (boolean blCheck : lstOfBoolean) {
			if (blCheck)
				elseFlag = false;
		}

		if (!elseIfFlag && elseFlag) {
			boolean elseIfParentFlag = getBooleanCheckForCondition(lnkLstOfIfStack);
			removeLastElementFromLinkedList(lnkLstOfIfStack);

			if (!elseIfParentFlag) {
				pushElseIntoStack(true, lnkLstOfIfStack);
			} else {
				pushElseIntoStack(false, lnkLstOfIfStack);
			}
		} else {
			removeLastElementFromLinkedList(lnkLstOfIfStack);
			pushElseIntoStack(false, lnkLstOfIfStack);
		}
		lstOfBoolean.add(elseIfFlag);
		lstOfPopValue.add(lstOfBoolean);
	}

	/**
	 * @method This method is used to check the parent node having value is false
	 * @return boolean
	 */
	private boolean getBooleanCheckForCondition(LinkedList<Map<String, Boolean>> lnkLstOfIfStack) {

		boolean flag = false;
		if (lnkLstOfIfStack.size() > 1) {

			for (int iIndex = 0; iIndex < lnkLstOfIfStack.size() - 1; iIndex++) {
				Map<String, Boolean> mapData = lnkLstOfIfStack.get(iIndex);
				if ((mapData.containsKey("if") && !mapData.get("if"))
						|| (mapData.containsKey("else") && !mapData.get("else"))
						|| (mapData.containsKey("elseif") && !mapData.get("elseif"))) {
					flag = true;
				}
			}
		}
		return flag;
	}

	@Override
	public void emptyLnkLstOfIfStack(LinkedList<Map<String, Boolean>> lnkLstOfIfStack) {

		lnkLstOfIfStack.clear();
	}
}
