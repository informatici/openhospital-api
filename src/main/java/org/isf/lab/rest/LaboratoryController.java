package org.isf.lab.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.lab.dto.LabWithRowsDTO;
import org.isf.lab.dto.LaboratoryDTO;
import org.isf.lab.dto.LaboratoryForPrintDTO;
import org.isf.lab.dto.LaboratoryRowDTO;
import org.isf.lab.manager.LabManager;
import org.isf.lab.mapper.LaboratoryForPrintMapper;
import org.isf.lab.mapper.LaboratoryMapper;
import org.isf.lab.mapper.LaboratoryRowMapper;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@RestController
@Api(value = "/laboratories", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value="apiKey")})
public class LaboratoryController {

    private final Logger logger = LoggerFactory.getLogger(LaboratoryController.class);

    @Autowired
    protected LabManager laboratoryManager;
    @Autowired
    protected ExamBrowsingManager examManager;
    @Autowired
    private PatientBrowserManager patientBrowserManager;
    @Autowired
    private LaboratoryMapper laboratoryMapper;
    @Autowired
    private LaboratoryRowMapper laboratoryRowMapper;
    @Autowired
    private LaboratoryForPrintMapper laboratoryForPrintMapper;

    public LaboratoryController(LabManager laboratoryManager, PatientBrowserManager patientBrowserManager, ExamBrowsingManager examManager, LaboratoryMapper laboratoryMapper, LaboratoryRowMapper laboratoryRowMapper, LaboratoryForPrintMapper laboratoryForPrintMapper) {
        this.laboratoryManager = laboratoryManager;
        this.patientBrowserManager = patientBrowserManager;
        this.examManager = examManager;
        this.laboratoryMapper = laboratoryMapper;
        this.laboratoryRowMapper = laboratoryRowMapper;
        this.laboratoryForPrintMapper = laboratoryForPrintMapper;
    }

    @PostMapping(value = "/laboratories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> newLaboratory(@RequestBody LabWithRowsDTO labWithRowsDTO) throws OHServiceException {

        LaboratoryDTO laboratoryDTO = labWithRowsDTO.getLaboratoryDTO();
        List<String> labRow = labWithRowsDTO.getLaboratoryRowList();

        Patient patient = patientBrowserManager.getPatient(laboratoryDTO.getPatientCode());
        if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
        }

        Exam exam = examManager.getExams().stream().filter(e -> e.getCode().equals(laboratoryDTO.getExam().getCode())).findFirst().orElse(null);
        if (exam == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam not found!", OHSeverityLevel.ERROR));
        }

        Laboratory labToInsert = laboratoryMapper.map2Model(laboratoryDTO);
        labToInsert.setExam(exam);
        labToInsert.setPatient(patient);

        ArrayList<String> labRows = new ArrayList<>();
        if (labRow != null) {
            labRows = new ArrayList<>(labRow);
        }

        boolean inserted = laboratoryManager.newLaboratory(labToInsert, labRows);

