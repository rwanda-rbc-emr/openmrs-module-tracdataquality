package org.openmrs.module.tracdataquality.utils;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;

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

/**
 *
 */
public class PatientSortByName implements Comparator<Patient> {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	
	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Patient p1, Patient p2) {
		// TODO Auto-generated method stubet
		if(p1!=null && p2 !=null){
			int returnObject = p1.getAge().compareTo(p2.getAge());
			return returnObject;
		}
		return 0;
	}
	
}
