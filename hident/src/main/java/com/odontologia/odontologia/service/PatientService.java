package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Optional<Patient> findByDni(String dni) {
        return patientRepository.findByDniAndStatusTrue(dni);
    }

    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    public List<Patient> findAllActive() {
        return patientRepository.findByStatusTrueOrderByLastNameAscFirstNameAsc();
    }

    public List<Patient> search(String term) {
        if (term == null || term.isBlank()) return findAllActive();
        return patientRepository.searchByTerm(term.trim());
    }

    public void softDelete(Long id, String deletedBy) {
        Patient p = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        p.setStatus(false);
        p.setUpdatedBy(deletedBy);
        p.setUpdatedAt(LocalDateTime.now());
        patientRepository.save(p);
    }

    public Patient register(String dni, String firstName, String lastName,
                            String gender, String birthDateStr, String phone,
                            String email, String address, String district,
                            String allergies, String medicalNotes, String dentalHistory,
                            String guardianName, String guardianPhone,
                            String emergencyPhone, String ageStr, String createdBy) {

        if (dni == null || !dni.matches("\\d{8}"))
            throw new RuntimeException("El DNI debe tener exactamente 8 dígitos");
        if (firstName == null || firstName.isBlank())
            throw new RuntimeException("Los nombres son obligatorios");
        if (lastName == null || lastName.isBlank())
            throw new RuntimeException("Los apellidos son obligatorios");
        if (patientRepository.existsByDni(dni))
            throw new RuntimeException("Ya existe un paciente con DNI: " + dni);

        Patient p = new Patient();
        p.setDni(dni.trim());
        p.setFirstName(firstName.trim());
        p.setLastName(lastName.trim());
        p.setGender(clean(gender));
        p.setPhone(clean(phone));
        p.setEmail(clean(email));
        p.setAddress(clean(address));
        p.setDistrict(clean(district));
        p.setAllergies(clean(allergies));
        p.setMedicalNotes(clean(medicalNotes));
        p.setDentalHistory(clean(dentalHistory));
        p.setGuardianName(clean(guardianName));
        p.setGuardianPhone(clean(guardianPhone));
        p.setEmergencyPhone(clean(emergencyPhone));
        p.setCreatedBy(clean(createdBy));
        p.setStatus(true);

        if (birthDateStr != null && !birthDateStr.isBlank()) {
            try { p.setBirthDate(LocalDate.parse(birthDateStr)); } catch (Exception ignored) {}
        }

        if (ageStr != null && !ageStr.isBlank()) {
            try { p.setAge(Integer.parseInt(ageStr.trim())); } catch (Exception ignored) {}
        } else {
            p.recalculateAge();
        }

        return patientRepository.save(p);
    }

    public Patient update(Long id, String firstName, String lastName,
                          String gender, String birthDateStr, String phone,
                          String email, String address, String district,
                          String allergies, String medicalNotes, String dentalHistory,
                          String guardianName, String guardianPhone,
                          String emergencyPhone, String ageStr, String updatedBy) {

        Patient p = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (firstName != null && !firstName.isBlank()) p.setFirstName(firstName.trim());
        if (lastName  != null && !lastName.isBlank())  p.setLastName(lastName.trim());
        p.setGender(clean(gender));
        p.setPhone(clean(phone));
        p.setEmail(clean(email));
        p.setAddress(clean(address));
        p.setDistrict(clean(district));
        p.setAllergies(clean(allergies));
        p.setMedicalNotes(clean(medicalNotes));
        p.setDentalHistory(clean(dentalHistory));
        p.setGuardianName(clean(guardianName));
        p.setGuardianPhone(clean(guardianPhone));
        p.setEmergencyPhone(clean(emergencyPhone));
        p.setUpdatedBy(clean(updatedBy));
        p.setUpdatedAt(LocalDateTime.now());

        if (birthDateStr != null && !birthDateStr.isBlank()) {
            try { p.setBirthDate(LocalDate.parse(birthDateStr)); } catch (Exception ignored) {}
        }

        if (ageStr != null && !ageStr.isBlank()) {
            try { p.setAge(Integer.parseInt(ageStr.trim())); } catch (Exception ignored) {}
        } else {
            p.recalculateAge();
        }

        return patientRepository.save(p);
    }

    private String clean(String val) {
        return val != null && !val.isBlank() ? val.trim() : null;
    }
}
