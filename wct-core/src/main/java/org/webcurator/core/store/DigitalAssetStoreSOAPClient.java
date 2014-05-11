package org.webcurator.core.store;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.core.util.WCTSoapCall;
import org.webcurator.domain.model.core.ArcHarvestFileDTO;
import org.webcurator.domain.model.core.ArcHarvestResourceDTO;
import org.webcurator.domain.model.core.ArcHarvestResultDTO;
import org.webcurator.domain.model.core.CustomDepositFormCriteriaDTO;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.webcurator.domain.model.core.HarvestResourceDTO;
import org.webcurator.domain.model.core.HarvestResultDTO;

public class DigitalAssetStoreSOAPClient implements DigitalAssetStore, DigitalAssetStoreConfig {
    
	/** the host name or ip-address for the das. */
    private String host = "localhost";
    /** the port number for the das. */
    private int port = 8080;
    /** the service name of the digital asset store. */
    private String service = WCTSoapCall.DAS_SERVICE;
    /** the service name of the digital asset store log reader. */
    private String logReaderService = WCTSoapCall.DAS_LOG_READER;
    
    /** the logger. */
    private static Log log = LogFactory.getLog(DigitalAssetStoreSOAPClient.class);

    public void save(String targetInstanceName, String directory, File file) throws DigitalAssetStoreException {
		try {
			String filename = file.getName();
			DataHandler handler = new DataHandler(new FileDataSource(file));
		
			WCTSoapCall call = new WCTSoapCall(host, port, service, "save");
			call.regTypes(DataHandler.class);
			call.invoke(targetInstanceName, directory, filename, handler);
		}
		catch(Exception ex) {
            throw new DigitalAssetStoreException("Failed to save to ARC File Store : " + ex.getMessage(), ex);
		}
    }
    
    public void save(String targetInstanceName, File file) throws DigitalAssetStoreException {
		try {
			String filename = file.getName();
			DataHandler handler = new DataHandler(new FileDataSource(file));
		
			WCTSoapCall call = new WCTSoapCall(host, port, service, "save");
			call.regTypes(DataHandler.class);
			
			//call.invoke(targetInstanceName, filename, handler);
			call.infiniteRetryingInvoke(30000l, targetInstanceName, filename, handler);
		}
		catch(Exception ex) {
            throw new DigitalAssetStoreException("Failed to save to ARC File Store : " + ex.getMessage(), ex);
		}
    }    
    
    
    public void save(String targetInstanceName, String directory, File[] files) throws DigitalAssetStoreException {
		try {
			int numAttachments = files.length;
			String[] filenames = new String[numAttachments];
			DataHandler[] handlers = new DataHandler[numAttachments];
			
			for(int i=0; i<numAttachments; i++) {
				filenames[i] = files[i].getName();
				handlers[i] = new DataHandler(new FileDataSource(files[i]));		
			}
		
			WCTSoapCall call = new WCTSoapCall(host, port, service, "save");
			call.regTypes(DataHandler.class);
			call.invoke(targetInstanceName, directory, filenames, handlers);
		}
		catch(Exception ex) {
            throw new DigitalAssetStoreException("Failed to save to ARC File Store : " + ex.getMessage(), ex);
		}
	}
    
	public void save(String targetInstanceName, File[] files) throws DigitalAssetStoreException {
		try {
			int numAttachments = files.length;
			String[] filenames = new String[numAttachments];
			DataHandler[] handlers = new DataHandler[numAttachments];
			
			for(int i=0; i<numAttachments; i++) {
				filenames[i] = files[i].getName();
				handlers[i] = new DataHandler(new FileDataSource(files[i]));		
			}
		
			WCTSoapCall call = new WCTSoapCall(host, port, service, "save");
			call.regTypes(DataHandler.class);
			call.invoke(targetInstanceName, filenames, handlers);
		}
		catch(Exception ex) {
            throw new DigitalAssetStoreException("Failed to save to ARC File Store : " + ex.getMessage(), ex);
		}

	}

	public File getResource(String targetInstanceName, int harvestResultNumber, HarvestResourceDTO resource) throws DigitalAssetStoreException {
		
		FileOutputStream  fos = null;
		File f = null;
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "getResource");
			call.regTypes(DataHandler.class, ArcHarvestResourceDTO.class);
			DataHandler dh = (DataHandler) call.invoke(targetInstanceName, harvestResultNumber, resource);
			
