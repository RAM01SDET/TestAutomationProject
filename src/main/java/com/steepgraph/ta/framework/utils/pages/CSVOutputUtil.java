package com.steepgraph.ta.framework.utils.pages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import com.opencsv.CSVWriter;
import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.utils.interfaces.ICSVOutputUtil;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;

/**
 * Class for CSV file reading, containing utility methods pertaining to it
 * 
 * @author Steepgraph Systems
 *
 */
public class CSVOutputUtil extends CSVUtil implements ICSVOutputUtil, Constants {

	CSVWriter writer;

	String[] currentOutputRowData;

	int headerLength = 0;

	/**
	 * CSVUtil constructor
	 * 
	 * @author Steepgraph Systems
	 * @param filePath
	 * @throws Exception
	 */
	public CSVOutputUtil(String filePath, String testCaseName, PropertyUtil propertyUtil) throws Exception {
		super(filePath);
		String filePathWithoutExt = getFilenameWithoutExt(filePath);

		boolean testOutputDirectory = propertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OUTPUT_FOLDER) == null
				|| "".equalsIgnoreCase(propertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OUTPUT_FOLDER).trim())
						? Boolean.TRUE
						: Boolean.valueOf(propertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OUTPUT_FOLDER));

		boolean resultToOverwrite = propertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OVERWRITE) == null
				|| "".equalsIgnoreCase(propertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OVERWRITE).trim())
						? Boolean.TRUE
						: Boolean.valueOf(propertyUtil.getProperty(PROPERTY_KEY_TESTRESULTS_OVERWRITE));

		filePathWithoutExt = resultToOverwrite ? Paths.get(filePath).getParent() + File.separator
				: Paths.get(filePath).getParent().toString() + File.separator + CommonUtil.FOLDER_NAME_TIME_STAMP
						+ File.separator;

		if (testOutputDirectory)
			filePathWithoutExt = resultToOverwrite
					? TEST_OUTPUT_DIR + TEST_RESULT + File.separator + testCaseName + File.separator
					: TEST_OUTPUT_DIR + TEST_RESULT + File.separator + testCaseName + File.separator
							+ CommonUtil.FOLDER_NAME_TIME_STAMP + File.separator;
		ICommonUtil.createDirectory(filePathWithoutExt);
		writer = new CSVWriter(new FileWriter(filePathWithoutExt + testCaseName + "_output.csv"));
		copyHeaderMap();
	}

	private String getFilenameWithoutExt(String filePath) {
		String returnString = filePath;
		if (returnString.contains(".")) {
			returnString = returnString.substring(0, returnString.lastIndexOf("."));
		}
		return returnString;
	}

	private void copyHeaderMap() throws Exception {
		if (headerMap != null) {
			headerLength = headerMap.size() + 3;
			currentOutputRowData = new String[headerLength];
			for (String key : headerMap.keySet()) {
				currentOutputRowData[headerMap.get(key)] = key;
			}
			currentOutputRowData[headerLength - 3] = "Execution Status";
			currentOutputRowData[headerLength - 2] = "Execution Failure Reason(if any)";
			currentOutputRowData[headerLength - 1] = "Notes(if any)";
			if (currentOutputRowData != null && currentOutputRowData.length > 0) {
				writer.writeNext(currentOutputRowData, true);
			}
		}
	}

	/**
	 * Method to read next row from CSV file
	 * 
	 * @author Steepgraph Systems
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean next() throws IOException {
		boolean returnBoolean = false;
		returnBoolean = super.next();
		if (returnBoolean)
			copyRowDataToOutputData();
		return returnBoolean;
	}

	private void copyRowDataToOutputData() {
		currentOutputRowData = new String[currentRowData.length + 3];
		for (int i = 0; i < currentRowData.length; i++) {
			currentOutputRowData[i] = currentRowData[i];
		}
		for (int i = currentRowData.length; i < headerLength; i++) {
			currentOutputRowData[i] = "";
		}
	}

	/**
	 * Method to write the current row to the CSV file alongwith the Exception
	 * Message
	 * 
	 * @author Steepgraph Systems
	 * @return boolean
	 */
	@Override
	public boolean write(Exception e, IHandler handler) {
		boolean returnBoolean = false;
		currentOutputRowData[headerLength - 3] = (e == null) ? TESTCASE_RESULT_PASS : TESTCASE_RESULT_FAIL;
		if (e == null) {
			currentOutputRowData[headerLength - 2] = "";
		} else {
			// Error Message to be truncated for the output File, if more than 2 lines.
			String message = e.getMessage();
			if (message != null) {
				if (message.contains("\n") && (message.indexOf("\n") != message.lastIndexOf("\n"))) {
					message = message.substring(0, message.indexOf("\n")) + ". Refer to Logs for full details.";
				}
				currentOutputRowData[headerLength - 2] = message;
			} else {
				currentOutputRowData[headerLength - 2] = e.toString();
			}
		}
		if (handler != null) {
			currentOutputRowData[headerLength - 1] = handler.getInfoMessage();
		}
		if (currentOutputRowData.length > 0) {
			writer.writeNext(currentOutputRowData, true);
			writer.flushQuietly();
		}
		return returnBoolean;
	}

	/**
	 * Method to write the current row to the CSV file alongwith the Exception
	 * Message
	 * 
	 * @author Steepgraph Systems
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean write(IHandler handler) {
		boolean returnBoolean = false;
		currentOutputRowData[headerLength - 3] = handler.getStatus();
		currentOutputRowData[headerLength - 2] = handler.getErrorMessage();
		currentOutputRowData[headerLength - 1] = handler.getInfoMessage();
		if (currentOutputRowData.length > 0) {
			writer.writeNext(currentOutputRowData, true);
			writer.flushQuietly();
		}
		return returnBoolean;
	}
}
