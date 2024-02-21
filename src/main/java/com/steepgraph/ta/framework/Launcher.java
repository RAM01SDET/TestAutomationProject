package com.steepgraph.ta.framework;

import com.steepgraph.ta.framework.utils.pages.CommonUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;

public class Launcher implements Constants {

	public static void main(String[] args) throws Exception {
		try {
			String perfResultFileName = "test-output/TestPlan_" + CommonUtil.FOLDER_NAME_TIME_STAMP
					+ "_performance.csv";
			System.setProperty("perf_result.csv", perfResultFileName);
			LoggerUtil.Configure();
			MasterApp.newInstance().start();
			System.exit(0);
		} catch (Exception e) {
			LoggerUtil.debug(e.getMessage(), e);
			LoggerUtil.error(e.getMessage(), e);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
