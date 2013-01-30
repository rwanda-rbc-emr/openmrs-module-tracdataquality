/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.tracdataquality.web.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.tracdataquality.utils.DataQualityByCheckTypeController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 
 */
public class DataqualityFormController extends ParameterizableViewController {
	
	static String msgToDisplay;
	
	static Object[] msgArguments;
	
	/**
	 * @return the msgToDisplay
	 */
	public static String getMsgToDisplay() {
		return msgToDisplay;
	}
	
	/**
	 * @param msgToDisplay the msgToDisplay to set
	 */
	public static void setMsgToDisplay(String msgToDisplay) {
		DataqualityFormController.msgToDisplay = msgToDisplay;
	}
	
	/**
	 * @param msgToDisplay the msgToDisplay to set
	 */
	public static void setMsgArguments(String msgToDisplay) {
		DataqualityFormController.msgToDisplay = msgToDisplay;
	}
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (Context.getAuthenticatedUser() == null)
			return new ModelAndView(new RedirectView(request.getContextPath() + "/login.htm"));
		
		//Session session = getSessionFactory().getCurrentSession();
		ModelAndView mav = new ModelAndView();
		mav.setViewName(getViewName());
		
		//getting paramenter
		String programIdKey = request.getParameter("checkType");
		String valueRangeType = request.getParameter("createriaValue");
		
		try{
			//defining some createria which should be resend to the same page for the user to add some other selection createrias
			DataQualityByCheckTypeController checkingDataQuality = new DataQualityByCheckTypeController();
			List<Patient> patients = new ArrayList<Patient>();
			patients = checkingDataQuality.checkTypeController(programIdKey, valueRangeType);
			
			//setting necessary ressources for the view
			mav.addObject("msgToDisplay", getMsgToDisplay());
			mav.addObject("thePatientList", patients);
			mav.addObject("checkType", programIdKey);
			mav.addObject("valueRangeType", valueRangeType);
		}catch (Exception e) {
			log.error(">>>>>TRAC>>DATA>>QUALITY>> " + e.getMessage());
			e.printStackTrace();
		}
		//    	    mav.setViewName("module/tracdataquality/dataQualitySuccess");
		
		return mav;
	}
}
