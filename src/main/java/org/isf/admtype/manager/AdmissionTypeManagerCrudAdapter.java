package org.isf.admtype.manager;

import java.util.List;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.isf.admtype.model.AdmissionType;
import org.isf.shared.manager.BasicManager;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdmissionTypeManagerCrudAdapter implements BasicManager<AdmissionType, String> {
	@Autowired
	protected AdmissionTypeBrowserManager manager;
	
	public AdmissionTypeBrowserManager getManager() {
		return manager;
	}

	public void setManager(AdmissionTypeBrowserManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean create(AdmissionType instance) throws OHServiceException {
		return manager.newAdmissionType(instance);
	}

	@Override
	public boolean update(AdmissionType instance) throws OHServiceException {
		return manager.updateAdmissionType(instance);
	}

	@Override
	public List<AdmissionType> getPage(int page, int size) throws OHServiceException {
		return manager.getAdmissionType();
	}

	@Override
	public AdmissionType get(String key) throws OHServiceException {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public boolean delete(AdmissionType instance) throws OHServiceException {
		return manager.deleteAdmissionType(instance);
	}
	
}
