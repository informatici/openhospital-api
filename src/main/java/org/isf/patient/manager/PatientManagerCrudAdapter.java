package org.isf.patient.manager;

import java.util.List;

import org.isf.patient.model.Patient;
import org.isf.shared.manager.BasicManager;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;

public class PatientManagerCrudAdapter 
	implements BasicManager<Patient, String> {
	@Autowired
	protected PatientBrowserManager manager;
	@Override
	public boolean create(Patient patient) throws OHServiceException {
		return manager.newPatient(patient);
	}

	@Override
	public boolean update(Patient patient) throws OHServiceException {
		return manager.updatePatient(patient);
	}

	@Override
	public List<Patient> getPage(int page, int size) throws OHServiceException {
		return manager.getPatient(page, size);
	}

	@Override
	public boolean delete(Patient instance) throws OHServiceException {
		return manager.deletePatient(instance);
	}

	@Override
	public Patient get(String key) throws OHServiceException {
		return manager.getPatient(key);
	}
}
