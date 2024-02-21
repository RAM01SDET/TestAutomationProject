package com.steepgraph.ta.framework.utils.pages;

import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.common.AssertionException;
import com.steepgraph.ta.framework.enums.HTTPMethods;
import com.steepgraph.ta.framework.utils.interfaces.IWebServiceUtil;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestWebServiceUtil implements IWebServiceUtil {

	Response response;

	RestAssuredConfig config;

	public RestWebServiceUtil(PropertyUtil propertyUtil) throws Exception {
		String sTimeout = propertyUtil.getProperty(Constants.PROPERTY_KEY_WEBSERVICE_TIMEOUT);

		Integer iTimeout = 120000;
		if (sTimeout != null && !"".equals(sTimeout))
			iTimeout = Integer.parseInt(sTimeout);

		LoggerUtil.debug(Constants.PROPERTY_KEY_WEBSERVICE_TIMEOUT + " : " + iTimeout);
		config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig().setParam("http.socket.timeout", iTimeout));
	}

	/**
	 * This method is used to call web service.
	 * 
	 * @param attributeMap : Has value of all attributes
	 * @param params       : Parameters to send it to WebService as body
	 */
	@Override
	public Object call(Map<String, String> attributeMap, String params) throws Exception {

		LoggerUtil.debug("Started call");

		String webServiceUrl = attributeMap.get("url");

		LoggerUtil.debug("url: " + webServiceUrl);
		RequestSpecification request = RestAssured.given().config(config);

		String username = attributeMap.get("username");

		if (username != null && !"".equals(username)) {
			request = request.auth().basic(username, attributeMap.get("password"));
		} else {
			String basicAuth = attributeMap.get("basicAuth");
			if (basicAuth != null && !"".equals(basicAuth)) {
				request.header("Authorization", attributeMap.get("basicAuth"));
			}
		}

		if (params != null && !"".equals(params)) {
			try {
				JsonObject requestParams = (JsonObject) JsonParser.parseString(params);
				request.body(requestParams.toString());
			} catch (Exception e) {
				throw new Exception("Exception in gson jar.");
			}
		}

		String strMethod = attributeMap.get("method");
		if (strMethod == null || "".equals(strMethod)) {
			strMethod = "get";
		}

		HTTPMethods reqMethod = HTTPMethods.valueOf(strMethod);

		switch (reqMethod) {
		case post:
			response = request.post(webServiceUrl);
			break;

		case put:
			response = request.put(webServiceUrl);
			break;

		case delete:
			response = request.delete(webServiceUrl);
			break;

		case get:
		default:
			response = request.get(webServiceUrl);
			break;
		}

		int statusCode = response.getStatusCode();
		if (statusCode != 200 && statusCode != 201)
			throw new AssertionException("Web Service Call failed. Returned status code =" + statusCode + " and body = "
					+ response.getBody().asString());

		LoggerUtil.debug("End of call");
		return response;
	}

	/**
	 * This method is used to get status code of response
	 * 
	 * @param url
	 * @param jsonParameters
	 */
	@Override
	public int getStatusCode(Object response, String jsonParameters) throws Exception {
		LoggerUtil.debug("Started getStatusCode");
		Response responseObj = (Response) response;
		int statusCode = responseObj.getStatusCode();
		LoggerUtil.debug("statusCode: " + statusCode);
		LoggerUtil.debug("End of getStatusCode");
		return statusCode;
	}

	/**
	 * This method is used to get value of given key from response
	 * 
	 * @param key
	 */
	@Override
	public String readResponse(Object response, String key) throws Exception {
		LoggerUtil.debug("Started readResponse");
		Response responseObj = (Response) response;
		JsonPath jsonPathEvaluator = responseObj.jsonPath();

		String returnValue = jsonPathEvaluator.get(key) != null ? jsonPathEvaluator.get(key).toString() : null;
		LoggerUtil.debug("returnValue: " + returnValue);
		LoggerUtil.debug("End of readResponse");
		return returnValue;
	}

}
