package com.frontend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;

import com.backend.entity.Buch;
import com.backend.entity.DVD;
import com.backend.entity.Kategorie;
import com.backend.entity.MusikCD;
import com.backend.entity.Produkt;
import com.frontend.dto.AngebotDTO;
import com.frontend.dto.BuchDetailsDTO;
import com.frontend.dto.DVDDetailsDTO;
import com.frontend.dto.MusikCDDetailsDTO;
import com.frontend.dto.ProduktDetailsDTO;
import com.frontend.dto.ProduktDto;
import com.frontend.dto.TopProduktDTO;
import com.frontend.model.Category;

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
        if (emf != null && emf.isOpen())
            emf.close();
    }

    @Override
    public Object getProduct(String produktId) {
        checkConnection();

        if (produktId == null) {
            throw new IllegalStateException("Invalide Produkt-ID: " + produktId);
        }

        if (produktId.isBlank()) {
            throw new IllegalStateException("Keine Produkt-ID angegeben.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            String hql = "SELECT p FROM Produkt p WHERE p.produktId=:produktId";
            TypedQuery<Produkt> query = em.createQuery(hql, Produkt.class);

            query.setParameter("produktId", produktId);

            List<Produkt> produkte = query.getResultList();

            if (produkte.isEmpty()) {
                throw new IllegalStateException("Kein Produkt für ID " + produktId + " gefunden.");
            }

            if (produkte.size() > 1) {
                throw new IllegalStateException("Mehrere Produkte für ID " + produktId + " gefunden.");
            }

            Produkt produkt = produkte.get(0);
            String typ = produkt.getClass().getSimpleName();

            if (produkt instanceof Buch buch) {
                return new BuchDetailsDTO(buch);
            } else if (produkt instanceof DVD dvd) {
                return new DVDDetailsDTO(dvd);
            } else if (produkt instanceof MusikCD musikCD) {
                return new MusikCDDetailsDTO(musikCD);
            } else {
                return new ProduktDetailsDTO(produkt, typ); // fallback
            }
        }
    }

    @Override
    public List<Object> getProducts(String pattern) {
        checkConnection();

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
    public List<Object> getCategoryTree() {
        checkConnection();

        try (EntityManager em = emf.createEntityManager()) {
            String hql = "SELECT k " +
                    "FROM Kategorie k " +
                    "WHERE k.parentKategorie IS NULL";
            TypedQuery<Kategorie> query = em.createQuery(hql, Kategorie.class);

            List<Kategorie> mainCategories = query.getResultList();
            List<Category> result = convertCategories(Set.copyOf(mainCategories));

            return new ArrayList<>(result);
        }
    }

    @Override
    public List<Object> getProductsByCategoryPath(String pfad) {
        return null;
    }

    @Override
    public List<Object> getTopProducts(int k) {
        checkConnection();

        if (k < 1) {
            throw new IllegalStateException("Invalide Angabe der (Top k)-Elemente: " + k);
        }

        try (EntityManager em = emf.createEntityManager()) {
            String hql = "SELECT new com.frontend.dto.TopProduktDTO(p, (SELECT COUNT(*) FROM Rezension r WHERE r.produkt.produktId = p.produktId) AS anzahlR) "
                    +
                    "FROM Produkt p " +
                    "WHERE p.rating IS NOT NULL " +
                    "ORDER BY p.rating DESC, anzahlR DESC, p.produktId ASC " +
                    "LIMIT :k";
            TypedQuery<TopProduktDTO> query = em.createQuery(hql, TopProduktDTO.class);

            query.setParameter("k", k);

            List<TopProduktDTO> produkte = query.getResultList();

            return new ArrayList<>(produkte);
        }
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
        checkConnection();

        if (produktId == null) {
            throw new IllegalStateException("Invalide Produkt-ID: " + produktId);
        }

        if (produktId.isBlank()) {
            throw new IllegalStateException("Keine Produkt-ID angegeben.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            String hql = "SELECT new com.frontend.dto.AngebotDTO(a.filiale, ad) "
                    +
                    "FROM Angebot a " +
                    "JOIN Angebotsdetails ad ON a.angebotId = ad.angebot.angebotId " +
                    "WHERE a.produkt.produktId=:produktId AND preis IS NOT NULL";
            TypedQuery<AngebotDTO> query = em.createQuery(hql, AngebotDTO.class);

            query.setParameter("produktId", produktId);

            final List<AngebotDTO> angebote = query.getResultList();

            return new ArrayList<>(angebote);
        }
    }

    private void checkConnection() {
        if (emf == null) {
            throw new IllegalStateException("Verbindung nicht initialisiert. Bitte zuerst init() ausführen.");
        }
    }

    private List<Category> convertCategories(Set<Kategorie> categories) {
        final List<Category> result = new ArrayList<>();

        for (final Kategorie kategorie : categories) {
            final List<Category> childs = convertCategories(kategorie.getUnterkategorien());

            result.add(new Category(kategorie.getKategorieId(), kategorie.getName(), childs));
        }

        return result;
    }

}
