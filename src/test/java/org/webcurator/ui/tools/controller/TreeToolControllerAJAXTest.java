package org.webcurator.ui.tools.controller;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.core.store.MockDigitalAssetStore;
import org.webcurator.core.store.tools.QualityReviewFacade;
import org.webcurator.core.store.tools.WCTNode;
import org.webcurator.core.store.tools.WCTNodeTree;
import org.webcurator.core.store.tools.tree.Node;
import org.webcurator.core.util.TestAuditor;
import org.webcurator.domain.MockTargetInstanceDAO;
import org.webcurator.domain.TargetInstanceDAO;
import org.webcurator.domain.model.core.HarvestResource;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.admin.command.AgencyCommand;
import org.webcurator.ui.tools.command.TreeToolCommand;

public class TreeToolControllerAJAXTest extends BaseWCTTest<TreeToolControllerAJAX> {

	private TargetInstanceDAO tidao;
	private QualityReviewFacade qrf;
	private MockHttpServletRequest aReq;
	private HttpServletResponse aResp;
	private TreeToolCommand aCmd;
	private BindException aErrors;
	private String viewName = "TreeToolAJAX";

	public TreeToolControllerAJAXTest() {
		super(
				TreeToolControllerAJAX.class,
				"src/test/java/org/webcurator/ui/tools/controller/TreeToolControllerTest.xml");

	}

	// Override BaseWCTTest setup method
	public void setUp() throws Exception {
		// call the overridden method as well

		super.setUp();
		tidao = new MockTargetInstanceDAO(testFile);
		qrf = new QualityReviewFacade();
		qrf.setTargetInstanceDao(tidao);
		qrf.setAuditor(new TestAuditor());
		qrf.setDigialAssetStore(new MockDigitalAssetStore());
		// just set up one request (this will then keep the session)
		aReq = new MockHttpServletRequest();

		testInstance.setQualityReviewFacade(qrf);

	}


		/* The XML returns the following tree structure for reference (in brackets is the index/id of the node):
		 * -Harvest (1)----------------------------------------
		 * --http://www.oakleigh.co.uk/(2)------------------------
		 * ---http://www.oakleigh.co.uk/About-Us (11)
		 * ---http://www.oakleigh.co.uk/Accessibility (7)
		 * ---http://www.oakleigh.co.uk/Careers (8)
		 * ---http://www.oakleigh.co.uk/Central%20Government%20Agencies (16)
		 * ---http://www.oakleigh.co.uk/Customise (18)
		 * ---http://www.oakleigh.co.uk/email.form (15)
		 * ---http://www.oakleigh.co.uk/Experience (12)
		 * ---http://www.oakleigh.co.uk/Healthcare (17)
		 * ---http://www.oakleigh.co.uk/Higher%20Education (9)
		 * ---http://www.oakleigh.co.uk/Homepage (6)
		 * ---http://www.oakleigh.co.uk/images/ (3)
		 * ----http://www.oakleigh.co.uk/images/getLatestWhitePaper.gif (19)
		 * ----http://www.oakleigh.co.uk/images/goButton.gif (14)
		 * ----http://www.oakleigh.co.uk/images/goOnWhite.gif (20)
		 * ----http://www.oakleigh.co.uk/images/icon-navArrow.gif (5)
		 * ----http://www.oakleigh.co.uk/images/logo.gif (10)
		 * ----http://www.oakleigh.co.uk/images/minusIcon.gif (4)
		 * ----http://www.oakleigh.co.uk/images/submit.gif (13)
		 * ---http://www.oakleigh.co.uk/White-papers (21)
		 */


	private final void setUpHandelParameters() {
		// set up command:
		aResp = new MockHttpServletResponse();
		aCmd = new TreeToolCommand();
		aErrors = new BindException(aCmd, aCmd.getActionCmd());
	}

