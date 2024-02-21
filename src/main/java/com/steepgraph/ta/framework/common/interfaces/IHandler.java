package com.steepgraph.ta.framework.common.interfaces;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.utils.interfaces.ICSVUtil;
import com.steepgraph.ta.framework.utils.pages.CommonUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;
import com.steepgraph.ta.framework.utils.pages.TestCase;

/**
 * Interface for Handler class
 * 
 * @author Steepgraph Systems
 *
 */
public interface IHandler {

	public void processElement(String element, TestCase testCase, ICSVUtil csvUtilObj,
			LinkedList<Map<String, Boolean>> lnkLstOfIfStack, LinkedList<List<Boolean>> lstOfPopValue) throws Exception;

	public void doLogin(Map<String, String> attributeMap) throws Exception;

	public void logOut(Map<String, String> attributeMap) throws Exception;

	public void close() throws Exception;

	String getTestCaseName();

	void setTestCaseName(String testCaseName);

	String getSuiteName();

	void setSuiteName(String suiteName);

	Driver getDriver();

	void initializeDriver(Map<String, String> parameterMap) throws Exception;

	ILibrary getLibrary();

	void prepareTagAttributes(ICSVUtil csvUtil, Map<String, String> attributeMap) throws Exception;

	void decryptPassword(Map<String, String> attributeMap) throws Exception;

	void setStatus(String status, String errorMessage, String infoMessage);

	void clearStatus();

	String getStatus();

	String getErrorMessage();

	String getInfoMessage();

	void setInfoMessage(String infoMessage);

	public ILibrary initializeLibray() throws Exception;

	public RegisterObjectUtil getRegisterUtil();

	public void setRegisterUtil(RegisterObjectUtil registerUtil);

	public PropertyUtil getPropertyUtil();

	public void setPropertyUtil(PropertyUtil propertyUtil);

	public CommonUtil getCommonUtil();

	public void setCommonUtil(CommonUtil commonUtil);

	public String parseAttribute(String attributeValue, ICSVUtil csvUtil, RegisterObjectUtil registerUtil)
			throws Exception;

	public void emptyLnkLstOfIfStack(LinkedList<Map<String, Boolean>> lnkLstOfIfStack);

	public String parseREGAttribute(String attributeValue, RegisterObjectUtil registerUtil) throws Exception;
}