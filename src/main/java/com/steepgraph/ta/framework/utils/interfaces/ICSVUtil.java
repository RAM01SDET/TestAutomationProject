package com.steepgraph.ta.framework.utils.interfaces;

import java.io.IOException;
import java.util.HashMap;

import com.opencsv.CSVReader;

/**
 * Interface for CSVUtil class
 * 
 * @author Steepgraph Systems
 *
 */
public interface ICSVUtil {

	String getCell(String columnName) throws Exception;

	public boolean next() throws IOException;

	public HashMap<String, Integer> getHeaderMap(CSVReader reader) throws Exception;

	public HashMap<String, Integer> getHeaderMap() throws Exception;
}