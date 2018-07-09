package org.webcurator.core.report;

import java.util.*;

public class MockReportGenerator implements ReportGenerator {

	List<ResultSet> results = new ArrayList<ResultSet>();
	
	public List<ResultSet> generateData(OperationalReport operationalReport) {
		return results;
	}
	
	public void setResultData(String[][] resultSet)
	{
		String[] columnNames = {};
		for(int i = 0; i < resultSet.length; i++)
		{
			if(i == 0)
			{
				//first row is the columnNames
				columnNames = resultSet[i];
			}
			else
			{
				MockResultSet result = new MockResultSet();
				result.setColumnNames(columnNames);
				result.setFields(resultSet[i]);
				results.add(result);
			}
		}
	}

}
