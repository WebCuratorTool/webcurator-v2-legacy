package org.webcurator.ui.tools.controller;

import java.text.SimpleDateFormat;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.webcurator.domain.model.core.ArcHarvestResource;
import org.webcurator.domain.model.core.ArcHarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResource;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResult;
import java.util.regex.*;

public class HarvestResourceUrlMapper {
	
	//private static Log log = LogFactory.getLog(HarvestResourceUrlMapper.class);
	
	public static final String NULL_RESULT_RETURN_VAL = "**Error-NULL-HarvestResult**";
	public static final String NULL_RESOURCE_RETURN_VAL = "**Error-NULL-HarvestResult**";
	public static final String NULL_URLMAP_RETURN_VAL = "**Error-NULL-UrlMap**";
	public static final String DISABLED_RETURN_VAL = "**Error-DISABLED-HarvestResourceUrlMapper**";
	public static final String DEFAULT_DATE_FORMAT = "yyyyMMddhhmmss";
	private static final String FIND_CREATE_DATE = "{$HarvestResult.CreationDate";
	
	private String urlMap;

	public String getUrlMap() {
		return urlMap;
	}

	public void setUrlMap(String map) {
		urlMap = map;
	}
	
	public String generateUrl(HarvestResult result, HarvestResourceDTO hRsr)
	{
		if (urlMap == null)
			return NULL_URLMAP_RETURN_VAL;
		if (hRsr == null)
			return NULL_RESOURCE_RETURN_VAL;
		if (result == null)
			return NULL_RESOURCE_RETURN_VAL;
		
		// note: the syntax hRsr.getName().replace("$", "\\$") is to fix a bug whereby seeds
		// with embedded $ characters caused an exception within the java String.replaceAll 
		// function (a know limitation). 
		String retVal = urlMap
		.replaceAll("\\{\\$HarvestResource\\.Name\\}", (hRsr.getName() == null)?"":hRsr.getName().replace("$", "\\$"))
		.replaceAll("\\{\\$HarvestResource\\.Length\\}", String.valueOf(hRsr.getLength()))
		.replaceAll("\\{\\$HarvestResource\\.Oid\\}", (hRsr.getOid() == null)?"":String.valueOf(hRsr.getOid()))
		.replaceAll("\\{\\$HarvestResource\\.StatusCode\\}", String.valueOf(hRsr.getStatusCode()))
		.replaceAll("\\{\\$HarvestResult\\.Oid\\}", (result.getOid() == null)?"":String.valueOf(result.getOid()))
		.replaceAll("\\{\\$HarvestResult\\.CreationDate\\}", (result.getCreationDate() == null)?"": new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(result.getCreationDate()))
		.replaceAll("\\{\\$HarvestResult\\.DerivedFrom\\}", (result.getDerivedFrom() == null)?"":String.valueOf(result.getDerivedFrom()))
		.replaceAll("\\{\\$HarvestResult\\.HarvestNumber\\}", String.valueOf(result.getHarvestNumber()))
		.replaceAll("\\{\\$HarvestResult\\.ProvenanceNote\\}", (result.getProvenanceNote() == null)?"":result.getProvenanceNote())
		.replaceAll("\\{\\$HarvestResult\\.State\\}", String.valueOf(result.getState()));
		
		if(hRsr instanceof ArcHarvestResourceDTO)
		{
			String fileDateRegex = "[12][0-9][0-9][0-9][01][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9]";
			Pattern pattern = Pattern.compile(fileDateRegex);
			String arcFileName = ((ArcHarvestResourceDTO)hRsr).getArcFileName();
			if(arcFileName != null)
			{
				Matcher matcher = pattern.matcher(arcFileName);
				if(matcher.find())
				{
					String fileDate = matcher.group();
					retVal = retVal.replaceAll("\\{\\$ArcHarvestResource\\.FileDate\\}", fileDate);
				}
				else
				{
					retVal = retVal.replaceAll("\\{\\$ArcHarvestResource\\.FileDate\\}", "*");
				}
			}
			else
			{
				retVal = retVal.replaceAll("\\{\\$ArcHarvestResource\\.FileDate\\}", "*");
			}
		}
		else
		{
			retVal = retVal.replaceAll("\\{\\$ArcHarvestResource\\.FileDate\\}", "*");
		}
		
		if (retVal.indexOf(FIND_CREATE_DATE) > 0)
			retVal = doFormattedDates(retVal, result);
		
		
		return retVal;
	}
	
	private String doFormattedDates(String url, HarvestResult result)
	{
		int ind = url.indexOf(FIND_CREATE_DATE);
		while (ind > 0)
		{
			int indFormatStart = ind + FIND_CREATE_DATE.length();
			int indFormatEnd = url.indexOf("}",indFormatStart);
			if (indFormatEnd < 0)
				break;
			if (url.substring(indFormatStart,indFormatStart + 1).equals(","))
			{
				String format = url.substring(indFormatStart + 1, indFormatEnd);
				SimpleDateFormat df = new SimpleDateFormat(format);
				url = url.substring(0,ind) + df.format(result.getCreationDate()) + url.substring(indFormatEnd +1 , url.length() );
				ind = url.indexOf(FIND_CREATE_DATE);
			}
			else
			{
				ind = url.indexOf(FIND_CREATE_DATE, indFormatStart );
			}
			
		}
		return url;
	}
	
}
