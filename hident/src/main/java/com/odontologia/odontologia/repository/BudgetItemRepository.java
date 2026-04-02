package com.odontologia.odontologia.repository;

import com.odontologia.odontologia.model.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {

    List<BudgetItem> findByBudgetIdBudgetOrderBySortOrderAsc(Long budgetId);

    void deleteByBudgetIdBudget(Long budgetId);
}
