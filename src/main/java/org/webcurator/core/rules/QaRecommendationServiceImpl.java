package org.webcurator.core.rules;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.webcurator.core.harvester.coordinator.HarvestCoordinator;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.store.tools.QualityReviewFacade;
import org.webcurator.domain.model.core.Indicator;
import org.webcurator.domain.model.core.IndicatorCriteria;
import org.webcurator.domain.model.core.TargetInstance;

public class QaRecommendationServiceImpl implements QaRecommendationService {
	
	/**
	 * The drools knowledge session though which all rules are executed
	 */
	private StatefulKnowledgeSession ksession = null;
	
	/**
	 * The interface for retrieving <code>HarvestResourceDTO</code>s
	 */
	private QualityReviewFacade qualityReviewFacade = null;
	
	/**
	 * The interface for retrieving log files from the server or digital asset store
	 */
	private HarvestCoordinator harvestCoordinator = null;
	
	/**
	 * The interface for retrieving harvest history
	 */
	private TargetInstanceManager targetInstanceManager = null;
	
	/** 
	 * the default HarvestResult to check after a harvest has completed
	 */
	private static final int FIRST_RESULT = 0;
	
	/**
	 * The filename of the Drools rules file (needs to be available on the classpath)
	 */
	private String rulesFileName = null;
	
    private Map<String, Integer> advicePriority = null;
    
    private Map<String, String> globals = null;
	
    private String stateFailed = null;
    
	/**
	 * the logger
	 */
	private static final Log LOG = LogFactory.getLog(QaRecommendationServiceImpl.class);

	public QaRecommendationServiceImpl() {
	}
	
	/**
	 * Derive the advice for the <code>TargetInstance<code>'s <code>Indicators<code>
	 */
	public String getRecommendation(List<Indicator> indicators) {
		
		// iterate through the QA Indicators and return the strongest advice
		// (Archive / Investigate / Reject)
		String advice = null;
		Iterator<Indicator> it = indicators.iterator();
		while (it.hasNext()){
			Indicator indicator = it.next();
			if (advice == null && indicator.getAdvice() != null) {
				advice = indicator.getAdvice();
			} else if (indicator.getAdvice() != null) {
				// select the advice with highest priority
				if (advicePriority.get(indicator.getAdvice()) > advicePriority.get(advice)) {
					advice = indicator.getAdvice();
				}
			}
		}

		return advice;
	}
	
	public final void buildKnowledgeSession() {
		if (ksession == null) {
			LOG.debug("Building Drools Knowledge Session");
			// initialise the rules
	        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
	                .newKnowledgeBuilder();
	
	        // pickup the rules file from the classpath
	        // this will parse and compile in one step
	        LOG.info("Loading rules file " + rulesFileName);
	        kbuilder.add( ResourceFactory.newClassPathResource( rulesFileName, QaRecommendationServiceImpl.class ),
	                      ResourceType.DRL );
	
	        // Check the builder for errors
	        if ( kbuilder.hasErrors() ) {
	        	LOG.error(kbuilder.getErrors().toString());
	            throw new RuntimeException( "Unable to compile \"" + rulesFileName + "\"." );
	        }
	
	        // add the packages to a knowledgebase (deploy the knowledge packages).
	        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();      
			kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
			
			// set the knowledge session
			ksession = kbase.newStatefulKnowledgeSession();
			
			// set the configured message formats and advice
			ksession.setGlobal("globals", globals);
		}
		
	}
	
