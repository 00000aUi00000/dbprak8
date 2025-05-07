package com.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.MainImportService;

@SpringBootApplication
@RestController
public class BackendApplication {
    public static void main(String[] args) {
      SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(MainImportService importService) {
        return args -> {
            importService.importAll();
        };
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
      return String.format("Hello %s!", name);
    }
}