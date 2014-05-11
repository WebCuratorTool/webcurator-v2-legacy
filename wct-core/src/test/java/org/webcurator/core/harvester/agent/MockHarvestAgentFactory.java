package org.webcurator.core.harvester.agent;

import org.webcurator.core.reader.*;

public class MockHarvestAgentFactory implements HarvestAgentFactory {

	static MockHarvestAgent agent = null;
	
	public HarvestAgent getHarvestAgent(String host, int port) {
		
		return getMockHarvestAgent();
	}

	public MockHarvestAgent getMockHarvestAgent() {
		
		if(agent == null)
		{
			agent = new MockHarvestAgent(); 
		}
		return agent;
	}

	public LogReader getLogReader(String host, int port) {
		// TODO Auto-generated method stub
		return new MockLogReader();
	}

}
