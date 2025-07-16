package com.frontend.controller;

import com.frontend.dto.ProduktDto;
import com.frontend.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Controller
public class UIController {

    @Autowired
    private ApplicationService service;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/init")
    @ResponseBody
    public String initApp() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            Properties props = new Properties();
            props.load(is);

            // Dynamische Umschaltung des Hostnamens
            String host = isRunningInDocker() ? "db" : "localhost";
            String url = "jdbc:postgresql://" + host + ":5432/mediastore";
            props.setProperty("hibernate.connection.url", url);

            service.init(props);
            return "Verbindung erfolgreich aufgebaut.";
        } catch (Exception e) {
            e.printStackTrace(); // für Log-Ausgabe
            return "Fehler beim Initialisieren: " + e.getMessage();
        }
    }

    private boolean isRunningInDocker() {
        return new File("/.dockerenv").exists();
    }

    @PostMapping("/finish")
    @ResponseBody
    public String finishApp() {
        service.finish();
        return "Verbindung geschlossen.";
    }

    @GetMapping("/getProduct")
    @ResponseBody
    public ResponseEntity<?> getProduct(@RequestParam(required = true) String id) {
        try {
            Object result = service.getProduct(id);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/getProducts")
    @ResponseBody
    public ResponseEntity<?> getProducts(@RequestParam(required = false) String pattern) {
        try {
            @SuppressWarnings("unchecked")
            List<ProduktDto> result = (List<ProduktDto>) (List<?>) service.getProducts(pattern);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", ex.getMessage()));
        }
    }


    @GetMapping("/getTopProducts")
    @ResponseBody
    public ResponseEntity<?> getTopProducts(@RequestParam(required = true) int max) {
        try {
            List<Object> result = service.getTopProducts(max);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/getCategoryTree")
    @ResponseBody
    public ResponseEntity<?> getCategoryTree() {
        try {
            List<Object> result = service.getCategoryTree();
            return ResponseEntity.ok(result);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/getProductsByCategoryPath")
    @ResponseBody
    public ResponseEntity<?> getProductsByCategoryPath(@RequestParam(required = true) String path) {
        try {
            List<Object> result = service.getProductsByCategoryPath(path);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/getOffers")
    @ResponseBody
    public ResponseEntity<?> getOffers(@RequestParam(required = true) String id) {
        try {
            List<Object> result = service.getOffers(id);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", ex.getMessage()));
        }
    }
    
    @GetMapping("/getTrolls")
    @ResponseBody
    public ResponseEntity<?> getTrolls(@RequestParam double maxRating,
                                    @RequestParam(required = false, defaultValue = "false") boolean asc) {
        try {
            List<Object> result = service.getTrolls(maxRating, asc);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/getSimilarCheaperProduct")
    @ResponseBody
    public ResponseEntity<?> getSimilarCheaperProduct(@RequestParam String id) {
        try {
            List<Object> result = service.getSimilarCheaperProduct(id);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/getRezensionen")
    @ResponseBody
    public ResponseEntity<?> getRezensionen(@RequestParam String produktId) {
        try {
            List<Object> result = new ArrayList<>(service.getRezensionenZuProdukt(produktId));
            return ResponseEntity.ok(result);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/addNewReview")
    @ResponseBody
    public ResponseEntity<?> addNewReview(@RequestBody Map<String, String> body) {
        try {
            service.addNewReview(body);
            return ResponseEntity.ok(Map.of("message", "Rezension erfolgreich gespeichert."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

}
