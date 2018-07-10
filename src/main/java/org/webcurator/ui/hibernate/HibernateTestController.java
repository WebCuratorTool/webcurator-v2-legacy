package org.webcurator.ui.hibernate;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.webcurator.domain.HibernateTestDAO;
import org.webcurator.domain.model.HibernateTest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class HibernateTestController extends AbstractController {
    private HibernateTestDAO hibernateTestDAO;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ModelAndView mav = new ModelAndView();
        List<HibernateTest> hibernateTests = hibernateTestDAO.getAll();
        mav.addObject("hibernateTests", hibernateTests);
        mav.setViewName("HibernateTestView");
        return mav;
    }

    public void setHibernateTestDAO(HibernateTestDAO hibernateTestDAO) {
        this.hibernateTestDAO = hibernateTestDAO;
    }
}
