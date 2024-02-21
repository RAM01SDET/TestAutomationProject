package com.steepgraph.ta.framework.utils.interfaces;

import java.util.Map;

public interface IWebServiceUtil {

	Object call(Map<String, String> attributeMap, String strInputText) throws Exception;

	int getStatusCode(Object response, String jsonParameters) throws Exception;

	String readResponse(Object response, String key) throws Exception;

}
