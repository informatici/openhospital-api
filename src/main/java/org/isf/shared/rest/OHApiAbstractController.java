package org.isf.shared.rest;

import org.isf.patient.manager.PatientBrowserManager;
import org.isf.shared.mapper.OHModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class OHApiAbstractController {

    @Autowired
    protected OHModelMapper ohModelMapper;


}
