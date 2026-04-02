package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.EvolutionNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvolutionNoteRepository extends JpaRepository<EvolutionNote, Long> {

    List<EvolutionNote> findByPatientIdPatientOrderByNoteDateDesc(Long idPatient);
}
