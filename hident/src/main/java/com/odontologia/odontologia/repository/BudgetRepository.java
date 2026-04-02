package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByPatientIdPatientOrderByCreatedAtDesc(Long patientId);

    List<Budget> findAllByOrderByCreatedAtDesc();

    List<Budget> findByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT b FROM Budget b JOIN b.patient p " +
           "WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR p.dni LIKE CONCAT('%', :term, '%') " +
           "ORDER BY b.createdAt DESC")
    List<Budget> searchByPatientTerm(@Param("term") String term);

    @Query("SELECT b FROM Budget b LEFT JOIN FETCH b.items WHERE b.idBudget = :id")
    Optional<Budget> findByIdWithItems(@Param("id") Long id);

    long countByPatientIdPatient(Long patientId);
}
