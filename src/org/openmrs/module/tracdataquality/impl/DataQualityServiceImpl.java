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
package org.openmrs.module.tracdataquality.impl;

import java.util.List;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.module.tracdataquality.db.DataQualityDAO;
import org.openmrs.module.tracdataquality.service.DataQualityService;

/**
 *
 */
public class DataQualityServiceImpl implements DataQualityService {
	
	/**
	 * @see org.openmrs.module.tracdataquality.service.DataQualityService#getPatientsWithoutIdentifiers()
	 */
	
	private DataQualityDAO dataQualityDAO;
	
	public DataQualityDAO getDataQualityDAO() {
		return dataQualityDAO;
	}
	
	public void setDataQualityDAO(DataQualityDAO dataQualityDAO) {
		this.dataQualityDAO = dataQualityDAO;
	}
	
	public List<Integer> getPatientsWithoutIdentifiers(int typeId) {
		return dataQualityDAO.getPatientsWithoutIdentifiers(typeId);
	}
	
	public List<Integer> getPatientsWithoutAttribute(int typeId) {
		return dataQualityDAO.getPatientsWithoutAttribute(typeId);
	}
	
	public List<Patient> getPatientsWithoutAnObs(Concept concept) {
		return dataQualityDAO.getPatientsWithoutAnObs(concept);
	}
	
	public List<Patient> getPatientsWithObs(Concept concept) {
		return dataQualityDAO.getPatientsWithObs(concept);
	}
	
	public List<Patient> getPatientsWithNoProgram() {
		return dataQualityDAO.getPatientsWithNoProgram();
	}
	
	public List<Integer> getPatientsWithoutNames() {
		return dataQualityDAO.getPatientsWithoutNames();
	}
	
	public List<Integer> getPatientsWithoutStartDate() {
		return dataQualityDAO.getPatientsWithoutStartDate();
	}
	
	public List<Integer> getPatientsWithDiscontinuedDateHigherThanDrugStartDate() {
		return dataQualityDAO.getPatinetsWithDiscontinuedDateHigherThanDrugStartDate();
	}
	
	public List<Integer> getPatientsDrugsWithDiscontinuedDateWithoutStartDate() {
		return dataQualityDAO.getPatientsDrugsWithDiscontinuedDateWithoutStartDate();
	}
	
	public List<Integer> getPatientsWithNoProgramsEnrollmentDates() {
		return dataQualityDAO.getPatientsWithNoProgramsEnrollmentDates();
	}
	
	public List<Integer> getPatientsWhoHaveAdultWhoStage() {
		return dataQualityDAO.getPatientsWhoHaveAdultWhoStage();
	}
	
	public List<Integer> getPatientsAdultWhoHavePedsWhoStage() {
		return dataQualityDAO.getPatientsAdultWhoHavePedsWhoStage();
	}
	public List<Integer> getPatientsExitedFromCare(){
		return dataQualityDAO.getPatientsExitedFromCare();
	}
	public List<Patient> getPatientsWithoutAnObsAdmissionMode(Concept concept){
		return dataQualityDAO.getPatientsWithoutAnObsAdmissionMode(concept);
	}

	/**
     * @see org.openmrs.module.tracdataquality.service.DataQualityService#getPatientsByProgram(org.openmrs.Program)
     */
    @Override
    public List<Patient> getPatientsByProgram(Program program) {
	    return dataQualityDAO.getPatientsByProgram(program);
    }

	/**
     * @see org.openmrs.module.tracdataquality.service.DataQualityService#getPatientWhoHaveProgram()
     */
    @Override
    public List<Patient> getPatientWhoHaveProgram() {
	    return dataQualityDAO.getPatientWhoHaveProgram();
    }

	/**
     * @see org.openmrs.module.tracdataquality.service.DataQualityService#getPatientsWhoStoppedDrugWithoutDiscontinuedReason()
     */
    @Override
    public List<Integer> getPatientsWhoStoppedDrugWithoutDiscontinuedReason() {
	    return dataQualityDAO.getPatientsWhoStoppedDrugWithoutDiscontinuedReason();
    }
}
