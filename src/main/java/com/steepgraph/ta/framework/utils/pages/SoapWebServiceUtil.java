package com.steepgraph.ta.framework.utils.pages;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.enums.HTTPMethods;
import com.steepgraph.ta.framework.utils.interfaces.IWebServiceUtil;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class SoapWebServiceUtil implements IWebServiceUtil {

	Response response;

	RestAssuredConfig config;

	public SoapWebServiceUtil(PropertyUtil propertyUtil) throws Exception {
		String sTimeout = propertyUtil.getProperty(Constants.PROPERTY_KEY_WEBSERVICE_TIMEOUT);

		Integer iTimeout = 120000;
		if (sTimeout != null && !"".equals(sTimeout))
			iTimeout = Integer.parseInt(sTimeout);

		LoggerUtil.debug(Constants.PROPERTY_KEY_WEBSERVICE_TIMEOUT + " : " + iTimeout);
		config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig().setParam("http.socket.timeout", iTimeout));
	}

	/*
	 * public static void main(String[] args) throws Exception {
	 * 
	 * SoapWebServiceUtil wbService = new SoapWebServiceUtil(); String[] arg = new
	 * String[] { "http://localhost:8070/SoapWebService/services/Calculator",
	 * "post",
	 * "D:\\Amit\\Projects\\Selenium\\Framework\\git\\P510_TestAutomationFrameWork_Selenium\\03_Source_Code\\Development\\TestAutomationProject\\src\\main\\resources\\SOAP Request.xml"
	 * , "" }; Object res = wbService.call(arg); String returnValue =
	 * wbService.readResponse(res, "addReturn"); System.out.println("returnValue : "
	 * + returnValue); }
	 */

	/**
	 * @param args[0]: Url
	 * @param args[1]: method
	 * @param args[2]: xmlFilePath
	 * @param args[3]: SOAPAction
	 */
	@Override
	public Object call(Map<String, String> attributeMap, String xmlFilePath) throws Exception {
		LoggerUtil.debug("Started call");

		String webServiceUrl = attributeMap.get("url");
		FileInputStream fileInputStream = new FileInputStream(new File(xmlFilePath));

		RequestSpecification request = RestAssured.given().config(config);

		String soapAction = attributeMap.get("soapaction");
		LoggerUtil.debug("soapAction " + soapAction);

		request.header("Content-Type", "text/xml").and().header("SOAPAction", soapAction).and()
				.body(IOUtils.toString(fileInputStream, "UTF-8"));

		LoggerUtil.debug("webServiceUrl: " + webServiceUrl);

		HTTPMethods reqMethod = HTTPMethods.valueOf(attributeMap.get("method"));

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
			response = request.get(webServiceUrl);
			break;
		default:
			break;
		}

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
		XmlPath jsXpath = new XmlPath(responseObj.asString());

		String returnValue = jsXpath.getString(key);
		LoggerUtil.debug("returnValue: " + returnValue);
		LoggerUtil.debug("End of readResponse");
		return returnValue;
	}
}
