package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.Proforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProformaRepository extends JpaRepository<Proforma, Long> {

    List<Proforma> findByPatientIdPatientOrderByCreatedAtDesc(Long patientId);

    List<Proforma> findAllByOrderByCreatedAtDesc();

    List<Proforma> findByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT p FROM Proforma p JOIN p.patient pat " +
           "WHERE LOWER(pat.firstName) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(pat.lastName) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR pat.dni LIKE CONCAT('%', :term, '%') " +
           "ORDER BY p.createdAt DESC")
    List<Proforma> searchByPatientTerm(@Param("term") String term);

    @Query("SELECT p FROM Proforma p LEFT JOIN FETCH p.items WHERE p.idProforma = :id")
    Optional<Proforma> findByIdWithItems(@Param("id") Long id);

    long countByPatientIdPatient(Long patientId);

    List<Proforma> findByPatientIdPatientAndStatusOrderByCreatedAtDesc(Long patientId, String status);
}