            f = File.createTempFile("wct", "tmp");
            fos = new FileOutputStream(f);
	        dh.writeTo(fos);
		}
		catch(Exception ex) {
            throw new DigitalAssetStoreException("Failed to get resource for " + targetInstanceName + " " + harvestResultNumber + ": " + ex.getMessage(), ex);
		}
		finally{
			try
			{
				if(fos != null)
				{
					fos.close();
				}
			}
			catch(Exception ex) {
	            throw new DigitalAssetStoreException("Failed to get resource for " + targetInstanceName + " " + harvestResultNumber + ": " + ex.getMessage(), ex);
			}
		}
	
		return f;
	}

	public Header[] getHeaders(String targetInstanceName, int harvestResultNumber, HarvestResourceDTO resource) throws DigitalAssetStoreException {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "getHeaders");
			call.regTypes(Header.class, HarvestResourceDTO.class, ArcHarvestResourceDTO.class, HeaderElement.class, ArcHarvestResultDTO.class, HarvestResultDTO.class, NameValuePair.class);
			Header[] headers = (Header[]) call.invoke(targetInstanceName, harvestResultNumber, resource);
            
	        return headers;	   
		}
		catch(Exception ex) {
            throw new DigitalAssetStoreException("Failed to get headers for " + targetInstanceName + " " + harvestResultNumber + ": " + ex.getMessage(), ex);
		}
	}

	public HarvestResultDTO copyAndPrune(String targetInstanceName, int orgHarvestResultNum, int newHarvestResultNum, List<String> urisToDelete, List<HarvestResourceDTO> hrsToImport) throws DigitalAssetStoreException {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "copyAndPrune");
			call.regTypes(HarvestResultDTO.class, ArcHarvestResultDTO.class, ArcHarvestFileDTO.class, ArcHarvestResourceDTO.class, HarvestResourceDTO.class);
			return (HarvestResultDTO) call.invoke(targetInstanceName, orgHarvestResultNum, newHarvestResultNum, urisToDelete, hrsToImport);
		}
		catch(ServiceException ex) {
			log.error("Error calling SOAP Service (copyAndPrune): " + ex.getMessage(), ex);
			return null;
		}
        catch (Exception e) {
            throw new DigitalAssetStoreException("Failed to copy and prune " + targetInstanceName + " " + orgHarvestResultNum + ": " + e.getMessage(), e);
        }
	}
	
	/**
	 * @see DigitalAssetStore#purge(String[]).
	 */
	public void purge(String[] targetInstanceNames) throws DigitalAssetStoreException {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "purge");		
			call.invoke((Object)targetInstanceNames);
		}
		catch(ServiceException ex) {
			log.error("Error calling SOAP Service (purge): " + ex.getMessage(), ex);			
		}
        catch (Exception e) {
            throw new DigitalAssetStoreException("Failed to purge : " + e.getMessage(), e);
        }
	}

	/**
	 * @see DigitalAssetStore#purgeAbortedTargetInstances(String[]).
	 */
	public void purgeAbortedTargetInstances(String[] targetInstanceNames) throws DigitalAssetStoreException {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "purgeAbortedTargetInstances");		
			call.invoke((Object)targetInstanceNames);
		}
		catch(ServiceException ex) {
			log.error("Error calling SOAP Service (purgeAbortedTargetInstances): " + ex.getMessage(), ex);			
		}
        catch (Exception e) {
            throw new DigitalAssetStoreException("Failed to purgeAbortedTargetInstances : " + e.getMessage(), e);
        }
	}

	/**
     * @param host The host to set.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @param port The port to set.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @param service The service to set.
     */
    public void setService(String service) {
        this.service = service;
    }

	public String getAssetStoreServiceName() {
		return service;
	}

	public String getHost() {
		return host;
	}

	public String getLogReaderServiceName() {
		return logReaderService;
	}

	public int getPort() {
		return port;
	}

	/**
	 * @param logReaderService the logReaderService to set
	 */
	public void setLogReaderService(String logReaderService) {
		this.logReaderService = logReaderService;
	}

	public void submitToArchive(String targetInstanceOid, String SIP, Map xAttributes, int harvestNumber) throws DigitalAssetStoreException {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "submitToArchive");
			call.invoke(targetInstanceOid, SIP, xAttributes, harvestNumber);
		}
		catch(ServiceException ex) {
			log.error("Error calling SOAP Service (submitToArchive): " + ex.getMessage(), ex);
		}
        catch (Exception e) {
            throw new DigitalAssetStoreException("Failed to submit to archive " + targetInstanceOid + " " + harvestNumber + ": " + e.getMessage(), e);
        }
	}

	public byte[] getSmallResource(String targetInstanceName, int harvestResultNumber, HarvestResourceDTO resource) throws DigitalAssetStoreException {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "getSmallResource");
			call.regTypes(ArcHarvestResourceDTO.class);
			byte[] result = (byte[]) call.invoke(targetInstanceName, harvestResultNumber, resource);
			
            return result;	        
		}
		catch(Exception ex) {
            throw new DigitalAssetStoreException("Failed to get resource for " + targetInstanceName + " " + harvestResultNumber + ": " + ex.getMessage(), ex);
		}
	}

	public void initiateIndexing(ArcHarvestResultDTO harvestResult) throws DigitalAssetStoreException {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "initiateIndexing");
			call.regTypes(ArcHarvestResultDTO.class);
			call.invoke(harvestResult);
		}
		catch(Exception ex) {
            throw new DigitalAssetStoreException("Failed to initiate indexing for " + harvestResult.getTargetInstanceOid(), ex);
		}
		
	}
	
	public void initiateRemoveIndexes(ArcHarvestResultDTO harvestResult) throws DigitalAssetStoreException {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "initiateRemoveIndexes");
			call.regTypes(ArcHarvestResultDTO.class);
			call.invoke(harvestResult);
		}
		catch(Exception ex) {
	        throw new DigitalAssetStoreException("Failed to initiate removeIndexes for " + harvestResult.getTargetInstanceOid(), ex);
		}
	}
	
	public Boolean checkIndexing(Long harvestResultOid) throws DigitalAssetStoreException {
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "checkIndexing");
			call.regTypes(Long.class, Boolean.class);
			return (Boolean)call.invoke(harvestResultOid);
		}
		catch(Exception ex) {
            throw new DigitalAssetStoreException("Failed to check indexing for Harvest Result " + harvestResultOid, ex);
		}
		
	}

	public CustomDepositFormResultDTO getCustomDepositFormDetails(CustomDepositFormCriteriaDTO criteria) throws DigitalAssetStoreException {
		CustomDepositFormResultDTO response = new CustomDepositFormResultDTO();
		if (criteria == null) return response;
		try {
			WCTSoapCall call = new WCTSoapCall(host, port, service, "getCustomDepositFormDetails");
			call.regTypes(CustomDepositFormResultDTO.class, CustomDepositFormCriteriaDTO.class);
			response = (CustomDepositFormResultDTO) call.invoke(criteria);
			toAbsoluteUrl(response);
			return response;
		}
		catch(Exception ex) {
			throw new DigitalAssetStoreException("Failed to get custom deposit form details for input: " + criteria, ex);
		}
	}
	
	public String toString() { 
		return "DAS@" + host + ":" + port + "/" + service;
	}

	/**
	 * Convert the custom deposit form URL into an absolute URL using the host and
	 * port configured for WCT digital asset store. The assumption is that if the 
	 * custom form URL is a relative URL, then it is hosted in the same web container
	 * that hosts the DAS. If it is already an absolute URL, this method doesn't change
	 * it.
	 * 
	 * @param response
	 */
	protected void toAbsoluteUrl(CustomDepositFormResultDTO response) {
		if (response == null) return;
		String customDepositFormURL = response.getUrlForCustomDepositForm();
		if (customDepositFormURL == null) return;
		if (customDepositFormURL.startsWith("http://") || customDepositFormURL.startsWith("https://")) return;
		if (customDepositFormURL.startsWith("/") == false) customDepositFormURL = "/" + customDepositFormURL;
		String urlPrefix = "http://" + getHost() + ":" + getPort();
		response.setUrlForCustomDepositForm(urlPrefix + customDepositFormURL);
	}
}
