package com.frontend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;

import com.backend.entity.Produkt;
import com.frontend.dto.ProduktDto;

@Service
public class ApplicationService implements ApplicationInterface {

    private EntityManagerFactory emf;

    @Override
    public void init(Properties props) {
        try {
            Configuration cfg = new Configuration();

            cfg.setProperty("hibernate.connection.url", props.getProperty("hibernate.connection.url"));
            cfg.setProperty("hibernate.connection.username", props.getProperty("hibernate.connection.username"));
            cfg.setProperty("hibernate.connection.password", props.getProperty("hibernate.connection.password"));
            cfg.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            cfg.setProperty("hibernate.dialect", props.getProperty("hibernate.dialect"));
            cfg.setProperty("hibernate.hbm2ddl.auto", "none");


            // Entity-Klassen (aus com.backend.entity)
            cfg.addAnnotatedClass(com.backend.entity.Produkt.class);
            cfg.addAnnotatedClass(com.backend.entity.Buch.class);
            cfg.addAnnotatedClass(com.backend.entity.DVD.class);
            cfg.addAnnotatedClass(com.backend.entity.MusikCD.class);
            cfg.addAnnotatedClass(com.backend.entity.Kategorie.class);
            cfg.addAnnotatedClass(com.backend.entity.Angebot.class);
            cfg.addAnnotatedClass(com.backend.entity.Kauf.class);
            cfg.addAnnotatedClass(com.backend.entity.Kunde.class);
            cfg.addAnnotatedClass(com.backend.entity.Rezension.class);
            cfg.addAnnotatedClass(com.backend.entity.Person.class);
            cfg.addAnnotatedClass(com.backend.entity.Filiale.class);

            // Beziehungsklassen
            cfg.addAnnotatedClass(com.backend.entity.AehnlichZu.class);
            cfg.addAnnotatedClass(com.backend.entity.AehnlichzuId.class);
            cfg.addAnnotatedClass(com.backend.entity.Angebotsdetails.class);
            cfg.addAnnotatedClass(com.backend.entity.AngebotsdetailsId.class);
            cfg.addAnnotatedClass(com.backend.entity.KaufDetails.class);
            cfg.addAnnotatedClass(com.backend.entity.KaufdetailsId.class);
            cfg.addAnnotatedClass(com.backend.entity.Autoren.class);
            cfg.addAnnotatedClass(com.backend.entity.AutorenId.class);
            cfg.addAnnotatedClass(com.backend.entity.Kuenstler.class);
            cfg.addAnnotatedClass(com.backend.entity.KuenstlerId.class);
            cfg.addAnnotatedClass(com.backend.entity.DVDRollen.class);
            cfg.addAnnotatedClass(com.backend.entity.DVDRollenId.class);
            cfg.addAnnotatedClass(com.backend.entity.Trackliste.class);
            cfg.addAnnotatedClass(com.backend.entity.Labelliste.class);

            SessionFactory sessionFactory = cfg.buildSessionFactory();
            this.emf = sessionFactory.unwrap(EntityManagerFactory.class);

            System.out.println("EMF erfolgreich erzeugt.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Erstellen der EntityManagerFactory", e);
        }
    }

    @Override
    public void finish() {
        if (emf != null && emf.isOpen()) emf.close();
    }

    @Override
    public Object getProduct(String produktId) {
        return null;
    }

    @Override
    public List<Object> getProducts(String pattern) {
        if (emf == null) {
            throw new IllegalStateException("Verbindung nicht initialisiert. Bitte zuerst init() ausführen.");
        }
        
        EntityManager em = emf.createEntityManager();
        try {
            String hql = "SELECT p FROM Produkt p";
            if (pattern != null && !pattern.trim().isEmpty()) {
                hql += " WHERE LOWER(p.titel) LIKE LOWER(:pattern)";
            }
            TypedQuery<Produkt> query = em.createQuery(hql, Produkt.class);

            if (pattern != null && !pattern.trim().isEmpty()) {
                query.setParameter("pattern", pattern.replace('*', '%'));
            }

            List<Produkt> produkte = query.getResultList();

            // Mapping zu DTO
            List<Object> dtoList = new ArrayList<>();
            for (Produkt p : produkte) {
                String typ = p.getClass().getSimpleName(); // z. B. „Buch“
                dtoList.add(new ProduktDto(p.getProduktId(), p.getTitel(), typ));
            }
            return dtoList;

        } finally {
            em.close();
        }
    }



    @Override
    public Object getCategoryTree() {
        return null;
    }

    @Override
    public List<Object> getProductsByCategoryPath(String pfad) {
        return null;
    }

    @Override
    public List<Object> getTopProducts(int k) {
        return null;
    }

    @Override
    public List<Object> getSimilarCheaperProduct(String produktId) {
        return null;
    }

    @Override
    public void addNewReview(Object review) {
    }

    @Override
    public List<Object> getTrolls(double maxRating) {
        return null;
    }

    @Override
    public List<Object> getOffers(String produktId) {
        return null;
    }
}
