package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.model.Proforma;
import com.odontologia.odontologia.model.ProformaItem;
import com.odontologia.odontologia.repository.ProformaItemRepository;
import com.odontologia.odontologia.repository.ProformaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProformaService {

    private final ProformaRepository proformaRepository;
    private final ProformaItemRepository itemRepository;
    private final PatientService patientService;

    public List<Proforma> findAll() {
        return proformaRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Proforma> findByPatient(Long patientId) {
        return proformaRepository.findByPatientIdPatientOrderByCreatedAtDesc(patientId);
    }

    public List<Proforma> search(String term) {
        return proformaRepository.searchByPatientTerm(term);
    }

    public Proforma findByIdWithItems(Long id) {
        return proformaRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Proforma no encontrada: " + id));
    }

    public Optional<Proforma> findById(Long id) {
        return proformaRepository.findById(id);
    }

    @Transactional
    public Proforma createWithItems(Long patientId, String proformaName, String doctorName,
                                     String notes, String actor, List<ItemRequest> items) {

        Patient patient = patientService.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Proforma proforma = new Proforma();
        proforma.setPatient(patient);
        proforma.setProformaName(proformaName);
        proforma.setDoctorName(doctorName);
        proforma.setNotes(notes);
        proforma.setCreatedBy(actor);
        proforma.setStatus("ACTIVA");

        int order = 0;
        for (ItemRequest req : items) {
            ProformaItem item = new ProformaItem();
            item.setProforma(proforma);
            item.setServiceId(req.serviceId());
            item.setServiceName(req.serviceName());
            item.setDescription(req.description());
            item.setPrice(req.price() != null ? req.price() : BigDecimal.ZERO);
            item.setCurrency(req.currency() != null ? req.currency() : "PEN");
            item.setSortOrder(++order);
            item.setCompleted(false);
            proforma.getItems().add(item);
        }

        proforma.recalculateTotal();
        return proformaRepository.save(proforma);
    }

    @Transactional
    public ProformaItem addItem(Long proformaId, Long serviceId, String serviceName,
                                 String description, BigDecimal price, String currency) {

        Proforma proforma = findByIdWithItems(proformaId);

        int nextOrder = proforma.getItems().stream()
                .mapToInt(ProformaItem::getSortOrder)
                .max().orElse(0) + 1;

        ProformaItem item = new ProformaItem();
        item.setProforma(proforma);
        item.setServiceId(serviceId);
        item.setServiceName(serviceName);
        item.setDescription(description);
        item.setPrice(price != null ? price : BigDecimal.ZERO);
        item.setCurrency(currency != null ? currency : "PEN");
        item.setSortOrder(nextOrder);
        item.setCompleted(false);

        proforma.getItems().add(item);
        proforma.recalculateTotal();
        proformaRepository.save(proforma);

        return item;
    }

    @Transactional
    public void updateItem(Long proformaId, Long itemId, String serviceName,
                           String description, BigDecimal price) {

        Proforma proforma = findByIdWithItems(proformaId);
        ProformaItem item = proforma.getItems().stream()
                .filter(i -> i.getIdItem().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado: " + itemId));

        if (serviceName != null) item.setServiceName(serviceName);
        if (description != null) item.setDescription(description);
        if (price != null) item.setPrice(price);

        proforma.recalculateTotal();
        proformaRepository.save(proforma);
    }

    @Transactional
    public void removeItem(Long proformaId, Long itemId) {
        Proforma proforma = findByIdWithItems(proformaId);
        proforma.getItems().removeIf(i -> i.getIdItem().equals(itemId));
        proforma.recalculateTotal();
        proformaRepository.save(proforma);
    }

    @Transactional
    public void markItemsCompleted(Long proformaId, List<Long> itemIds, String actor) {
        Proforma proforma = findByIdWithItems(proformaId);

        proforma.getItems().stream()
                .filter(i -> itemIds.contains(i.getIdItem()))
                .filter(i -> !Boolean.TRUE.equals(i.getCompleted()))
                .forEach(i -> i.markCompleted(actor));

        proforma.setUpdatedBy(actor);

        boolean allDone = proforma.getItems().stream()
                .allMatch(i -> Boolean.TRUE.equals(i.getCompleted()));
        if (allDone) {
            proforma.setStatus("COMPLETADA");
        }

        proformaRepository.save(proforma);
    }

    @Transactional
    public void changeStatus(Long id, String newStatus, String actor) {
        Proforma proforma = proformaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proforma no encontrada"));
        proforma.setStatus(newStatus);
        proforma.setUpdatedBy(actor);
        proformaRepository.save(proforma);
    }

    @Transactional
    public void delete(Long id) {
        proformaRepository.deleteById(id);
    }

    public record ItemRequest(Long serviceId, String serviceName, String description,
                               BigDecimal price, String currency) {}
}
