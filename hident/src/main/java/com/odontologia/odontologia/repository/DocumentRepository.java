package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByPatientIdPatientOrderByCreatedAtDesc(Long idPatient);

    Optional<Document> findByIdDocumentAndPatientIdPatient(Long idDocument, Long idPatient);
}
