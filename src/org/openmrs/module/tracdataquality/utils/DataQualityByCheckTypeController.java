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

import java.util.*;

//import org.openmrs.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.tracdataquality.service.DataQualityService;
import org.openmrs.module.tracdataquality.web.controller.DataqualityFormController;
import org.springframework.transaction.UnexpectedRollbackException;

/**
 * controls dataQuality types
 */
public class DataQualityByCheckTypeController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * gets patients by checking Type of abnormal datas
	 * 
	 * @param programIdKey
	 * @param session
	 * @param valueRangeType
	 * @return
	 */
	public List<Patient> checkTypeController(String programIdKey, String valueRangeType) throws Exception {
		
		//log.info("@@@@@@@@@@@@@@@@@@@@@ programIdKey "+programIdKey);
		
		
		List<Patient> patients = new ArrayList<Patient>();
		List<Patient> activePatients = new ArrayList<Patient>();
		if (programIdKey.equals("PatientsWithNoProgramsEnrollmentDates")) {
			patients = getPatientsWithNoProgramsEnrollmentDates();
			DataqualityFormController.setMsgToDisplay("Patients with no programs enrollment dates");
		} else if (programIdKey.equals("PatientsDrugsWithDiscontinuedDateWithoutStartDate")) {
			patients = getPatientsDrugsWithDiscontinuedDateWithoutStartDate();
			DataqualityFormController.setMsgToDisplay("Patients Drugs with discontinued date without start date");
		} else if (programIdKey.equals("DrugsWithDiscontinuedDateHigherThanDrugStartDate")) {
			patients = getPatientsWithDiscontinuedDateHigherThanDrugStartDate();
			DataqualityFormController.setMsgToDisplay("");
		} else if (programIdKey.equals("DrugsWithoutStartDate")) {
			patients = getPatientsWithoutStartDate();
			DataqualityFormController.setMsgToDisplay("Patients with Drugs without start date");
		} else if (programIdKey.equals("patientrsWithoutNames")) {
			patients = getPatientsWithoutNames();
			DataqualityFormController.setMsgToDisplay("Patientrs without names");
		} else if (programIdKey.equals("patientInAnyHivProgramWithoutAdmitionMode")) {
			//log.info("top  patientInAnyHivProgramWithoutAdmitionMode");
			List<Patient> patientsInHIVProgram = new ArrayList<Patient>();
			List<Patient> patientsWithNoAdmitionMode = new ArrayList<Patient>();
			//getting programs
			ProgramWorkflowService programService = Context.getProgramWorkflowService();
			Program hivProgram = programService.getProgram(getGlobalProperty("HIVProgramId"));
			patientsInHIVProgram = getPatientsCurrentlyInHIVProgram(hivProgram);
			patientsWithNoAdmitionMode = getPatientWithoutAdmitionModeConcept(getConcept(getGlobalProperty("concept.methodOfEnrollement")));
			for (Patient patient : patientsWithNoAdmitionMode) {
				if (patientsInHIVProgram.contains(patient)) {
					patients.add(patient);
					
				}
			}
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.patientInAnyHivProgramWithoutAdmitionMode");
			log.info("bottom  patientInAnyHivProgramWithoutAdmitionMode");
		} else if (programIdKey.equals("patientsWithoutProgram")) {
			log.info("top  patientsWithoutProgram");
			patients = getPatientWithoutProgram();
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.patientsWithoutProgram");
			log.info("bottom  patientsWithoutProgram");
		} else if (programIdKey.equals("patientsWithnoWeight")) {
			log.info("top  patientsWithnoWeight");
			Concept concept = getConcept(getGlobalProperty("concept.weight"));
			patients = getPatientWithoutAgivenConcept(concept);
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.patientsWithnoWeight");
			log.info("bottom  patientsWithnoWeight");
		} else if (programIdKey.equals("patientsWithNoHeight")) {
			log.info("top  patientsWithNoHeight");
			Concept concept = getConcept(getGlobalProperty("concept.height"));
			patients = getPatientWithoutAgivenConcept(concept);
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.patientsWithNoHeight");
			log.info("bottom  patientsWithNoHeight");
		} else if (programIdKey.equals("patientWithNoHIVViralLoad")) {
			log.info("top  patientWithNoHIVViralLoad");
			Concept concept = getConcept(getGlobalProperty("concept.viralLoad"));
			patients = getPatientWithoutAgivenConcept(concept);
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.patientWithNoHIVViralLoad");
			log.info("bottom  patientWithNoHIVViralLoad");
		} else if (programIdKey.equals("patientWithHIVPositiveAndNoCD4")) {
			log.info("top  patientWithHIVPositiveAndNoCD4");
			List<Patient> patientsWithNoCD4 = new ArrayList<Patient>();
			List<Patient> patientsInHIVProgram = new ArrayList<Patient>();
			Concept cd4 = getConcept(getGlobalProperty("concept.cd4_count"));
			ProgramWorkflowService programService = Context.getProgramWorkflowService();
			Program hivProgram = programService.getProgram(getGlobalProperty("HIVProgramId"));
			patientsWithNoCD4 = getPatientWithoutAgivenConcept(cd4);
			patientsInHIVProgram = getPatientsCurrentlyInHIVProgram(hivProgram);
			
			for (Patient patient : patientsInHIVProgram) {
				if (patientsWithNoCD4.contains(patient)) {
					patients.add(patient);
				}
			}
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.patientWithHIVPositiveAndNoCD4");
			log.info("bottom  patientWithHIVPositiveAndNoCD4");
		} else if (programIdKey.equals("patientWithNoWhoStage")) {
			log.info("top  patientWithNoWhoStage");
			Concept concept = getConcept(getGlobalProperty("concept.whoStage"));
			patients = getPatientWithoutAgivenConcept(concept);
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.patientWithNoWhoStage");
			log.info("bottom  patientWithNoWhoStage");
		} else if (programIdKey.equals("patientWithMoreKgs")) {
			log.info("top  patientWithMoreKgs");
			//log.info("!!!!!!!!!!!!!!!!!!!!!!!! am getting in if statment");
			Concept concept = getConcept(getGlobalProperty("concept.weight"));
			//log.info("  !!!@@@@###$$%%^^&&**(())__++++++++++++++++  "+concept);
			double valueRange = Double.parseDouble(valueRangeType);
			//log.info("!!!!!!!!!!!!!!!!!!!!!!!  !!!@@@@###$$%%^^&&**(())__++++++++++++++++  "+valueRange);
			patients = getPatientsWithMoreValueOnConcept(concept, valueRange);
			DataqualityFormController.setMsgToDisplay("Patients with more than " + valueRange + "   Weight");
			log.info("bottom  patientWithMoreKgs");
		} else if (programIdKey.equals("patientsWithMoreHeight")) {
			log.info("top  patientsWithMoreHeight");
			
			Concept concept = getConcept(getGlobalProperty("concept.height"));
			double valueRange = Double.parseDouble(valueRangeType);
			patients = getPatientsWithMoreValueOnConcept(concept, valueRange);
			DataqualityFormController.setMsgToDisplay("Patients with more than " + valueRange + "   height");
			log.info("bottom  patientsWithMoreHeight");
		} else if (programIdKey.equals("patientsWithMoreBMI")) {
			log.info("top  patientsWithMoreBMI");
			Concept concept = getConcept(getGlobalProperty("concept.BMI"));
			double valueRange = Double.parseDouble(valueRangeType);
			patients = getPatientsWithMoreValueOnConcept(concept, valueRange);
			DataqualityFormController.setMsgToDisplay("Patients with more than  " + valueRange + "   BMI");
			log.info("bottom  patientsWithMoreBMI");
		} else if (programIdKey.equals("patientsWithMoreBLOODOXYGENSATURATION")) {
			log.info("top  patientsWithMoreBLOODOXYGENSATURATION");
			double valueRange = Double.parseDouble(valueRangeType);
			patients = getPatientsWithMoreValueOnConcept(getConcept(getGlobalProperty("concept.bloodOxygenSaturation")),
			    valueRange);
			DataqualityFormController.setMsgToDisplay("Patients with more than " + valueRange + "  BLOODOXYGENSATURATION");
			log.info("bottom  patientsWithMoreBLOODOXYGENSATURATION");
		}

		else if (programIdKey.equals("patientsWithMoreDIASTOLICBLOODPRESSURE")) {
			log.info("top  patientsWithMoreDIASTOLICBLOODPRESSURE");
			double valueRange = Double.parseDouble(valueRangeType);
			patients = getPatientsWithMoreValueOnConcept(getConcept(getGlobalProperty("concept.diastolicBloodPressure")),
			    valueRange);
			DataqualityFormController
			        .setMsgToDisplay("Patients with more than " + valueRange + "  of DIASTOLICBLOODPRESSURE");
			log.info("bottom  patientsWithMoreDIASTOLICBLOODPRESSURE");
		}

		else if (programIdKey.equals("patientsWithMoreHEADCIRCUMFERENCE")) {
			log.info("top  patientsWithMoreHEADCIRCUMFERENCE");
			double valueRange = Double.parseDouble(valueRangeType);
			patients = getPatientsWithMoreValueOnConcept(getConcept(getGlobalProperty("concept.headCircumference")),
			    valueRange);
			DataqualityFormController.setMsgToDisplay("Patients with more than " + valueRange + "   HEADCIRCUMFERENCE");
			log.info("bottom patientsWithMoreHEADCIRCUMFERENCE");
		}

		else if (programIdKey.equals("patientsWithMoreKARNOFSKYPERFORMANCESCORE")) {
			log.info("top  patientsWithMoreKARNOFSKYPERFORMANCESCORE");
			double valueRange = Double.parseDouble(valueRangeType);
			patients = getPatientsWithMoreValueOnConcept(getConcept(getGlobalProperty("concept.karnofskyPerformanceScore")),
			    valueRange);
			DataqualityFormController.setMsgToDisplay("Patients with more than " + valueRange
			        + "  of KARNOFSKYPERFORMANCESCORE");
			log.info("bottom  patientsWithMoreKARNOFSKYPERFORMANCESCORE");
		}

		else if (programIdKey.equals("patientsWithMorePULSE")) {
			log.info("top  patientsWithMorePULSE");
			double valueRange = Double.parseDouble(valueRangeType);
			patients = getPatientsWithMoreValueOnConcept(getConcept(getGlobalProperty("concept.pulse")), valueRange);
			DataqualityFormController.setMsgToDisplay("Patients with more than " + valueRange + "   PULSE");
			log.info("bottom  patientsWithMorePULSE");
		}

		else if (programIdKey.equals("patientsWithMoreRESPIRATORYRATE")) {
			log.info("top  patientsWithMoreRESPIRATORYRATE");
			double valueRange = Double.parseDouble(valueRangeType);
			patients = getPatientsWithMoreValueOnConcept(getConcept(getGlobalProperty("concept.respiratoryRate")),
			    valueRange);
			DataqualityFormController.setMsgToDisplay("Patients with more than " + valueRange + "   RESPIRATORYRATE");
			log.info("bottom  patientsWithMoreRESPIRATORYRATE");
		}

		else if (programIdKey.equals("patientsWithMoreSYSTOLICBLOODPRESSURE")) {
			log.info("top  patientsWithMoreSYSTOLICBLOODPRESSURE");
			double valueRange = Double.parseDouble(valueRangeType);
			patients = getPatientsWithMoreValueOnConcept(getConcept(getGlobalProperty("concept.systolicBloodPressure")),
			    valueRange);
			DataqualityFormController.setMsgToDisplay("Patients with more than " + valueRange + "   SYSTOLICBLOODPRESSURE");
			log.info("bottom  patientsWithMoreSYSTOLICBLOODPRESSURE");
		}

		else if (programIdKey.equals("patientsWithMoreTEMPERATURE")) {
			log.info("top  patientsWithMoreTEMPERATURE");
			double valueRange = Double.parseDouble(valueRangeType);
			patients = getPatientsWithMoreValueOnConcept(getConcept(getGlobalProperty("concept.temperature")), valueRange);
			DataqualityFormController.setMsgToDisplay("Patients.With.More.Than" + valueRange + "  TEMPERATURE");
			log.info("bottom patientsWithMoreTEMPERATURE");
		}

		else if (programIdKey.equals("patientWithNoContactInformation")) {
			log.info("top  patientWithNoContactInformation");
			List<Patient> patientsWithoutPhoneNumber = new ArrayList<Patient>();
			List<Patient> patientsWithoutAlternativePhoneNumber = new ArrayList<Patient>();
			patientsWithoutPhoneNumber = getPatientWithoutAgivenConcept(getConcept(getGlobalProperty("concept.contactPhoneNumber")));
			patientsWithoutAlternativePhoneNumber = getPatientWithoutAgivenConcept(getConcept(getGlobalProperty("concept.phoneNumber")));
			for (Patient patient : patientsWithoutPhoneNumber) {
				if (patientsWithoutAlternativePhoneNumber.contains(patient)) {
					patients.add(patient);
				}
			}
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.patientWithNoContactInformation");
			log.info("bottom patientWithNoContactInformation");
		} else if (programIdKey.equals("patientWithNoTRACnet")) {
			log.info("top  patientWithNoTRACnet");
			//log.info("$$$$$$$$$$$$$$$$$$$$$$$$$ this is the tracnet type"+getGlobalProperty("patientIdentifierType.TRACnetID"));
			patients = getPatientWithNoGivenIdentifier(getGlobalProperty("patientIdentifierType.TRACnetID"));
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.PatientsWithoutTRACnetID");
			log.info("bottom  patientWithNoTRACnet");
		} else if (programIdKey.equals("patientWithNoNID")) {
			log.info("top  patientWithNoNID");
			patients = getPatientWithNoGivenIdentifier(getGlobalProperty("patientIdentifierType.NID"));
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.PatientsWithoutNID");
			log.info("bottom  patientWithNoNID");
		} 
		
		else if (programIdKey.equals("exitedPatientWithProgram")) {
			log.info("top  exitedPatientWithProgram");
			List<Patient> patientsExitedFromCare = new ArrayList<Patient>();
			List<Patient> patientWithoutProgram = new ArrayList<Patient>();
			patientsExitedFromCare = getPatientWithAgivenConcept(getConcept(getGlobalProperty("concept.reasonExitedCare")));
			patientWithoutProgram = getPatientWithoutProgram();

			for (Patient patient : patientsExitedFromCare) {
				if (!patientWithoutProgram.contains(patient)) {
					patients.add(patient);
				}
			}
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.exitedPatientWithProgram");
			log.info("bottom  exitedPatientWithProgram");
		}
		
		
		else if (programIdKey.equals("patientInAnyHIVProgramWithNoHIVTestDate")) {
			log.info("top  patientInAnyHIVProgramWithNoHIVTestDate");
//			List<Patient> patientsHIVProgram = new ArrayList<Patient>();
			List<Patient> patientsWithoutHIVTestDate = new ArrayList<Patient>();
//			ProgramWorkflowService programService = Context.getProgramWorkflowService();
			patientsWithoutHIVTestDate = getPatientWithoutAdmitionModeConcept(getConcept(getGlobalProperty("concept.HIVTestDate")));
			log.info("number of patients with no hiv test date : " + patientsWithoutHIVTestDate.size());
			patients.addAll(patientsWithoutHIVTestDate);
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.patientInAnyHIVProgramWithNoHIVTestDate");
			log.info("bottom  patientInAnyHIVProgramWithNoHIVTestDate");
		} else if (programIdKey.equals("PatientsWhoStoppedRegimenAndReasonIsNotRecorded")) {
			log.info("top  PatientsWhoStoppedRegimenAndReasonIsNotRecorded");
			List<Integer> patientIds = new ArrayList<Integer>();
			
			DataQualityService serviceQualityCheck = ((DataQualityService) Context.getService(DataQualityService.class));
			patientIds=serviceQualityCheck.getPatientsWhoStoppedDrugWithoutDiscontinuedReason();
			
			for (Integer integer : patientIds) {
				Patient patient = getPatientById(integer);
				patients.add(patient);
			}
			DataqualityFormController
			        .setMsgToDisplay("tracdataquality.indicator.PatientsWhoStoppedRegimenAndReasonIsNotRecorded");
			log.info("bottom  PatientsWhoStoppedRegimenAndReasonIsNotRecorded");
		}

		else if (programIdKey.equals("PatientsPediatricsWhoHaveAdultWhoStage")) {
			log.info("top  PatientsPediatricsWhoHaveAdultWhoStage");
			patients = getPatientsPediatricsWhoHaveAdultWhoStage();
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.PatientsPediatricWhoHaveAdultWhoStage");
			/*log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@bottom PatientsPediatricsWhoHaveAdultWhoStage"
			        + patients);*/
		} else if (programIdKey.equals("PatientsAdultWhoHaveChildWhoStage")) {
			log.info("top  PatientsAdultWhoHaveChildWhoStage");
			patients = getPatientsAdultWhoHavePedsWhoStage();
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.PatientsAdultWhoHaveChildWhoStage");
			log.info("bottom  PatientsAdultWhoHaveChildWhoStage");
		} else if (programIdKey.equals("PatientsWhoAreInPMTCTWithNoCPNId")) {
			log.info("top  PatientsWhoAreInPMTCTWithNoCPNId");
			patients = getPatientsWhoAreInPMTCTWithNoCPNId();
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.PatientsInPMTCTWithNoCPNid");
			log.info("bottom  PatientsWhoAreInPMTCTWithNoCPNId");
		}

		else if (programIdKey.equals("PatientsWithNoReturnVisitDate")) {
			log.info("top  PatientsWithNoReturnVisitDate");
			patients = getPatientWithoutAgivenConcept(getConcept(getGlobalProperty("concept.returnVisitDate")));
			DataqualityFormController.setMsgToDisplay("tracdataquality.indicator.PatientsWithNoReturnVisitDate");
			log.info("bottom  PatientsWithNoReturnVisitDate");
			
		}
		DataQualityService serviceQualityCheck = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Integer> exitedPatient = serviceQualityCheck.getPatientsExitedFromCare();
		//log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+exitedPatient);
		//List<Integer> patientsExited = getPatientsExitedFromCare();
		for (Patient patient : patients) {
			//&& !patientsExited.contains(patient.getPatientId())
			
			// = getPatientsExitedFromCare();
			if (!patient.isVoided() && !exitedPatient.contains(patient.getPatientId())) {
				
				activePatients.add(patient);
			}
		}

		Collections.sort(activePatients, new PatientSortByName());
		return activePatients;
	}
	
	//==========================================================Methods===============================================================
	
	/**
	 * gets program by programId
	 * 
	 * @param programId
	 * @return
	 */
	public Program getProgramByProgramId(int programId) {
		ProgramWorkflowService programService = Context.getProgramWorkflowService();
		Program program = programService.getProgram(programId);
		
		return program;
	}
	
	/**
	 * gets patients by program
	 * 
	 * @param program
	 * @return patients
	 */
	public List<Patient> getPatientsByProgram(Program program) {
		DataQualityService serviceQualityCheck = ((DataQualityService) Context.getService(DataQualityService.class));
		return serviceQualityCheck.getPatientsByProgram(program);
	}
	
	/**
	 * gets patients who are currently in a program
	 * 
	 * @param session
	 * @return patientsWhoHaveProgram
	 */
	
	public List<Patient> getPatientWhoHaveProgram() {
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		return dqs.getPatientWhoHaveProgram();
	}
	
	/**
	 * gets patients Without any Program
	 * 
	 * @param session
	 * @return patients
	 */
	public List<Patient> getPatientWithoutProgram() {
		
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Patient> patientList = new ArrayList<Patient>();
		try {
			patientList = dqs.getPatientsWithNoProgram();
		}
		catch (UnexpectedRollbackException unre) {
			unre.printStackTrace();
		}
		
		return patientList;
	}
	
	/**
	 * gets patients without a given Concept
	 * 
	 * @param concept
	 * @return allpatients
	 */
	public List<Patient> getPatientWithoutAgivenConcept(Concept concept) {
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Patient> patients = new ArrayList<Patient>();
		List<Patient> patientList = dqs.getPatientsWithoutAnObs(concept);
		
		for (Patient patient : patientList) {
			if (!(patient.isUser())) {
				patients.add(patient);
			}
		}
		return patients;
	}
	/**
	 * gets patients without a given Concept
	 * 
	 * @param concept
	 * @return allpatients
	 */
	public List<Patient> getPatientWithoutAdmitionModeConcept(Concept concept) {
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Patient> patients = new ArrayList<Patient>();
		List<Patient> patientList = dqs.getPatientsWithoutAnObsAdmissionMode(concept);
		
		for (Patient patient : patientList) {
			if (!(patient.isUser())) {
				patients.add(patient);
			}
		}
		return patients;
	}
	
	/**
	 * gets patients with a given Concept
	 * 
	 * @param concept
	 * @return patients
	 */
	public List<Patient> getPatientWithAgivenConcept(Concept concept) {
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Patient> patientList = dqs.getPatientsWithObs(concept);
		
		return patientList;
		
	}
	
	/**
	 * gets patients with more value on a given concept
	 * 
	 * @param concept
	 * @param value
	 * @return patients
	 */
	public List<Patient> getPatientsWithMoreValueOnConcept(Concept concept, double value) {
		List<Patient> allPatients = new ArrayList<Patient>();
		List<Patient> patients = new ArrayList<Patient>();
		List<Person> persons = new ArrayList<Person>();
		List<Obs> observers = new ArrayList<Obs>();
		allPatients = getAllPatients();
		observers = getObservationsByPersonAndConcept(null, concept);
		
		
		
		for (Obs obs : observers) {
			if (value <= obs.getValueNumeric()) {
				//log.info("XXXXXXXXXXXXXXXXXXX value "+value+"BBBBBBBBBBBBBBBBBBBBB obs.getValueNumeric() "+obs.getValueNumeric());
				persons.add(obs.getPerson());
			}
		}
		for (Patient patient : allPatients) {
			
			if (persons.contains(patient) ) {
				patients.add(patient);
			}
		}
		return patients;
	}
	
	/**
	 * gets Concept by conceptId
	 * 
	 * @param conceptId
	 * @return concept
	 */
	public Concept getConcept(int conceptId) {
		
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConcept(conceptId);
		return concept;
	}
	
	/**
	 * gets allPatients in system
	 * 
	 * @return allpatients
	 */
	public List<Patient> getAllPatients() {
		List<Patient> allPatients = new ArrayList<Patient>();
		PatientService patientService = Context.getPatientService();
		allPatients = patientService.getAllPatients();
		return allPatients;
	}
	
	/**
	 * gets observations by person and concept
	 * 
	 * @param patient
	 * @param concept
	 * @return observations
	 */
	public List<Obs> getObservationsByPersonAndConcept(Object patient, Concept concept) {
		List<Obs> observations = new ArrayList<Obs>();
		ObsService obsService = Context.getObsService();
		observations = obsService.getObservationsByPersonAndConcept(null, concept);
		return observations;
		
	}
	
	/**
	 * gets globalProperty value by giving globalProperty Key
	 * 
	 * @param propertyName
	 * @return propertyValue
	 */
	public int getGlobalProperty(String propertyName) {
		AdministrationService administrationService = Context.getAdministrationService();
		int propertyValue = 0;
		if(propertyName!=null && !propertyName.equals(""))
		propertyValue = Integer.parseInt(administrationService.getGlobalProperty(propertyName));
		return propertyValue;
	}
	
	/**
	 * gets patients without a given person attribute type
	 * 
	 * @param patientAttributeId
	 * @return
	 */
	public List<Patient> getPatientWithNoGivenAttribute(int patientAttributeId) {
		PatientService patientService = Context.getPatientService();
		ArrayList<Patient> patientList = new ArrayList<Patient>();
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Integer> patientIds = dqs.getPatientsWithoutAttribute(patientAttributeId);
		for (Integer patientId : patientIds) {
			patientList.add(patientService.getPatient(patientId));
		}
		
		return patientList;
		
	}
	
	/**
	 * gets patients without a given identifier type TODO use: select patient.patient_id from
	 * patient left join patient_identifier on patient.patient_id = patient_identifier.patient_id
	 * where patient_identifier.patient_id is null;
	 * 
	 * @param PatientIdentifierId
	 * @return patients
	 */
	public List<Patient> getPatientWithNoGivenIdentifier(int identifierTypeId) {
		PatientService patientService = Context.getPatientService();
		ArrayList<Patient> patientList = new ArrayList<Patient>();
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Integer> patientIds = dqs.getPatientsWithoutIdentifiers(identifierTypeId);
		for (Integer patientId : patientIds) {
			patientList.add(patientService.getPatient(patientId));
		}
		return patientList;
	}
	
	/**
	 * gets patients by id
	 * 
	 * @param patientId
	 * @return patient
	 */
	public Patient getPatientById(int patientId) {
		PatientService patientService = Context.getPatientService();
		return patientService.getPatient(patientId);
		
	}
	
	/**
	 * gets patients who exited care and still have a program
	 * 
	 * @param session
	 * @return
	 */
	public List<Patient> getPatientsWhoExitedCareAndStillHaveAProgram() {
		List<Patient> patientsWithoutACompletedDate = new ArrayList<Patient>();
		List<Patient> patientsWithoutExitFromCareConcept = new ArrayList<Patient>();
		List<Patient> patients = new ArrayList<Patient>();
		
		patientsWithoutACompletedDate = getPatientWhoHaveProgram();
		patientsWithoutExitFromCareConcept = getPatientWithoutAgivenConcept(getConcept(getGlobalProperty("concept.reasonExitedCare")));
		
		for (Patient patient : patientsWithoutACompletedDate) {
			if (patientsWithoutExitFromCareConcept.contains(patient)) {
				patients.add(patient);
			}
		}
		
		return patients;
		
	}
	
	/**
	 * gets patients who are currently in HIV program
	 * 
	 * @param program
	 * @param session
	 * @return patients
	 */
	
	public List<Patient> getPatientsCurrentlyInHIVProgram(Program program) {
		
		List<Patient> patientsCurrentlyInAProgram = new ArrayList<Patient>();
		List<Patient> patientsInProgram = new ArrayList<Patient>();
		patientsInProgram = getPatientsByProgram(program);
		for (Patient patient : patientsInProgram) {
			if (!patient.isUser()) {
				patientsCurrentlyInAProgram.add(patient);
			}
		}
		
		return patientsCurrentlyInAProgram;
		
	}
	
	/**
	 * gets patients pediatrics who have adult who stage
	 * 
	 * @param session
	 * @return patientsPediatricsWhoHaveAdultWhoStage
	 */
	public List<Patient> getPatientsPediatricsWhoHaveAdultWhoStage() {
		List<Patient> patientsInHIVProgram = new ArrayList<Patient>();
		List<Patient> patientsPediatricsInHIVProgram = new ArrayList<Patient>();
		
		List<Patient> patientsPediatricsWhoHaveAdultWhoStage = new ArrayList<Patient>();
		List<Patient> patientsWhoHaveAdultWhoStage = new ArrayList<Patient>();
//		List<Person> persons = new ArrayList<Person>();
//		List<Obs> observers = new ArrayList<Obs>();
		ProgramWorkflowService programService = Context.getProgramWorkflowService();
		Program program = programService.getProgram(2);
		
		patientsInHIVProgram = getPatientsCurrentlyInHIVProgram(program);
		
		for (Patient patient : patientsInHIVProgram) {
			if (patient.getAge() < 15) {
				patientsPediatricsInHIVProgram.add(patient);
			}
		}
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Integer> patientIds = dqs.getPatientsWhoHaveAdultWhoStage();
		for (Integer patientId : patientIds) {
			patientsWhoHaveAdultWhoStage.add(getPatientById(patientId));
		}
		
		for (Patient patient : patientsWhoHaveAdultWhoStage) {
			if (patientsPediatricsInHIVProgram.contains(patient)) {
				patientsPediatricsWhoHaveAdultWhoStage.add(patient);
			}
		}
		
		return patientsPediatricsWhoHaveAdultWhoStage;
		
	}
	
	/**
	 * gets patients adult who have pediatric who stage
	 * 
	 * @param session
	 * @return patientsAdultsWhoHavePedsWhoStage
	 */
	public List<Patient> getPatientsAdultWhoHavePedsWhoStage() {
		List<Patient> patientsInHIVProgram = new ArrayList<Patient>();
		List<Patient> patientsInHIVPMTCT = new ArrayList<Patient>();
		List<Patient> patientsAdultsInHIVProgram = new ArrayList<Patient>();
		
		List<Patient> patientsAdultsWhoHavePedsWhoStage = new ArrayList<Patient>();
		List<Patient> patientsWhoHavePedsWhoStage = new ArrayList<Patient>();
//		List<Person> persons = new ArrayList<Person>();
//		List<Obs> observers = new ArrayList<Obs>();
		ProgramWorkflowService programService = Context.getProgramWorkflowService();
		Program programHIV = programService.getProgram(2);
		Program programPMTCT = programService.getProgram(1);
		
		patientsInHIVProgram = getPatientsCurrentlyInHIVProgram(programHIV);
		patientsInHIVPMTCT = getPatientsCurrentlyInHIVProgram(programPMTCT);
		for (Patient patient : patientsInHIVProgram) {
			if (patient.getAge() > 15) {
				patientsAdultsInHIVProgram.add(patient);
			}
		}
		for (Patient patient : patientsInHIVPMTCT) {
			if (patient.getAge() > 15) {
				if(!patientsAdultsInHIVProgram.contains(patient))
				patientsAdultsInHIVProgram.add(patient);
			}
		}
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Integer> patientIds = dqs.getPatientsAdultWhoHavePedsWhoStage();
		for (Integer patientId : patientIds) {
			patientsWhoHavePedsWhoStage.add(getPatientById(patientId));
		}
		
		for (Patient patient : patientsWhoHavePedsWhoStage) {
			if (patientsAdultsInHIVProgram.contains(patient)) {
				patientsAdultsWhoHavePedsWhoStage.add(patient);
			}
		}
		
		return patientsAdultsWhoHavePedsWhoStage;
	}
	
	/**
	 * gets patients who are in PMTCT program with no CPN ID
	 * 
	 * @param session
	 * @return
	 */
	public List<Patient> getPatientsWhoAreInPMTCTWithNoCPNId() {
		List<Patient> patientsInPMTCTProgram = new ArrayList<Patient>();
		List<Patient> patientsWithNoCPN = new ArrayList<Patient>();
		List<Patient> patientsInPMTCTWithNoCPNId = new ArrayList<Patient>();
		
		ProgramWorkflowService programService = Context.getProgramWorkflowService();
		Program program = programService.getProgram(1);
		
		patientsInPMTCTProgram = getPatientsCurrentlyInHIVProgram(program);
		patientsWithNoCPN = getPatientWithNoGivenAttribute(getGlobalProperty("patientIdentifierType.CPNID"));
		
		for (Patient patient : patientsInPMTCTProgram) {
			if (patientsWithNoCPN.contains(patient)) {
				patientsInPMTCTWithNoCPNId.add(patient);
			}
		}
		
		return patientsInPMTCTWithNoCPNId;
	}
	
	/**
	 * gets patients without names
	 * 
	 * @return
	 */
	public List<Patient> getPatientsWithoutNames() {
		List<Patient> patientsWithoutNames = new ArrayList<Patient>();
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Integer> patientIds = dqs.getPatientsWithoutNames();
		for (Integer patientId : patientIds) {
			patientsWithoutNames.add(getPatientById(patientId));
		}
		return patientsWithoutNames;
	}
	
	/**
	 * gets patients without start date
	 * 
	 * @return
	 */
	public List<Patient> getPatientsWithoutStartDate() {
		List<Patient> patientsWithoutStartDate = new ArrayList<Patient>();
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Integer> patientIds = dqs.getPatientsWithoutStartDate();
		for (Integer patientId : patientIds) {
			patientsWithoutStartDate.add(getPatientById(patientId));
		}
		return patientsWithoutStartDate;
	}
	
	/**
	 * gets patients with discontinued date higher than drug startdate
	 * 
	 * @return
	 */
	public List<Patient> getPatientsWithDiscontinuedDateHigherThanDrugStartDate() {
		List<Patient> patinetsWithDiscontinuedDateHigherThanDrugStartDate = new ArrayList<Patient>();
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Integer> patientIds = dqs.getPatientsWithDiscontinuedDateHigherThanDrugStartDate();
		for (Integer patientId : patientIds) {
			patinetsWithDiscontinuedDateHigherThanDrugStartDate.add(getPatientById(patientId));
		}
		
		return patinetsWithDiscontinuedDateHigherThanDrugStartDate;
	}
	
	/**
	 * gets patients drugs with discontinued date without startdate
	 * 
	 * @return
	 */
	public List<Patient> getPatientsDrugsWithDiscontinuedDateWithoutStartDate() {
		List<Patient> patientsDrugsWithDiscontinuedDateWithoutStartDate = new ArrayList<Patient>();
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Integer> patientIds = dqs.getPatientsDrugsWithDiscontinuedDateWithoutStartDate();
		for (Integer patientId : patientIds) {
			patientsDrugsWithDiscontinuedDateWithoutStartDate.add(getPatientById(patientId));
		}
		
		return patientsDrugsWithDiscontinuedDateWithoutStartDate;
	}
	
	/**
	 * gets patients with no programs enrollment dates
	 * 
	 * @return
	 */
	public List<Patient> getPatientsWithNoProgramsEnrollmentDates() {
		List<Patient> patientsWithNoProgramsEnrollmentDates = new ArrayList<Patient>();
		DataQualityService dqs = ((DataQualityService) Context.getService(DataQualityService.class));
		List<Integer> patientIds = dqs.getPatientsWithNoProgramsEnrollmentDates();
		for (Integer patientId : patientIds) {
			patientsWithNoProgramsEnrollmentDates.add(getPatientById(patientId));
		}
		return patientsWithNoProgramsEnrollmentDates;
	}
	
	
	/*public List<Integer> getActivePatients(List<Integer> patientsUnderARVNewList) {
		List<Integer> exitedPatient = getPatientsExitedFromCare();
		List<Integer> patientsActive = new ArrayList<Integer>();
		
		boolean notFound;
		for (int i : patientsUnderARVNewList) {
			notFound = true;
			for (int j : exitedPatient) {
				if (j == i) {
					notFound = false;
					break;
				}
			}
			if (notFound)
				patientsActive.add(i);
		}
		
		return patientsActive;//patientsActive;
	}*/
	
	/*public List<Integer> getPatientsExitedFromCare() {
		Session session = sessionFactory.getCurrentSession();
		//CamerwaGlobalProperties gp = new CamerwaGlobalProperties();
		//int exitedFromCareConceptId = gp.getConceptIdAsInt("camerwa.ExitedFromCareConceptId");
		SQLQuery allPatientsExitedFromCare = session
		        .createSQLQuery("select distinct pa.patient_id from patient pa inner join person pe on pa.patient_id = pe.person_id inner join obs ob on ob.person_id = pe.person_id where ob.concept_id = "
		                +1811+ "");
		List<Integer> patientsVoided = (List<Integer>) getPatientsVoided();
		List<Integer> patientsExitedFromCare = (List<Integer>) union(allPatientsExitedFromCare.list(), patientsVoided);
		return patientsExitedFromCare;
		
	}
	public List<Integer> getPatientsVoided() {
		Session session = sessionFactory.getCurrentSession();
		SQLQuery patientsVoided = session.createSQLQuery("select distinct patient_id from patient pe where pe.voided=1");
		
		return patientsVoided.list();
		
	}
	public Collection union(Collection coll1, Collection coll2) {
		List union = new ArrayList(coll1); //Set union = new HashSet(coll1);
		union.addAll(new ArrayList(coll2));
		return new ArrayList(union);
	}*/
}

