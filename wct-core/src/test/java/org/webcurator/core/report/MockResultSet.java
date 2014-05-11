package org.webcurator.core.report;

import java.util.*;

public class MockResultSet implements ResultSet {

	String[] columnNames;
	String[] fields;
	
	protected MockResultSet()
	{
	}
	
	public String[] getColumnHTMLNames() {
		return columnNames;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public String[] getDisplayableFields() {
		return fields;
	}

	public List<Object> getFields() {
		List<Object> fieldList = new ArrayList<Object>();
		for(int i = 0; i < fields.length; i++)
		{
			fieldList.add(fields[i]);
		}
		
		return fieldList;
	}

	protected void setColumnNames(String[] columnNames)
	{
		this.columnNames = columnNames;
	}

	protected void setFields(String[] fields)
	{
		this.fields = fields;
	}
}