        if (!inserted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Laboratory is not created!", OHSeverityLevel.ERROR));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    @PostMapping(value = "/laboratories/insertList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity newLaboratory2(@RequestBody List<LabWithRowsDTO> labsWithRows) throws OHServiceException {

        List<Laboratory> labsToInsert = new ArrayList<>();
        ArrayList<ArrayList<LaboratoryRow>> labsRowsToInsert = new ArrayList<>();

        for (LabWithRowsDTO labWithRowsDTO : labsWithRows) {
            LaboratoryDTO laboratoryDTO = labWithRowsDTO.getLaboratoryDTO();
            Patient patient = patientBrowserManager.getPatient(laboratoryDTO.getPatientCode());
            if (patient == null) {
                throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
            }

            Exam exam = examManager.getExams().stream().filter(e -> e.getCode().equals(laboratoryDTO.getExam().getCode())).findFirst().orElse(null);
            if (exam == null) {
                throw new OHAPIException(new OHExceptionMessage(null, "Exam not found!", OHSeverityLevel.ERROR));
            }

            Laboratory labToInsert = laboratoryMapper.map2Model(laboratoryDTO);
            labToInsert.setExam(exam);
            labToInsert.setPatient(patient);

            labsToInsert.add(labToInsert);

            if (labWithRowsDTO.getLaboratoryRowList() != null) {
                ArrayList<LaboratoryRow> labRowToInsert = new ArrayList<>();
                for (String rowDescription : labWithRowsDTO.getLaboratoryRowList()) {
                    labRowToInsert.add(laboratoryRowMapper.map2Model(new LaboratoryRowDTO(rowDescription, laboratoryDTO)));
                }
                if (!labRowToInsert.isEmpty()) {
                    labsRowsToInsert.add(labRowToInsert);
                }
            }
        }

        boolean inserted = laboratoryManager.newLaboratory2(labsToInsert, labsRowsToInsert);

        if (!inserted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Laboratory is not created!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    @PutMapping(value = "/laboratories/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLaboratory(@PathVariable Integer code, @RequestBody LabWithRowsDTO labWithRowsDTO) throws OHServiceException {

        LaboratoryDTO laboratoryDTO = labWithRowsDTO.getLaboratoryDTO();
        List<String> labRow = labWithRowsDTO.getLaboratoryRowList();

        if (code != laboratoryDTO.getCode()) {
            throw new OHAPIException(new OHExceptionMessage(null, "Laboratory code mismatch!", OHSeverityLevel.ERROR));
        }

        if (laboratoryManager.getLaboratory().stream().noneMatch(l -> l.getCode() == code)) {
            throw new OHAPIException(new OHExceptionMessage(null, "Laboratory Not Found!", OHSeverityLevel.ERROR));
        }
        Patient patient = patientBrowserManager.getPatient(laboratoryDTO.getPatientCode());
        if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
        }

        Exam exam = examManager.getExams().stream().filter(e -> e.getCode().equals(laboratoryDTO.getExam().getCode())).findFirst().orElse(null);
        if (exam == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam not found!", OHSeverityLevel.ERROR));
        }

        Laboratory labToInsert = laboratoryMapper.map2Model(laboratoryDTO);
        labToInsert.setExam(exam);
        labToInsert.setPatient(patient);

        ArrayList<String> labRows = new ArrayList<>();
        if (labRow != null) {
            labRows = new ArrayList<>(labRow);
        }

        boolean updated = laboratoryManager.updateLaboratory(labToInsert, labRows);

        if (!updated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Laboratory is not updated!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(true);
    }

    @DeleteMapping(value = "/laboratories/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteExam(@PathVariable Integer code) throws OHServiceException {
        Laboratory labToDelete = laboratoryManager.getLaboratory().stream().filter(l -> l.getCode() == code).findFirst().orElse(null);
        if (labToDelete == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Laboratory Not Found!", OHSeverityLevel.ERROR));
        }
        if (!laboratoryManager.deleteLaboratory(labToDelete)) {
            throw new OHAPIException(new OHExceptionMessage(null, "Laboratory is not deleted!", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping(value = "/laboratories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LaboratoryDTO>> getLaboratory() throws OHServiceException {
        List<Laboratory> labList = laboratoryManager.getLaboratory();
        if (labList == null || labList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(laboratoryMapper.map2DTOList(labList));
        }
    }

    @GetMapping(value = "/laboratories/byPatientId/{patId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LaboratoryDTO>> getLaboratory(@PathVariable Integer patId) throws OHServiceException {
        Patient patient = patientBrowserManager.getPatient(patId);
        if (patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient not found!", OHSeverityLevel.ERROR));
        }

        List<Laboratory> labList = laboratoryManager.getLaboratory(patient);
        if (labList == null || labList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(laboratoryMapper.map2DTOList(labList));
        }
    }

    @GetMapping(value = "/laboratories/materials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getMaterials() throws OHServiceException {
        List<String> materialList = laboratoryManager.getMaterialList();
        if (materialList == null || materialList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(materialList);
        }
    }

    @GetMapping(value = "/laboratories/exams", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LaboratoryForPrintDTO>> getLaboratoryForPrint(@RequestParam String examName, @RequestParam(value = "dateFrom") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date dateFrom, @RequestParam(value = "dateTo") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date dateTo) throws OHServiceException {
        GregorianCalendar datefrom = new GregorianCalendar();
        datefrom.setTime(dateFrom);

        GregorianCalendar dateto = new GregorianCalendar();
        dateto.setTime(dateTo);

        List<LaboratoryForPrint> laboratoryForPrintList = laboratoryManager.getLaboratoryForPrint(examName, datefrom, dateto);
        if (laboratoryForPrintList == null || laboratoryForPrintList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(laboratoryForPrintMapper.map2DTOList(laboratoryForPrintList));
        }
    }

}
