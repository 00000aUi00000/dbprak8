package com.backend.controller;

import com.backend.entity.Rezension;
import com.backend.service.RezensionService;
import com.backend.service.dto.RezensionDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rezension")
public class RezensionController {

    private final RezensionService rezensionService;

    public RezensionController(RezensionService rezensionService) {
        this.rezensionService = rezensionService;
    }

    /**
     * Gibt alle Rezensionen zu einem bestimmten Produkt zurück.
     * @param produktId Die Produkt-ID
     * @return Liste der Rezensionen
     */
    @GetMapping("/product/{produktId}")
    public ResponseEntity<List<RezensionDTO>> getRezensionenByProduktId(@PathVariable String produktId) {
        List<RezensionDTO> rezensionen = rezensionService.getRezensionenByProduktId(produktId);
        return ResponseEntity.ok(rezensionen);
    }
}
