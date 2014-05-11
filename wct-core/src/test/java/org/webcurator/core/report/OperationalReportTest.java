package org.webcurator.core.report;

import static org.junit.Assert.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.webcurator.core.report.parameter.Parameter;
import org.webcurator.test.*;

public class OperationalReportTest extends BaseWCTTest<OperationalReport> {

	private final String[][] reportData = {
			{"Id", "Name", "Value", "Comment"},
			{"101", "Name1, Test", "100", "101 Comment"},
			{"102", "Name2; Test", "200", "102 Comment"},
			{"103", "Name3. Test", "300", "103 Comment"},
			{"104", "Name \"4\"", "400", "104 Comment"}
	};
	
	private final String expectedOutput = "Id,Name,Value,Comment\n101,\"Name1, Test\",100,101 Comment\n102,Name2; Test,200,102 Comment\n103,Name3. Test,300,103 Comment\n104,Name \"4\",400,104 Comment\n";	

	private final String expectedHTMLOutput = "<b>Test Report</b><br><br>4 results:<table border=\"1\" cellspacing=\"0\">\n<b><tr><th>Id</th><th>Name</th><th>Value</th><th>Comment</th></tr></b><tr><td>101</td><td>Name1, Test</td><td>100</td><td>101 Comment</td></tr>\n<tr><td>102</td><td>Name2; Test</td><td>200</td><td>102 Comment</td></tr>\n<tr><td>103</td><td>Name3. Test</td><td>300</td><td>103 Comment</td></tr>\n<tr><td>104</td><td>Name \"4\"</td><td>400</td><td>104 Comment</td></tr>\n</table>\n";
	
	public OperationalReportTest()
	{
		super(OperationalReport.class, "");
	}
	
	@Test
	public final void testOperationalReport() {

		String name = "Test Report";
		String info = "Some info";
		List<Parameter> parameters = new ArrayList<Parameter>();
		MockReportGenerator repGen =  new MockReportGenerator();
		repGen.setResultData(reportData);
		
		testInstance = new OperationalReport(name, info, parameters, repGen);
		assertNotNull(repGen);
		assertEquals(testInstance.getName(), name);
		assertEquals(testInstance.getInfo(), info);
		assertEquals(testInstance.getParameters(), parameters);
		assertEquals(testInstance.getReportGenerator(), repGen);
	}

	@Test
	public final void testGetRendering() {
		testOperationalReport();
		
		try
		{
			String output = testInstance.getRendering(FileFactory.CSV_FORMAT);
			assertEquals(output, expectedOutput);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try
		{
			String output = testInstance.getRendering(FileFactory.HTML_FORMAT);
			assertEquals(output, expectedHTMLOutput);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetHTMLRendering() {
		testOperationalReport();
		
		try
		{
			String output = testInstance.getHTMLRendering();
			assertEquals(output, expectedHTMLOutput);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetDownloadRendering1() {
		testOperationalReport();
		
		try
		{
			HttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse(); 

			assertFalse(resp.containsHeader("Content-Disposition"));

			testInstance.getDownloadRendering(req, resp, "report", FileFactory.CSV_FORMAT);
			
			assertTrue(resp.containsHeader("Content-Disposition"));
			assertEquals(resp.getContentAsString(), expectedOutput);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public final void testGetDownloadRendering2() {
		testOperationalReport();
		
		try
		{
			HttpServletRequest req = new MockHttpServletRequest();
			MockHttpServletResponse resp = new MockHttpServletResponse(); 
			
			String dest = resp.encodeRedirectURL(req.getContextPath() + "/curator/report/report.html");
			
			testInstance.getDownloadRendering(req, resp, "report", FileFactory.HTML_FORMAT, dest);

			assertTrue(resp.containsHeader("Content-Disposition"));
			assertEquals(resp.getContentAsString(), expectedHTMLOutput);
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

}
