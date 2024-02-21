package com.steepgraph.ta.framework.utils.pages;

import java.io.IOException;
import java.util.Iterator;

import com.matrixone.apps.domain.util.MqlUtil;
import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.utils.interfaces.IPropertyUtil;

import matrix.db.Context;
import matrix.db.ServerVersion;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class EnoviaUtil {

	private Context context;

	private String strVersion = "";

	private IPropertyUtil iPropertyUtil;

	private static EnoviaUtil singleInstance;

	private EnoviaUtil() {
	}

	public static synchronized EnoviaUtil newInstance(IPropertyUtil propertyUtil) throws IOException {
		if (singleInstance == null) {
			singleInstance = new EnoviaUtil();
			singleInstance.iPropertyUtil = propertyUtil;
		}
		return singleInstance;
	}

	/**
	 * Method will be used to connect to Enovia
	 * 
	 * @author SteepGraph Systems
	 * @return boolean
	 * @throws Exception
	 */
	public boolean connect() throws Exception {
		if (context == null || !context.isConnected()) {
			String enoviaURL = iPropertyUtil.getProperty(Constants.PROPERTY_KEY_3DSPACE_VALIDATION_URL);
			enoviaURL = (enoviaURL == null) ? "" : enoviaURL;
			LoggerUtil.debug("enoviaURL " + enoviaURL);

			String username = iPropertyUtil.getProperty(Constants.PROPERTY_KEY_3DSPACE_VALIDATION_USERNAME);
			if (username == null || "".equals(username)) {
				LoggerUtil.debug(Constants.PROPERTY_KEY_3DSPACE_VALIDATION_USERNAME
						+ " key is missing in TestAutomationFramework.properties. MQL validation will be ignored");
				return false;
			}

			LoggerUtil.debug("username: " + username);

			String password = iPropertyUtil.getProperty(Constants.PROPERTY_KEY_3DSPACE_VALIDATION_PASSWORD);

			LoggerUtil.debug("password: " + password);

			context = new Context(enoviaURL);
			context.setUser(username);
			context.setPassword(password);
			String role = iPropertyUtil.getProperty(Constants.PROPERTY_KEY_3DSPACE_VALIDATION_ROLE);
			if (role != null && !"".equals(role)) {
				LoggerUtil.debug(Constants.PROPERTY_KEY_3DSPACE_VALIDATION_ROLE
						+ " key is missing in configuration property file.");
				context.setRole(role);
			}

			context.connect();
		}

		return true;
	}

	/**
	 * Method will be used to close exiting Enovia connection
	 * 
	 * @author SteepGraph Systems
	 * @return boolean
	 * @throws Exception
	 */
	public void disconnect() {
		if (context != null && context != Context.NULL_CONTEXT && context.isConnected()) {
			try {
				context.shutdown();
			} catch (MatrixException e) {
				LoggerUtil.debug("Error while disconnecting 3DEXPERIENCE", e);
			}
		}
		singleInstance = null;
	}

	/**
	 * Method will be used run MQL on Enovia Server
	 * 
	 * @author SteepGraph Systems
	 * @return boolean
	 * @throws Exception
	 */
	public String executeMQL(String strMqlCommand) throws Exception {
		LoggerUtil.debug("executeMQL => strMqlCommand : " + strMqlCommand);
		connect();
		String strResult = MqlUtil.mqlCommand(context, strMqlCommand, false, true);
		LoggerUtil.debug("executeMQL => result : " + strResult);
		return strResult;

	}

	/**
	 * Method will create mql query from string having text in pattern of
	 * type|name|revision
	 * 
	 * @author SteepGraph Systems
	 * @return boolean
	 * @throws Exception
	 */
	public String getMQLfromTNR(String tnr) throws Exception {
		LoggerUtil.debug("getMQLfromTNR => tnr : " + tnr);
		String mql = null;
		if (tnr == null || "".equals(tnr))
			return mql;

		String[] tnrArray = tnr.split("\\|");
		
		String revision = "";
		if (tnrArray.length > 2) 
			revision = tnrArray[2];						
		mql = "print bus '" + tnrArray[0] + "' '" + tnrArray[1] + "' '" + revision + "' ";
		LoggerUtil.debug("getMQLfromTNR => mql : " + mql);
		return mql;

	}

	/**
	 * Method will create mql query from string having text contains object id
	 * 
	 * @author SteepGraph Systems
	 * @return boolean
	 * @throws Exception
	 */
	public String getMQLfromId(String objectId) throws Exception {
		LoggerUtil.debug("getMQLfromId => tnr : " + objectId);
		String mql = null;
		if (objectId == null || "".equals(objectId))
			return mql;

		mql = "print bus '" + objectId + "' ";
		LoggerUtil.debug("getMQLfromId => mql : " + mql);
		return mql;

	}

	/**
	 * Method will append selectables to mql query
	 * 
	 * @author SteepGraph Systems
	 * @return boolean
	 * @throws Exception
	 */
	public String appendSelectablesToMql(String mql, StringList selectables) throws Exception {
		LoggerUtil.debug("appendSelectablesToMql => mql : " + mql);

		if (mql == null || "".equals(mql))
			return mql;

		if (selectables == null || selectables.size() < 1)
			return mql;

		mql += " select ";
		for (Iterator<String> iterator = selectables.iterator(); iterator.hasNext();) {
			String selectExpression = (String) iterator.next();

			mql += " '" + selectExpression + "' ";

		}

		mql += " dump |";

		LoggerUtil.debug("appendSelectablesToMql => mql : " + mql);
		return mql;

	}

	/**
	 * Method will be used to get enovia version
	 * 
	 * @author SteepGraph Systems
	 * @return boolean
	 * @throws Exception
	 */
	public String getVersionCode() throws Exception {

		LoggerUtil.debug("Start of getVersionCode : ");

		if (strVersion != null && !"".equals(strVersion)) {
			return strVersion;
		}
		connect();
		ServerVersion version = new ServerVersion();
		version.open(context);
		strVersion = version.getVersion(context);
		version.close(context);

		LoggerUtil.debug("End of getVersionCode : " + strVersion);
		return strVersion;
	}
}
