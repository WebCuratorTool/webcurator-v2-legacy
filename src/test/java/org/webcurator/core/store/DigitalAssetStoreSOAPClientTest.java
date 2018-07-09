package org.webcurator.core.store;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.webcurator.domain.model.core.CustomDepositFormResultDTO;
import org.webcurator.test.BaseWCTTest;

public class DigitalAssetStoreSOAPClientTest  extends BaseWCTTest<DigitalAssetStoreSOAPClient>{

	public DigitalAssetStoreSOAPClientTest() {
		super(DigitalAssetStoreSOAPClient.class, "");
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		testInstance.setHost("wctstore.natlib.govt.nz");
		testInstance.setPort(19090);
	}

	@Test
	public void testToAbsoluteUrl() {
		String expectedHostPart = "http://wctstore.natlib.govt.nz:19090";
		String url;

		// An absolute http URL is passed in. No changes expected
		url = "http://some.host.natlib.govt.nz/aURL";
		testToAbsoluteUrl(url, url);
		// An absolute https URL is passed in. No changes expected
		url = "https://some.host.natlib.govt.nz/aURL";
		testToAbsoluteUrl(url, url);
		// A relative URL is passed in. Expect an absolute URL to be returned
		url = "/aURL/someOtherPart";
		testToAbsoluteUrl(expectedHostPart + url, url);
		// A relative URL is passed in. Expect an absolute URL to be returned
		url = "aURL/someOtherPart";
		testToAbsoluteUrl(expectedHostPart + "/" + url, url);
		// A null URL passed in. Expect a null URL back
		testToAbsoluteUrl(null, null);
	}

	private void testToAbsoluteUrl(String expectedUrl, String inputUrl) {
		CustomDepositFormResultDTO response = new CustomDepositFormResultDTO();
		response.setUrlForCustomDepositForm(inputUrl);
		testInstance.toAbsoluteUrl(response);
		assertEquals(expectedUrl, response.getUrlForCustomDepositForm());
	}
}
