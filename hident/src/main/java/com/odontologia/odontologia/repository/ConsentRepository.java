package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.Consent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsentRepository extends JpaRepository<Consent, Long> {

    List<Consent> findByPatientIdPatientOrderByCreatedAtDesc(Long idPatient);
}
