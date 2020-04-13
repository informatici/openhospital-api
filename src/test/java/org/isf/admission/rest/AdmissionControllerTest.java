package org.isf.admission.rest;

import static org.junit.Assert.fail;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.shared.exceptions.OHResponseEntityExceptionHandler;
import org.isf.ward.manager.WardBrowserManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AdmissionControllerTest {
		
	@Mock
	private AdmissionBrowserManager admissionManagerMock;

	@Mock
	private PatientBrowserManager patientManagerMock;

	@Mock
	private WardBrowserManager wardManagerMock;

	@Mock
	private DiseaseBrowserManager diseaseManagerMock;

	@Mock
	private OperationBrowserManager operationManagerMock;

	@Mock
	private PregnantTreatmentTypeBrowserManager pregTraitTypeManagerMock;

	@Mock
	private DeliveryTypeBrowserManager dlvrTypeManagerMock;

	@Mock
	private DeliveryResultTypeBrowserManager dlvrrestTypeManagerMock;
	

    private MockMvc mockMvc;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	this.mockMvc = MockMvcBuilders
				.standaloneSetup(new AdmissionController(
						admissionManagerMock,
						patientManagerMock,
						wardManagerMock,
						diseaseManagerMock,
						operationManagerMock,
						pregTraitTypeManagerMock,
						dlvrTypeManagerMock,
						dlvrrestTypeManagerMock
						))
   				.setControllerAdvice(new OHResponseEntityExceptionHandler())
   				.build();
    }

	@Test
	public void testAdmissionController() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAdmissions() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCurrentAdmission() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAdmittedPatients() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPatientAdmissions() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNextYProg() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUsedWardBed() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteAdmissionType() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewAdmissions() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateAdmissions() {
		fail("Not yet implemented");
	}

}
