package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.*;
import com.odontologia.odontologia.repository.BudgetItemRepository;
import com.odontologia.odontologia.repository.BudgetRepository;
import com.odontologia.odontologia.repository.DentalServiceRepository;
import com.odontologia.odontologia.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository itemRepository;
    private final PatientRepository patientRepository;
    private final DentalServiceRepository serviceRepository;

    public List<Budget> findAll() {
        return budgetRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Budget> findByPatient(Long patientId) {
        return budgetRepository.findByPatientIdPatientOrderByCreatedAtDesc(patientId);
    }

    public List<Budget> findByStatus(String status) {
        return budgetRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public List<Budget> search(String term) {
        return budgetRepository.searchByPatientTerm(term);
    }

    public Budget findByIdWithItems(Long id) {
        return budgetRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + id));
    }

    public Budget findById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado: " + id));
    }

    @Transactional
    public Budget create(Long patientId, String budgetName, String doctorName,
                         String patientNote, String internalNote, String username) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado: " + patientId));

        Budget budget = new Budget();
        budget.setPatient(patient);
        budget.setBudgetName(budgetName);
        budget.setDoctorName(doctorName);
        budget.setPatientNote(patientNote);
        budget.setInternalNote(internalNote);
        budget.setStatus("BORRADOR");
        budget.setTotal(BigDecimal.ZERO);
        budget.setCurrency("PEN");
        budget.setCreatedBy(username);
        budget.setUpdatedBy(username);

        return budgetRepository.save(budget);
    }

    @Transactional
    public BudgetItem addItem(Long budgetId, Long serviceId, String tooth,
                              Integer quantity, BigDecimal unitPrice,
                              BigDecimal discount, String comment) {
        Budget budget = findById(budgetId);
        DentalService service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + serviceId));

        BudgetItem item = new BudgetItem();
        item.setBudget(budget);
        item.setService(service);
        item.setServiceName(service.getName());
        item.setCategory(service.getCategory());
        item.setTooth(tooth);
        item.setQuantity(quantity != null ? quantity : 1);
        item.setUnitPrice(unitPrice != null ? unitPrice : service.getUnitPrice());
        item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
        item.setCurrency(service.getCurrency());
        item.setSortOrder(budget.getItems().size() + 1);
        item.calculateSubtotal();

        BudgetItem saved = itemRepository.save(item);

        budget.getItems().add(saved);
        budget.recalculateTotal();
        budgetRepository.save(budget);

        return saved;
    }

    @Transactional
    public BudgetItem updateItem(Long budgetId, Long itemId, String tooth,
                                  Integer quantity, BigDecimal unitPrice,
                                  BigDecimal discount, String comment) {

        Budget budget = findByIdWithItems(budgetId);
        BudgetItem item = budget.getItems().stream()
                .filter(i -> i.getIdItem().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado en este presupuesto"));

        if (tooth != null) item.setTooth(tooth.isBlank() ? null : tooth.trim());
        if (quantity != null && quantity > 0) item.setQuantity(quantity);
        if (unitPrice != null) item.setUnitPrice(unitPrice);
        if (discount != null) item.setDiscount(discount);
        if (comment != null) item.setComment(comment.isBlank() ? null : comment.trim());

        item.calculateSubtotal();
        itemRepository.save(item);

        budget.recalculateTotal();
        budgetRepository.save(budget);

        return item;
    }

    @Transactional
    public void removeItem(Long budgetId, Long itemId) {
        Budget budget = findByIdWithItems(budgetId);
        budget.getItems().removeIf(i -> i.getIdItem().equals(itemId));
        budget.recalculateTotal();
        budgetRepository.save(budget);
    }

    @Transactional
    public Budget updateInfo(Long budgetId, String budgetName, String doctorName,
                             String patientNote, String internalNote, String username) {
        Budget budget = findById(budgetId);
        if (budgetName != null) budget.setBudgetName(budgetName);
        if (doctorName != null) budget.setDoctorName(doctorName);
        if (patientNote != null) budget.setPatientNote(patientNote);
        if (internalNote != null) budget.setInternalNote(internalNote);
        budget.setUpdatedBy(username);
        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget changeStatus(Long budgetId, String newStatus, String username) {
        Budget budget = findById(budgetId);
        budget.setStatus(newStatus);
        budget.setUpdatedBy(username);
        return budgetRepository.save(budget);
    }

    @Transactional
    public void delete(Long budgetId) {
        budgetRepository.deleteById(budgetId);
    }

    public long countByPatient(Long patientId) {
        return budgetRepository.countByPatientIdPatient(patientId);
    }

    @Transactional
    public Budget createWithItems(Long patientId, String budgetName, String doctorName,
                                  String patientNote, String internalNote, String username,
                                  List<ItemRequest> itemRequests) {
        Budget budget = create(patientId, budgetName, doctorName, patientNote, internalNote, username);

        for (int i = 0; i < itemRequests.size(); i++) {
            ItemRequest req = itemRequests.get(i);
            DentalService service = serviceRepository.findById(req.serviceId())
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

            BudgetItem item = new BudgetItem();
            item.setBudget(budget);
            item.setService(service);
            item.setServiceName(service.getName());
            item.setCategory(service.getCategory());
            item.setTooth(req.tooth());
            item.setQuantity(req.quantity() != null ? req.quantity() : 1);
            item.setUnitPrice(req.unitPrice() != null ? req.unitPrice() : service.getUnitPrice());
            item.setDiscount(req.discount() != null ? req.discount() : BigDecimal.ZERO);
            item.setCurrency(service.getCurrency());
            item.setSortOrder(i + 1);
            item.setComment(req.comment());
            item.calculateSubtotal();

            budget.getItems().add(item);
        }

        budget.recalculateTotal();
        return budgetRepository.save(budget);
    }

    public record ItemRequest(
            Long serviceId,
            String tooth,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal discount,
            String comment
    ) {}
}
