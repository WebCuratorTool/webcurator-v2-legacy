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
package org.webcurator.core.harvester.coordinator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.domain.model.core.TargetInstance;

/**
 * The Bandwidth Calculator is a utility class that 
 * provides a convienient method for calculating
 * how much bandwidth should be allocated to a list of
 * target instances.
 * @author nwaight
 */
public class BandwidthCalculatorImpl implements BandwidthCalculator {
	/** the logger. */
	private static Log log = LogFactory.getLog(BandwidthCalculatorImpl.class);

	/** 
	 * Return a list of target instances with their allocated bandwidth
	 * calculated and set.
	 * @param aRunningTargetInstances the list of target instances to calculate for
	 * @param aMaxBandwidth the total bandwith usage for all target instances
	 * @param aMaxBandwidthPercent the max bandwidth percentage to be allocated
	 * @return the list of target instances with their allocated bandwidth set
	 */
	@Override
	public HashMap<Long, TargetInstance> calculateBandwidthAllocation(List<TargetInstance> aRunningTargetInstances, long aMaxBandwidth, int aMaxBandwidthPercent) {
		HashMap<Long, TargetInstance> results = new HashMap<Long, TargetInstance>();
		
		ArrayList<TargetInstance> bwpTIs = new ArrayList<TargetInstance>();
        ArrayList<TargetInstance> stdTIs = new ArrayList<TargetInstance>();
        
        TargetInstance ti = null;
        Iterator it = aRunningTargetInstances.iterator();
        while (it.hasNext()) {
            ti = (TargetInstance) it.next();
            if (ti.getBandwidthPercent() == null) {            	
                stdTIs.add(ti);
            }
            else {            	
                bwpTIs.add(ti);
            }
        }
		
        // Calculate the bandwidth allocation for all target instances.
        if (stdTIs.isEmpty() && bwpTIs.size() == 1) {
            // allocate all the bandwidth to this TargetInstance
        	processStandardTargetInstances(results, bwpTIs, aMaxBandwidth);  
        }
        else if (stdTIs.isEmpty() && !bwpTIs.isEmpty()) {
            // allocate all the bandwidth to this TargetInstance
        	processSpecialTargetInstances(results, bwpTIs, aMaxBandwidth, 100);
        }                
        else { 
        	// distribute the special allocatable bandwidth based on the 
            // allocated perentage for the non-standard target instances        	
        	// then distrubute the remaining bandwidth evenly among the 
        	// remianing standard target instances
        	double remainingBW = processSpecialTargetInstances(results, bwpTIs, aMaxBandwidth, aMaxBandwidthPercent);
        	processStandardTargetInstances(results, stdTIs, remainingBW);        	
        }   
		
        if (log.isDebugEnabled()) {
        	for (TargetInstance instance : results.values()) {
        		log.debug("Allocated " + instance.getAllocatedBandwidth() + "KB to " + instance.getJobName());
			}        	
        }
        
		return results;
	}
		
	/** 
	 * Calculate the allocated bandwith for a list of target instances that have a 
	 * bandwidth percentage override set.
	 * @param aResults the result list to populate
	 * @param aBandwidthPercentTIs the target instances to calaculate for
	 * @param aRemainingBandwidth the amount of bandwith that can be allowcated
	 * @param aMaxBandwidthPercent the max percent of the bandwidth that can be allocated
	 * @return the remaining amount of bandwidth after these allocations
	 */
	private static double processSpecialTargetInstances(HashMap<Long, TargetInstance> aResults, ArrayList<TargetInstance> aBandwidthPercentTIs, long aRemainingBandwidth, int aMaxBandwidthPercent) {
		if (aBandwidthPercentTIs == null || aBandwidthPercentTIs.isEmpty()) {
			return aRemainingBandwidth;
		}
					
		double maxbw = aRemainingBandwidth;
        double remainingbw = aRemainingBandwidth;
        double maxAllocateableBW = (maxbw * (aMaxBandwidthPercent / 100d));        
		TargetInstance ti = null;
		Double allocatedbw = null;
		
		if (aBandwidthPercentTIs.size() == 1) {			
			ti = aBandwidthPercentTIs.iterator().next();				
			allocatedbw = new Double(maxbw * (ti.getBandwidthPercent() / 100d));			
            remainingbw -= allocatedbw.doubleValue();
            ti.setAllocatedBandwidth(new Long(allocatedbw.longValue()));  
            aResults.put(ti.getOid(), ti);
		}
		else {
			Iterator it = null;
			// Check the target instance for a percentage allocation.
	        double totalBWPercents = 0;
	        if (!aBandwidthPercentTIs.isEmpty()) {                
	            it = aBandwidthPercentTIs.iterator();
	            while (it.hasNext()) {
	                ti = (TargetInstance) it.next();
	                totalBWPercents += ti.getBandwidthPercent().intValue();               
	            }            
	        }
			                                   
            it = aBandwidthPercentTIs.iterator();
            while (it.hasNext()) {
                ti = (TargetInstance) it.next();
                allocatedbw = new Double(maxAllocateableBW * (ti.getBandwidthPercent() / totalBWPercents));               
                remainingbw -= allocatedbw.doubleValue();                
                ti.setAllocatedBandwidth(new Long(allocatedbw.longValue()));                  
                aResults.put(ti.getOid(), ti);
            }
		}
		
		return remainingbw;
	}
		
	/**
	 * Calculate the allocated bandwith for a list of target instances that have a 
	 * no bandwidth percentage override set.
	 * @param aResults he result list to populate
	 * @param aStandardTIs the target instances to calaculate for
	 * @param aRemainingBandwidth tha amount of bandwidth to split between these target instances
	 */
	private static void processStandardTargetInstances(HashMap<Long, TargetInstance> aResults, ArrayList<TargetInstance> aStandardTIs, double aRemainingBandwidth) {
		if (aStandardTIs == null || aStandardTIs.isEmpty()) {
			return;
		}
		
		TargetInstance ti = null;
		Double allocatedbw = null;
		
		Iterator it = aStandardTIs.iterator();
        while (it.hasNext()) {
            ti = (TargetInstance) it.next();
            allocatedbw = new Double(aRemainingBandwidth / aStandardTIs.size());                
            ti.setAllocatedBandwidth(new Long(allocatedbw.longValue()));                
            aResults.put(ti.getOid(), ti);
        }
	}
}
