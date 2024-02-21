package com.steepgraph.ta.framework.utils.pages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.utils.interfaces.IDBUtil;
import com.steepgraph.ta.framework.utils.interfaces.IPropertyUtil;

/**
 * This class will be used to insert test case execution details into database
 * 
 * @author Steepgraph Systems
 */
public class DBUtil implements IDBUtil {

	private static ComboPooledDataSource cpds;

	private static Connection conn;

	private static String recordExecution;

	private IPropertyUtil iPropertyUtil;

	public DBUtil(IPropertyUtil propertyUtil) throws Exception {
		this.iPropertyUtil = propertyUtil;
		if (recordExecution == null) {
			recordExecution = propertyUtil.getProperty(Constants.PROPERTY_KEY_DB_RECORDING_ENABLE);
			if (recordExecution == null || "".equals(recordExecution) || "true".equalsIgnoreCase(recordExecution)) {
				recordExecution = "true";
			} else {
				recordExecution = "false";
			}
		}

		LoggerUtil.debug("recordexecution : " + recordExecution);
	}

	/**
	 * This method will initialize database connection parameters
	 * 
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public boolean configure() throws Exception {

		if ("false".equalsIgnoreCase(recordExecution)) {
			return false;
		}

		if (cpds == null) {
			cpds = new ComboPooledDataSource();

			String strJDBCUrl = this.iPropertyUtil.getProperty(Constants.PROPERTY_KEY_DB_JDBCURL);
			LoggerUtil.debug("JDBC URL " + strJDBCUrl);
			cpds.setJdbcUrl(strJDBCUrl);

			String strUserName = this.iPropertyUtil.getProperty(Constants.PROPERTY_KEY_DB_USERNAME);
			LoggerUtil.debug("strUserName " + strUserName);
			cpds.setUser(strUserName);

			cpds.setPassword(DecryptionUtil.decrypt(this.iPropertyUtil.getProperty(Constants.PROPERTY_KEY_DB_PASSWORD)));

			cpds.setInitialPoolSize(
					new Integer((String) this.iPropertyUtil.getProperty(Constants.PROPERTY_KEY_DB_INITIAL_POOLSIZE)));
			cpds.setAcquireIncrement(
					new Integer((String) this.iPropertyUtil.getProperty(Constants.PROPERTY_KEY_DB_ACQUIRE_INCREMENT)));
			cpds.setMaxPoolSize(
					new Integer((String) this.iPropertyUtil.getProperty(Constants.PROPERTY_KEY_DB_MAX_POOLSIZE)));
			cpds.setMinPoolSize(
					new Integer((String) this.iPropertyUtil.getProperty(Constants.PROPERTY_KEY_DB_MIN_POOLSIZE)));
			cpds.setMaxStatements(
					new Integer((String) this.iPropertyUtil.getProperty(Constants.PROPERTY_KEY_DB_MAX_STATEMENTS)));
		}
		return true;
	}

	/**
	 * This method will return database connection
	 * 
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public synchronized Connection getConnection() throws Exception {
		if ("false".equalsIgnoreCase(recordExecution)) {
			return null;
		}

		if (cpds == null)
			configure();

		if (conn == null)
			return cpds.getConnection();
		else
			return conn;
	}

	/**
	 * This method will be used to insert record to Master Suite Database table.
	 * 
	 * @param rowData: Map contains database table column name and its value.
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public boolean insertIntoMasterSuite(Map<String, String> rowData) throws Exception {
		if ("false".equalsIgnoreCase(recordExecution)) {
			return false;
		}

		String sql = "insert into MASTERSUITE (suiteid, suitename,starttime,endtime,status) values (?,?,?,?,?)";
		Connection conn = getConnection();
		PreparedStatement preparedStmt = conn.prepareStatement(sql);

		preparedStmt.setObject(1, rowData.get("suiteid"));
		preparedStmt.setObject(2, rowData.get("suitename"));
		preparedStmt.setObject(3, rowData.get("starttime"));
		preparedStmt.setObject(4, rowData.get("endtime"));
		preparedStmt.setObject(5, rowData.get("status"));

		preparedStmt.executeUpdate();
		conn.commit();

		preparedStmt.close();
		return true;
	}

	/**
	 * This method will be used to insert record to Test Case Database table.
	 * 
	 * @param rowData: Map contains database table column name and its value.
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public boolean insertIntoTestCase(Map<String, String> rowData) throws Exception {

		if ("false".equalsIgnoreCase(recordExecution)) {
			return false;
		}

		String sql = "insert into TESTCASE (suiteid, suitename, testcasename,status,starttime,endtime,errormessage,xmlfilepath,csvfilepath,testcaseid,id) values (?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = getConnection();
		PreparedStatement preparedStmt = conn.prepareStatement(sql);

		preparedStmt.setObject(1, rowData.get("suiteid"));
		preparedStmt.setObject(2, rowData.get("suitename"));
		preparedStmt.setObject(3, rowData.get("testcasename"));
		preparedStmt.setObject(4, rowData.get("status"));
		preparedStmt.setObject(5, rowData.get("starttime"));
		preparedStmt.setObject(6, rowData.get("endtime"));
		preparedStmt.setObject(7, rowData.get("errormessage"));
		preparedStmt.setObject(8, rowData.get("xmlfilepath"));
		preparedStmt.setObject(9, rowData.get("csvfilepath"));
		preparedStmt.setObject(10, rowData.get("testcaseid"));
		preparedStmt.setObject(11, rowData.get("id"));

		preparedStmt.executeUpdate();
		conn.commit();

		preparedStmt.close();
		return true;
	}

	/**
	 * This method will be used to insert record to Data Registration Database
	 * table.
	 * 
	 * @param rowData: Map contains database table column name and its value.
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public boolean insertIntoDataRegistration(Map<String, String> rowData) throws Exception {

		String sql = "insert into DataRegistration (suiteid, regKey, regValue) values (?,?,?)";
		Connection conn = getConnection();
		PreparedStatement preparedStmt = conn.prepareStatement(sql);

		preparedStmt.setObject(1, rowData.get("suiteid"));
		preparedStmt.setObject(2, rowData.get("key"));
		preparedStmt.setObject(3, rowData.get("value"));

		preparedStmt.executeUpdate();
		conn.commit();

		preparedStmt.close();
		return true;
	}

	/**
	 * This method will be used to insert record to Data Registration Database
	 * table.
	 * 
	 * @param rowData: Map contains database table column name and its value.
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public String selectDataRegistration(String suiteId, String strKey) throws Exception {

		String sql = "select regValue from DataRegistration where suiteid = ? and regKey=?";
		Connection conn = getConnection();
		PreparedStatement preparedStmt = conn.prepareStatement(sql);

		preparedStmt.setObject(1, suiteId);
		preparedStmt.setObject(2, strKey);

		ResultSet rs = preparedStmt.executeQuery();

		String strRegisteredValue = "";
		while (rs.next()) {
			strRegisteredValue = rs.getString(1);
			break;
		}

		rs.close();
		preparedStmt.close();

		return strRegisteredValue;
	}

	/**
	 * This method will be used close existing database connection
	 * 
	 * @param rowData: Map contains database table column name and its value.
	 * @author Steepgraph Systems *
	 * @throws Exception
	 */
	@Override
	public void close() throws Exception {

		if (conn != null)
			conn.close();
	}

