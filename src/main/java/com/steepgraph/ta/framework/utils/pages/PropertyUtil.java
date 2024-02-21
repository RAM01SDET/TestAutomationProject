package com.steepgraph.ta.framework.utils.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.utils.interfaces.IPropertyUtil;

/**
 * 
 * @author SGSPC063
 *
 */
public class PropertyUtil implements Constants, IPropertyUtil {

	private static PropertyUtil singleInstance;

	private Properties prop;

	private static final FilenameFilter PROPERTY_FILE_FILTER = (dir, name) -> name.endsWith(".properties");

	// Browser Details for Automation Testing
	private static final String STR_Browser = "edge";
	private static final String STR_WEBDRIVER_PATH = "drivers/msedgedriver.exe";
	private static final String STR_OPENURL_URL = "https://outlook.office.com/";
	private static final String STR_WEBDRIVER_AUTODOWNLOAD = "false";
	private static final String STR_BROWSER_PROXY = "false";
	private static final String STR_BROWSER_HEADLESS = "false";
	private static final String STR_FIREFOX_MARIONETTE = "true";
	private static final String STR_IE_REQUIREFOCUS = "false";
	private static final String STR_NATIVE_EVENTS = "true";
	private static final String STR_PRIVATE_MODE = "false";
	private static final String STR_ENABLE_OTP = "false";
	private static final String STR_FOR_DRAG_AND_DROP_GENERIC = "true";
	private static final String STR_BROWSER_REMOTE = "false";
	private static final String STR_REMOTE_PLATFORM = "WINDOWS";
	private static final String STR_REMOTE_NODEURL = "http://192.168.1.5:5566/wd/hub";
	private static final String STR_ALLOW_IN_SECURE_SITES = "false";

	// Enovia server details
	private static final String STR_3DSPACE_RELEASE = "V6R2021x";
	private static final String STR_ARAS_RELEASE = "ARAS12";
	private static final String STR_3DSPACE_URL = "about:blank";
	private static final String STR_3DSPACE_URL_VERIFY = "false";
	private static final String STR_3DSPACE_URL_ISCAS = "true";
	private static final String STR_3DSPACE_HAS_TVC = "false";
	private static final String STR_SECURITY_CONTEXT_PAGE = "false";
	private static final String STR_VALIDATION_URL = "";
	private static final String STR_VALIDATION_USERNAME = "creator";
	private static final String STR_VALIDATION_PASSWORD = "";

	// 3DX_TAS_PROPERTIES
	private static final String STR_RUN = "QA-Run-01";
	private static final String STR_SUITE_FILE = "TestPlan.xml";
	private static final String STR_INITIAL_URL_VERIFY = "false";
	private static final String STR_EXECUTION_POOLSIZE = "1";
	private static final String STR_EXECUTION_STEP_INTERVAL = "0";
	private static final String STR_EXECUTION_STEP_TIMEOUT = "30";
	private static final String STR_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL = "5";
	private static final String STR_STEP_RETRY_COUNT = "2";
	private static final String STR_PRIVATE_KEY = "Bar12345Bar12345";
	private static final String STR_TESTRESULTS_OUTPUT_FOLDER = "true";
	private static final String STR_TESTRESULTS_OVERWRITE = "false";
	private static final String STR_SNAPSHOT_ENABLE = "false";
	private static final String STR_VIDEO_RECORD_ENABLE = "true";
	private static final String STR_VIDEO_FORMAT = "mp4";
	private static final String STR_HIGHLIGHT_WEBELEMENT_ENABLE = "true";
	private static final String STR_SYSTEM_NOTIFICATION_ENABLE = "true";
	private static final String STR_DB_RECORDING_ENABLE = "false";
	private static final String STR_DB_DRIVERCLASS = "oracle.jdbc.driver.OracleDriver";
	private static final String STR_DB_JDBCURL = "jdbc:oracle:thin:@192.168.0.189:1521:R2019x";
	private static final String STR_DB_USERNAME = "SeleniumTest19x";
	private static final String STR_DB_PASSWORD = "SeleniumTest19x";
	private static final String STR_DB_ACQUIRE_INCREMENT = "10";
	private static final String STR_DB_INITIAL_POOLSIZE = "10";
	private static final String STR_DB_MAX_POOLSIZE = "200";
	private static final String STR_DB_MIN_POOLSIZE = "10";
	private static final String STR_maxstatements = "200";
	private static final String STR_DB_RECORD_SCRIPTS_ONLY = "false";
	private static final String STR_DB_RERUN_EXECUTION = "false";
	private static final String STR_DB_RERUN_EXECUTION_DATE_FORMAT = "DD-MM-YYYY";
	private static final String STR_DB_RERUN_EXECUTION_DATE = "11-03-2019";
	private static final String STR_MAIL_SENDREPORT_ENABLE = "false";
	private static final String STR_MAIL_FROM = "amitn@steepgraph.com";
	private static final String STR_MAIL_TO = "amitn@steepgraph.com";
	private static final String STR_MAIL_CC = "amitn@steepgraph.com";
	private static final String STR_MAIL_BCC = "amitn@steepgraph.com";
	private static final String STR_MAIL_SMTP_USERNAME = "amitn@steepgraph.com";
	private static final String STR_MAIL_SMTP_HOST = "smtp.gmail.com";
	private static final String STR_MAIL_SMTP_PORT = "587";
	private static final String STR_MAIL_SUBJECT = "";
	private static final String STR_MAIL_BODY = "";
	private static final String STR_DB_RERUN_EXECUTION_CRITERIA = "";
	private static final String STR_SKIPERROR_TAGLIST = "";
	private static final String STR_HIGHLIGHT_WEBELEMENT_STYLE = "";
	private static final String STR_WEBSERVICE_TIMEOUT = "";
	private static final String STR_INITIAL_URL = "";
	private static final String STR_FILTER_TESTCASES = "";
	private static final String STR_FILTER_SUITES = "";
	private static final String STR_MAIL_SMTP_PASSWORD = "";
	public static final String STR_DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String STR_DEFAULT_DOWNLOAD_FILE_PATH = "Downloads";

