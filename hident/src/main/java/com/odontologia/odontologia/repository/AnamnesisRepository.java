package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.Anamnesis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnamnesisRepository extends JpaRepository<Anamnesis, Long> {

    Optional<Anamnesis> findByPatientIdPatient(Long idPatient);

    boolean existsByPatientIdPatient(Long idPatient);
}
