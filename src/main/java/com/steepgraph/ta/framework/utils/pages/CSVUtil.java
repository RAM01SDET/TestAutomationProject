package com.steepgraph.ta.framework.utils.pages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.tika.detect.AutoDetectReader;
import org.testng.SkipException;

import com.opencsv.CSVReader;
import com.steepgraph.ta.framework.utils.interfaces.ICSVUtil;

/**
 * Class for CSV file reading, containing utility methods pertaining to it
 * 
 * @author Steepgraph Systems
 *
 */
public class CSVUtil implements ICSVUtil {

	private CSVReader csvReader;

	protected String[] currentRowData;

	private BufferedReader in;

	protected HashMap<String, Integer> headerMap;

	/**
	 * CSVUtil constructor
	 * 
	 * @author Steepgraph Systems
	 * @param filePath
	 * @throws Exception
	 */
	public CSVUtil(String filePath) throws Exception {
		// reader = new CSVReader(new FileReader(filePath));
		File fileDir = new File(filePath);
		try (AutoDetectReader autoDetectReader = new AutoDetectReader(new FileInputStream(fileDir))) {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), autoDetectReader.getCharset()));
		} catch (IOException e) {

		}
		csvReader = new CSVReader(in);
		getHeaderMap(csvReader);
	}

	public CSVUtil(String filePath, int skip) throws Exception {
		// reader = new CSVReader(new FileReader(filePath));
		File fileDir = new File(filePath);
		try (AutoDetectReader autoDetectReader = new AutoDetectReader(new FileInputStream(fileDir))) {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), autoDetectReader.getCharset()));
		} catch (IOException e) {

		}
		csvReader = new CSVReader(in);
		int i = 0;
		while (i < skip) {
			csvReader.readNext();
			i++;
		}
		getHeaderMap(csvReader);
	}

	/**
	 * Method to get map of headers from CSV file
	 * 
	 * @author Steepgraph Systems
	 * @param reader
	 * @return void
	 * @throws Exception
	 */
	@Override
	public HashMap<String, Integer> getHeaderMap(CSVReader reader) throws Exception {
		// LoggerUtil.debug("Start of getHeaderMap");
		headerMap = new HashMap<String, Integer>();
		String[] header = reader.readNext();
		if (header != null) {
			for (int i = 0; i < header.length; i++) {
				headerMap.put(header[i], i);
			}
		}

		LoggerUtil.debug("End of getHeaderMap headerMap" + headerMap);
		return headerMap;
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

		boolean hasNext = false;
		while ((currentRowData = csvReader.readNext()) != null) {
			if (currentRowData.length <= 1 && (currentRowData[0] == null || currentRowData[0].trim().isEmpty()))
				continue;

			hasNext = true;
			break;
		}
		return hasNext;
	}

	/**
	 * Method to get data from current row from CSV file
	 * 
	 * @author Steepgraph Systems
	 * @param columnName
	 * @return String
	 * @throws Exception
	 */
	@Override
	public String getCell(String columnName) throws Exception {
		// LoggerUtil.debug("Start of getCell");
		String strCelValue = "";

		if (headerMap.containsKey(columnName)) {
			int columnIndex = headerMap.get(columnName);
			strCelValue = currentRowData[columnIndex];
		}
		if ((null == strCelValue || strCelValue.isEmpty())) {
			LoggerUtil.debug("WARNING : Value in CSV is null or empty");
			throw new IllegalArgumentException("Column header is not defined for the attribute");
		}

		if (strCelValue != null && strCelValue.equalsIgnoreCase("${SKIPIT}")) {
			throw new SkipException(
					"Tag processing is skipped as csv cell value is matching with expression ${SKIPIT}.");
		}

		LoggerUtil.debug("columnName : " + columnName + " : strCelValue : " + strCelValue);

		// LoggerUtil.debug("End of getCell");
		return strCelValue;
	}

	@Override
	public HashMap<String, Integer> getHeaderMap() {
		return headerMap;
	}

}
