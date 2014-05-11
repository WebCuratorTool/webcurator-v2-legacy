package org.webcurator.ui.tools.controller;

import java.util.regex.Pattern;

public class RegexReplacer implements StringReplacer {
	private Pattern search;
	private String replace;
	
	
	public String replace(String source) {
		return source == null ? "" : search.matcher(source).replaceAll(replace);
	}
	
	/**
	 * @param replace the replace to set
	 */
	public void setReplace(String replace) {
		this.replace = replace;
	}
	/**
	 * @param search the search to set
	 */
	public void setSearch(String search) {
		this.search = Pattern.compile(search);
	}
	
}
