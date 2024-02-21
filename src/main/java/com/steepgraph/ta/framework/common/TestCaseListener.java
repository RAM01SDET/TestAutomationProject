package com.steepgraph.ta.framework.common;

import java.util.HashMap;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.MasterApp;
import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.interfaces.IDBUtil;
import com.steepgraph.ta.framework.utils.pages.DBUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;

public class TestCaseListener implements ITestListener {

	private String masterSuiteId;

	private PropertyUtil propertyUtil;

	@Override
	public void onTestStart(ITestResult result) {
		LoggerUtil.debug("Started onTestStart");
		try {
			masterSuiteId = MasterApp.newInstance().getSuiteId();
			IHandler handler = (IHandler) result.getTestContext().getAttribute("handler");
			this.propertyUtil = handler.getPropertyUtil();
			// this.propertyUtil = PropertyUtil.newInstance();

			String strEnableVideoRecord = this.propertyUtil.getProperty(Constants.PROPERTY_KEY_VIDEO_RECORD_ENABLE);
			String strVideoFormat = this.propertyUtil.getProperty(Constants.PROPERTY_KEY_VIDEO_FORMAT);
			ICommonUtil commonUtil = handler.getCommonUtil();

			if ("true".equalsIgnoreCase(strEnableVideoRecord) && "mp4".equalsIgnoreCase(strVideoFormat)
					&& null == commonUtil.getMediaPlayerFactory()) {
				throw new Exception(
						"Dependencies is missing for VLCJ and its related files like vlcj, gson, jna, jnaplatform, log4j, sfl4j-api , sfl4j-nop, libvlc.dll, libvlccore.dll \n or contact to administrator.");
			}
			System.out.println(result.getParameters());
		} catch (Exception e) {
			LoggerUtil.error("Test case start failed", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		// TODO Auto-generated method stub

		try {
			LoggerUtil.debug("Started onTestSuccess");
			String strStartDate = ICommonUtil.formatDate(result.getTestContext().getStartDate());
			String strEndDateTime = ICommonUtil.getCurrentDateTime();

			String suiteName = result.getTestContext().getSuite().getName();
			LoggerUtil.debug("suiteName =" + suiteName);

			String testCaseName = result.getTestContext().getCurrentXmlTest().getName();
			LoggerUtil.debug("testCaseName =" + testCaseName);

			recordTestCaseExecution(masterSuiteId, suiteName, testCaseName, strStartDate, strEndDateTime, "1", "", "");
			LoggerUtil.debug("End of onTestSuccess");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onTestFailure(ITestResult result) {
		try {
			LoggerUtil.debug("Started onTestFailure");
			String strStartDate = ICommonUtil.formatDate(result.getTestContext().getStartDate());
			String strEndDateTime = ICommonUtil.getCurrentDateTime();

			String errorMessage = result.getThrowable().getMessage();

			String suiteName = result.getTestContext().getSuite().getName();
			LoggerUtil.debug("suiteName =" + suiteName);

			String testCaseName = result.getTestContext().getCurrentXmlTest().getName();
			LoggerUtil.debug("testCaseName =" + testCaseName);

			// String filename = commonUtilObj.takeSnapShot(suiteName, testCaseName);
			String filename = "";
			recordTestCaseExecution(masterSuiteId, suiteName, testCaseName, strStartDate, strEndDateTime, "0",
					errorMessage, filename);

			LoggerUtil.debug("End of onTestFailure");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		try {
			LoggerUtil.debug("Started onTestSkipped");
			String strStartDate = ICommonUtil.formatDate(result.getTestContext().getStartDate());
			String strEndDateTime = ICommonUtil.getCurrentDateTime();

			String errorMessage = result.getThrowable().getMessage();

			String suiteName = result.getTestContext().getSuite().getName();
			LoggerUtil.debug("suiteName =" + suiteName);

			String testCaseName = result.getTestContext().getCurrentXmlTest().getName();
			LoggerUtil.debug("testCaseName =" + testCaseName);

			// String filename = commonUtilObj.takeSnapShot(suiteName, testCaseName);
			String filename = "";
			recordTestCaseExecution(masterSuiteId, suiteName, testCaseName, strStartDate, strEndDateTime, "2",
					errorMessage, filename);

			LoggerUtil.debug("End of onTestSkipped");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart(ITestContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinish(ITestContext context) {
		// TODO Auto-generated method stub

	}

	/**
	 * This Method will be used to record test case execution details in database.
	 * 
	 * @author SteepGraph Systems
	 * @param suiteId
	 * @param suiteName
	 * @param testcasename
	 * @param starttime
	 * @param endtime
	 * @param status
	 * @return errorMessage
	 * @throws Exception
	 */
	public void recordTestCaseExecution(String suiteId, String suiteName, String testcasename, String starttime,
			String endtime, String status, String errorMessage, String filename) throws Exception {

		LoggerUtil.debug("Start of recordTestCaseExecution");

		long executionTimeInSec = ICommonUtil.getDateDiffInSec(starttime, endtime);

		String testCaseId = testcasename;
		if (testcasename != null && testcasename.contains("_")) {
			testCaseId = testcasename.substring(0, testcasename.indexOf("_"));
			testcasename = testcasename.substring(testcasename.indexOf("_") + 1, testcasename.length());
		}

		// record test case execution in file
		StringBuilder executionResultBuilder = new StringBuilder();
		executionResultBuilder.append("\"").append(suiteName).append("\",");
		executionResultBuilder.append("\"").append(testcasename).append("\",");
		executionResultBuilder.append("\"").append(testCaseId).append("\",");
		if ("2".equals(status))
			executionResultBuilder.append("\"").append("Aborted").append("\",");
		else if ("1".equals(status))
			executionResultBuilder.append("\"").append("Pass").append("\",");
		else
			executionResultBuilder.append("\"").append("Fail").append("\",");
		executionResultBuilder.append("\"").append(executionTimeInSec).append("\",");
		executionResultBuilder.append("\"").append(starttime).append("\",");
		executionResultBuilder.append("\"").append(errorMessage).append("\",");
		executionResultBuilder.append("\"").append(filename).append("\",");
		LoggerUtil.recordTestResult(executionResultBuilder.toString());

		// record test case execution in database
		Map<String, String> rowData = new HashMap<String, String>();
		rowData.put("suiteid", suiteId);
		rowData.put("suitename", suiteName);
		rowData.put("testcasename", testcasename);
		rowData.put("starttime", starttime);
		rowData.put("endtime", endtime);
		rowData.put("status", status);
		rowData.put("errormessage", errorMessage);
		rowData.put("execution_time", "" + executionTimeInSec);

		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("suiteid", suiteId);
		criteria.put("suitename", suiteName);
		criteria.put("testcaseid", testCaseId);
		criteria.put("testcasename", testcasename);

		IDBUtil datasource = new DBUtil(this.propertyUtil);
		datasource.updateTestCaseStatus(rowData, criteria);

		LoggerUtil.debug("Test case execution recorded in database");

		LoggerUtil.debug("End of recordTestCaseExecution");
	}

}
