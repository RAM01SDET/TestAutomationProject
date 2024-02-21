package com.steepgraph.ta.framework.db;

import java.sql.Connection;

import com.steepgraph.ta.framework.utils.pages.PropertyUtil;

public interface IDatabase {

	Connection connect(String url, String username, String password, PropertyUtil propertyUtil) throws Exception;

	Long read(Connection connection, String jsonFile) throws Exception;

}
