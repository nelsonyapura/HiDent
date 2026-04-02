package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByDniAndStatusTrue(String dni);

    boolean existsByDni(String dni);

    List<Patient> findByStatusTrueOrderByLastNameAscFirstNameAsc();

    @Query("SELECT p FROM Patient p WHERE p.status = true AND (" +
           "LOWER(p.dni) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :term, '%'))) " +
           "ORDER BY p.lastName ASC, p.firstName ASC")
    List<Patient> searchByTerm(@Param("term") String term);
}
