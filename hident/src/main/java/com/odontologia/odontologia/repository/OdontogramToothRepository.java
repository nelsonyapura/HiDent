package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.OdontogramTooth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OdontogramToothRepository extends JpaRepository<OdontogramTooth, Long> {

    List<OdontogramTooth> findByPatientIdPatientAndNomenclature(Long idPatient, String nomenclature);

    List<OdontogramTooth> findByPatientIdPatientAndToothCodeAndNomenclature(
            Long idPatient, String toothCode, String nomenclature);

    Optional<OdontogramTooth> findByPatientIdPatientAndToothCodeAndNomenclatureAndCondition(
            Long idPatient, String toothCode, String nomenclature, String condition);

    @Modifying
    @Transactional
    @Query("DELETE FROM OdontogramTooth t " +
           "WHERE t.patient.idPatient = :idPatient " +
           "AND t.toothCode = :toothCode " +
           "AND t.nomenclature = :nomenclature " +
           "AND t.condition = :condition")
    void deleteByToothAndCondition(@Param("idPatient")    Long idPatient,
                                   @Param("toothCode")    String toothCode,
                                   @Param("nomenclature") String nomenclature,
                                   @Param("condition")    String condition);

    @Modifying
    @Transactional
    @Query("DELETE FROM OdontogramTooth t " +
           "WHERE t.patient.idPatient = :idPatient " +
           "AND t.toothCode = :toothCode " +
           "AND t.nomenclature = :nomenclature")
    void deleteAllByTooth(@Param("idPatient")    Long idPatient,
                          @Param("toothCode")    String toothCode,
                          @Param("nomenclature") String nomenclature);

    @Modifying
    @Transactional
    @Query("DELETE FROM OdontogramTooth t " +
           "WHERE t.patient.idPatient = :idPatient " +
           "AND t.nomenclature = :nomenclature")
    void deleteByPatientAndNomenclature(@Param("idPatient")    Long idPatient,
                                        @Param("nomenclature") String nomenclature);
}
