package org.webcurator.core.check;

import org.webcurator.core.harvester.agent.*;

public class HarvestAgentMemoryChecker extends MemoryChecker {

	/** this harvest agent. */
	private HarvestAgent harvestAgent;
	
	public HarvestAgentMemoryChecker() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.check.MemoryChecker#onSetWarning()
	 */
	protected void onSetWarning()
	{
		super.onSetWarning();
		harvestAgent.setMemoryWarning(true);
	}
	
	/* (non-Javadoc)
	 * @see org.webcurator.core.check.MemoryChecker#onSetError()
	 */
	protected void onSetError()
	{
		super.onSetError();
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.check.MemoryChecker#onRemoveWarning()
	 */
	protected void onRemoveWarning()
	{
		super.onRemoveWarning();
		harvestAgent.setMemoryWarning(false);
	}

	/* (non-Javadoc)
	 * @see org.webcurator.core.check.MemoryChecker#onRemoveError()
	 */
	protected void onRemoveError()
	{
		super.onRemoveError();
	}
	
	public HarvestAgent getHarvestAgent()
	{
		return harvestAgent;
	}
	
	public void setHarvestAgent(HarvestAgent harvestAgent)
	{
		this.harvestAgent = harvestAgent;
	}
}
