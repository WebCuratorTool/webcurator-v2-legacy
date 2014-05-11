package org.webcurator.ui.tools.controller;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.auth.AuthorityManagerImpl;
import org.webcurator.core.scheduler.MockTargetInstanceManager;
import org.webcurator.core.targets.MockTargetManager;
import org.webcurator.domain.MockTargetInstanceDAO;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.site.command.DefaultSiteCommand;
import org.webcurator.ui.tools.command.QualityReviewToolCommand;


public class QualityReviewToolControllerTest extends BaseWCTTest<QualityReviewToolController>{

	public QualityReviewToolControllerTest()
	{
		super(QualityReviewToolController.class, "src/test/java/org/webcurator/ui/tools/controller/qualityreviewtoolcontrollertest.xml");
	}
	AuthorityManagerImpl authorityManager;
	
    //Override BaseWCTTest setup method
	public void setUp() throws Exception {
		//call the overridden method as well
		super.setUp();
		
		//add the extra bits
		testInstance.setArchiveUrl("archiveURL");
		testInstance.setEnableBrowseTool(true);
		testInstance.setEnableAccessTool(true);
		testInstance.setTargetManager(new MockTargetManager(testFile));
		testInstance.setTargetInstanceDao(new MockTargetInstanceDAO(testFile));
		testInstance.setTargetInstanceManager(new MockTargetInstanceManager(testFile));

		HarvestResourceUrlMapper harvestResourceUrlMapper = new HarvestResourceUrlMapper();
		harvestResourceUrlMapper.setUrlMap("http://test?url={$HarvestResource.Name}");
		testInstance.setHarvestResourceUrlMapper(harvestResourceUrlMapper);
	}
	

	
	@Test
	public final void testHandle() {
		try
		{
			ModelAndView mav = getHandelMav();
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("quality-review-toc"));
			List<SeedMapElement> seeds = (List<SeedMapElement>)mav.getModel().get(QualityReviewToolCommand.MDL_SEEDS);
			assertTrue(seeds.size() == 1 );
			Iterator<SeedMapElement> it = seeds.iterator();
			assertTrue(it.hasNext());
			SeedMapElement sme = it.next();
			assertEquals("http://www.oakleigh.co.uk/", sme.getSeed());
			assertEquals("curator/tools/browse/111000/http://www.oakleigh.co.uk/", sme.getBrowseUrl());
			assertEquals("http://test?url=http://www.oakleigh.co.uk/", sme.getAccessUrl());
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public final void testHandleWithAccessToolDiasbled() {
		try
		{
			testInstance.setEnableAccessTool(false);
			
			ModelAndView mav = getHandelMav();
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("quality-review-toc"));
			List<SeedMapElement> seeds = (List<SeedMapElement>)mav.getModel().get(QualityReviewToolCommand.MDL_SEEDS);
			assertTrue(seeds.size() == 1 );
			Iterator<SeedMapElement> it = seeds.iterator();
			assertTrue(it.hasNext());
			SeedMapElement sme = it.next();
			assertEquals("http://www.oakleigh.co.uk/", sme.getSeed());
			assertEquals("curator/tools/browse/111000/http://www.oakleigh.co.uk/", sme.getBrowseUrl());
			assertEquals("", sme.getAccessUrl());
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public final void testHandleWithBrowseToolDiasbled() {
		try
		{
			testInstance.setEnableBrowseTool(false);
			
			ModelAndView mav = getHandelMav();
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("quality-review-toc"));
			List<SeedMapElement> seeds = (List<SeedMapElement>)mav.getModel().get(QualityReviewToolCommand.MDL_SEEDS);
			assertTrue(seeds.size() == 1 );
			Iterator<SeedMapElement> it = seeds.iterator();
			assertTrue(it.hasNext());
			SeedMapElement sme = it.next();
			assertEquals("http://www.oakleigh.co.uk/", sme.getSeed());
			assertEquals("", sme.getBrowseUrl());
			assertEquals("http://test?url=http://www.oakleigh.co.uk/", sme.getAccessUrl());
			
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	private ModelAndView getHandelMav() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		QualityReviewToolCommand comm = new QualityReviewToolCommand();
		comm.setHarvestResultId(111000L);
		comm.setTargetInstanceOid(5000L);
		
		BindException aError = new BindException(new DefaultSiteCommand(), null);
		ModelAndView mav = testInstance.handle(request, response, comm, aError);
		return mav;
	}

	
}
