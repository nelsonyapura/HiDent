package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.DentalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DentalServiceRepository extends JpaRepository<DentalService, Long> {

    List<DentalService> findByStatusTrueOrderByCategoryAscSortOrderAsc();

    List<DentalService> findByCategoryAndStatusTrueOrderBySortOrderAsc(String category);

    @Query("SELECT s FROM DentalService s WHERE s.status = true " +
           "AND LOWER(s.name) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "ORDER BY s.category, s.sortOrder")
    List<DentalService> searchByTerm(@Param("term") String term);

    Optional<DentalService> findByCategoryAndName(String category, String name);

    long countByCategoryAndStatusTrue(String category);

    @Query("SELECT DISTINCT s.category FROM DentalService s WHERE s.status = true ORDER BY s.category")
    List<String> findDistinctCategories();
}
