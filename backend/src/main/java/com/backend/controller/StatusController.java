package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.ConnectionStatusService;

@RestController
@RequestMapping("/status")
public class StatusController {

    @Autowired
    private ConnectionStatusService statusService;

    @GetMapping("/db")
    public ResponseEntity<String> getConnectionStatus() {
        if (statusService.isInitialized()) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NOT_INITIALIZED");
        }
    }
}
