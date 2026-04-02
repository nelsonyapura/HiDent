package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.model.Budget;
import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.service.BudgetService;
import com.odontologia.odontologia.service.DentalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final DentalServiceService dentalServiceService;

    @GetMapping
    public String list(@AuthenticationPrincipal User user,
                       @RequestParam(value = "q", required = false) String query,
                       @RequestParam(value = "status", required = false) String status,
                       Model model) {

        if (user == null) return "redirect:/auth/login";

        List<Budget> budgets;
        if (query != null && !query.isBlank()) {
            budgets = budgetService.search(query);
        } else if (status != null && !status.isBlank() && !"TODOS".equals(status)) {
            budgets = budgetService.findByStatus(status);
        } else {
            budgets = budgetService.findAll();
        }

        model.addAttribute("username",   user.getUsername());
        model.addAttribute("name",       user.getName());
        model.addAttribute("activePage", "budgets");
        model.addAttribute("budgets",    budgets);
        model.addAttribute("totalCount", budgets.size());
        model.addAttribute("query",      query != null ? query : "");
        model.addAttribute("status",     status != null ? status : "TODOS");

        return "budget/list";
    }

    @GetMapping("/create")
    public String create(@AuthenticationPrincipal User user,
                         @RequestParam(value = "patientId", required = false) Long patientId,
                         Model model) {

        if (user == null) return "redirect:/auth/login";

        model.addAttribute("username",   user.getUsername());
        model.addAttribute("name",       user.getName());
        model.addAttribute("activePage", "budgets");
        model.addAttribute("doctorName", user.getName());
        model.addAttribute("patientId",  patientId);
        model.addAttribute("categories", dentalServiceService.getCategories());

        return "budget/create";
    }

    @GetMapping("/{id}")
    public String view(@AuthenticationPrincipal User user,
                       @PathVariable Long id,
                       Model model) {

        if (user == null) return "redirect:/auth/login";

        Budget budget = budgetService.findByIdWithItems(id);

        List<Budget> patientBudgets = budgetService.findByPatient(
                budget.getPatient().getIdPatient());

        model.addAttribute("username",        user.getUsername());
        model.addAttribute("name",            user.getName());
        model.addAttribute("activePage",      "budgets");
        model.addAttribute("budget",          budget);
        model.addAttribute("patientBudgets",  patientBudgets);
        model.addAttribute("patientBudgetCount", patientBudgets.size());

        return "budget/view";
    }
}
