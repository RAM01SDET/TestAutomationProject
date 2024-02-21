package com.steepgraph.ta.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.testng.ISuiteListener;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import com.steepgraph.ta.framework.common.SuiteListener;
import com.steepgraph.ta.framework.common.TestCaseLoader;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.interfaces.IDBUtil;
import com.steepgraph.ta.framework.utils.pages.CSVUtil;
import com.steepgraph.ta.framework.utils.pages.CommonUtil;
import com.steepgraph.ta.framework.utils.pages.DBUtil;
import com.steepgraph.ta.framework.utils.pages.EMailUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Class from where test automation begins by reading login data file
 * 
 * @author Steepgraph Systems
 *
 */
public class MasterApp implements Constants {

	private String suiteId;

	private String suiteName;

	private PropertyUtil propertyUtil;

	private static MasterApp singleInstance;

	private MasterApp() {
	}

	public static synchronized MasterApp newInstance() throws Exception {
		if (singleInstance == null) {
			singleInstance = new MasterApp();
			singleInstance.propertyUtil = PropertyUtil.newInstance();
		}
		return singleInstance;
	}

	/**
	 * Main method where test automation begins by reading login data file
	 * 
	 * @author SteepGraph Systems
	 * @param args
	 * @return void
	 * @throws Exception
	 */
	public void start() throws Exception {

		System.out.println("3DX-TAS execution started....!");

		LoggerUtil.Configure();
		LoggerUtil.debug("3DX-TAS Execution started.");

		ICommonUtil commonUtil = new CommonUtil(this.propertyUtil);
		this.propertyUtil.setTerminationInvoked(false);
		commonUtil.setExecutionResultFileHeader();
		commonUtil.setPerformanceResultFileHeader();
		commonUtil.showNotification("Execution started");

		String autodownloadBrowser;
		String browserName;
		String driverPath;

		autodownloadBrowser = propertyUtil.getProperty(Constants.PROPERTY_KEY_WEBDRIVER_AUTODOWNLOAD);
		browserName = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_NAME);
		driverPath = System.getProperty("user.dir") + "\\drivers";
		LoggerUtil.debug("Auto Download browser driver : " + autodownloadBrowser);
		if ("true".equalsIgnoreCase(autodownloadBrowser)) {
			switch (browserName) {
			case "chrome":
				LoggerUtil.debug("Chrome driver downloading started.");
				WebDriverManager.chromedriver().cachePath(driverPath).avoidOutputTree().setup();
				LoggerUtil.debug("Chrome driver downloading completed.");
				propertyUtil.setProperty(Constants.PROPERTY_KEY_BROWSER_WEBDRIVER_PATH,
						driverPath + "//chromedriver.exe");
				LoggerUtil.debug("Web driver path set to drivers/chromedriver.exe.");
				break;
			case "chromium":
				LoggerUtil.debug("chromium driver downloading started.");
				WebDriverManager.chromiumdriver().cachePath(driverPath).avoidOutputTree().setup();
				LoggerUtil.debug("chromium driver downloading completed.");
				propertyUtil.setProperty(Constants.PROPERTY_KEY_BROWSER_WEBDRIVER_PATH,
						driverPath + "//chromiumdriver.exe");
				LoggerUtil.debug("Web driver path set to drivers/chromiumdriver.exe.");
				break;
			case "firefox":
				LoggerUtil.debug("firefox driver downloading started.");
				WebDriverManager.firefoxdriver().cachePath(driverPath).avoidOutputTree().setup();
				LoggerUtil.debug("firefox driver downloading completed.");
				propertyUtil.setProperty(Constants.PROPERTY_KEY_BROWSER_WEBDRIVER_PATH,
						driverPath + "//geckodriver.exe");
				LoggerUtil.debug("Web driver path set to drivers/firefoxdriver.exe.");
				break;
			case "opera":
				LoggerUtil.debug("opera driver downloading started.");
				WebDriverManager.operadriver().cachePath(driverPath).avoidOutputTree().setup();
				LoggerUtil.debug("opera driver downloading completed.");
				propertyUtil.setProperty(Constants.PROPERTY_KEY_BROWSER_WEBDRIVER_PATH,
						driverPath + "//operadriver.exe");
				LoggerUtil.debug("Web driver path set to drivers/operadriver.exe.");
				break;
			case "edge":
				LoggerUtil.debug("edge driver downloading started.");
				WebDriverManager.edgedriver().cachePath(driverPath).avoidOutputTree().setup();
				LoggerUtil.debug("edge driver downloading completed.");
				propertyUtil.setProperty(Constants.PROPERTY_KEY_BROWSER_WEBDRIVER_PATH,
						driverPath + "//msedgedriver.exe");
				LoggerUtil.debug("Web driver path set to drivers/msedgedriver.exe.");
				break;
			case "ie":
				LoggerUtil.debug("ie driver downloading started.");
				WebDriverManager.iedriver().cachePath(driverPath).avoidOutputTree().setup();
				LoggerUtil.debug("ie driver downloading completed.");
				propertyUtil.setProperty(Constants.PROPERTY_KEY_BROWSER_WEBDRIVER_PATH, driverPath + "//iedriver.exe");
				LoggerUtil.debug("Web driver path set to drivers/iedriver.exe.");
				break;
			default:
				throw new Exception("Please provide a valid browser name.");
			}
		}

