package com.steepgraph.ta.framework.utils.pages;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.steepgraph.licensing.LicensingManager;
import com.steepgraph.licensing.LicensingManager.LicenseInfo;
import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.MasterApp;
import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.interfaces.IDBUtil;
import com.steepgraph.ta.framework.utils.interfaces.IPropertyUtil;

import atu.testrecorder.ATUTestRecorder;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

/**
 * Class for common utility methods
 * 
 * @author Steepgraph Systems
 */
public class CommonUtil implements ICommonUtil, Constants {

	private SystemTray systemTray;

	private TrayIcon systemTrayIcon;

	private ATUTestRecorder videoRecorder;

	private MediaPlayerFactory mediaPlayerFactory;

	private MediaPlayer mediaPlayer;

	private String executionServerName;

	public static final String FOLDER_NAME_TIME_STAMP = getCurrentTimeStampFolderName();

	private IPropertyUtil iPropertyUtil;

	private static final String[] OPTIONS = { "--quiet", "--quiet-synchro", "--intf", "dummy" };

	private static final String MRL = "screen://";

	private static final String SOUT = ":sout=#transcode{vcodec=h264,vb=%d,scale=%f}:duplicate{dst=file{dst=%s}}";

	private static final String FPS = ":screen-fps=%d";

	private static final String CACHING = ":screen-caching=%d";

	private static final int fps = 20;

	private static final int caching = 500;

	private static final int bits = 1024;

	private static final float scale = 0.5f;

	public CommonUtil(PropertyUtil propertyUtil) {
		this.iPropertyUtil = propertyUtil;
	}

	/**
	 * This Method will display notification in system tray
	 * 
	 * @author Steepgraph Systems
	 * @return String
	 * @throws Exception
	 */
	@Override
	public void showNotification(String message) throws Exception {
		String strShowNotification = this.iPropertyUtil.getProperty(PROPERTY_KEY_SYSTEM_NOTIFICATION_ENABLE);
		if (strShowNotification == null || "".equals(strShowNotification))
			strShowNotification = "true";

		if ("true".equalsIgnoreCase(strShowNotification)) {
			// Obtain only one instance of the SystemTray object

			if (systemTray == null) {
				systemTray = SystemTray.getSystemTray();
				Image image = Toolkit.getDefaultToolkit()
						.createImage(this.getClass().getClassLoader().getResource("SteepgraphLogo.png"));

				// Alternative (if the icon is on the classpath):
				// Image image =
				// Toolkit.getToolkit().createImage(getClass().getResource("icon.png"));

				systemTrayIcon = new TrayIcon(image, "3DX-TAS");
				// Let the system resize the image if needed
				systemTrayIcon.setImageAutoSize(true);
				// Set tooltip text for the tray icon
				systemTrayIcon.setToolTip("3DX-TAS");
				systemTray.add(systemTrayIcon);
			}

			systemTrayIcon.displayMessage(message, "3DX-TAS", MessageType.NONE);
		}
	}

	/**
	 * This Method will be used get JVM Bit version.
	 * 
	 * @author SteepGraph Systems
	 * @throws Exception
	 */
	@Override
	public String getJVMBitVersion() throws Exception {
		return System.getProperty("sun.arch.data.model");
	}

	/**
	 * This Method will be used check license valid or not.
	 * 
	 * @author SteepGraph Systems
	 * @throws Exception
	 */
	@Override
	public boolean isLicenseValid() throws Exception {
		boolean valideLicense = false;
		LicensingManager licenseManager = new LicensingManager();
		LicenseInfo licenseInfo = licenseManager.validateLicense(LICENSE_FILE_PATH, PRODUCT_TYPE);
		if (licenseInfo == LicenseInfo.LICENSE_VALID) {
			valideLicense = true;
		}
		return valideLicense;
	}

