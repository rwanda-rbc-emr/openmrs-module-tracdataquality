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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.tracdataquality.utils.DataQualityByCheckTypeController;
import org.openmrs.module.tracdataquality.utils.TracDataQualityUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

public class DataQualityAlertsController extends ParameterizableViewController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * defines criterias, the number of the patients in each criteria and sets them on views as
	 * alerts
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//check if the user is logged in
		if (Context.getAuthenticatedUser() == null)
			return new ModelAndView(new RedirectView(request.getContextPath() + "/login.htm"));
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName(getViewName());
		
		//start looking for data quality by checking each indicator
		Map<String, Integer> patientNumbers = new HashMap<String, Integer>();
		try {
			for (String key : TracDataQualityUtil.getCriteriaKeys()) {
				DataQualityByCheckTypeController checkingDataQuality = new DataQualityByCheckTypeController();
				patientNumbers.put(key, checkingDataQuality.checkTypeController(key, "").size());
				
			}
			mav.addObject("patientNumbers", patientNumbers);
			
		}
		catch (Exception e) {
			log.error(">>>>>TRAC>>DATA>>QUALITY>> " + e.getMessage());
			e.printStackTrace();
		}
		
		return mav;
	}
}
