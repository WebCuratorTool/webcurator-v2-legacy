package org.webcurator.ui.target.command;

/**
 * Command class for retrieving live content files from the web.
 * @author oakleigh_ku
 *
 */
public class LiveContentRetrieverCommand {

	private String url;
	private String contentFileName;
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setContentFileName(String contentFileName) {
		this.contentFileName = contentFileName;
	}
	
	public String getContentFileName() {
		return contentFileName;
	}

}
