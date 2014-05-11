package org.webcurator.ui.site.controller;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.*;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.webcurator.test.BaseWCTTest;
import org.webcurator.ui.site.SiteEditorContext;
import org.webcurator.ui.site.command.*;
import org.webcurator.core.sites.*;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.core.*;

public class SitePermissionControllerTest extends BaseWCTTest<SitePermissionController>{

	public SitePermissionControllerTest()
	{
		super(SitePermissionController.class,
				"src/test/java/org/webcurator/ui/site/controller/sitegeneralhandlertest.xml");
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.UK);

	private final String[][] ANNOTATIONS = {
			{"4", "01-APR-2001 00:00:00"},
			{"2", "01-FEB-2001 00:00:00"},
			{"1", "01-JAN-2001 00:00:00"},
			{"3", "01-MAR-2001 00:00:00"}
	};
	private Annotation createAnnotation(String note, String date) throws ParseException
	{
		Annotation ann = new Annotation();
		
		ann.setDate(sdf.parse(date));
		ann.setNote(note);
		ann.setUser(AuthUtil.getRemoteUserObject());
		return ann;
	}
	
	private List<Annotation> createAnnotationList() throws ParseException
	{
		List<Annotation> list = new ArrayList<Annotation>();
		for(int i = 0; i < ANNOTATIONS.length; i++)
		{
			list.add(createAnnotation(ANNOTATIONS[i][0], ANNOTATIONS[i][1]));
		}
		return list;
	}
	
	private boolean checkSortedList(List<Annotation> list) throws ParseException
	{
		Date lastDate = sdf.parse("01-JAN-2070 00:00:00");
		Annotation[] array = list.toArray(new Annotation[list.size()]);
		assertEquals(array.length, list.size());
		for(int i = 0; i < array.length; i++)
		{
			if(array[i].getDate().after(lastDate))
			{
				return false;
			}
			
			lastDate = array[i].getDate();
		}
		
		return true;
	}
	
	
	@Test
	public final void testHandle() {
		try
		{
			HttpServletRequest aReq = new MockHttpServletRequest();
			SiteManager siteManager = new MockSiteManagerImpl(testFile);
			
			Site site = siteManager.getSite(9000L, true);
			SiteEditorContext ctx = new SiteEditorContext(site);
			HttpServletResponse aResp = new MockHttpServletResponse(); 
			SitePermissionCommand aCmd = new SitePermissionCommand();
	
			aCmd.setActionCmd(SitePermissionCommand.ACTION_ADD_NOTE);
			aCmd.setNote("A note");
			
			Iterator<Permission> it = site.getPermissions().iterator();
			assertTrue(it.hasNext());
			Permission p = it.next();
			List<Annotation> list = createAnnotationList(); 
			assertFalse(checkSortedList(list));
			p.setAnnotations(list);
			
			ctx.putObject(p);
			aCmd.setIdentity(p.getIdentity());
			
			aReq.getSession().setAttribute(SiteController.EDITOR_CONTEXT, ctx);
			
			BindException aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			int numAnnotations = p.getAnnotations().size();
			
			ModelAndView mav = testInstance.handle(aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site-permissions"));
			assertTrue(((Permission)mav.getModel().get("permission")).equals(p));
			int listSize = p.getAnnotations().size(); 
			assertTrue(listSize > 0);
			int noteIndex = 0;
			assertTrue(p.getAnnotations().size() == (numAnnotations+1));
			assertTrue(p.getAnnotations().get(noteIndex).getNote().equals("A note"));
			assertTrue(checkSortedList(p.getAnnotations()));
			
			aCmd.setActionCmd(SitePermissionCommand.ACTION_MODIFY_NOTE);
			aCmd.setNote("A new note");
			aCmd.setNoteIndex(noteIndex);
			aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			mav = testInstance.handle(aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site-permissions"));
			assertTrue(((Permission)mav.getModel().get("permission")).equals(p));
			listSize = p.getAnnotations().size(); 
			assertTrue(listSize > 0);
			int newNoteIndex = 0;
			assertTrue(newNoteIndex == noteIndex);
			assertTrue(p.getAnnotations().size() == (numAnnotations+1));
			assertFalse(p.getAnnotations().get(noteIndex).getNote().equals("A note"));
			assertTrue(p.getAnnotations().get(noteIndex).getNote().equals("A new note"));
			assertTrue(checkSortedList(p.getAnnotations()));
			
			aCmd.setActionCmd(SitePermissionCommand.ACTION_DELETE_NOTE);
			aCmd.setNoteIndex(noteIndex);
			aErrors = new BindException(aCmd, aCmd.getActionCmd());
			
			mav = testInstance.handle(aReq, aResp, aCmd, aErrors);
			assertTrue(mav != null);
			assertTrue(mav.getViewName().equals("site-permissions"));
			assertTrue(((Permission)mav.getModel().get("permission")).equals(p));
			int newListSize = p.getAnnotations().size(); 
			assertTrue(newListSize == (listSize-1));
			assertTrue(p.getAnnotations().size() == numAnnotations);
			assertTrue(checkSortedList(p.getAnnotations()));
		}
		catch(Exception e)
		{
			fail(e.getClass().getName()+" - "+e.getMessage());
		}
	}

}
