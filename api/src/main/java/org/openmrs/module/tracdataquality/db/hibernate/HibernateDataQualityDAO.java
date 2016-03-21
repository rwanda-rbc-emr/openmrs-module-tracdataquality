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
package org.openmrs.module.tracdataquality.db.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.PropertyAccessException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Program;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.tracdataquality.db.DataQualityDAO;
import org.openmrs.module.tracdataquality.utils.DataQualityByCheckTypeController;
import org.springframework.transaction.UnexpectedRollbackException;

public class HibernateDataQualityDAO implements DataQualityDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private SessionFactory sessionFactory;
	
	/**
	 * sets session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * gets session factory
	 * 
	 * @return
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * gets patients without program
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsWithNoProgram()
	 */
	@SuppressWarnings("unchecked")
	public List<Patient> getPatientsWithNoProgram() {
		ArrayList<Patient> patientList = new ArrayList<Patient>();
		PatientService patientService = Context.getPatientService();
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session
		        .createSQLQuery("select distinct patient_id from patient where patient_id not in(select distinct patient_id from patient_program where date_completed is null and voided = 0)");
		List<Integer> patientIds = query.list();
		
		List<Integer> patientExitedFromCare = getPatientsExitedFromCare();
		List<Integer> patientsNotExited = new ArrayList<Integer>(); //getPatientsExitedFromCare();
		// getting patients with trac net id
		
		List<Integer> patientsWithTracNet = getPatientsWithIdentifiers(getGlobalProperty("patientIdentifierType.TRACnetID"));
		
		for (Integer patientId : patientIds) {
			if (!patientExitedFromCare.contains(patientId)) {
				patientsNotExited.add(patientId);
			}
			
		}
		
		List<Integer> patientsNotExitedWithTracNet = new ArrayList<Integer>(); //getPatientsExitedFromCare();
		for (Integer patientId : patientsNotExited) {
			if (patientsWithTracNet.contains(patientId)) {
				patientsNotExitedWithTracNet.add(patientId);
			}
			
		}
		
		for (Integer patientId : patientsNotExitedWithTracNet) {
			try {
				patientList.add(patientService.getPatient(patientId));
			}
			catch (IllegalArgumentException iae) {
				log.info("illegal argument exception while trying to load patient " + patientId);
			}
			catch (PropertyAccessException pae) {
				log.info("property access exception while trying to load patient " + patientId);
			}
			catch (UnexpectedRollbackException pae) {
				log.info("roll back exception when trying to load patient " + patientId);
			}
		}
		
		return patientList;
	}
	
	/**
	 * gets patients without observation for a given concept
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsWithoutAnObs(org.openmrs.Concept)
	 */
	@SuppressWarnings("unchecked")
	public List<Patient> getPatientsWithoutAnObs(Concept concept) {
		ArrayList<Patient> patientList = new ArrayList<Patient>();
		PatientService patientService = Context.getPatientService();
		PersonService personService = Context.getPersonService();
		Session session = sessionFactory.getCurrentSession();
		
		//SQLQuery query = session.createSQLQuery("select distinct person_id from obs where person_id not in(select distinct person_id from obs where concept_id = ?) ");
		SQLQuery query = session
		        .createSQLQuery("select distinct ob.person_id from obs ob"
		                + " INNER JOIN person s on s.person_id=ob.person_id"
		                + " INNER JOIN patient p on s.person_id=p.patient_id"
		                + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
		                + " INNER JOIN program prog on prog.program_id=pg.program_id AND (prog.program_id=1 OR prog.program_id=2)"
		                + " where ob.person_id not in"
		                + " (select distinct obb.person_id from obs obb where obb.concept_id = ?) and pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ;");
		query.setInteger(0, concept.getConceptId());
		List<Integer> personIds = query.list();
		for (Integer personId : personIds) {
			try {
				if (personService.getPerson(personId).isPatient() && !(patientService.getPatient(personId) == null)) {
					patientList.add(patientService.getPatient(personId));
				}
			}
			catch (ObjectNotFoundException onfe) {
				log.info("patient with id " + personId + "  not found");
			}
			catch (UnexpectedRollbackException pae) {
				log.info("roll back exception when trying to load patient " + personId);
			}
			catch (PropertyAccessException pae) {
				log.info("property access exception when trying to load patient " + personId);
			}
		}
		
		return patientList;
	}
	
	public List<Patient> getPatientsWithoutAnObsAdmissionMode(Concept concept) {
		ArrayList<Patient> patientList = new ArrayList<Patient>();
		PatientService patientService = Context.getPatientService();
		PersonService personService = Context.getPersonService();
		Session session = sessionFactory.getCurrentSession();
		DataQualityByCheckTypeController dataQualityByCheckTypeController = new DataQualityByCheckTypeController();
		int transferInConcept = dataQualityByCheckTypeController.getGlobalProperty("programOver.transferredInConceptId");
		//SQLQuery query = session.createSQLQuery("select distinct person_id from obs where person_id not in(select distinct person_id from obs where concept_id = ?) ");
		SQLQuery query = session
		        .createSQLQuery("select distinct ob.person_id from obs ob"
		                + " INNER JOIN person s on s.person_id=ob.person_id"
		                + " INNER JOIN patient p on s.person_id=p.patient_id"
		                + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
		                + " INNER JOIN program prog on prog.program_id=pg.program_id AND prog.program_id=2"
		                + " where ob.person_id not in"
		                + " (select distinct obb.person_id from obs obb where obb.concept_id = ? or obb.concept_id = "+transferInConcept+") and pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ;");
		query.setInteger(0, concept.getConceptId());
		List<Integer> personIds = query.list();
		for (Integer personId : personIds) {
			try {
				if (personService.getPerson(personId).isPatient() && !(patientService.getPatient(personId) == null)) {
					patientList.add(patientService.getPatient(personId));
				}
			}
			catch (ObjectNotFoundException onfe) {
				log.info("patient with id " + personId + "  not found");
			}
			catch (UnexpectedRollbackException pae) {
				log.info("roll back exception when trying to load patient " + personId);
			}
			catch (PropertyAccessException pae) {
				log.info("property access exception when trying to load patient " + personId);
			}
		}
		
		return patientList;
	}
	
	/**
	 * gets patients with observation for a given concept
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsWithObs(org.openmrs.Concept)
	 */
	@SuppressWarnings("unchecked")
	public List<Patient> getPatientsWithObs(Concept concept) {
		ArrayList<Patient> patientList = new ArrayList<Patient>();
		PatientService patientService = Context.getPatientService();
		PersonService personService = Context.getPersonService();
		Session session = sessionFactory.getCurrentSession();
		//SQLQuery query = session.createSQLQuery("select distinct person_id from obs where concept_id = ? and voided = 0");

		//this cause an error, cannot cast a array into integer
		//		SQLQuery query = session
//		        .createSQLQuery("select distinct ob.person_id,pg.date_completed from obs ob"
//		                + " INNER JOIN person s on s.person_id=ob.person_id"
//		                + " INNER JOIN patient p on s.person_id=p.patient_id"
//		                + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
//		                + " INNER JOIN program prog on prog.program_id=pg.program_id AND (prog.program_id=1 OR prog.program_id=2) where ob.concept_id = ? and ob.voided = 0 "
//		                + " AND pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ");
		SQLQuery query = session
        .createSQLQuery("select distinct ob.person_id from obs ob"
                + " INNER JOIN person s on s.person_id=ob.person_id"
                + " INNER JOIN patient p on s.person_id=p.patient_id"
                + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
                + " INNER JOIN program prog on prog.program_id=pg.program_id AND (prog.program_id=1 OR prog.program_id=2) where ob.concept_id = ? and ob.voided = 0 "
                + " AND pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ");
		query.setInteger(0, concept.getConceptId());
		List<Integer> personIds = query.list();
		for (Integer personId : personIds) {
			try {
				if (personService.getPerson(personId).isPatient()) {
					patientList.add(patientService.getPatient(personId));
				}
			}
			catch (ObjectNotFoundException onfe) {
				log.info("patient with id " + personId + "  not found");
			}
			catch (UnexpectedRollbackException pae) {
				log.info("roll back exception when trying to load patient " + personId);
			}
			catch (PropertyAccessException pae) {
				log.info("property access exception when trying to load patient " + personId);
			}
		}
		
		return patientList;
	}
	
	/**
	 * gets patients without identifiers
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsWithoutIdentifiers()
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getPatientsWithoutIdentifiers(int typeId) {
		Session session = sessionFactory.getCurrentSession();
		
		/*SQLQuery query = session
		        .createSQLQuery("select patient_id from patient where patient_id not in (select patient_id from patient_identifier where identifier_type=?)");
		*/
		SQLQuery query = session
		        .createSQLQuery("select p.patient_id from patient p"
		                + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
		                + " INNER JOIN program prog on prog.program_id=pg.program_id"
		                + " where p.patient_id not in"
		                + " (select pi.patient_id from patient_identifier pi where pi.identifier_type=?)"
		                + " AND (prog.program_id=1 OR prog.program_id=2) AND pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ");
		
		query.setInteger(0, typeId);
		List<Integer> patientIds = query.list();
		
		return patientIds;
		
	}
	
	public List<Integer> getPatientsWithIdentifiers(int typeId) {
		Session session = sessionFactory.getCurrentSession();
		
		SQLQuery query = session.createSQLQuery("select pi.patient_id from patient_identifier pi where pi.identifier_type="
		        + typeId + "");
		
		List<Integer> patientIds = query.list();
		
		return patientIds;
		
	}
	
	/**
	 * gets patients without attribute
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsWithoutAttribute(int)
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getPatientsWithoutAttribute(int typeId) {
		Session session = sessionFactory.getCurrentSession();
		/*SQLQuery query = session
		        .createSQLQuery("select person_id from person where person_id not in (select person_id from person_attribute where person_attribute_type_id= ?)");*/

		SQLQuery query = session
		        .createSQLQuery("select person_id from person s"
		                + " INNER JOIN patient p on s.person_id=p.patient_id"
		                + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
		                + " INNER JOIN program prog on prog.program_id=pg.program_id"
		                + " where s.person_id not in"
		                + " (select person_id from person_attribute where person_attribute_type_id= ?)"
		                + " AND (prog.program_id=1 OR prog.program_id=2) AND pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ");
		
		query.setInteger(0, typeId);
		List<Integer> patientIds = query.list();
		
		return patientIds;
	}
	
	/**
	 * gets patients without names
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsWithoutNames()
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getPatientsWithoutNames() {
		Session session = sessionFactory.getCurrentSession();
		/*SQLQuery query = session
		.createSQLQuery("select patient.patient_id from patient left join person_name on patient.patient_id = person_name.person_id where person_id is null or person_name.family_name is null");*/
		SQLQuery query = session.createSQLQuery("select p.patient_id from patient p"
		        + " left join person_name on p.patient_id = person_name.person_id"
		        + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
		        + " INNER JOIN program prog on prog.program_id=pg.program_id"
		        + " where person_id is null or person_name.family_name is null"
		        + " AND (prog.program_id=1 OR prog.program_id=2)"
		        + " AND pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ;");
		
		List<Integer> patientIds = query.list();
		///log.info("999999999999999999999999999999999999 " );
		return patientIds;
	}
	
	/**
	 * gets patients without start date
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsWithoutStartDate()
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getPatientsWithoutStartDate() {
		Session session = sessionFactory.getCurrentSession();
		//SQLQuery query = session.createSQLQuery("select distinct patient_id from orders where start_date is null and voided=0");
		
		SQLQuery query = session.createSQLQuery("select distinct ord.patient_id from orders ord"
		        + " INNER JOIN patient p on ord.patient_id=p.patient_id"
		        + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
		        + " INNER JOIN program prog on prog.program_id=pg.program_id "
		        + " where start_date is null and ord.voided=0 AND (prog.program_id=1 OR prog.program_id=2)"
		        + " AND pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ;");
		
		List<Integer> patientIds = query.list();
		return patientIds;
		
	}
	
	/**
	 * gets Patients with discontinued date higher than drug start date
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatinetsWithDiscontinuedDateHigherThanDrugStartDate()
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getPatinetsWithDiscontinuedDateHigherThanDrugStartDate() {
		Session session = sessionFactory.getCurrentSession();
		/*SQLQuery query = session
		.createSQLQuery("select distinct patient_id from orders where discontinued_date < start_date and discontinued_date is not null and voided=0;");*/

		SQLQuery query = session.createSQLQuery("select distinct ord.patient_id from orders ord"
		        + " INNER JOIN patient p on ord.patient_id=p.patient_id"
		        + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
		        + " INNER JOIN program prog on prog.program_id=pg.program_id"
		        + " where discontinued_date < start_date and ord.discontinued_date is not null and ord.voided=0"
		        + " AND (prog.program_id=1 OR prog.program_id=2)"
		        + " AND pg.date_completed is null and p.voided = 0 and pg.voided = 0 ;");
		List<Integer> patientIds = query.list();
		return patientIds;
	}
	
	/**
	 * gets patients drugs with discontinued date without start date
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsDrugsWithDiscontinuedDateWithoutStartDate()
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getPatientsDrugsWithDiscontinuedDateWithoutStartDate() {
		Session session = sessionFactory.getCurrentSession();
		/*SQLQuery query = session
		.createSQLQuery("select distinct patient_id from orders where start_date is null and discontinued_date is not null and voided=0;");*/

		SQLQuery query = session
		        .createSQLQuery("select distinct ord.patient_id from orders ord"
		                + " INNER JOIN patient p on ord.patient_id=p.patient_id"
		                + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
		                + " INNER JOIN program prog on prog.program_id=pg.program_id"
		                + " where start_date is null and ord.discontinued_date is not null and ord.voided=0 AND (prog.program_id=1 OR prog.program_id=2)"
		                + " AND pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ;");
		
		List<Integer> patientIds = query.list();
		return patientIds;
	}
	
	/**
	 * gets patients with no programs enrollment dates
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsWithNoProgramsEnrollmentDates()
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getPatientsWithNoProgramsEnrollmentDates() {
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session
		        .createSQLQuery("select distinct prg.patient_id from patient_program prg where prg.date_enrolled is null and prg.voided=0 AND (prg.program_id=1 OR prg.program_id=2);");
		List<Integer> patientIds = query.list();
		return patientIds;
	}
	
	/**
	 * gets patients with no programs enrollment dates
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsWhoHaveAdultWhoStage()
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getPatientsWhoHaveAdultWhoStage() {
		Session session = sessionFactory.getCurrentSession();
		/*SQLQuery query = session
		.createSQLQuery("select distinct person_id from obs where concept_id = 1480 and voided = 0 and (value_coded = 1204 or value_coded = 1205 or value_coded  = 1206 or value_coded = 1207);");*/
		SQLQuery query = session.createSQLQuery("select distinct ob.person_id from obs ob"
		        + " INNER JOIN person s on s.person_id=ob.person_id" + " INNER JOIN patient p on s.person_id=p.patient_id"
		        + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
		        + " INNER JOIN program prog on prog.program_id=pg.program_id"
		        + " where ob.concept_id = 1480 and ob.voided = 0 AND (prog.program_id=1 OR prog.program_id=2)"
		        + " and (ob.value_coded = 1204 or ob.value_coded = 1205 or ob.value_coded  = 1206 or ob.value_coded = 1207)"
		        + " AND pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ;");
		
		List<Integer> patientIds = query.list();
		
		return patientIds;
	}
	
	/**
	 * gets patients adult who have pediatric who stage
	 * 
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsAdultWhoHavePedsWhoStage()
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getPatientsAdultWhoHavePedsWhoStage() {
		Session session = sessionFactory.getCurrentSession();
		/*SQLQuery query = session
		.createSQLQuery("select distinct person_id from obs where concept_id = 1480 and voided = 0 and (value_coded = 1220 or value_coded = 1221 or value_coded  = 1222 or value_coded = 1223);");*/

		SQLQuery query = session.createSQLQuery("select distinct ob.person_id from obs ob"
		        + " INNER JOIN person s on s.person_id=ob.person_id" + " INNER JOIN patient p on s.person_id=p.patient_id"
		        + " INNER JOIN patient_program pg on pg.patient_id=p.patient_id"
		        + " INNER JOIN program prog on prog.program_id=pg.program_id"
		        + " where ob.concept_id = 1480 and ob.voided = 0 AND (prog.program_id=1 OR prog.program_id=2)"
		        + " and (ob.value_coded = 1222 or ob.value_coded = 1220 or ob.value_coded  = 1221 or ob.value_coded = 1223)"
		        + " AND pg.date_completed is  null and p.voided = 0 and pg.voided = 0 ;");
		List<Integer> patientIds = query.list();
		
		return patientIds;
		
	}
	
	public List<Integer> getPatientsExitedFromCare() {
		Session session = sessionFactory.getCurrentSession();
		//CamerwaGlobalProperties gp = new CamerwaGlobalProperties();
		//int exitedFromCareConceptId = gp.getConceptIdAsInt("camerwa.ExitedFromCareConceptId");
		SQLQuery allPatientsExitedFromCare = session
		        .createSQLQuery("select distinct pa.patient_id from patient pa inner join person pe on pa.patient_id = pe.person_id inner join obs ob on ob.person_id = pe.person_id where ob.concept_id = "
		                + 1811 + "");
		//List<Integer> patientsVoided = (List<Integer>) getPatientsVoided();
		//List<Integer> patientsExitedFromCare = (List<Integer>) union(allPatientsExitedFromCare.list(), patientsVoided);
		return allPatientsExitedFromCare.list();
		
	}
	
	public int getGlobalProperty(String propertyName) {
		AdministrationService administrationService = Context.getAdministrationService();
		int propertyValue = Integer.parseInt(administrationService.getGlobalProperty(propertyName));
		return propertyValue;
	}
	
	/**
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsByProgram(org.openmrs.Program)
	 */
	@Override
	public List<Patient> getPatientsByProgram(Program program) {
		Session session = sessionFactory.getCurrentSession();
		Criteria c = session.createCriteria(PatientProgram.class).setProjection(Property.forName("patient")).add(
		    Restrictions.eq("program", program));
		List<Patient> patients = c.list();
		List<Patient> patientsByProgram = new ArrayList<Patient>();
		
		for (Object patient : patients) {
			if (!((Person) patient).isUser() && !((Person) patient).isVoided() && !((Person) patient).isPersonVoided()) {
				patientsByProgram.add((Patient) patient);
			}
		}
		//log.info("@@@@@@@@@@@@@@@@@@@@# : " + patientsByProgram);
		return patientsByProgram;
	}
	
	/**
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientWhoHaveProgram()
	 */
	@Override
	public List<Patient> getPatientWhoHaveProgram() {
		List<Integer> patientIds = new ArrayList<Integer>();
		List<Patient> patientsWhoHaveAProgram = new ArrayList<Patient>();
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session
		        .createSQLQuery("select distinct patient_id from patient_program where date_enrolled is not null and date_completed is null");
		for (int i = 0; i < query.list().size(); i++) {
			
			patientIds.add((Integer) query.list().get(i));
		}
		
		for (Integer integer : patientIds) {
			Patient patient = Context.getPatientService().getPatient(integer);
			patientsWhoHaveAProgram.add(patient);
		}
		
		return patientsWhoHaveAProgram;
	}
	
	/**
	 * @see org.openmrs.module.tracdataquality.db.DataQualityDAO#getPatientsWhoStoppedDrugWithoutDiscontinuedReason()
	 */
	@Override
	public List<Integer> getPatientsWhoStoppedDrugWithoutDiscontinuedReason() {
		List<Integer> patientIds = new ArrayList<Integer>();
		Session session = sessionFactory.getCurrentSession();
		SQLQuery query = session
		        .createSQLQuery("select distinct patient_id from orders where discontinued_date is not null and discontinued_reason is null and voided=0");
		for (int i = 0; i < query.list().size(); i++) {
			
			patientIds.add((Integer) query.list().get(i));
		}
		return patientIds;
	}
}
