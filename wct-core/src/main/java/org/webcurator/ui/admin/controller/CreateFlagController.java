/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.ui.admin.controller;

import java.awt.Color;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.webcurator.auth.AuthorityManager;
import org.webcurator.core.agency.AgencyUserManager;
import org.webcurator.core.util.AuthUtil;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.Privilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.Flag;
import org.webcurator.ui.admin.command.CreateFlagCommand;
import org.webcurator.ui.admin.command.FlagCommand;
import org.webcurator.ui.common.Constants;

/**
 * Manages the creation flow for a Rejection Reason within WCT
 * @author oakleigh_sk
 */
public class CreateFlagController extends AbstractFormController {
	/** the logger. */
    private Log log = null;
    /** the agency user manager. */
    private AgencyUserManager agencyUserManager = null;
    /** the authority manager. */
    private AuthorityManager authorityManager = null;
    /** the message source. */
    private MessageSource messageSource = null;
    
    /** Default Constructor. */
    public CreateFlagController() {
        log = LogFactory.getLog(CreateFlagController.class);
        setCommandClass(CreateFlagCommand.class);
    }
    
    @Override
    public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    	// enable null values for long and float fields
        NumberFormat nf = NumberFormat.getInstance(request.getLocale());
        binder.registerCustomEditor(java.lang.Long.class, new CustomNumberEditor(java.lang.Long.class, nf, true));   
        binder.registerCustomEditor(java.lang.Float.class, new CustomNumberEditor(java.lang.Float.class, nf, true));   
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest arg0,
            HttpServletResponse arg1, BindException arg2) throws Exception {
        
        return null;
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest aReq,
            HttpServletResponse aRes, Object aCommand, BindException aError)
            throws Exception {

        ModelAndView mav = null;
        CreateFlagCommand flagCmd = (CreateFlagCommand) aCommand;
        
            
        if (flagCmd != null) {
            if (aError.hasErrors()) {
                mav = new ModelAndView();
                List agencies = agencyUserManager.getAgenciesForLoggedInUser();
                mav.addObject(CreateFlagCommand.MDL_AGENCIES, agencies);

                String mode = flagCmd.getMode();
                if (CreateFlagCommand.ACTION_EDIT.equals(mode)) {
                    mav.addObject(CreateFlagCommand.ACTION_EDIT, mode);
                }
                mav.addObject(Constants.GBL_CMD_DATA, aError.getTarget());
                mav.addObject(Constants.GBL_ERRORS, aError);
                mav.setViewName("newFlag");
                
            } else if (CreateFlagCommand.ACTION_NEW.equals(flagCmd.getAction())) {
                mav = new ModelAndView();
                List agencies = agencyUserManager.getAgenciesForLoggedInUser();
                mav.addObject(CreateFlagCommand.MDL_AGENCIES, agencies);
                mav.setViewName("newFlag");
                
            } else if (CreateFlagCommand.ACTION_VIEW.equals(flagCmd.getAction()) ||
            		CreateFlagCommand.ACTION_EDIT.equals(flagCmd.getAction())) {
                //View/Edit an existing flag
                mav = new ModelAndView();
                Long flagOid = flagCmd.getOid(); 
                Flag flag = agencyUserManager.getFlagByOid(flagOid);
                CreateFlagCommand editCmd = new CreateFlagCommand();
                editCmd.setOid(flagOid);
                editCmd.setAgencyOid(flag.getAgency().getOid());
                editCmd.setName(flag.getName());
                editCmd.setRgb(flag.getRgb());
                editCmd.setMode(flagCmd.getAction());
                
                List agencies = agencyUserManager.getAgenciesForLoggedInUser();
                mav.addObject(CreateFlagCommand.MDL_AGENCIES, agencies);
                mav.addObject(Constants.GBL_CMD_DATA, editCmd);
                mav.setViewName("newFlag");
                
            } else if (CreateFlagCommand.ACTION_SAVE.equals(flagCmd.getAction())) {
                
                
                    try {
                        Flag flag = new Flag();
                        boolean update = (flagCmd.getOid() != null);
                        if (update == true) {
                            // Update an existing flag object by loading it in first
                        	flag = agencyUserManager.getFlagByOid(flagCmd.getOid());
                        } else {
                        	// Save the newly created flag object
                        
                            //load Agency
                            Long agencyOid = flagCmd.getAgencyOid();
                            Agency agency = agencyUserManager.getAgencyByOid(agencyOid);
                            flag.setAgency(agency);
                        }
                        
                        flag.setRgb(flagCmd.getRgb());
                        String complementColour = null;
                        if (flagCmd.getRgb().equals("000000")) {
                        	complementColour = "ffffff";
                        } else {
                        	complementColour = getComplementColour(flagCmd.getRgb());
                        }
                        flag.setComplementRgb(complementColour);
                        flag.setName(flagCmd.getName());
                        
                        agencyUserManager.updateFlag(flag, update);
                        
                        List flags = agencyUserManager.getFlagForLoggedInUser();
                        List agencies = null;
                        if (authorityManager.hasPrivilege(Privilege.MANAGE_FLAGS, Privilege.SCOPE_ALL)) {
                        	agencies = agencyUserManager.getAgencies();
                        } else {
                            User loggedInUser = AuthUtil.getRemoteUserObject();
                            Agency usersAgency = loggedInUser.getAgency();
                            agencies = new ArrayList<Agency>();
                            agencies.add(usersAgency);
                        }
                        
                        mav = new ModelAndView();
                        String message;
                        if (update == true) {
                            message = messageSource.getMessage("flag.updated", new Object[] { flagCmd.getName() }, Locale.getDefault());
                        } else {
                            message = messageSource.getMessage("flag.created", new Object[] { flagCmd.getName() }, Locale.getDefault());
                        }
                        String agencyFilter = (String)aReq.getSession().getAttribute(FlagCommand.MDL_AGENCYFILTER);
                        if(agencyFilter == null)
                        {
                        	agencyFilter = AuthUtil.getRemoteUserObject().getAgency().getName();
                        }
                        mav.addObject(FlagCommand.MDL_AGENCYFILTER, agencyFilter);
                        mav.addObject(FlagCommand.MDL_LOGGED_IN_USER, AuthUtil.getRemoteUserObject());
                        mav.addObject(FlagCommand.MDL_FLAGS, flags);
                        mav.addObject(FlagCommand.MDL_AGENCIES, agencies);

                        mav.addObject(Constants.GBL_MESSAGES, message );

                        mav.setViewName("viewFlags");
                    }
                    catch (DataAccessException e) {
                    	e.printStackTrace();
                    }     
                
            }
        } else {
            log.warn("No Action provided for CreateFlagController.");
        }
            
        return mav;
    }

	public String getComplementColour(String hexColour) throws JspException  {
		
		String output = null;
		Integer i = null;
		
		if (hexColour == null || hexColour.equals("")) {
			i = 0;
		} else {
			i = Integer.parseInt(hexColour, 16);
		}
		
    	int red = (i >> 16);
    	int green = (i >> 8) & 0xFF;
    	int blue = i & 0xFF;
		
		float[] hsl;
		float alpha;
		
		Color rgb = new Color(red, green, blue);
		hsl = fromRGB( rgb );
		alpha = rgb.getAlpha() / 255.0f;
		
		float hue = (hsl[0] + 180.0f) % 360.0f;
		Color complement = toRGB(hue, hsl[1], hsl[2], 1.0f);
		
		output = rgb2hex(complement.getRed(), complement.getGreen(), complement.getBlue());
		
		return output;
	}
 	
	/**
	 *  Convert a RGB Color to it corresponding HSL values.
	 *
	 *  @return an array containing the 3 HSL values.
	 */
	public static float[] fromRGB(Color color)
	{
		//  Get RGB values in the range 0 - 1

		float[] rgb = color.getRGBColorComponents( null );
		float r = rgb[0];
		float g = rgb[1];
		float b = rgb[2];

		//	Minimum and Maximum RGB values are used in the HSL calculations

		float min = Math.min(r, Math.min(g, b));
		float max = Math.max(r, Math.max(g, b));

		//  Calculate the Hue

		float h = 0;

		if (max == min)
			h = 0;
		else if (max == r)
			h = ((60 * (g - b) / (max - min)) + 360) % 360;
		else if (max == g)
			h = (60 * (b - r) / (max - min)) + 120;
		else if (max == b)
			h = (60 * (r - g) / (max - min)) + 240;

		//  Calculate the Luminance

		float l = (max + min) / 2;
		//System.out.println(max + " : " + min + " : " + l);

		//  Calculate the Saturation

		float s = 0;

		if (max == min)
			s = 0;
		else if (l <= .5f)
			s = (max - min) / (max + min);
		else
			s = (max - min) / (2 - max - min);

		return new float[] {h, s * 100, l * 100};
	}

	/**
	 *  Convert HSL values to a RGB Color.
	 *
	 *  @param h Hue is specified as degrees in the range 0 - 360.
	 *  @param s Saturation is specified as a percentage in the range 1 - 100.
	 *  @param l Lumanance is specified as a percentage in the range 1 - 100.
	 *  @param alpha  the alpha value between 0 - 1
	 *
	 *  @returns the RGB Color object
	 */
	public static Color toRGB(float h, float s, float l, float alpha)
	{
		if (s <0.0f || s > 100.0f)
		{
			String message = "Color parameter outside of expected range - Saturation";
			throw new IllegalArgumentException( message );
		}

		if (l <0.0f || l > 100.0f)
		{
			String message = "Color parameter outside of expected range - Luminance";
			throw new IllegalArgumentException( message );
		}

		if (alpha <0.0f || alpha > 1.0f)
		{
			String message = "Color parameter outside of expected range - Alpha";
			throw new IllegalArgumentException( message );
		}

		//  Formula needs all values between 0 - 1.

		h = h % 360.0f;
		h /= 360f;
		s /= 100f;
		l /= 100f;

		float q = 0;

		if (l < 0.5)
			q = l * (1 + s);
		else
			q = (l + s) - (s * l);

		float p = 2 * l - q;

		float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
		float g = Math.max(0, HueToRGB(p, q, h));
		float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

		r = Math.min(r, 1.0f);
		g = Math.min(g, 1.0f);
		b = Math.min(b, 1.0f);

		return new Color(r, g, b, alpha);
	}

	private static float HueToRGB(float p, float q, float h)
	{
		if (h < 0) h += 1;

		if (h > 1 ) h -= 1;

		if (6 * h < 1)
		{
			return p + ((q - p) * 6 * h);
		}

		if (2 * h < 1 )
		{
			return  q;
		}

		if (3 * h < 2)
		{
			return p + ( (q - p) * 6 * ((2.0f / 3.0f) - h) );
		}

   		return p;
	}

    private String rgb2hex(int r, int g, int b) {
    	String rs = Integer.toHexString(r);
    	String gs = Integer.toHexString(g);
    	String bs = Integer.toHexString(b);
    	if (rs.length() == 1) rs = "0" + rs;
    	if (gs.length() == 1) gs = "0" + gs;
    	if (bs.length() == 1) bs = "0" + bs;
    	return (rs + gs + bs);
    }

    
    
    /** 
     * @param agencyUserManager the agency user manager.
     */
    public void setAgencyUserManager(AgencyUserManager agencyUserManager) {
        this.agencyUserManager = agencyUserManager;
    }

    /** 
     * @param messageSource the message source.
     */
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
	 * Spring setter method for the Authority Manager.
	 * @param authorityManager The authorityManager to set.
	 */
	public void setAuthorityManager(AuthorityManager authorityManager) {
		this.authorityManager = authorityManager;
	}
}