    /**
     * Retrieves a <code>TargetInstance</code>s <code>HarvestResoureDTO</code>s and submits them to the Drools rules engine
     * @param ti	the <code>TargetInstance</code>
     * @param referenceCrawl the <code>TargetInstance</code> of the reference crawl (or null if no reference crawl exists)
     * @param indicators a <code>Set</code> of <code>Indicator</code>s to be processed by the rules engine
     * @throws DroolsParserException
     * @throws IOException
     */
	public final void applyRules(TargetInstance ti, TargetInstance referenceCrawl, List<IndicatorCriteria> criterias) throws DroolsParserException, IOException {
		
        // fetch the indictors to compute
        List<Indicator> indicators = ti.getIndicators();
        
		Iterator<IndicatorCriteria> criteriaIt = criterias.iterator();
		while (criteriaIt.hasNext()) {
			IndicatorCriteria criteria = criteriaIt.next();
	        // ensure that the indicator for each indicator criteria exists and generate it if not
	        findAndCreateOrUpdateIndicator(ti, criteria, indicators);			
		}
		
    	LOG.info(indicators.size() + " QA indicators retrieved, submitting to Rules Engine for target instance with oid " + ti.getOid() + " ...");

        // insert the rule parameters
        ksession.insert(ti);
    	LOG.info("inserted ti with oid " + ti.getOid());
       
        if (referenceCrawl !=null) {
        	ksession.insert(referenceCrawl);
        	LOG.info("inserted reference crawl with oid " + referenceCrawl.getOid());
        }
        
		// insert any indicators into the rules engine
		Iterator<Indicator> indicatorIt = indicators.iterator();
		while (indicatorIt.hasNext()) {
			// reset the indicator value
			Indicator indicator = indicatorIt.next();
			//indicator.setFloatValue(0.0F);
			// and the advice
			indicator.setAdvice(null);
			// and the justification
			indicator.setJustification(null);
			// and clear the report lines
			//targetInstanceManager.deleteIndicatorReportLines(indicator);
			//while (indicator.getIndicatorReportLines().size() > 0) {
			//	indicator.getIndicatorReportLines().remove(indicator.getIndicatorReportLines().get(0));
			//}
			indicator.getIndicatorReportLines().clear();
			ksession.insert(indicator);
		}
		
		// save the ti to remove any existing report lines from the indicators
		//targetInstanceManager.save(ti);
		
		// initialise the message map to hold the rule engine messages
		RuleMessageMap<String, String> messageMap = new RuleMessageMap<String, String>(); 
		ksession.insert(messageMap);
		
		// pass in the Quality Review Facade so that we can process the robots.txt file within the rules engine
		ksession.insert(qualityReviewFacade);
		
		// pass in the Harvest Coordinator so that we can retrieve log files (eg: Heritrix Error Codes)
		ksession.insert(harvestCoordinator);
		
		// pass the Target Instance Manager so that we can retrieve harvest history
		ksession.insert(targetInstanceManager);
		
		// run the rules
		try {
			ksession.fireAllRules();
		}
		// catch any unexpected exceptions
        catch (Exception e) {
        	LOG.error(e);
        	// set the last indicator status to failed
        	indicators.get(indicators.size()-1).setAdvice(stateFailed);
        	indicators.get(indicators.size()-1).setJustification(e.getCause().getMessage());
        	LOG.error(e.getCause());
        } finally {
            // clean up the knowledge session
            ksession.dispose();
            ksession = null;
        }
        
        LOG.info("Returning supporting facts: ");
        
        Iterator<String> facts = messageMap.entrySet().iterator();
        while (facts.hasNext()) {
        	LOG.info(facts.next());
        }

		// derive the recommendation
		String recommendation = getRecommendation(indicators);
		
		// persist the recommendation on the target instance
		ti.setRecommendation(recommendation);
	}
	
	private final void findAndCreateOrUpdateIndicator(TargetInstance targetInstance, IndicatorCriteria criteria, List<Indicator> indicators) {
		
		Boolean found = false;
		Iterator<Indicator> it = indicators.iterator();
		while (it.hasNext()) {
			Indicator indicator = it.next();
			// if the indicator already exists
			if (indicator.getIndicatorCriteria() != null 
					&& criteria.getOid() == indicator.getIndicatorCriteria().getOid()) {
				found = true;
				// populate the indicator based on the indicator criteria
				populateIndicator(criteria, indicator);
			}
		}
		// otherwise create the indicator
		if (!found) {
			Indicator newIndicator = new Indicator();
			newIndicator.setFloatValue(0.0F);
			newIndicator.setDateTime(new Date());
			// the target instance is needed for searching
			newIndicator.setTargetInstanceOid(targetInstance.getOid());
			populateIndicator(criteria, newIndicator);
			// and add it to the list of indicators
			indicators.add(newIndicator);
		}
		
	}
	
	private final void populateIndicator(IndicatorCriteria criteria, Indicator indicator) {
		// copy the values from the IndicatorCriteria to the Indicator
		indicator.setName(criteria.getName());
		indicator.setLowerLimit(criteria.getLowerLimit());
		indicator.setUpperLimit(criteria.getUpperLimit());
		indicator.setLowerLimitPercentage(criteria.getLowerLimitPercentage());
		indicator.setUpperLimitPercentage(criteria.getUpperLimitPercentage());
		indicator.setUnit(criteria.getUnit());
		indicator.setShowDelta(criteria.getShowDelta());
		// and the IndicatorCriteria oid since we may be generating and indicator from scratch
		indicator.setIndicatorCriteria(criteria);
		indicator.setAgency(criteria.getAgency());
		
	}
	
	/**
	 *	Setter for the Quality Review Facade used by Spring 
	 */
	public void setQualityReviewFacade(QualityReviewFacade qaFacade) {
		this.qualityReviewFacade = qaFacade;
	}
	
	/**
	 *	Setter for the Harvest Coordinator used by Spring 
	 */
	public void setHarvestCoordinator(HarvestCoordinator harvestCoordinator) {
		this.harvestCoordinator = harvestCoordinator;
	}
	
	/**
	 *	Setter for the Target Instance Manager used by Spring 
	 */
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager) {
		this.targetInstanceManager = targetInstanceManager;
	}
  
	/**
	 * Setter for the advice priority used by Spring
	 */
	public void setAdvicePriority(Map<String, Integer> advicePriority) {
		this.advicePriority = advicePriority;
	}
	
	/**
	 * The <code>String</code> that will be used to denote an unexpected exception in the Rules Engine
 	 * @param stateFailed The <code>String</code> that denotes a failed condition
	 */
	public void setStateFailed(String stateFailed) {
		this.stateFailed = stateFailed;
	}
	
	/**
	 * The name of the drools file that should be loaded into the rules engine
	 */
	public void setRulesFileName(String rulesFileName) {
		this.rulesFileName = rulesFileName;
	}
	
	/**
	 * The configured message formats and advice for <code>Indicator</code>s
	 */
	public void setGlobals(Map<String, String> globals) {
		this.globals = globals;
	}
}
