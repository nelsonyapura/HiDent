package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.DentalService;
import com.odontologia.odontologia.repository.DentalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DentalServiceService {

    private final DentalServiceRepository repository;

    public List<DentalService> findAllActive() {
        return repository.findByStatusTrueOrderByCategoryAscSortOrderAsc();
    }

    public Map<String, List<DentalService>> findAllGroupedByCategory() {
        List<DentalService> all = findAllActive();

        return all.stream().collect(Collectors.groupingBy(
                DentalService::getCategory,
                LinkedHashMap::new,
                Collectors.toList()));
    }

    public List<DentalService> findByCategory(String category) {
        return repository.findByCategoryAndStatusTrueOrderBySortOrderAsc(category);
    }

    public List<DentalService> search(String term) {
        return repository.searchByTerm(term);
    }

    public DentalService findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + id));
    }

    @Transactional
    public DentalService create(String category, String name, BigDecimal unitPrice, String currency) {
        if (repository.findByCategoryAndName(category, name).isPresent()) {
            throw new RuntimeException("Ya existe un servicio con ese nombre en la categoría");
        }
        long count = repository.countByCategoryAndStatusTrue(category);
        DentalService service = new DentalService();
        service.setCategory(category);
        service.setName(name);
        service.setUnitPrice(unitPrice);
        service.setCurrency(currency != null ? currency : "PEN");
        service.setSortOrder((int) count + 1);
        service.setStatus(true);
        return repository.save(service);
    }

    @Transactional
    public DentalService updatePrice(Long id, BigDecimal newPrice) {
        DentalService service = findById(id);
        service.setUnitPrice(newPrice);
        return repository.save(service);
    }

    @Transactional
    public DentalService update(Long id, String name, BigDecimal unitPrice, String currency) {
        DentalService service = findById(id);
        if (name != null) service.setName(name);
        if (unitPrice != null) service.setUnitPrice(unitPrice);
        if (currency != null) service.setCurrency(currency);
        return repository.save(service);
    }

    @Transactional
    public void deactivate(Long id) {
        DentalService service = findById(id);
        service.setStatus(false);
        repository.save(service);
    }

    @Transactional
    public void activate(Long id) {
        DentalService service = findById(id);
        service.setStatus(true);
        repository.save(service);
    }

    public List<String> getCategories() {
        return repository.findDistinctCategories();
    }
}