	protected static final HashMap<String, String> mpOfProperties = new HashMap<String, String>() {
		private static final long serialVersionUID = -6169314256432441464L;
		{
			// Browser Details for Automation Testing
			put(Constants.PROPERTY_KEY_BROWSER_NAME, STR_Browser);
			put(Constants.PROPERTY_KEY_BROWSER_WEBDRIVER_PATH, STR_WEBDRIVER_PATH);
			put(Constants.PROPERTY_KEY_BROWSER_TAG_OPENURL_URL, STR_OPENURL_URL);
			put(Constants.PROPERTY_KEY_WEBDRIVER_AUTODOWNLOAD, STR_WEBDRIVER_AUTODOWNLOAD);
			put(Constants.PROPERTY_KEY_BROWSER_PROXY, STR_BROWSER_PROXY);
			put(Constants.PROPERTY_KEY_BROWSER_HEADLESS, STR_BROWSER_HEADLESS);
			put(Constants.PROPERTY_KEY_BROWSER_FIREFOX_MARIONETTE, STR_FIREFOX_MARIONETTE);
			put(Constants.PROPERTY_KEY_BROWSER_IE_REQUIREFOCUS, STR_IE_REQUIREFOCUS);
			put(Constants.PROPERTY_KEY_BROWSER_NATIVE_EVENTS, STR_NATIVE_EVENTS);
			put(Constants.PROPERTY_KEY_BROWSER_PRIVATE_MODE, STR_PRIVATE_MODE);
			put(Constants.PROPERTY_KEY_ENABLE_OTP, STR_ENABLE_OTP);
			put(Constants.PROPERTY_KEY_FOR_DRAG_AND_DROP_GENERIC, STR_FOR_DRAG_AND_DROP_GENERIC);
			put(Constants.PROPERTY_KEY_BROWSER_DATE_FORMAT, STR_DEFAULT_DATE_FORMAT);
			// Below keys used for remove execution for Web Driver
			put(Constants.PROPERTY_KEY_BROWSER_REMOTE, STR_BROWSER_REMOTE);
			put(Constants.PROPERTY_KEY_BROWSER_REMOTE_PLATFORM, STR_REMOTE_PLATFORM);
			put(Constants.PROPERTY_KEY_BROWSER_REMOTE_NODEURL, STR_REMOTE_NODEURL);

			// Enovia server details
			put(Constants.PROPERTY_KEY_3DSPACE_RELEASE, STR_3DSPACE_RELEASE);
			put(Constants.PROPERTY_KEY_ARAS_RELEASE, STR_ARAS_RELEASE);
			put(Constants.PROPERTY_KEY_3DSPACE_URL, STR_3DSPACE_URL);
			put(Constants.PROPERTY_KEY_3DSPACE_URL_VERIFY, STR_3DSPACE_URL_VERIFY);
			put(Constants.PROPERTY_KEY_3DSPACE_URL_ISCAS, STR_3DSPACE_URL_ISCAS);
			put(Constants.PROPERTY_KEY_3DSPACE_HAS_TVC, STR_3DSPACE_HAS_TVC);
			put(Constants.PROPERTY_KEY_3DSPACE_HAS_SECURITY_CONTEXT_PAGE, STR_SECURITY_CONTEXT_PAGE);
			// This keys will be used to run mql
			// This is URL must be internal server URL
			put(Constants.PROPERTY_KEY_3DSPACE_VALIDATION_URL, STR_VALIDATION_URL);
			put(Constants.PROPERTY_KEY_3DSPACE_VALIDATION_USERNAME, STR_VALIDATION_USERNAME);
			put(Constants.PROPERTY_KEY_3DSPACE_VALIDATION_PASSWORD, STR_VALIDATION_PASSWORD);

			// 3DX_TAS_PROPERTIES
			put(Constants.PROPERTY_KEY_EXECUTION_TITILE, STR_RUN);
			put(Constants.PROPERTY_KEY_SUITE_FILE, STR_SUITE_FILE);
			put(Constants.PROPERTY_KEY_INITIAL_URL_VERIFY, STR_INITIAL_URL_VERIFY);
			put(Constants.PROPERTY_KEY_PARALLEL_EXECUTION_POOLSIZE, STR_EXECUTION_POOLSIZE);
			put(Constants.PROPERTY_KEY_EXECUTION_STEP_INTERVAL, STR_EXECUTION_STEP_INTERVAL);
			put(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT, STR_EXECUTION_STEP_TIMEOUT);
			put(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL,
					STR_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL);
			put(Constants.PROPERTY_KEY_EXECUTION_STEP_RETRY_COUNT, STR_STEP_RETRY_COUNT);
			put(Constants.PROPERTY_KEY_EXECUTION_PRIVATE_KEY, STR_PRIVATE_KEY);
			put(Constants.PROPERTY_KEY_TESTRESULTS_OUTPUT_FOLDER, STR_TESTRESULTS_OUTPUT_FOLDER);
			put(Constants.PROPERTY_KEY_TESTRESULTS_OVERWRITE, STR_TESTRESULTS_OVERWRITE);
			put(Constants.PROPERTY_KEY_SNAPSHOT_ENABLE, STR_SNAPSHOT_ENABLE);
			put(Constants.PROPERTY_KEY_VIDEO_RECORD_ENABLE, STR_VIDEO_RECORD_ENABLE);
			put(Constants.PROPERTY_KEY_VIDEO_FORMAT, STR_VIDEO_FORMAT);
			put(Constants.PROPERTY_KEY_HIGHLIGHT_WEBELEMENT_ENABLE, STR_HIGHLIGHT_WEBELEMENT_ENABLE);
			put(Constants.PROPERTY_KEY_SYSTEM_NOTIFICATION_ENABLE, STR_SYSTEM_NOTIFICATION_ENABLE);
			put(Constants.PROPERTY_KEY_DB_RECORDING_ENABLE, STR_DB_RECORDING_ENABLE);
			put(Constants.PROPERTY_KEY_DB_DRIVERCLASS, STR_DB_DRIVERCLASS);
			put(Constants.PROPERTY_KEY_DB_JDBCURL, STR_DB_JDBCURL);
			put(Constants.PROPERTY_KEY_DB_USERNAME, STR_DB_USERNAME);
			put(Constants.PROPERTY_KEY_DB_PASSWORD, STR_DB_PASSWORD);
			put(Constants.PROPERTY_KEY_DB_ACQUIRE_INCREMENT, STR_DB_ACQUIRE_INCREMENT);
			put(Constants.PROPERTY_KEY_DB_INITIAL_POOLSIZE, STR_DB_INITIAL_POOLSIZE);
			put(Constants.PROPERTY_KEY_DB_MAX_POOLSIZE, STR_DB_MAX_POOLSIZE);
			put(Constants.PROPERTY_KEY_DB_MIN_POOLSIZE, STR_DB_MIN_POOLSIZE);
			put(Constants.PROPERTY_KEY_DB_MAX_STATEMENTS, STR_maxstatements);
			put(Constants.PROPERTY_KEY_DB_RECORD_SCRIPTS_ONLY, STR_DB_RECORD_SCRIPTS_ONLY);
			put(Constants.PROPERTY_KEY_DB_RERUN_EXECUTION, STR_DB_RERUN_EXECUTION);
			put(Constants.PROPERTY_KEY_DB_RERUN_EXECUTION_DATE_FORMAT, STR_DB_RERUN_EXECUTION_DATE_FORMAT);
			put(Constants.PROPERTY_KEY_DB_RERUN_EXECUTION_DATE, STR_DB_RERUN_EXECUTION_DATE);
			put(Constants.PROPERTY_KEY_MAIL_SENDREPORT_ENABLE, STR_MAIL_SENDREPORT_ENABLE);
			put(Constants.PROPERTY_KEY_MAIL_FROM, STR_MAIL_FROM);
			put(Constants.PROPERTY_KEY_MAIL_TO, STR_MAIL_TO);
			put(Constants.PROPERTY_KEY_MAIL_CC, STR_MAIL_CC);
			put(Constants.PROPERTY_KEY_MAIL_BCC, STR_MAIL_BCC);
			put(Constants.PROPERTY_KEY_MAIL_SMTP_USERNAME, STR_MAIL_SMTP_USERNAME);
			put(Constants.PROPERTY_KEY_MAIL_SMTP_PASSWORD, STR_MAIL_SMTP_PASSWORD);
			put(Constants.PROPERTY_KEY_MAIL_SMTP_HOST, STR_MAIL_SMTP_HOST);
			put(Constants.PROPERTY_KEY_MAIL_SMTP_PORT, STR_MAIL_SMTP_PORT);
			put(Constants.PROPERTY_KEY_MAIL_SUBJECT, STR_MAIL_SUBJECT);
			put(Constants.PROPERTY_KEY_MAIL_BODY, STR_MAIL_BODY);
			put(Constants.PROPERTY_KEY_DB_RERUN_EXECUTION_CRITERIA, STR_DB_RERUN_EXECUTION_CRITERIA);
			put(Constants.PROPERTY_KEY_SKIPERROR_TAGLIST, STR_SKIPERROR_TAGLIST);
			put(Constants.PROPERTY_KEY_HIGHLIGHT_WEBELEMENT_STYLE, STR_HIGHLIGHT_WEBELEMENT_STYLE);
			put(Constants.PROPERTY_KEY_WEBSERVICE_TIMEOUT, STR_WEBSERVICE_TIMEOUT);
			put(Constants.PROPERTY_KEY_INITIAL_URL, STR_INITIAL_URL);
			put(Constants.PROPERTY_KEY_FILTER_TESTCASES, STR_FILTER_TESTCASES);
			put(Constants.PROPERTY_KEY_FILTER_SUITES, STR_FILTER_SUITES);
			put(Constants.PROPERTY_KEY_ALLOW_IN_SECURE_SITES, STR_ALLOW_IN_SECURE_SITES);
			put(Constants.PROPERTY_KEY_DEFAULT_DOWNLOAD_FILE_PATH, STR_DEFAULT_DOWNLOAD_FILE_PATH);

		}
	};

