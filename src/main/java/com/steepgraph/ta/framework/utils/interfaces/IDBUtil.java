package com.steepgraph.ta.framework.utils.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public interface IDBUtil {

	void close() throws Exception;

	boolean insertIntoTestCase(Map<String, String> rowData) throws Exception;

	boolean insertIntoMasterSuite(Map<String, String> rowData) throws Exception;

	Connection getConnection() throws SQLException, Exception;

	boolean configure() throws Exception;

	boolean insertIntoDataRegistration(Map<String, String> rowData) throws Exception;

	String selectDataRegistration(String suiteId, String strKey) throws Exception;

	boolean updateMasterSuite(Map<String, String> data, Map<String, String> criteria) throws Exception;

	boolean processUpdate(Map<String, String> data, Map<String, String> criteria, String tableName) throws Exception;

	boolean updateTestCaseStatus(Map<String, String> data, Map<String, String> criteria) throws Exception;

	String getMasteSuiteExecutionId(String serverName, String executionDate, String executionDateFormat)
			throws Exception;

	String getTestCaseExecutionStatus(String suiteId, String suiteName, String testCaseName, String additionCriteria)
			throws Exception;

}
