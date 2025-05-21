package com.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.MainImportService;

// Entry Point der Spring-Boot-Application
@SpringBootApplication
@RestController
public class BackendApplication {

  // Main Methode, startet die Anwendung
  public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }

  /* Methode die am Start des Programms einmal ausgeführt wird und das Laden 
     der Daten mit den Importservice startet */
  @Bean
  public CommandLineRunner run(MainImportService importService) {
    return args -> {
      importService.importAll();
    };
  }

  // Test-GET-Mapping, Verwendbar zur Überprüfung, ob Anwendung läuft
  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

}