	private final void setUpAndTestFirstRequest() {
		try {
			setUpHandelParameters();
			aCmd.setLoadTree((long) 111000);

			// test handle:
			ModelAndView mav = testInstance.handle(aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals(viewName));
			// test tree session variable:
			WCTNodeTree tree = (WCTNodeTree) aReq.getSession().getAttribute(
					"tree");
			assertTrue(tree != null);
		} catch (Exception e) {
			fail(e.getClass().getName() + " - " + e.getMessage());
		}
	}
	@Test
	public final void testInitBinder() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestDataBinder binder = new ServletRequestDataBinder(new AgencyCommand(), "command");
		try
		{
			testInstance.initBinder(request, binder);
		}
		catch (Exception e)
		{
			String message = e.getClass().toString() + " - " + e.getMessage();
			log.debug(message);
			fail(message);
		}
	}
	
	@Test
	public final void testHandelAction() {
		try {
			// testing first-time request functionality ****************
			setUpAndTestFirstRequest();

			// testing toggle functionality ****************

			// set up new command (Don't set aCmd.setLoadTree((long)111000) to
			// ensure session variable is being picked up )
			setUpHandelParameters();
			aCmd.setActionCmd(TreeToolCommand.ACTION_TREE_ACTION);
			aCmd.setToggleId((long) 2);
			// save original toggle state to variable
			WCTNodeTree tree = (WCTNodeTree) aReq.getSession().getAttribute(
					"tree");
			Node<HarvestResource> node = tree.getNodeCache().get((long) 2);
			Boolean isopen = node.isOpen();
			// test handle
			ModelAndView mav = testInstance.handle(aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals(viewName));
			// test tree session variable:
			tree = (WCTNodeTree) aReq.getSession().getAttribute("tree");
			assertTrue(tree != null);
			// test the the toggle action as been done:
			node = tree.getNodeCache().get((long) 2);
			assertTrue(node.isOpen() != isopen);

			// testing delete (no propagate) functionality ****************
			// set up new command (to clear toggle command)
			setUpHandelParameters();
			aCmd.setActionCmd(TreeToolCommand.ACTION_TREE_ACTION);
			// supply delete parameters
			aCmd.setMarkForDelete((long) 21);
			aCmd.setPropagateDelete(false);
			// test handle
			mav = testInstance.handle(aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals(viewName));
			// test tree session variable:
			tree = (WCTNodeTree) aReq.getSession().getAttribute("tree");
			assertTrue(tree != null);
			// test the the toggle action as been done:
			WCTNode wctnode = (WCTNode) tree.getNodeCache().get((long) 21);
			assertTrue(wctnode.isMarkedForDelete());
			assertTrue(tree.getPrunedNodes().contains(wctnode));

			// testing delete (propagate) functionality ****************
			// set up new command (to clear last delete command)
			setUpHandelParameters();
			aCmd.setActionCmd(TreeToolCommand.ACTION_TREE_ACTION);
			// supply delete parameters
			aCmd.setMarkForDelete((long) 3);
			aCmd.setPropagateDelete(true);
			// test handle
			mav = testInstance.handle(aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals(viewName));
			// test tree session variable:
			tree = (WCTNodeTree) aReq.getSession().getAttribute("tree");
			assertTrue(tree != null);
			// test the the toggle action as been done on parent:
			wctnode = (WCTNode) tree.getNodeCache().get((long) 3);
			assertTrue(wctnode.isMarkedForDelete());
			assertTrue(tree.getPrunedNodes().contains(wctnode));
			// test the the toggle action as been done on kids:
			for (Node<HarvestResource> inode1 : wctnode.getChildren()) {
				WCTNode wctnode1 = (WCTNode) tree.getNodeCache().get(
						(inode1.getId()));
				assertTrue(wctnode1.isMarkedForDelete());
				assertTrue(tree.getPrunedNodes().contains(wctnode1));
			}
		} catch (Exception e) {
			fail(e.getClass().getName() + " - " + e.getMessage());
		}
	}








}
