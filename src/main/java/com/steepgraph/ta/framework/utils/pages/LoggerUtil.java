package com.steepgraph.ta.framework.utils.pages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.steepgraph.ta.framework.Constants;

/**
 * Class for Logger configuration
 * 
 * @author Steepgraph Systems
 */
public class LoggerUtil implements Constants {

	static Logger debugLogger;

	static Logger errorLogger;

	static Logger testCaseResultLogger;

	static Map<String, Logger> debugLoggerMap;

	static Map<String, Logger> errorLoggerMap;

	static Logger performanceLogger;

	static Logger performanceResultLogger;

	/**
	 * Method to configure Logger
	 * 
	 * @author Steepgraph Systems
	 * @return void
	 * @throws IOException
	 * @throws Exception
	 */
	public static synchronized void Configure() throws IOException {
		if (debugLogger == null || errorLogger == null) {
			PropertyConfigurator.configure(LOG4J_PATH);
			debugLogger = Logger.getLogger("3DX-TAS");
			errorLogger = Logger.getLogger("3DX-TAS-ERROR");
			testCaseResultLogger = Logger.getLogger("3DX-TAS-RESULT");
			performanceLogger = Logger.getLogger("3DX-TAS-PERFORMANCE");
			performanceResultLogger = Logger.getLogger("3DX-TAS-PERFORMANCE-RESULT");
			debugLoggerMap = new HashMap<String, Logger>();
			errorLoggerMap = new HashMap<String, Logger>();
		}
	}

	/**
	 * Method to log error messages
	 * 
	 * @author Steepgraph Systems
	 * @param errorMessage
	 * @param e
	 * @return void
	 * @throws Exception
	 */
	public static synchronized void error(String errorMessage, Exception e) {
		if (Thread.currentThread().getName().equalsIgnoreCase("main")) {
			errorLogger.error(errorMessage, e);
		} else {
			Logger multiThreadErrorLogger = null;
			if (!errorLoggerMap.containsKey("ErrorThread" + Thread.currentThread().getName())) {
				multiThreadErrorLogger = Logger.getLogger("ErrorThread" + Thread.currentThread().getName());
				multiThreadErrorInit();
				errorLoggerMap.put("ErrorThread" + Thread.currentThread().getName(), multiThreadErrorLogger);
			} else {
				multiThreadErrorLogger = errorLoggerMap.get("ErrorThread" + Thread.currentThread().getName());
			}
			multiThreadErrorLogger.error(errorMessage, e);
		}
	}

	/**
	 * Method to log debug messages
	 * 
	 * @author Steepgraph Systems
	 * @param errorMessage
	 * @param e
	 * @return void
	 * @throws Exception
	 */
	public static synchronized void debug(String errorMessage, Exception e) {
		if (Thread.currentThread().getName().equalsIgnoreCase("main")) {
			debugLogger.debug(errorMessage, e);
		} else {
			Logger multiThreadDebugLogger = null;
			if (!debugLoggerMap.containsKey("Thread" + Thread.currentThread().getName())) {
				multiThreadDebugLogger = Logger.getLogger("Thread" + Thread.currentThread().getName());
				multiThreadDebugInit();
				debugLoggerMap.put("Thread" + Thread.currentThread().getName(), multiThreadDebugLogger);
			} else {
				multiThreadDebugLogger = debugLoggerMap.get("Thread" + Thread.currentThread().getName());
			}
			multiThreadDebugLogger.debug(errorMessage, e);
		}
	}

	/**
	 * Method to log debug messages
	 * 
	 * @author Steepgraph Systems
	 * @param message
	 * @return void
	 * @throws Exception
	 */
	public static synchronized void debug(String message) {
		if (Thread.currentThread().getName().equalsIgnoreCase("main")) {
			debugLogger.debug(message);
		} else {
			Logger multiThreadDebugLogger = null;
			if (!debugLoggerMap.containsKey("Thread" + Thread.currentThread().getName())) {
				multiThreadDebugLogger = Logger.getLogger("Thread" + Thread.currentThread().getName());
				multiThreadDebugInit();
				debugLoggerMap.put("Thread" + Thread.currentThread().getName(), multiThreadDebugLogger);
			} else {
				multiThreadDebugLogger = debugLoggerMap.get("Thread" + Thread.currentThread().getName());
			}
			multiThreadDebugLogger.debug(message);
		}
	}

	/**
	 * Method to record test case execution result in log file
	 * 
	 * @author Steepgraph Systems
	 * @param message
	 * @return void
	 * @throws Exception
	 */
	public static synchronized void recordTestResult(String message) {
		testCaseResultLogger.debug(message);
	}

	/**
	 * Method to log performance messages
	 * 
	 * @author Steepgraph Systems
	 * @param message
	 * @return void
	 * @throws Exception
	 */
	public static synchronized void performance(String message) {
		performanceLogger.debug(message);
	}

	/**
	 * Method to record performance result in log file
	 * 
	 * @author Steepgraph Systems
	 * @param message
	 * @return void
	 * @throws Exception
	 */
	public static synchronized void recordPerformanceResult(String message) {
		performanceResultLogger.debug(message);
	}

	public static synchronized void multiThreadDebugInit() {
		Properties props = new Properties();
		props.setProperty("log4j.appender.file", "org.apache.log4j.RollingFileAppender");
		props.setProperty("log4j.appender.file.maxFileSize", "50MB");
		props.setProperty("log4j.appender.file.maxBackupIndex", "1000");
		props.setProperty("log4j.appender.file.File",
				"logs\\TestAutomationDebug_" + Thread.currentThread().getName() + ".log");
		props.setProperty("log4j.appender.file.threshold", "debug");
		props.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
		props.setProperty("log4j.appender.file.layout.ConversionPattern", "%d [%c] %x - %m%n");
		props.setProperty("log4j.appender.file.Append", "false");
		props.setProperty("log4j.logger." + "Thread" + Thread.currentThread().getName(), "DEBUG, file");
		PropertyConfigurator.configure(props);
	}

	public static synchronized void multiThreadErrorInit() {
		Properties props = new Properties();
		props.setProperty("log4j.appender.file", "org.apache.log4j.RollingFileAppender");
		props.setProperty("log4j.appender.file.maxFileSize", "50MB");
		props.setProperty("log4j.appender.file.maxBackupIndex", "1000");
		props.setProperty("log4j.appender.file.File",
				"logs\\TestAutomationError_" + Thread.currentThread().getName() + ".log");
		props.setProperty("log4j.appender.file.threshold", "debug");
		props.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
		props.setProperty("log4j.appender.file.layout.ConversionPattern", "%d [%c] %x - %m%n");
		props.setProperty("log4j.appender.file.Append", "false");
		props.setProperty("log4j.logger." + "ErrorThread" + Thread.currentThread().getName(), "DEBUG, file");
		PropertyConfigurator.configure(props);
	}

}