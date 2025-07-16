package com.backend.service;

import com.backend.entity.Rezension;
import com.backend.repository.RezensionRepository;
import com.backend.service.dto.RezensionDTO;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RezensionService {

    private final RezensionRepository rezensionRepository;

    public RezensionService(RezensionRepository rezensionRepository) {
        this.rezensionRepository = rezensionRepository;
    }

    /**
     * Gibt alle Rezensionen zu einem bestimmten Produkt zurück.
     * @param produktId Die ID des Produkts.
     * @return Liste der zugehörigen Rezensionen.
     */
    public List<RezensionDTO> getRezensionenByProduktId(String produktId) {
        return rezensionRepository.findAllByProdukt_ProduktId(produktId)
                .stream()
                .map(RezensionDTO::fromEntity)
                .toList();
    }
}