		if (!commonUtil.isLicenseValid())
			throw new Exception("License is not valid.");

		LoggerUtil.debug("License is valid");

		// commonUtil.validateEnoviaVersion();
		LoggerUtil.debug("Enovia Release is valid");

		// commonUtil.setExecutionResultFileHeader();
		LoggerUtil.debug("Excecution result file header created");

		this.suiteName = getSuiteName();
		this.suiteId = getSuiteId();

		String strStartDateTime = ICommonUtil.getCurrentDateTime();

		IDBUtil datasource = new DBUtil(this.propertyUtil);
		datasource.configure();

		TestCaseLoader testCaseLoader = new TestCaseLoader(commonUtil);
		testCaseLoader.startLoading(this.suiteId, this.propertyUtil, commonUtil);

		String loadScriptsOnly = propertyUtil.getProperty(PROPERTY_KEY_DB_RECORD_SCRIPTS_ONLY);
		LoggerUtil.debug("loadScriptsOnly : " + loadScriptsOnly);
		if (loadScriptsOnly != null && "true".equalsIgnoreCase(loadScriptsOnly)) {
			System.out.println("Test automation execution completed....!");
			commonUtil.showNotification("Test automation execution completed");
			LoggerUtil.debug("Test automation execution completed....!");
			datasource.close();
			return;
		}

		CSVUtil loginData = null;
		String loginInputFile = propertyUtil.getProperty("enovia.login.datafile");
		if (loginInputFile != null && !"".equals(loginInputFile.trim())) {
			loginData = new CSVUtil(loginInputFile);
		}
		String testingXmlFilePath = TEST_SUITES_DIR + propertyUtil.getProperty(PROPERTY_KEY_SUITE_FILE);

		LoggerUtil.debug("testingXmlFilePath " + testingXmlFilePath);

		LoggerUtil.debug("Execution Server Name :" + suiteName);

		List<String> suiteFileList = new ArrayList<>();
		suiteFileList.add(testingXmlFilePath);

		TestNG testng = new TestNG();
		testng.setTestSuites(suiteFileList);
		testng.setPreserveOrder(true);
		testng.setListenerClasses(Arrays.asList(com.steepgraph.ta.framework.common.TestCaseListener.class,
				com.steepgraph.ta.framework.common.SuiteListener.class));

		// To execute tests in separate threads, set parallel attribute value as tests
		String parallelExecutionPoolsize = propertyUtil.getProperty(PROPERTY_KEY_PARALLEL_EXECUTION_POOLSIZE);
		if (NumberUtils.isParsable(parallelExecutionPoolsize) && Integer.parseInt(parallelExecutionPoolsize) > 1) {
			testng.setParallel(XmlSuite.ParallelMode.TESTS);
			testng.setThreadCount(Integer.parseInt(parallelExecutionPoolsize));
		}

		// update execution status and start time in database
		Map<String, String> rowData = new HashMap<>();
		rowData.put("suiteid", suiteId);
		rowData.put("suitename", suiteName);
		rowData.put("starttime", strStartDateTime);
		rowData.put("status", "2");

		Map<String, String> criteria = new HashMap<>();
		criteria.put("suiteid", suiteId);
		criteria.put("suitename", suiteName);

		datasource.updateMasterSuite(rowData, criteria);

