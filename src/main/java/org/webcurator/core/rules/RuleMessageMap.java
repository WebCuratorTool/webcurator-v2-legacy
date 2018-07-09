package org.webcurator.core.rules;

import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Typed LinkedHashMap to hold the messages returned by the rules engine and retain the order of insertion
 * @author twoods
 *
 */
public class RuleMessageMap<K, V> extends LinkedHashMap {
	
	/**
	 * A logger to use within the Drools rules
	 */
	private static final Log LOG = LogFactory.getLog(RuleMessageMap.class);;
	
	public final void put(String ruleName, String message) {
		super.put(ruleName, message);
	}
	
	public final Log getLog() {
		return LOG;
	}
}
