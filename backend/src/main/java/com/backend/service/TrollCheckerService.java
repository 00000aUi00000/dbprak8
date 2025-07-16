package com.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.backend.entity.Kunde;
import com.backend.repository.KundeRepository;
import com.backend.repository.RezensionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrollCheckerService {

    private final KundeRepository kundeRepository;
    private final RezensionRepository rezensionRepository;

    @Transactional
    public List<TrollDTO> getTrollsBelowRating(double maxRating) {
        return kundeRepository.findAll().stream()
                .map(kunde -> {
                    Double avg = rezensionRepository.averageRatingByKundeId(kunde.getKundeId());
                    return avg == null ? null : new TrollDTO(kunde.getKundeId(), kunde.getName(), avg);
                })
                .filter(dto -> dto != null && dto.avgRating() < maxRating)
                .collect(Collectors.toList());
    }

    public record TrollDTO(Long id, String name, double avgRating) {}
}
