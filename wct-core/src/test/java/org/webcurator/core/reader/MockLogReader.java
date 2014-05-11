package org.webcurator.core.reader;

public class MockLogReader extends LogReaderImpl {

	private String baseDir = "src/test/java/org/webcurator/core/reader/logs";
	
	public MockLogReader()
	{
		super.setLogProvider(new MockLogProvider(baseDir));
	}
	
	public void setLogProvider(LogProvider provider)
	{
	}
}
