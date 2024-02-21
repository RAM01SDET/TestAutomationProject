package com.steepgraph.ta.framework.utils.interfaces;

/**
 * Interface for Library class
 * 
 * @author Steepgraph Systems
 *
 */
public interface IPropertyUtil {

	String getProperty(String strKey);

	void setProperty(String strKey, String strValue);

	boolean isTerminationInvoked();

	void setTerminationInvoked(boolean setTerminationValue);

}