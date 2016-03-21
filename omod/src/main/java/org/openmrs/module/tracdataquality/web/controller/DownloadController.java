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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.tracdataquality.utils.ContextProvider;
import org.openmrs.module.tracdataquality.utils.DataQualityByCheckTypeController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 */
public class DownloadController extends AbstractController {
	
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
		
		//getting necessary parameters
		String programIdKey = request.getParameter("checkType");
		String valueRangeType = request.getParameter("createriaValue");
		
		try {
			//getting patients responding to criteria asked
			DataQualityByCheckTypeController checkingDataQuality = new DataQualityByCheckTypeController();
			List<Patient> patients = new ArrayList<Patient>();
			patients = checkingDataQuality.checkTypeController(programIdKey, valueRangeType);
			
			//building the fileName based on current time and the name of the indicator
			String timeNow = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss").format(new Date());
			String fileName = ((request.getParameter("checkType") != null) ? request.getParameter("checkType") + "_"
			        : "dataQualityReport_")
			        + timeNow + ".csv";
			
			//download the report in csv file
			doDownload(request, response, patients, fileName, ContextProvider.getMessage("tracdataquality.indicator."
			        + programIdKey));
		}
		catch (Exception e) {
			log.info(">>>>>TRAC>>DATA>>QUALITY>> " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param request
	 * @param response
	 * @param patients
	 * @param filename
	 * @param title
	 * @throws IOException
	 */
	private void doDownload(HttpServletRequest request, HttpServletResponse response, List<Patient> patients,
	                        String filename, String title) throws IOException {
		
		//creating file writer object
		ServletOutputStream outputStream = response.getOutputStream();
		
		//creating the file
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		
		//creating file header
		outputStream.println("Report Name, " + title);
		outputStream.println("Author, " + Context.getAuthenticatedUser().getPersonName());
		outputStream.println("Printed on, " + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date()));
		outputStream.println("Number of Patients, " + patients.size());
		outputStream.println();
		outputStream.println("Identifier,Given Name,Middle Name,Family Name,Age,Gender,Creator");
		outputStream.println();
		
		//populating content of the report
		for (Patient patient : patients) {
			outputStream.println(patient.getPatientIdentifier() + "," + patient.getGivenName() + ","
			        + patient.getMiddleName() + "," + patient.getFamilyName() + "," + patient.getAge() + ","
			        + patient.getGender() + "," + patient.getCreator());
		}
		
		outputStream.flush();
		outputStream.close();
	}
	
}
