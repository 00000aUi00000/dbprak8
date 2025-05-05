package main.java.com.backend.repository;

import main.java.com.backend.entity.Produkt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduktRepository extends JpaRepository<Produkt, String> {}