	private static final Exception e = null;

	protected PropertyUtil() throws IOException {
		this.prop = new Properties();
		File configFolder = new File(CONFIG);
		for (File file : configFolder.listFiles(PROPERTY_FILE_FILTER)) {
			InputStream input = new FileInputStream(file);
			prop.load(input);
			input.close();
		}
	}

	public static synchronized PropertyUtil newInstance() throws IOException {
		if (singleInstance == null) {
			singleInstance = new PropertyUtil();
		}
		return singleInstance;
		// return new PropertyUtil();
	}

	@Override
	public String getProperty(String key) {
		String returnValue = "";
		if (this.prop.get(key) != null) {
			returnValue = (String) this.prop.get(key);
		} else {
			if (mpOfProperties.containsKey(key)) {

				if (key.equalsIgnoreCase("3dx-tas.download.filepath")) {
					returnValue = System.getProperty("user.home") + File.separator + mpOfProperties.get(key);
				} else {
					returnValue = mpOfProperties.get(key);
				}
				LoggerUtil.debug("execution started with default TAS property as key is not defined : " + key + " = "
						+ returnValue);
				LoggerUtil.error("execution started with default TAS property as key is not defined : " + key + " = "
						+ returnValue, e);
			}
		}

		if (key.equalsIgnoreCase(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT)
				|| key.equalsIgnoreCase(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL)
				|| (key.equalsIgnoreCase(Constants.PROPERTY_KEY_EXECUTION_STEP_INTERVAL))) {
			returnValue = String.valueOf(Integer.parseInt(returnValue) * 1000);
		}

		return returnValue;
	}

	@Override
	public void setProperty(String key, String value) {
		this.prop.setProperty(key, value);
	}

	public boolean isTerminationInvoked() {

		boolean terminationInvoked = false;
		try {
			FileInputStream fis = new FileInputStream("terminationFlag.txt");
			Scanner sc = new Scanner(fis);
			String line = "false";
			if (sc.hasNextLine()) {
				line = sc.nextLine();
			}
			sc.close();
			if (line != null && line.startsWith("true")) {
				terminationInvoked = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return terminationInvoked;
	}

	public void setTerminationInvoked(boolean setTerminationValue) {
		try {
			FileWriter writeFile = new FileWriter("terminationFlag.txt");
			if (setTerminationValue) {
				writeFile.write("true");
			} else {
				writeFile.write("false");
			}
			writeFile.close();

		} catch (Exception e) {
			System.out.println("Failed to set the TerminationInvoked flag.");
			e.printStackTrace();
		}
	}

}