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
package org.openmrs.module.tracdataquality.db;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Program;


/**
 *
 */
public interface DataQualityDAO {
	
	public List<Integer> getPatientsWithoutIdentifiers(int typeId);
	public List<Integer> getPatientsWithoutAttribute(int typeId);
	public List<Patient> getPatientsWithoutAnObs(Concept concept);
	public List<Patient> getPatientsWithObs(Concept concept);
	public List<Patient> getPatientsWithNoProgram();
	public List<Integer> getPatientsWithoutNames();
	public List<Integer> getPatientsWithoutStartDate();
	public List<Integer> getPatinetsWithDiscontinuedDateHigherThanDrugStartDate();
	public List<Integer> getPatientsDrugsWithDiscontinuedDateWithoutStartDate();
	public List<Integer> getPatientsWithNoProgramsEnrollmentDates();
	public List<Integer> getPatientsWhoHaveAdultWhoStage();
	public List<Integer> getPatientsAdultWhoHavePedsWhoStage();
	public List<Integer> getPatientsExitedFromCare();
	public List<Patient> getPatientsWithoutAnObsAdmissionMode(Concept concept);
	public List<Patient> getPatientsByProgram(Program program);
	public List<Patient> getPatientWhoHaveProgram();
	public List<Integer> getPatientsWhoStoppedDrugWithoutDiscontinuedReason();
}