		int executionStatus = 1;
		try {
			if (loginData == null) {
				testng.run();
			} else {
				loginData.next();
				do {
					testng.run();
				} while (loginData.next());
			}
		} catch (Exception e) {
			LoggerUtil.debug(e.getMessage(), e);
			LoggerUtil.error(e.getMessage(), e);
			executionStatus = 0;
		} finally {
			List<ISuiteListener> suiteListner = testng.getSuiteListeners();
			for (ISuiteListener listner : suiteListner) {
				if (listner instanceof SuiteListener) {
					((SuiteListener) listner).releaseResources();
				}
			}
		}

		System.out.println("Don't close the application.Please wait...");

		System.out.println("Generating Test Report...!");
		commonUtil.showNotification("Generating Test Report");

		String strEndDateTime = ICommonUtil.getCurrentDateTime();
		long executionTimeInSec = ICommonUtil.getDateDiffInSec(strStartDateTime, strEndDateTime);

		rowData.clear();
		rowData.put("endtime", strEndDateTime);
		rowData.put("status", "" + executionStatus);
		rowData.put("execution_time", "" + executionTimeInSec);

		datasource.updateMasterSuite(rowData, criteria);

		System.out.println("Test Report Generated...!");

		String sendMail = propertyUtil.getProperty(PROPERTY_KEY_MAIL_SENDREPORT_ENABLE);

		if (sendMail != null && "true".equalsIgnoreCase(sendMail)) {
			System.out.println("Sending mail...!");
			commonUtil.showNotification("Sending mail");
			EMailUtil emailUtil = new EMailUtil();
			emailUtil.sendTestReportInMail(this.propertyUtil);
		}

		commonUtil.showNotification("Execution completed");
		loginData = null;
		datasource.close();

		LoggerUtil.debug("3DX-TAS execution completed....!");
		System.out.println("3DX-TAS Execution completed....!");
	}

	public String getSuiteName() {
		if (suiteName == null || "".equals(suiteName)) {
			suiteName = propertyUtil.getProperty(PROPERTY_KEY_EXECUTION_TITILE);
			if (suiteName == null || "".equals(suiteName))
				suiteName = "DefaultTitle";
		}
		return suiteName;
	}

	/**
	 * This function will be used to create suite or get existing suiteId from
	 */
	public String getSuiteId() throws Exception {
		if (StringUtils.isBlank(suiteId)) {
			LoggerUtil.debug("Started getSuiteId ");
			suiteId = UUID.randomUUID().toString();
			String strRerunExecution = propertyUtil.getProperty(PROPERTY_KEY_DB_RERUN_EXECUTION);

			boolean rerunExecution = false;
			if (strRerunExecution != null && "false".equalsIgnoreCase(strRerunExecution))
				rerunExecution = false;
			else
				rerunExecution = true;

			if (rerunExecution) {
				String strExecutionServerName = suiteName;
				if (strExecutionServerName == null || "".equalsIgnoreCase(strExecutionServerName))
					throw new Exception(
							"execution.servername key is not defined or empty in TestAutomationFramework.properties");

				LoggerUtil.debug("strExecutionServerName : " + strExecutionServerName);

				String strExecutionDate = propertyUtil.getProperty(PROPERTY_KEY_DB_RERUN_EXECUTION_DATE);
				if (strExecutionDate == null || "".equalsIgnoreCase(strExecutionDate))
					throw new Exception(PROPERTY_KEY_DB_RERUN_EXECUTION_DATE
							+ " key is not defined or empty in TestAutomationFramework.properties");

				LoggerUtil.debug("strExecutionDate : " + strExecutionDate);

				String strExecutionDateFormat = propertyUtil.getProperty(PROPERTY_KEY_DB_RERUN_EXECUTION_DATE_FORMAT);
				if (strExecutionDateFormat == null || "".equalsIgnoreCase(strExecutionDateFormat))
					throw new Exception(PROPERTY_KEY_DB_RERUN_EXECUTION_DATE_FORMAT
							+ " key is not defined or empty in TestAutomationFramework.properties");

				LoggerUtil.debug("strExecutionDateFormat : " + strExecutionDateFormat);

				IDBUtil dbUtil = new DBUtil(this.propertyUtil);
				suiteId = dbUtil.getMasteSuiteExecutionId(strExecutionServerName, strExecutionDate,
						strExecutionDateFormat);

				if (suiteId == null || "".equals(suiteId))
					throw new Exception("Existing execution is not found with server name " + strExecutionServerName
							+ " and execution date " + strExecutionDate);
			}
			LoggerUtil.debug("suiteId: " + suiteId);
		}
		return suiteId;
	}
}