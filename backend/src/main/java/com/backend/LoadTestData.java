package main.java.com.backend;

import main.java.com.backend.model.Product;
import main.java.com.backend.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadTestData {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            repository.save(new Product("A1", "Erstes Produkt"));
            repository.save(new Product("A2", "Zweites Produkt"));
        };
    }
}