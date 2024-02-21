package com.steepgraph.ta.framework.utils.interfaces;

import com.steepgraph.ta.framework.common.interfaces.IHandler;

/**
 * Interface for CSVUtil class
 * 
 * @author Steepgraph Systems
 *
 */
public interface ICSVOutputUtil extends ICSVUtil {

	public boolean write(Exception e, IHandler handler);

	boolean write(IHandler handler);
}