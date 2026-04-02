package com.odontologia.odontologia.service;

import com.odontologia.odontologia.dto.ToothUpdateRequest;
import com.odontologia.odontologia.model.OdontogramTooth;
import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.repository.OdontogramToothRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OdontogramService {

    private final OdontogramToothRepository toothRepository;

    public OdontogramService(OdontogramToothRepository toothRepository) {
        this.toothRepository = toothRepository;
    }

    public List<OdontogramTooth> findByPatient(Long idPatient, String nomenclature) {
        return toothRepository.findByPatientIdPatientAndNomenclature(idPatient, nomenclature);
    }

    public List<OdontogramTooth> findByTooth(Long idPatient, String toothCode, String nomenclature) {
        return toothRepository.findByPatientIdPatientAndToothCodeAndNomenclature(
                idPatient, toothCode, nomenclature);
    }

    public OdontogramTooth saveTooth(Patient patient, ToothUpdateRequest req, String updatedBy) {
        OdontogramTooth tooth = toothRepository
                .findByPatientIdPatientAndToothCodeAndNomenclatureAndCondition(
                        patient.getIdPatient(),
                        req.getToothCode(),
                        req.getNomenclature(),
                        req.getCondition())
                .orElse(new OdontogramTooth());

        tooth.setPatient(patient);
        tooth.setToothCode(req.getToothCode());
        tooth.setToothCodeEnd(clean(req.getToothCodeEnd()));
        tooth.setNomenclature(req.getNomenclature());
        tooth.setCondition(clean(req.getCondition()));
        tooth.setSurfaces(clean(req.getSurfaces()));
        tooth.setColor(clean(req.getColor()));
        tooth.setNote(clean(req.getNote()));
        tooth.setUpdatedBy(updatedBy);

        return toothRepository.save(tooth);
    }

    public void clearCondition(Long idPatient, String toothCode, String nomenclature, String condition) {
        toothRepository.deleteByToothAndCondition(idPatient, toothCode, nomenclature, condition);
    }

    public void clearTooth(Long idPatient, String toothCode, String nomenclature) {
        toothRepository.deleteAllByTooth(idPatient, toothCode, nomenclature);
    }

    private String clean(String val) {
        return val != null && !val.isBlank() ? val.trim() : null;
    }
}
