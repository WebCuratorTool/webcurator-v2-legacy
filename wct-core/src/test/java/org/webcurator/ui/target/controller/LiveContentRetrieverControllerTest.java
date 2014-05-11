package org.webcurator.ui.target.controller;

import static org.junit.Assert.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.domain.model.core.TargetInstance;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.common.Constants;
import org.webcurator.ui.target.command.LiveContentRetrieverCommand;
import org.webcurator.ui.target.command.LogReaderCommand;

public class LiveContentRetrieverControllerTest extends BaseWCTTest<LiveContentRetrieverController>{

	public LiveContentRetrieverControllerTest()
	{
		super(LiveContentRetrieverController.class, 
				"src/test/java/org/webcurator/ui/target/controller/logreadercontrollertest.xml");
	}
	
	
	@Test
	@Ignore
	public final void testHandle() {
		try
		{
			MockHttpServletRequest aReq = new MockHttpServletRequest();
			MockHttpServletResponse aResp = new MockHttpServletResponse(); 
			LiveContentRetrieverCommand aCmd = new LiveContentRetrieverCommand();
			
			aCmd.setUrl("http://www.bl.uk");
			aCmd.setContentFileName("test.html");
			
			BindException aErrors = new BindException(aCmd, "LiveContentRetrieverCommand");
			
			ModelAndView mav = testInstance.handle(aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertEquals(mav.getModel().size(), 0);
			assertTrue(mav.getView() instanceof AttachmentView);
			AttachmentView view = (AttachmentView)mav.getView();
			
			view.render(null, aReq, aResp);
			
			assertTrue(aResp.getHeader("Content-Disposition").toString().endsWith("test.html"));
			assertTrue(aResp.getContentAsString().contains("British Library"));
			
			assertFalse(aErrors.hasErrors());
		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

}
