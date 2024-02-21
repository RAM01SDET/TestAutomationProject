package com.steepgraph.ta.framework.common;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.common.pages.Handler;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;

public class SuiteListener implements ISuiteListener {

	private PropertyUtil propertyUtil;

	private Deque<Handler> availableHandlers = new LinkedList<>();

	private Map<String, Handler> allocatedHandlers = new HashMap<>();

	public SuiteListener() throws Exception {
		this.propertyUtil = PropertyUtil.newInstance();
		String parallelExecutionPoolsize = propertyUtil.getProperty(Constants.PROPERTY_KEY_PARALLEL_EXECUTION_POOLSIZE);
		int poolSize = 1;
		if (NumberUtils.isParsable(parallelExecutionPoolsize) && Integer.parseInt(parallelExecutionPoolsize) > 1) {
			poolSize = Integer.parseInt(parallelExecutionPoolsize);
		}
		// for (int i = 0; i < poolSize; i++) {
		// availableHandlers.push(new Handler());
		// }
		if (poolSize == 1) {
			availableHandlers.push(new Handler());
		}
	}

	@Override
	public void onStart(ISuite suite) {
		LoggerUtil.debug("Suite " + suite.getName() + " execution started");
		Handler handler = (Handler) suite.getAttribute("handler");
		if (handler == null) {
			handler = allocatedHandlers.get(Thread.currentThread().getName());
			if (handler == null && !availableHandlers.isEmpty()) {
				handler = availableHandlers.pop();
				allocatedHandlers.put(Thread.currentThread().getName(), handler);
				suite.setAttribute("handler", handler);
				LoggerUtil.debug("Handler allocated");
			}
			// else {
			// //throw new IllegalStateException("No Available Handlers Available for suite
			// execution: " + suite.getName());
			// }
		}
	}

	@Override
	public void onFinish(ISuite suite) {
		LoggerUtil.debug("Suite " + suite.getName() + " execution finished");
		Handler handler = (Handler) suite.getAttribute("handler");
		if (handler != null) {
			suite.removeAttribute("handler");
			handler = allocatedHandlers.get(Thread.currentThread().getName());
			if (handler != null) {
				allocatedHandlers.remove(Thread.currentThread().getName());
				if (!availableHandlers.contains(handler)) {
					availableHandlers.push(handler);
				}
				LoggerUtil.debug("Handler dellocated");
			}
		}
	}

	/**
	 * Method to release resources
	 * 
	 * @author Steepgraph Systems
	 * @return void
	 * @throws Exception
	 */
	public void releaseResources() throws Exception {
		LoggerUtil.debug("Start of releaseResources");
		for (Handler handler : availableHandlers) {
			handler.close();
			handler = null;
		}
		LoggerUtil.debug("End of releaseResources");
	}
}
