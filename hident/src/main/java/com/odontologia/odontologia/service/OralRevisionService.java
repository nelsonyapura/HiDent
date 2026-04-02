package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.OralRevision;
import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.repository.OralRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OralRevisionService {

    private final OralRevisionRepository oralRevisionRepository;

    public List<OralRevision> findByPatientId(Long idPatient) {
        return oralRevisionRepository.findByPatientIdPatientOrderByCreatedAtDesc(idPatient);
    }

    public Optional<OralRevision> findLatestByPatientId(Long idPatient) {
        return oralRevisionRepository.findFirstByPatientIdPatientOrderByCreatedAtDesc(idPatient);
    }

    public Optional<OralRevision> findById(Long id) {
        return oralRevisionRepository.findById(id);
    }

    public OralRevision create(Patient patient, OralRevision data, String createdBy) {
        OralRevision revision = new OralRevision();
        revision.setPatient(patient);
        copyFields(data, revision);
        revision.setCreatedBy(clean(createdBy));
        return oralRevisionRepository.save(revision);
    }

    public OralRevision update(Long idRevision, OralRevision data, String updatedBy) {
        OralRevision revision = oralRevisionRepository.findById(idRevision)
                .orElseThrow(() -> new RuntimeException("Revisión no encontrada"));
        copyFields(data, revision);
        revision.setUpdatedBy(clean(updatedBy));
        return oralRevisionRepository.save(revision);
    }

    private void copyFields(OralRevision source, OralRevision target) {
        target.setLabios(cleanDefault(source.getLabios(), "Sin alteraciones"));
        target.setCarrillos(cleanDefault(source.getCarrillos(), "Sin alteraciones"));
        target.setPaladarDuro(cleanDefault(source.getPaladarDuro(), "Sin alteraciones"));
        target.setPaladarBlando(cleanDefault(source.getPaladarBlando(), "Sin alteraciones"));
        target.setEncias(cleanDefault(source.getEncias(), "Sin alteraciones"));
        target.setLengua(cleanDefault(source.getLengua(), "Sin alteraciones"));
        target.setPisoBoca(cleanDefault(source.getPisoBoca(), "Sin alteraciones"));
        target.setOrofaringe(cleanDefault(source.getOrofaringe(), "Sin alteraciones"));
        target.setAtm(cleanDefault(source.getAtm(), "Sin alteraciones"));
        target.setHigiene(cleanDefault(source.getHigiene(), "Regular"));
        target.setObservations(clean(source.getObservations()));
    }

    private String clean(String val) {
        return val != null && !val.isBlank() ? val.trim() : null;
    }

    private String cleanDefault(String val, String defaultVal) {
        return val != null && !val.isBlank() ? val.trim() : defaultVal;
    }
}
