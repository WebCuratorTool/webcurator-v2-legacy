package org.webcurator.ui.hibernate;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.webcurator.domain.HibernateTestDAO;
import org.webcurator.domain.model.core.HibernateTest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HibernateTestAddController extends AbstractController {
    private HibernateTestDAO hibernateTestDAO;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        // add random data to the hibernate test table
        HibernateTest hibernateTest = new HibernateTest();
        Integer max = hibernateTestDAO.maxID();
        hibernateTest.setId(max + 1);
        UUID uuid = UUID.randomUUID();
        String column_1_data = uuid.toString();
        hibernateTest.setColumn1(column_1_data);
        Date now = new Date();
        hibernateTest.setColumn2(now.toString());
        hibernateTestDAO.saveOrUpdate(hibernateTest);
        // update model
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
