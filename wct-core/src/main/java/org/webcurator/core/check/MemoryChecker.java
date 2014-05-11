/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.core.check;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An memory usage checker.
 * @author nwaight
 */
public class MemoryChecker extends AbstractChecker { 
	/** The warning used memory threshold. */
	private long warnThreshold;
	/** The error uused memory threshold. */
	private long errorThreshold;
	/** The used memory is above the warning threshold. */
	private boolean aboveWarnThreshold = false;
	/** The used memory is above the error threshold. */ 
	private boolean aboveErrorThreshold = false;
	
	/** the logger. */
	private static Log log = LogFactory.getLog(MemoryChecker.class);	
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.check.Checker#check()
	 */
	public void check() {		
		long memUsedKB = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
		if (log.isDebugEnabled()) {
			log.debug(memUsedKB + " KB of memory used.");
		}
				
		if (memUsedKB >= warnThreshold && !aboveWarnThreshold) {
			aboveWarnThreshold = true;
			if (log.isWarnEnabled()) {
				log.warn("The used memory is above the warning threshold " + warnThreshold + "KB and is " + memUsedKB + "KB");
			}
			onSetWarning();
			notify(LEVEL_WARNING, "The used memory is above the warning threshold " + warnThreshold + "KB and is " + memUsedKB + "KB");
		}
		else if (memUsedKB >= errorThreshold && !aboveErrorThreshold) {
			aboveErrorThreshold = true;
			if (log.isErrorEnabled()) {
				log.error("The used memory is above the error threshold " + errorThreshold + "KB and is " + memUsedKB + "KB");
			}
			onSetError();
			notify(LEVEL_ERROR, "The used memory is above the error threshold " + errorThreshold + "KB and is " + memUsedKB + "KB");
		}
		else if (memUsedKB < warnThreshold && aboveWarnThreshold) {
			aboveWarnThreshold = false;
			if (log.isInfoEnabled()) {
				log.info("The used memory has recovered below the warning threshold " + warnThreshold + "KB and is " + memUsedKB + "KB");
			}
			onRemoveWarning();
		}
		else if (memUsedKB < errorThreshold && aboveErrorThreshold) {
			aboveErrorThreshold = false;
			if (log.isInfoEnabled()) {
				log.info("The used memory has recovered below the error threshold " + errorThreshold + "KB and is " + memUsedKB + "KB");
			}
			onRemoveError();
		}		
	}

	/** Take some action when passing the warning threshold */
	protected void onSetWarning()
	{
		if(log.isWarnEnabled())
		{
			log.warn("Warning Threshold reached: Attempting Garbage Collection");
		}
		System.gc(); //have a go at garbage collection to attempt to reduce memory usage
	}
	
	/** Take some action when passing the error threshold */
	protected void onSetError()
	{
		if(log.isErrorEnabled())
		{
			log.error("Error Threshold reached: Attempting Garbage Collection");
		}
		System.gc(); //have a go at garbage collection to attempt to reduce memory usage
	}

	/** Take some action when leaving the warning threshold */
	protected void onRemoveWarning()
	{
	}

	/** Take some action when leaving the error threshold */
	protected void onRemoveError()
	{
	}
	
	/**
	 * @return the errorThreshold
	 */
	public long getErrorThreshold() {
		return errorThreshold;
	}

	/**
	 * @param errorThreshold the errorThreshold to set
	 */
	public void setErrorThreshold(long errorThreshold) {
		this.errorThreshold = errorThreshold;
	}

	/**
	 * @return the warnThreshold
	 */
	public long getWarnThreshold() {
		return warnThreshold;
	}

	/**
	 * @param warnThreshold the warnThreshold to set
	 */
	public void setWarnThreshold(long warnThreshold) {
		this.warnThreshold = warnThreshold;
	}
}
