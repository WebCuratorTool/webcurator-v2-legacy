package org.webcurator.ui.tools.controller;

public class SeedMapElement {
	
	private String seed = "";
	private String browseUrl = "";
	private String accessUrl = "";
	private boolean primary = false;
	
	public SeedMapElement(String seed)
	{
		this.seed = seed;
	}
	
	public void setSeed(String seed) {
		this.seed = seed;
	}
	
	public String getSeed() {
		return seed;
	}
	
	public void setBrowseUrl(String browseUrl) {
		this.browseUrl = browseUrl;
	}
	
	public String getBrowseUrl() {
		return browseUrl;
	}
	
	public void setAccessUrl(String accessUrl) {
		this.accessUrl = accessUrl;
	}
	
	public String getAccessUrl() {
		return accessUrl;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	
	public boolean getPrimary() {
		return primary;
	}
}
