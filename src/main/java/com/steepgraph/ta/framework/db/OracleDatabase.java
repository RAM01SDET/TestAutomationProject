package com.steepgraph.ta.framework.db;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;

public class OracleDatabase implements IDatabase {

	/**
	 * This method will initialize database connection parameters
	 * 
	 * @author Steepgraph Systems
	 * @return
	 * @throws Exception
	 */
	@Override
	public Connection connect(String url, String username, String password, PropertyUtil propertyUtil)
			throws Exception {

		LoggerUtil.debug("Started connect");

		ComboPooledDataSource cpds = new ComboPooledDataSource();

		LoggerUtil.debug("url: " + url);
		cpds.setJdbcUrl(url);

		LoggerUtil.debug("username: " + username);
		cpds.setUser(username);

		cpds.setPassword(password);

		cpds.setInitialPoolSize(new Integer(propertyUtil.getProperty(Constants.PROPERTY_KEY_DB_INITIAL_POOLSIZE)));
		cpds.setAcquireIncrement(new Integer(propertyUtil.getProperty(Constants.PROPERTY_KEY_DB_ACQUIRE_INCREMENT)));
		cpds.setMaxPoolSize(new Integer(propertyUtil.getProperty(Constants.PROPERTY_KEY_DB_MAX_POOLSIZE)));
		cpds.setMinPoolSize(new Integer(propertyUtil.getProperty(Constants.PROPERTY_KEY_DB_MIN_POOLSIZE)));
		cpds.setMaxStatements(new Integer(propertyUtil.getProperty(Constants.PROPERTY_KEY_DB_MAX_STATEMENTS)));
		LoggerUtil.debug("End of connect");
		return cpds.getConnection();
	}

	/**
	 * This method will be read records from table.
	 * 
	 * @param connection
	 * @param tableName
	 * @param jsonParam
	 * @author SteepGraph Systems
	 * @throws Exception
	 */
	@Override
	public Long read(Connection connection, String jsonFilePath) throws Exception {

		LoggerUtil.debug("Started read");

		File jsonfile = new File(jsonFilePath);
		if (!jsonfile.exists())
			throw new Exception("Json file is not exist at: " + jsonfile.getAbsolutePath());

		String jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

		JsonObject jsonObj;
		try {
			jsonObj = (JsonObject) JsonParser.parseString(jsonString);
		} catch (Exception e) {
			throw new Exception("Exception in gson jar.");
		}
		StringBuilder sql = new StringBuilder();

		int currentIndex = 0;
		int criteriaLength = 0;
		long rowCount = (long) 0;
		ArrayList<String> criteriaList = new ArrayList<String>();

		for (Entry<String, JsonElement> keyValue : jsonObj.entrySet()) {

			String tablename = keyValue.getKey();
			LoggerUtil.debug("tablename : " + tablename);

			sql.setLength(0);

			sql.append("select count(*) from \"").append(tablename).append("\" ").append(" where ");
			JsonArray jsonCriteriaArray;
			try {
				jsonCriteriaArray = (JsonArray) keyValue.getValue();
				criteriaLength = jsonCriteriaArray.size();
			} catch (Exception e) {
				throw new Exception("Exception in gson jar.");
			}

			LoggerUtil.debug("json array size : " + criteriaLength);

			criteriaList.clear();
			for (int i = 0; i < criteriaLength; i++) {

				JsonObject jsonCriteriaObj;
				try {
					jsonCriteriaObj = (JsonObject) jsonCriteriaArray.get(i);
				} catch (Exception e) {
					throw new Exception("Exception in gson jar.");
				}
				String columnName = jsonCriteriaObj.get("name").getAsString();
				LoggerUtil.debug("columnName : " + columnName);
				String columnValue = jsonCriteriaObj.get("value").getAsString();
				LoggerUtil.debug("columnName : " + columnName);
				String operator = jsonCriteriaObj.get("operator").getAsString();
				LoggerUtil.debug("columnName : " + columnName);

				String condition = "";
				JsonElement conditionElement;
				try {
					conditionElement = jsonCriteriaObj.get("condition");
				} catch (Exception e) {
					throw new Exception("Exception in gson jar.");
				}
				if (conditionElement != null)
					condition = conditionElement.getAsString();

				if (condition.isEmpty())
					condition = "and";

				LoggerUtil.debug("condition : " + condition);

				if (i == 0) {
					sql.append(columnName).append(operator).append(" ? ");
				} else {
					sql.append(condition).append(" ").append(columnName).append(operator).append(" ? ");
				}

				criteriaList.add(columnValue);
			}

			String strSQL = sql.toString();
			LoggerUtil.debug("sql : " + strSQL);

			PreparedStatement preparedStmt = connection.prepareStatement(strSQL);

			currentIndex = 0;
			for (String columnValue : criteriaList) {
				currentIndex++;
				preparedStmt.setString(currentIndex, columnValue);
			}

			ResultSet rs = preparedStmt.executeQuery();
			while (rs.next()) {
				rowCount += rs.getLong(1);
				break;
			}

			LoggerUtil.debug("rowCount: " + rowCount);

			rs.close();
			preparedStmt.close();

		}
		LoggerUtil.debug("End of read. rowCount: " + rowCount);
		return rowCount;
	}

}
