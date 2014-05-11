package org.springframework.web.context;

import javax.servlet.ServletContext;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.ui.context.*;
import org.springframework.ui.context.support.DelegatingThemeSource;
import org.springframework.mock.web.MockServletContext;

import org.webcurator.core.common.*;
 
public class MockWebApplicationContext extends GenericApplicationContext implements WebApplicationContext
{
    ServletContext sc;
    DelegatingThemeSource themeSource;
 
    public MockWebApplicationContext() {
        this.sc = new MockServletContext();
    }

    public ServletContext getServletContext() {
        return this.sc;
    }
    
    public Theme getTheme(String themeName)
    {
    	return themeSource.getTheme(themeName);
    }
    
    //TODO: Drive this class from an XML file
    public Object getBean(String beanName)
    {
    	Object retObj = null;
    	
    	if(beanName.equals("environment"))
    	{
    		EnvironmentImpl env = new EnvironmentImpl();
    		
    		env.setApplicationVersion("Test");
    		env.setDaysToSchedule(1);

    		retObj = (Object)env;
    	}
    	else
    	{
    		throw new RuntimeException("MockWebApplicationContext does not support the bean name '"+beanName+"'.");
    	}
    	
    	return retObj;
    }
 }