	/**
	 * This method will be used to update record of testcase table
	 * 
	 * @param data:     Map contains database table column name and its value.
	 * @param criteria: Map contains database table column name and its value will
	 *                  be used as where criteria.
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public boolean updateTestCaseStatus(Map<String, String> data, Map<String, String> criteria) throws Exception {
		return processUpdate(data, criteria, "TESTCASE");
	}

	/**
	 * This method will be used to update record of mastersuite table
	 * 
	 * @param data:     Map contains database table column name and its value.
	 * @param criteria: Map contains database table column name and its value will
	 *                  be used as where criteria.
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public boolean updateMasterSuite(Map<String, String> data, Map<String, String> criteria) throws Exception {
		return processUpdate(data, criteria, "MASTERSUITE");
	}

	/**
	 * This method will be used to insert record to Test Case Database table.
	 * 
	 * @param data:     Map contains database table column name and its value.
	 * @param criteria: Map contains database table column name and its value will
	 *                  be used as where criteria.
	 * @param tableName name of the table
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public boolean processUpdate(Map<String, String> data, Map<String, String> criteria, String tableName)
			throws Exception {

		if ("false".equalsIgnoreCase(recordExecution)) {
			return false;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(tableName).append(" set ");

		ArrayList<String> valueList = new ArrayList<String>();

		Set<String> dataKeySet = data.keySet();
		int dataLength = dataKeySet.size();
		int currentIndex = 0;
		for (Object name : dataKeySet) {
			currentIndex++;

			if (currentIndex == dataLength)
				sql.append(name).append("= ? ");
			else
				sql.append(name).append("= ? , ");

			valueList.add(data.get(name));
		}

		// Set<String> criteriaKeySet = criteria.keySet();
		int criteriaLength = criteria.size();
		currentIndex = 0;

		ArrayList<String> criteriaList = new ArrayList<String>();

		sql.append(" where ");
		for (Object name : criteria.keySet()) {
			currentIndex++;

			if (currentIndex == criteriaLength) {
				sql.append(name).append("= ? ");
			} else {
				sql.append(name).append("= ? and ");
			}
			criteriaList.add(criteria.get(name));

		}

		String finalSql = sql.toString();

		LoggerUtil.debug("finalSql " + finalSql);

		// status=?, where suiteid=? and suitename=? ";
		Connection conn = getConnection();
		PreparedStatement preparedStmt = conn.prepareStatement(finalSql);

		currentIndex = 0;
		for (int i = 0; i < dataLength; i++) {
			currentIndex++;
			preparedStmt.setObject(currentIndex, valueList.get(i));
		}

		for (int i = 0; i < criteriaLength; i++) {
			currentIndex++;
			preparedStmt.setObject(currentIndex, criteriaList.get(i));
		}

		preparedStmt.executeUpdate();
		conn.commit();

		preparedStmt.close();
		return true;

	}

	/**
	 * This method will be used to get test case execution id.
	 * 
	 * @param suiteId:          This is execution id
	 * @param testCaseName:     test case name
	 * @param additionCriteria: any additional where clause to TESTCASE table. This
	 *                          is optional
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public String getTestCaseExecutionStatus(String suiteId, String suiteName, String testCaseId,
			String additionCriteria) throws Exception {
		LoggerUtil.debug("Started getTestCaseExecutionId ");

		if ("false".equalsIgnoreCase(recordExecution)) {
			return null;
		}

		String sql = "select status from TESTCASE where suiteid=? and suitename=? and testcaseid=?";
		if (additionCriteria != null && !"".equals(additionCriteria))
			sql += " and " + additionCriteria;

		Connection conn = getConnection();
		PreparedStatement preparedStmt = conn.prepareStatement(sql);

		preparedStmt.setObject(1, suiteId);
		preparedStmt.setObject(2, suiteName);
		preparedStmt.setObject(3, testCaseId);
		ResultSet rs = preparedStmt.executeQuery();

		String status = null;
		while (rs.next()) {
			status = rs.getString(1);
			break;
		}

		LoggerUtil.debug("suiteId : " + suiteId);

		rs.close();
		preparedStmt.close();
		return status;
	}

	/**
	 * This method will be used to get master suite execution id.
	 * 
	 * @param serverName:    execution server name given at the time of execution
	 * @param executionDate: execution start date.
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	@Override
	public String getMasteSuiteExecutionId(String serverName, String executionDate, String executionDateFormat)
			throws Exception {

		LoggerUtil.debug("Started getTestCaseExecutionId ");

		if ("false".equalsIgnoreCase(recordExecution)) {
			return null;
		}

		String sql = "select suiteid from MASTERSUITE where suitename = ? and to_char(to_date(starttime,'DD-MM-YYYY HH:MI:SS AM'),'"
				+ executionDateFormat + "') = ?  order by to_date(starttime,'DD-MM-YYYY HH:MI:SS AM') DESC";

		Connection conn = getConnection();
		PreparedStatement preparedStmt = conn.prepareStatement(sql);

		preparedStmt.setObject(1, serverName);
		preparedStmt.setObject(2, executionDate);
		ResultSet rs = preparedStmt.executeQuery();

		LoggerUtil.debug("sql select query executed");

		String suiteId = "";
		while (rs.next()) {
			suiteId = rs.getString(1);
			break;
		}

		LoggerUtil.debug("suiteId : " + suiteId);

		rs.close();
		preparedStmt.close();

		return suiteId;
	}
}
