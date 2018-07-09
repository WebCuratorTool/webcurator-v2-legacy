package org.webcurator.core.rules;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.drools.compiler.DroolsParserException;
import org.webcurator.core.scheduler.TargetInstanceManager;
import org.webcurator.core.store.tools.QualityReviewFacade;
import org.webcurator.domain.model.core.IndicatorCriteria;
import org.webcurator.domain.model.core.TargetInstance;

/**
 * Provides a rules execution service for a specific <code>TargetInstance</code>.<p/>
 * Uses the <code>Indicator</code>s defined for the owning <code>Agency</code> for the <code>TargetInstance</code> as a basis for a QA recommendation.
 * @author twoods
 *
 */
public interface QaRecommendationService {
	/**
	 * Validate a <code>TargetInstance</code> against the rules to obtain a recommendation.<p/>
	 * The rules are run by the JSR94 compliant rules engine (DROOLS).<p/>
	 * The advice is populated and returned on each <code>Indicator</code> along with the computed <code>Indicator</code> values.<p/>
	 * A justification is also returned on each <code>Indicator</code> for presentation to the user.
	 * @param ti the <code>TargetInstance</code> to validate
	 * @param referenceCrawl the <code>TargetInstance</code> representing the reference crawl, or null if none exists
	 * @param criterias a <code>List</code> of <code>IndicatorCriteria</code> from which the <code>Indicator</code>s will be generated for the <code>TargetInstance</code>
	 * @throws DroolsParserException if the rules file cannot be compiled
	 * @throws IOException if a file cannot be retrieved (log files or robots.txt)
	 */
	public void applyRules(TargetInstance ti, TargetInstance referenceCrawl, List<IndicatorCriteria> criterias) throws DroolsParserException, IOException;
	
	public void buildKnowledgeSession();
	
	public void setQualityReviewFacade(QualityReviewFacade qaFacade);
	
	public void setTargetInstanceManager(TargetInstanceManager targetInstanceManager);
	
	public void setAdvicePriority(Map<String, Integer> advicePriority);
	
	public void setStateFailed(String stateFailed);
	
	public void setRulesFileName(String rulesFileName);
	
	public void setGlobals(Map<String, String> globals);
}
