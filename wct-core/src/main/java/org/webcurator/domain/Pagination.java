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
package org.webcurator.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.webcurator.ui.common.Constants;

/**
 * Pagination object makes all the decisions about the pagination of result sets, 
 * to help display them in a page by page manner.
 * @author bprice
 */
public class Pagination extends HibernateDaoSupport {
	/** the list of results. */
	private List results;

	/** the max number of records to display on a page. */
	private int pageSize;
	
	/** The page number. */
	private int page;
	
	/** Total results */
	private int total;

	/** The logger. */
	private static Log log = LogFactory.getLog(Pagination.class);

	/** The default constructor. */
	public Pagination() {
		results = new ArrayList();
		page = 0;
	}	

	
	/**
	 * Constructor taking a list of items.
	 */
	public Pagination(Collection items, int aPage, int aPageSize) {
		results = new LinkedList();
		page = aPage;
		pageSize = aPageSize;
		
		int ix = 0;
		Iterator it = items.iterator();
		
		// Skip initial elements.
		for(int i=ix; i < page * pageSize && it.hasNext(); i++) {
			it.next();
		}
		
		// Add the next pageSize + 1 elements
		for(int i=ix; i < pageSize + 1 && it.hasNext(); i++ ) {
			results.add( it.next());
		}
		
		total = items.size();
	}
	
	public Pagination(List newItems, Query cntExistingItems, Query existingItems, int aPage, int aPageSize) {
		this.pageSize = aPageSize;
		this.page     = aPage;
		
		int startNum = page * pageSize;
		int endNum   = (page+1) * pageSize + 1;
		
		int resultIndex = startNum;
		
		results = new LinkedList();
		
		if( page * pageSize < newItems.size()) {
			for(int i=startNum; i < newItems.size() && i < endNum; i++) {
				results.add(newItems.get(i));
				resultIndex++;
			}
		}
		
		if(resultIndex < endNum) {
			existingItems.setFirstResult(Math.max(0, resultIndex - newItems.size()));
			existingItems.setMaxResults(endNum - resultIndex);		
			results.addAll(existingItems.list());		
		}
		
		total = newItems.size() + ((Number)cntExistingItems.uniqueResult()).intValue();
		
		
	}
	
	
	/** 
	 * Constructor that accepts the query params and page number to display.
	 * @param query The query to run.
	 * @param aPage the page number to return.
	 * @param aPageSize the size of the page to return
	 */
	public Pagination(final Criteria cntQuery, final Criteria query, final int aPage, final int aPageSize) {
		this.pageSize = aPageSize;
		this.page = aPage;
		init(cntQuery, query);
	}	
	
	public Pagination(final Query cntQuery, final Query query, final int aPage, final int aPageSize) {
		this.pageSize = aPageSize;
		this.page = aPage;
		init(cntQuery, query);
	}
	
	private void init(Criteria cntQuery, Criteria query) {
		query.setFirstResult(page * pageSize).setMaxResults(pageSize + 1);		
		results = query.list();
		
		total = ((Number)cntQuery.uniqueResult()).intValue();
	}
	
	private void init(Query cntQuery, Query query) {
		query.setFirstResult(page * pageSize).setMaxResults(pageSize + 1);
		results = query.list();		
		
		total = ((Number)cntQuery.uniqueResult()).intValue();
		
	}
	

	/** 
	 * Constructor that accepts the query params and page number to display.
	 * @param aQuery query.
	 * @param aParams the query parameters.
	 * @param aPage the page number to return.
	 * @param aPageSize the size of the page to return
	 * @param isNamedQuery true if the query is a named query
	 * @param aSessionFactory the session factory to use with the pagination object
	 */
	public Pagination(final String cntQuery, final String aQuery, final Map aParams, final int aPage, final int aPageSize, final boolean isNamedQuery, SessionFactory aSessionFactory) {
		setSessionFactory(aSessionFactory);
        page = aPage;
        pageSize = aPageSize;
		getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						Query q = isNamedQuery ? session.getNamedQuery(aQuery) : session.createQuery(aQuery);
						Query cq = isNamedQuery ? session.getNamedQuery(cntQuery) : session.createQuery(cntQuery);
						bindQueryParams(q, aParams);
						bindQueryParams(cq, aParams);
	                    init(cq, q);
	                    return null;
					}
				}
			);		
	}
	

	/**
	 * Return a flag to indicate if there is a next page.
	 * @return a flag to indicate if there is a next page
	 */
	public boolean isNextPage() {
		// return true unless we're on the last page
		if( page == (int)(total/pageSize) ) {
			return false;
		} else {
			return true;
		}
	}

	/** 
	 * Return a flag to indicate if there is a previous page.
	 * @return a flag to indicate if there is a previous page
	 */
	public boolean isPreviousPage() {
		// return true unless we're on the first page
		return page > 0;
	}

	/** 
	 * Return a page set of results.
	 * @return a page set of results
	 */
	public List getList() {
		if(isNextPage() && results.size() > pageSize)
		{
			return results.subList(0, pageSize);
		}
		else
		{
			return results;
		}
	}

	/**
	 * Bind the parameters passed in to the query.
	 * @param q the query to bind to
	 * @param map the query parameters
	 */
	private void bindQueryParams(Query q, Map map) {
		String[] allParams = q.getNamedParameters();
		log.debug("Binding parameters in Pagination");
		for (int i = 0; i < allParams.length; i++) {
			// for each named parameter, bind it to the query
			if (map.containsKey(allParams[i])) {
				log.debug("Found a parameter in Map to bind to query called " + allParams[i] + " value = " + map.get(allParams[i]));
				q.setParameter(allParams[i], map.get(allParams[i]));
			}
			else {
				throw new HibernateException("Map doesn't contain the correct parameters to bind to query");
			}
		}
	}

	/** 
	 * Return the size of the page.
	 * @return the size of the page
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Set the size of the page.
	 * @param aPageSize the size of the page
	 */
	public void setPageSize(int aPageSize) {
		this.pageSize = aPageSize;
	}

	/** 
	 * Return a flag to indicate if the result is empty.
	 * @return a flag to indicate if the result is empty
	 */
	public boolean isEmpty() {
		return this.results.isEmpty();
	}
	    
    public void close() {
        results = null;
    }


	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}


	/**
	 * @return the count
	 */
	public int getTotal() {
		return total;
	}
	
	public int getFirstResult() {
		return total == 0 ? 0 : page * pageSize + 1;
	}
	
	public int getLastResult() {
		return page * pageSize + Math.min(results.size(), pageSize);
	}
	
	public int getNumberOfPages() {
		return (total - 1) / pageSize;
	}
}
