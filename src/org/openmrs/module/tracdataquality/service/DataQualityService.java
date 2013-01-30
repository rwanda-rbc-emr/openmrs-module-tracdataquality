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
package org.openmrs.module.tracdataquality.service;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 */
@Transactional
public interface DataQualityService {

	@Transactional(readOnly=true)
	public List<Integer> getPatientsWithoutIdentifiers(int typeId);
	
	@Transactional(readOnly=true)
	public List<Integer> getPatientsWithoutAttribute(int typeId);
	
	@Transactional(readOnly=true)
	public List<Patient> getPatientsWithoutAnObs(Concept concept);
	
	@Transactional(readOnly=true)
	public List<Patient> getPatientsWithObs(Concept concept);
	
	@Transactional(readOnly=true)
	public List<Patient> getPatientsWithNoProgram();
	@Transactional(readOnly=true)
	public List<Integer> getPatientsWithoutNames();
	@Transactional(readOnly=true)
	public List<Integer> getPatientsWithoutStartDate();
	@Transactional(readOnly=true)
	public List<Integer> getPatientsWithDiscontinuedDateHigherThanDrugStartDate();
	@Transactional(readOnly=true)
	public List<Integer> getPatientsDrugsWithDiscontinuedDateWithoutStartDate();
	@Transactional(readOnly=true)
	public List<Integer> getPatientsWithNoProgramsEnrollmentDates();
	@Transactional(readOnly=true)
	public List<Integer> getPatientsWhoHaveAdultWhoStage();
	@Transactional(readOnly=true)
	public List<Integer> getPatientsAdultWhoHavePedsWhoStage();
	@Transactional(readOnly=true)
	public List<Integer> getPatientsExitedFromCare();
	@Transactional(readOnly=true)
	public List<Patient> getPatientsWithoutAnObsAdmissionMode(Concept concept);
	@Transactional(readOnly=true)
	public List<Patient> getPatientsByProgram(Program program);
	@Transactional(readOnly=true)
	public List<Patient> getPatientWhoHaveProgram();
	@Transactional(readOnly=true)
	public List<Integer> getPatientsWhoStoppedDrugWithoutDiscontinuedReason();
}
