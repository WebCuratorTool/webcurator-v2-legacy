package org.webcurator.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.domain.model.core.HeatmapConfig;

public class HeatmapDAOImpl extends HibernateDaoSupport implements HeatmapDAO {

	private Log log = LogFactory.getLog(HarvestCoordinatorDAOImpl.class);
	private TransactionTemplate txTemplate = null;

	public Map<String, HeatmapConfig> getHeatmapConfigurations() {
		Map<String, HeatmapConfig> result = new HashMap<String, HeatmapConfig>();
		List<HeatmapConfig> configurations = getHibernateTemplate()
				.findByNamedQuery(HeatmapConfig.QUERY_ALL);
		for (HeatmapConfig config : configurations) {
			result.put(config.getName(), config);
		}
		return result;
	}

	public void saveOrUpdate(final HeatmapConfig config) {
		if (log.isDebugEnabled()) {
			log.debug("Saving " + config.getClass().getName());
		}
		txTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus ts) {
				try {
					getSession().saveOrUpdate(config);
				} catch (Exception ex) {
					ts.setRollbackOnly();
				}
				return null;
			}
		});

	}

	/**
	 * @param txTemplate
	 *            The txTemplate to set.
	 */
	public void setTxTemplate(TransactionTemplate txTemplate) {
		this.txTemplate = txTemplate;
	}

	@Override
	public HeatmapConfig getConfigByOid(final Long oid) {
		return (HeatmapConfig) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						Query query = session
								.getNamedQuery(HeatmapConfig.QRY_GET_CONFIG_BY_OID);
						query.setLong(0, oid);
						return query.uniqueResult();
					}
				});
	}

}
