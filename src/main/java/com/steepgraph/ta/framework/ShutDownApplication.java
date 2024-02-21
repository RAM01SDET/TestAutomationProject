package com.steepgraph.ta.framework;

import java.io.IOException;

import com.steepgraph.ta.framework.utils.pages.PropertyUtil;

public class ShutDownApplication {

	public static void main(String[] args) {
		PropertyUtil propertyUtil = null;
		try {
			propertyUtil = PropertyUtil.newInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("terminationInvoked before = " + propertyUtil.isTerminationInvoked());
		if (propertyUtil.isTerminationInvoked()) {
			System.out.println("Stopping the application...");

		} else {
			propertyUtil.setTerminationInvoked(true);
			System.out.println("Continuing with the application...");

		}
		System.out.println("terminationInvoked after = " + propertyUtil.isTerminationInvoked());
	}

}