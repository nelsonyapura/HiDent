package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.OralRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OralRevisionRepository extends JpaRepository<OralRevision, Long> {

    List<OralRevision> findByPatientIdPatientOrderByCreatedAtDesc(Long idPatient);

    Optional<OralRevision> findFirstByPatientIdPatientOrderByCreatedAtDesc(Long idPatient);
}
