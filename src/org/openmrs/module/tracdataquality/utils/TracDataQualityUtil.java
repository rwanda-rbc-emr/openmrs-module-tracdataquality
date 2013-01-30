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
package org.openmrs.module.tracdataquality.utils;

import org.openmrs.Patient;

/**
 *
 */
public class TracDataQualityUtil {
	
	private static String[] criteriaKeys = { "patientInAnyHivProgramWithoutAdmitionMode", "patientsWithoutProgram",
	        "patientsWithnoWeight", "patientsWithNoHeight", "patientWithNoHIVViralLoad", "patientWithHIVPositiveAndNoCD4",
	        "patientWithNoWhoStage", "patientWithNoContactInformation", "exitedPatientWithProgram",
	        "patientInAnyHIVProgramWithNoHIVTestDate", "PatientsWhoStoppedRegimenAndReasonIsNotRecorded",
	        "PatientsWithNoReturnVisitDate", "patientrsWithoutNames", "DrugsWithoutStartDate",
	        "DrugsWithDiscontinuedDateHigherThanDrugStartDate", "PatientsDrugsWithDiscontinuedDateWithoutStartDate",
	        "PatientsWithNoProgramsEnrollmentDates", "PatientsPediatricsWhoHaveAdultWhoStage",
	        "PatientsAdultWhoHaveChildWhoStage" };
	
	public static String[] getCriteriaKeys() {
		return criteriaKeys;
	}
	
	public int compare(Patient p1, Patient p2) {
		if (p1 != null && p2 != null) {
			int returnObject = p1.getAge().compareTo(p2.getAge());
			return returnObject;
		}
		return 0;
	}
	
}