	/**
	 * This Method will be used start video recording of test case execution
	 * 
	 * @author SteepGraph Systems
	 * @throws Exception
	 */
	@Override
	public void startVideoRecord(String filePath, String testcaseName) throws Exception {
		LoggerUtil.debug("Started  startVideoRecord");
		String strEnableVideoRecord = this.iPropertyUtil.getProperty(PROPERTY_KEY_VIDEO_RECORD_ENABLE);

		LoggerUtil.debug("strEnableVideoRecord " + strEnableVideoRecord);

		boolean enableVideoRecord = true;

		if (strEnableVideoRecord == null || "".equals(strEnableVideoRecord)
				|| "false".equalsIgnoreCase(strEnableVideoRecord))
			enableVideoRecord = false;

		if (enableVideoRecord) {
			String strVideoRecordPath = getFilenameWithoutExt(filePath);

			boolean testOutputDirectory = this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OUTPUT_FOLDER) == null
					|| "".equalsIgnoreCase(
							this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OUTPUT_FOLDER).trim()) ? true
									: Boolean.valueOf(
											this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OUTPUT_FOLDER));

			boolean resultToOverwrite = this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OVERWRITE) == null
					|| "".equalsIgnoreCase(this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OVERWRITE).trim())
							? true
							: Boolean.valueOf(this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OVERWRITE));
			strVideoRecordPath = resultToOverwrite ? Paths.get(filePath).getParent() + File.separator
					: Paths.get(filePath).getParent().toString() + File.separator + CommonUtil.FOLDER_NAME_TIME_STAMP
							+ File.separator;

			if (testOutputDirectory)
				strVideoRecordPath = resultToOverwrite
						? TEST_OUTPUT_DIR + TEST_RESULT + File.separator + testcaseName + File.separator
						: TEST_OUTPUT_DIR + TEST_RESULT + File.separator + testcaseName + File.separator
								+ FOLDER_NAME_TIME_STAMP + File.separator;

			LoggerUtil.debug("strVideoRecordPath " + strVideoRecordPath);
			ICommonUtil.createDirectory(strVideoRecordPath);
			LoggerUtil.debug("strVideoName " + testcaseName);

			String strVideoFormat = this.iPropertyUtil.getProperty(PROPERTY_KEY_VIDEO_FORMAT);

			if (strVideoFormat == null || "".equalsIgnoreCase(strVideoFormat))
				strVideoFormat = "mov";

			if (strVideoFormat.equalsIgnoreCase("mp4")) {
				try {
					mediaPlayerFactory = new MediaPlayerFactory(OPTIONS);
					mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();

					mediaPlayer.playMedia(MRL, getMediaOptions(strVideoRecordPath + testcaseName + ".mp4"));
				} catch (Exception ex) {
					throw new Exception(ex.getMessage());
				}
			} else {
				videoRecorder = new ATUTestRecorder(strVideoRecordPath, testcaseName, false);
				videoRecorder.start();
			}

			LoggerUtil.debug("End startVideoRecord");
		}
	}

	private String[] getMediaOptions(String destination) {
		return new String[] { String.format(SOUT, bits, scale, destination), String.format(FPS, fps),
				String.format(CACHING, caching) };
	}

	/**
	 * This Method will be used stop video recording of test case execution.
	 * 
	 * @author SteepGraph Systems
	 * @throws Exception
	 */
	@Override
	public void stopVideoRecord() throws Exception {
		LoggerUtil.debug("Started  stopVideoRecord");

		String strVideoFormat = this.iPropertyUtil.getProperty(PROPERTY_KEY_VIDEO_FORMAT);

		if (strVideoFormat == null || "".equalsIgnoreCase(strVideoFormat))
			strVideoFormat = "mov";

		if (strVideoFormat.equalsIgnoreCase("mp4")) {
			if (mediaPlayerFactory != null)
				mediaPlayer.stop();

			mediaPlayerFactory = null;
		} else {
			if (videoRecorder != null)
				videoRecorder.stop();

			videoRecorder = null;
		}

		LoggerUtil.debug("End  stopVideoRecord");
	}

	/**
	 * This function will take screenshot
	 * 
	 * @param webdriver
	 * @param testCaseName
	 * @throws Exception
	 */
	@Override
	public String takeSnapShot(String filePath, String testCaseName, int rowNo, Driver driver) throws Exception {
		boolean enableSnapShot = true;
		String strSnapShotEnable = this.iPropertyUtil.getProperty(PROPERTY_KEY_SNAPSHOT_ENABLE);

		if (strSnapShotEnable == null || "".equals(strSnapShotEnable) || "false".equalsIgnoreCase(strSnapShotEnable))
			enableSnapShot = false;

		String destFileName = "";
		String filePathWithoutExt = getFilenameWithoutExt(filePath);
		if (enableSnapShot) {
			boolean testOutputDirectory = this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OUTPUT_FOLDER) == null
					|| "".equalsIgnoreCase(
							this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OUTPUT_FOLDER).trim()) ? true
									: Boolean.valueOf(
											this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OUTPUT_FOLDER));

			boolean resultToOverwrite = this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OVERWRITE) == null
					|| "".equalsIgnoreCase(this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OVERWRITE).trim())
							? true
							: Boolean.valueOf(this.iPropertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OVERWRITE));
			filePathWithoutExt = resultToOverwrite ? Paths.get(filePath).getParent() + File.separator
					: Paths.get(filePath).getParent().toString() + File.separator + CommonUtil.FOLDER_NAME_TIME_STAMP
							+ File.separator;

			if (testOutputDirectory)
				filePathWithoutExt = resultToOverwrite
						? TEST_OUTPUT_DIR + TEST_RESULT + File.separator + testCaseName + File.separator
						: TEST_OUTPUT_DIR + TEST_RESULT + File.separator + testCaseName + File.separator
								+ FOLDER_NAME_TIME_STAMP + File.separator;

			destFileName = filePathWithoutExt + testCaseName + "_" + rowNo + ".png";
			// Move image file to new destination
			File destFile = new File(destFileName);
			if (destFile.exists()) {
				destFile.delete();
			}

			// Convert web driver object to TakeScreenshot
			TakesScreenshot scrShot = ((TakesScreenshot) driver.getWebDriver());

			// Call getScreenshotAs method to create image file
			File srcFile = scrShot.getScreenshotAs(OutputType.FILE);

			try {
				FileUtils.moveFile(srcFile, destFile);
			} catch (IOException e) {
				LoggerUtil.debug("TakeSnapshot Failed during file move operations.");
				LoggerUtil.error("TakeSnapshot Failed during file move operations.", e);
			}
		}
		return destFileName;
	}

	private String getFilenameWithoutExt(String filePath) {
		String returnString = filePath;
		if (returnString.contains(".")) {
			returnString = returnString.substring(0, returnString.lastIndexOf("."));
		}
		return returnString;
	}

	/**
	 * This function will return execution server name defined in configuration
	 * file.
	 */
	@Override
	public String getExecutionServerName() throws Exception {
		if (executionServerName == null || "".equals(executionServerName)) {
			executionServerName = this.iPropertyUtil.getProperty(PROPERTY_KEY_EXECUTION_TITILE);
			if (executionServerName == null || "".equals(executionServerName))
				executionServerName = "DefaultTitle";
		}

		return executionServerName;
	}

	/**
	 * This function will check whether current test is allowed for execution or not
	 */
	@Override
	public boolean allowTestCaseExecution(String suiteName, String testCaseId) throws Exception {
		LoggerUtil.debug("Started allowTestCaseExecution ");

		String strRerunExecution = this.iPropertyUtil.getProperty(PROPERTY_KEY_DB_RERUN_EXECUTION);
		if (strRerunExecution != null && "true".equalsIgnoreCase(strRerunExecution)) {

			String strExecutionCriteria = this.iPropertyUtil.getProperty(PROPERTY_KEY_DB_RERUN_EXECUTION_CRITERIA);

			if (strExecutionCriteria == null || "".equals(strExecutionCriteria))
				strExecutionCriteria = "status not in (1)";

			IDBUtil dbUtil = new DBUtil(this.iPropertyUtil);
			String status = dbUtil.getTestCaseExecutionStatus(MasterApp.newInstance().getSuiteId(), suiteName,
					testCaseId, strExecutionCriteria);

			if (status == null || "".equals(status))
				return false;
		} else {
			String suiteNames = this.iPropertyUtil.getProperty(PROPERTY_KEY_FILTER_SUITES);
			if (suiteNames == null || "".equals(suiteNames))
				return true;

			List<String> suiteNameList = Arrays.asList(suiteNames.split(","));

			if (suiteNameList.contains(suiteName)) {
				String testcases = this.iPropertyUtil.getProperty(PROPERTY_KEY_FILTER_TESTCASES);

				if (testcases == null || "".equals(testcases))
					return true;

				List<String> testcasesList = Arrays.asList(testcases.split(","));

				if (!testcasesList.contains(testCaseId))
					return false;
			}
		}

		return true;

	}

	/**
	 * Return TestCaseId and name from test case id and name combination string
	 */
	@Override
	public String[] getTestCaseIdandName(String testCaseName) throws Exception {
		String testCaseId = testCaseName;
		// commented for SELENIUM-754
		// if (testCaseName != null && testCaseName.contains("_")) {
		// testCaseId = testCaseName.substring(0, testCaseName.indexOf("_"));
		// testCaseName = testCaseName.substring(testCaseName.indexOf("_") + 1,
		// testCaseName.length());
		// }

		return new String[] { testCaseId, testCaseName };
	}

	/**
	 * This Method will be used validate enovia version.
	 * 
	 * @author SteepGraph Systems
	 * @throws Exception
	 */
	@Override
	public void validateEnoviaVersion() throws Exception {

		LoggerUtil.debug("Start of  isValidEnoviaVersion");
		String enoviaRelease = this.iPropertyUtil.getProperty(PROPERTY_KEY_3DSPACE_RELEASE);
		if (enoviaRelease == null || "".equals(enoviaRelease))
			throw new Exception(PROPERTY_KEY_3DSPACE_RELEASE + " key is missing in configuration property file");

		LoggerUtil.debug("enoviaRelease: " + enoviaRelease);

		EnoviaUtil enoviaUtil = EnoviaUtil.newInstance(this.iPropertyUtil);
		String enoviaVersionCode = enoviaUtil.getVersionCode();

		LoggerUtil.debug("enoviaVersionCode: " + enoviaVersionCode);

		enoviaVersionCode = enoviaVersionCode.toLowerCase();
		enoviaRelease = enoviaRelease.toLowerCase();

		enoviaRelease = enoviaRelease.replace("v6", "");

		if (enoviaVersionCode == null || "".equals(enoviaVersionCode) || !enoviaVersionCode.contains(enoviaRelease))
			throw new Exception(PROPERTY_KEY_3DSPACE_RELEASE + " is not matching with 3DSPACE Version.");

	}

	/**
	 * This Method will be used set execution result file header
	 * 
	 * @author SteepGraph Systems
	 * @throws Exception
	 */
	@Override
	public void setExecutionResultFileHeader() throws Exception {
		// record test case execution in file
		StringBuilder executionResultBuilder = new StringBuilder();
		executionResultBuilder.append("\"").append("Suite Name").append("\",");
		executionResultBuilder.append("\"").append("Test Case Name").append("\",");
		executionResultBuilder.append("\"").append("Test Case Id").append("\",");
		executionResultBuilder.append("\"").append("Execution Status").append("\",");
		executionResultBuilder.append("\"").append("Execution Time(second)").append("\",");
		executionResultBuilder.append("\"").append("Execution Start Time").append("\",");
		executionResultBuilder.append("\"").append("Message").append("\",");
		executionResultBuilder.append("\"").append("Snap Shot Name").append("\",");
		LoggerUtil.recordTestResult(executionResultBuilder.toString());
	}

	/**
	 * This Method will be used set performance result file header
	 * 
	 * @author SteepGraph Systems
	 * @throws Exception
	 */
	@Override
	public void setPerformanceResultFileHeader() throws Exception {
		// record test case performance execution in file
		StringBuilder executionResultBuilder = new StringBuilder();
		executionResultBuilder.append("\"").append("TimeStamp").append("\",");
		executionResultBuilder.append("\"").append("TestID Name").append("\",");
		executionResultBuilder.append("\"").append("Performance").append("\",");
		executionResultBuilder.append("\"").append("Cookies").append("\",");
		executionResultBuilder.append("\"").append("ErrorMessage(If failed)").append("\"");
		LoggerUtil.recordPerformanceResult(executionResultBuilder.toString());
	}

	/**
	 * This Method will be used compare two list
	 * 
	 * @author SteepGraph Systems
	 * @throws Exception
	 */
	@Override
	public boolean equals(List<String> listOne, List<String> listTwo) throws Exception {

		List<String> sourceList = new ArrayList<String>(listOne);
		List<String> destinationList = new ArrayList<String>(listTwo);

		sourceList.removeAll(listTwo);
		destinationList.removeAll(listOne);

		System.out.println("after remove sourceList: " + sourceList);
		System.out.println("after remove destinationList: " + sourceList);

		if (sourceList.size() <= 0 && destinationList.size() <= 0)
			return true;

		return false;
	}

	public static String getCurrentTimeStampFolderName() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate.replace(" ", "_").replace(":", "").replace(".", "").replace("-", "_");
	}

	@Override
	public List<String> getSkipErrorTagList() throws Exception {
		String tags = this.iPropertyUtil.getProperty(PROPERTY_KEY_SKIPERROR_TAGLIST);
		List<String> tagList = new ArrayList<>();
		if (!StringUtils.isBlank(tags)) {
			String[] tagArr = tags.split(",");
			for (String tag : tagArr) {
				tagList.add(tag.trim().toLowerCase());
			}
		}
		return tagList;
	}

	public MediaPlayerFactory getMediaPlayerFactory() {
		return this.mediaPlayerFactory;
	}
}