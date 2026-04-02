package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.Anamnesis;
import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.repository.AnamnesisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnamnesisService {

    private final AnamnesisRepository anamnesisRepository;

    public Optional<Anamnesis> findByPatientId(Long idPatient) {
        return anamnesisRepository.findByPatientIdPatient(idPatient);
    }

    public Anamnesis saveOrUpdate(Patient patient, Anamnesis data) {
        Anamnesis anamnesis = anamnesisRepository.findByPatientIdPatient(patient.getIdPatient())
                .orElse(new Anamnesis());

        anamnesis.setPatient(patient);

        anamnesis.setHypertension(bool(data.getHypertension()));
        anamnesis.setDiabetes(bool(data.getDiabetes()));
        anamnesis.setHeartDisease(bool(data.getHeartDisease()));
        anamnesis.setHepatitis(bool(data.getHepatitis()));
        anamnesis.setHiv(bool(data.getHiv()));
        anamnesis.setTuberculosis(bool(data.getTuberculosis()));
        anamnesis.setAsthma(bool(data.getAsthma()));
        anamnesis.setEpilepsy(bool(data.getEpilepsy()));
        anamnesis.setKidneyDisease(bool(data.getKidneyDisease()));
        anamnesis.setBleedingDisorders(bool(data.getBleedingDisorders()));
        anamnesis.setOtherConditions(clean(data.getOtherConditions()));

        anamnesis.setCurrentMedications(clean(data.getCurrentMedications()));

        anamnesis.setAllergyPenicillin(bool(data.getAllergyPenicillin()));
        anamnesis.setAllergyLatex(bool(data.getAllergyLatex()));
        anamnesis.setAllergyAnesthesia(bool(data.getAllergyAnesthesia()));
        anamnesis.setAllergyNsaids(bool(data.getAllergyNsaids()));
        anamnesis.setOtherAllergies(clean(data.getOtherAllergies()));

        anamnesis.setSmoker(bool(data.getSmoker()));
        anamnesis.setAlcohol(bool(data.getAlcohol()));
        anamnesis.setBruxism(bool(data.getBruxism()));

        anamnesis.setPreviousSurgeries(clean(data.getPreviousSurgeries()));
        anamnesis.setLastDentalVisit(clean(data.getLastDentalVisit()));

        anamnesis.setPregnant(bool(data.getPregnant()));
        anamnesis.setBreastfeeding(bool(data.getBreastfeeding()));

        anamnesis.setObservations(clean(data.getObservations()));

        return anamnesisRepository.save(anamnesis);
    }

    private Boolean bool(Boolean val) { return val != null && val; }
    private String clean(String val) { return val != null && !val.isBlank() ? val.trim() : null; }
}
