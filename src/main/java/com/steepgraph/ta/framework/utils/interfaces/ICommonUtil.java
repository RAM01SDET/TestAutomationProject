package com.steepgraph.ta.framework.utils.interfaces;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;

/**
 * Interface for CommonUtil class
 * 
 * @author Steepgraph Systems
 *
 */
public interface ICommonUtil {

	void showNotification(String message) throws Exception;

	String getJVMBitVersion() throws Exception;

	boolean isLicenseValid() throws Exception;

	void startVideoRecord(String filePath, String testcaseName) throws Exception;

	void stopVideoRecord() throws Exception;

	String takeSnapShot(String filePath, String testCaseName, int rowNo, Driver driver) throws Exception;

	String getExecutionServerName() throws Exception;

	boolean allowTestCaseExecution(String suiteId, String testCaseId) throws Exception;

	String[] getTestCaseIdandName(String testCaseName) throws Exception;

	void validateEnoviaVersion() throws Exception;

	void setExecutionResultFileHeader() throws Exception;

	void setPerformanceResultFileHeader() throws Exception;

	boolean equals(List<String> listOne, List<String> listTwo) throws Exception;

	List<String> getSkipErrorTagList() throws Exception;

	MediaPlayerFactory getMediaPlayerFactory() throws Exception;

	/**
	 * This function will return date difference between two dates in sec
	 */
	public static long getDateDiffInSec(String startDate, String endDate) throws Exception {
		Date dStartDate = new SimpleDateFormat(Constants.DB_DATE_FORMAT).parse(startDate);
		Date dEndDate = new SimpleDateFormat(Constants.DB_DATE_FORMAT).parse(endDate);

		return ((dEndDate.getTime() - dStartDate.getTime()) / 1000);
	}

	public static File getLatestDownloadedFile(String filePath, String ext) {

		LoggerUtil.debug("Start of getLatestDownloadedFile");
		if ("".equalsIgnoreCase(filePath) || filePath == null)
			filePath = System.getProperty("user.home") + "/Downloads/";

		if ("".equalsIgnoreCase(ext) || ext == null)
			ext = "csv";

		File theNewestFile = null;
		File dir = new File(filePath);
		FileFilter fileFilter = new WildcardFileFilter("*." + ext);
		File[] files = dir.listFiles(fileFilter);

		if (files.length > 0) {
			/** The newest file comes first **/
			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			theNewestFile = files[0];
		}

		LoggerUtil.debug("theNewestFile : " + theNewestFile.getAbsolutePath());
		LoggerUtil.debug("End of getLatestDownloadedFile");

		return theNewestFile;
	}

	/**
	 * This Method will return current date and time of the system
	 * 
	 * @author Steepgraph Systems
	 * @return String
	 * @throws Exception
	 */
	public static String getCurrentDateTime() throws Exception {
		LoggerUtil.debug("Start of getCurrentDateTime");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.DB_DATE_FORMAT);
		LocalDateTime now = LocalDateTime.now();
		String strDateTime = dateFormatter.format(now);

		LoggerUtil.debug("strDateTime : " + strDateTime);
		LoggerUtil.debug("End of getCurrentDateTime");

		return strDateTime;
	}

	/**
	 * This Method will return current date and time of the system
	 * 
	 * @author Steepgraph Systems
	 * @return String
	 * @throws Exception
	 */
	public static String formatDate(Date date) throws Exception {
		LoggerUtil.debug("Start of formatDate");
		String strFormattedDate = new SimpleDateFormat(Constants.DB_DATE_FORMAT).format(date);

		LoggerUtil.debug("strFormattedDate : " + strFormattedDate);
		LoggerUtil.debug("End of formatDate");

		return strFormattedDate;
	}

	public static void createDirectory(String path) throws Exception {
		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

}