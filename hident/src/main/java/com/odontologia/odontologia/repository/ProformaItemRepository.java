package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.ProformaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProformaItemRepository extends JpaRepository<ProformaItem, Long> {

    List<ProformaItem> findByProformaIdProformaOrderBySortOrderAsc(Long proformaId);

    List<ProformaItem> findByProformaIdProformaAndCompletedTrueOrderByCompletedAtAsc(Long proformaId);

    List<ProformaItem> findByProformaIdProformaAndCompletedFalseOrderBySortOrderAsc(Long proformaId);
}
