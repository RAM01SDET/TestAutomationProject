package com.steepgraph.ta.framework.utils.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.MasterApp;
import com.steepgraph.ta.framework.utils.interfaces.IDBUtil;

/**
 * 
 * @author Steepgraph Systems
 *
 */
public class RegisterObjectUtil {

	// private String testCaseName;

	private Properties prop;

	private String registrationType;

	private PropertyUtil propertyUtil;

	public void close() {
		prop = null;
		propertyUtil = null;
		registrationType = null;
	}

	/**
	 * This method will be used to configure registration
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: label
	 * @return void
	 * @throws Exception
	 */
	public RegisterObjectUtil(PropertyUtil propertyUtil) throws Exception {
		String parallelExecutionPoolsize = propertyUtil.getProperty(Constants.PROPERTY_KEY_PARALLEL_EXECUTION_POOLSIZE);
		this.propertyUtil = propertyUtil;
		this.registrationType = "propertyfile";

		if (!"database".equalsIgnoreCase(registrationType)) {
			prop = new Properties();

			File folder = new File(Constants.DATA_FOLDER_PATH);

			if (!folder.exists() && !folder.isDirectory()) {
				folder.mkdir();
				LoggerUtil.debug(Constants.DATA_FOLDER_PATH + " Folder is missing, new folder created");
			}

			if (NumberUtils.isParsable(parallelExecutionPoolsize) && Integer.parseInt(parallelExecutionPoolsize) > 1) {
				File file_parallelExecution = new File(
						"resources\\data\\objectregister " + Thread.currentThread().getName() + ".properties");
				file_parallelExecution.createNewFile();

				InputStream input_parallelExecution = new FileInputStream(
						"resources\\data\\objectregister " + Thread.currentThread().getName() + ".properties");
				prop.load(input_parallelExecution);
				input_parallelExecution.close();
			} else {
				File file = new File(Constants.REGISTER_FILE_PATH);
				if (!file.exists()) {
					file.createNewFile();
					LoggerUtil.debug(Constants.REGISTER_FILE_PATH + " File is missing, new file created");

				}
				InputStream input = new FileInputStream(file);
				prop.load(input);
				input.close();
			}

		}
	}

	private void setProperty(String key, String value) throws Exception {
		LoggerUtil.debug("Start of setProperty.");
		String parallelExecutionPoolsize = propertyUtil.getProperty(Constants.PROPERTY_KEY_PARALLEL_EXECUTION_POOLSIZE);
		if (value == null)
			throw new Exception("No value found against key : " + key + ", to set property");

		if (NumberUtils.isParsable(parallelExecutionPoolsize) && Integer.parseInt(parallelExecutionPoolsize) > 1) {
			FileOutputStream fos_parallelExecution = new FileOutputStream(
					"resources\\data\\objectregister " + Thread.currentThread().getName() + ".properties");
			prop.setProperty(key, value);
			prop.store(fos_parallelExecution, null);
			fos_parallelExecution.close();

		} else {
			FileOutputStream fos = new FileOutputStream(Constants.REGISTER_FILE_PATH);
			prop.setProperty(key, value);
			prop.store(fos, null);
			fos.close();
		}
		LoggerUtil.debug("End of setProperty.");
	}

	private String getProperty(String key) throws Exception {
		return (String) prop.get(key);
	}

	/**
	 * This method will be register newly created data into property file or
	 * database
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: label
	 * @return void
	 * @throws Exception
	 */
	public void registerObject(String key, String value) throws Exception {
		LoggerUtil.debug("Start of registerObject.");
		LoggerUtil.debug("strRegistrationType: " + registrationType);
		if ("database".equalsIgnoreCase(registrationType)) {
			registerObjectInDB(key, value);
		} else {
			registerObjectInPropertyFile(key, value);
		}
		LoggerUtil.debug("End of registerObject.");
	}

	private void registerObjectInPropertyFile(String key, String value) throws Exception {
		LoggerUtil.debug("Start of registerObjectInPropertyFile.");
		// Register name of object in property file
		setProperty(key, value);
		LoggerUtil.debug("End of registerObjectInPropertyFile.");
	}

	private void registerObjectInDB(String key, String value) throws Exception {
		LoggerUtil.debug("Start of registerObjectInDB.");
		Map<String, String> rowData = new HashMap<>();
		rowData.put("suiteid", MasterApp.newInstance().getSuiteId());
		rowData.put("key", key);
		rowData.put("value", value);

		IDBUtil dbUtil = new DBUtil(this.propertyUtil);
		dbUtil.insertIntoDataRegistration(rowData);
		// Register name of object in property file

		LoggerUtil.debug("End of registerObjectInDB.");
	}

	/**
	 * This method will be register newly created data into property file or
	 * database
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: label
	 * @return void
	 * @throws Exception
	 */
	public String getRegisteredData(String suiteId, String strKey) throws Exception {
		LoggerUtil.debug("Start of registerObject.");
		LoggerUtil.debug("strRegistrationType: " + registrationType);
		String strRegisteredValue = "";

		if ("database".equalsIgnoreCase(registrationType)) {
			IDBUtil dbUtil = new DBUtil(this.propertyUtil);
			strRegisteredValue = dbUtil.selectDataRegistration(suiteId, strKey);
		} else if ("propertyfile".equalsIgnoreCase(registrationType)) {
			strRegisteredValue = getProperty(strKey);
		}

		LoggerUtil.debug("strRegisteredValue : " + strRegisteredValue);
		LoggerUtil.debug("End of registerObject.");
		return strRegisteredValue;
	}

}
