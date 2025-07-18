package com.frontend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.backend.entity.Rezension;
import com.frontend.dto.AngebotDTO;
import com.frontend.dto.BuchDetailsDTO;
import com.frontend.dto.DVDDetailsDTO;
import com.frontend.dto.MusikCDDetailsDTO;
import com.frontend.dto.ProduktDetailsDTO;
import com.frontend.dto.ProduktDto;
import com.frontend.dto.RezensionDTO;
import com.frontend.dto.TopProduktDTO;
import com.frontend.dto.Category;

@Service
public class ApplicationService implements ApplicationInterface {

    private static final String PATH_DELIMITER = ">";

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
    public List<ProduktDto> getProducts(String pattern) {
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
            List<ProduktDto> dtoList = new ArrayList<>();
            for (Produkt p : produkte) {
                dtoList.add(new ProduktDto(
                    p.getProduktId(),
                    p.getTitel(),
                    p.getClass().getSimpleName(),
                    p.getRating(),
                    p.getVerkaufsrang(),
                    p.getErscheinungsdatum()
                ));
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
        checkConnection();

        if (pfad == null) {
            throw new IllegalStateException("Invalider Pfad: " + pfad);
        }

        if (pfad.isBlank()) {
            throw new IllegalStateException("Kein Pfad angegeben.");
        }

        String[] categories = pfad.split(PATH_DELIMITER);

        if (categories.length == 0) {
            throw new IllegalStateException("Bitte gebe die Kategorie getrennt an mit: \'" + PATH_DELIMITER + "\'");
        }

        try (EntityManager em = emf.createEntityManager()) {
            String hql = "SELECT k " +
                    "FROM Kategorie k " +
                    "WHERE k.parentKategorie IS NULL";
            TypedQuery<Kategorie> query = em.createQuery(hql, Kategorie.class);

            List<Kategorie> mainCategories = query.getResultList();
            Kategorie targetCategory = findCategoryByPath(mainCategories, categories);

            if (targetCategory == null) {
                throw new IllegalStateException("Konnte Kategorienpfad \'" + pfad + "\' nicht rekonstruieren.");
            }

            return findProductsOfCategory(targetCategory, em);
        }
    }

    @Override
    public List<Object> getTopProducts(int k, String typ) {
        checkConnection();

        if (k < 1) {
            throw new IllegalStateException("Invalide Angabe der (Top k)-Elemente: " + k);
        }

        try (EntityManager em = emf.createEntityManager()) {
            StringBuilder hql = new StringBuilder(
                "SELECT new com.frontend.dto.TopProduktDTO(p, " +
                "(SELECT COUNT(*) FROM Rezension r WHERE r.produkt.produktId = p.produktId)) " +
                "FROM Produkt p "
            );

            // Optionaler JOIN basierend auf Produkttyp
            if ("DVD".equalsIgnoreCase(typ)) {
                hql.append("JOIN DVD d ON p.produktId = d.produktId ");
            } else if ("MusikCD".equalsIgnoreCase(typ)) {
                hql.append("JOIN MusikCD m ON p.produktId = m.produktId ");
            } else if ("Buch".equalsIgnoreCase(typ)) {
                hql.append("JOIN Buch m ON p.produktId = m.produktId ");
            }
            
            hql.append("WHERE p.rating IS NOT NULL ");
            hql.append("ORDER BY p.rating DESC, " +
                    "(SELECT COUNT(*) FROM Rezension r WHERE r.produkt.produktId = p.produktId) DESC, " +
                    "p.produktId ASC");

            TypedQuery<TopProduktDTO> query = em.createQuery(hql.toString(), TopProduktDTO.class);
            query.setMaxResults(k);

            List<TopProduktDTO> produkte = query.getResultList();
            return new ArrayList<>(produkte);
        }
    }

    @Override
    public List<Object> getTrolls(double maxRating, boolean sortAsc) {
        checkConnection();

        try (EntityManager em = emf.createEntityManager()) {
            String order = sortAsc ? "ASC" : "DESC";

            String hql = """
                SELECT new com.frontend.dto.TrollDTO(
                    r.username,
                    AVG(r.punkte)
                )
                FROM Rezension r
                GROUP BY r.username
                HAVING AVG(r.punkte) < :maxRating
                ORDER BY AVG(r.punkte) """ + order;

            TypedQuery<com.frontend.dto.TrollDTO> query = em.createQuery(hql, com.frontend.dto.TrollDTO.class);
            query.setParameter("maxRating", maxRating);

            return new ArrayList<>(query.getResultList());
        }
    }

    @Override
    public List<Object> getSimilarCheaperProduct(String produktId) {
        checkConnection();

        try (EntityManager em = emf.createEntityManager()) {

            // 1. Preis des Ursprungsprodukts
            Double referenzPreis = em.createQuery("""
                SELECT MIN(ad.preis)
                FROM Angebot a
                JOIN a.angebotsdetails ad
                WHERE a.produkt.produktId = :produktId AND ad.preis IS NOT NULL
            """, Double.class).setParameter("produktId", produktId).getSingleResult();

            if (referenzPreis == null) {
                throw new IllegalStateException("Kein Preis für Produkt " + produktId + " gefunden.");
            }

            // 2. Ähnliche Produkte, deren billigster Preis günstiger ist
            String hql = """
                SELECT new com.frontend.dto.SimilarProductDTO(
                    p.produktId,
                    p.titel,
                    TYPE(p),
                    MIN(ad.preis)
                )
                FROM AehnlichZu az
                JOIN az.produktB p
                JOIN Angebot a ON a.produkt = p
                JOIN a.angebotsdetails ad
                WHERE az.produktA.produktId = :produktId AND ad.preis IS NOT NULL
                GROUP BY p.produktId, p.titel, TYPE(p)
                HAVING MIN(ad.preis) < :referenzPreis
            """;

            TypedQuery<com.frontend.dto.SimilarProductDTO> query = em.createQuery(hql, com.frontend.dto.SimilarProductDTO.class);
            query.setParameter("produktId", produktId);
            query.setParameter("referenzPreis", referenzPreis);

            return new ArrayList<>(query.getResultList());
        }
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

    private Kategorie findCategoryByPath(List<Kategorie> mainCategories, String[] categories) {
        Kategorie currentCategory = null;

        for (int i = 0; i < categories.length; i++) {
            final String category = categories[i];

            if (category == null) {
                return null;
            }

            currentCategory = ((i == 0) ? mainCategories : currentCategory.getUnterkategorien()).stream()
                    .filter(it -> it.getName() != null && it.getName().trim().equals(category.trim()))
                    .findAny()
                    .orElse(null);

            if (currentCategory == null) {
                return null;
            }
        }

        return currentCategory;
    }

    @Override
    public List<RezensionDTO> getRezensionenZuProdukt(String produktId) {
        checkConnection();
        try (EntityManager em = emf.createEntityManager()) {
            String hql = """
                SELECT new com.frontend.dto.RezensionDTO(
                    r.rezensionId,
                    r.kunde.kundeId,
                    r.produkt.produktId,
                    r.punkte,
                    r.zusammenfassung,
                    r.text,
                    r.username,
                    r.datum,
                    r.anzahlNuetzlich
                )
                FROM Rezension r
                WHERE r.produkt.produktId = :produktId
                ORDER BY r.datum DESC
            """;

            TypedQuery<RezensionDTO> query = em.createQuery(hql, RezensionDTO.class);
            query.setParameter("produktId", produktId);

            return query.getResultList();
        }
    }

    @Override
    public void addNewReview(Object reviewData) {
        checkConnection();

        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) reviewData;

        String produktId = map.get("produktId");
        String username = map.get("username");
        String zusammenfassung = map.get("zusammenfassung");
        String text = map.get("text");
        Integer punkte = Integer.parseInt(map.get("punkte"));

        if (produktId == null || username == null || punkte == null)
            throw new IllegalArgumentException("Fehlende Pflichtfelder.");

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Produkt produkt = em.find(Produkt.class, produktId);
            if (produkt == null)
                throw new IllegalArgumentException("Kein Produkt mit ID " + produktId + " gefunden.");

            Rezension rezension = new Rezension();
            rezension.setProdukt(produkt);
            rezension.setUsername(username);
            rezension.setZusammenfassung(zusammenfassung);
            rezension.setText(text);
            rezension.setPunkte(punkte);
            rezension.setDatum(LocalDate.now());
            rezension.setAnzahlNuetzlich(0);

            em.persist(rezension);
            em.getTransaction().commit();
        }
    }

    private List<Object> findProductsOfCategory(Kategorie kategorie, EntityManager em) {
        String hql = "SELECT p " +
                "FROM Produkt p JOIN p.kategorien k " +
                "WHERE k.kategorieId = :kId";
        TypedQuery<Produkt> query = em.createQuery(hql, Produkt.class);

        query.setParameter("kId", kategorie.getKategorieId());

        List<Produkt> products = query.getResultList();
        List<Object> dtoList = new ArrayList<>();

        for (Produkt p : products) {
            String typ = p.getClass().getSimpleName();
            dtoList.add(new ProduktDto(p.getProduktId(), p.getTitel(), typ));
        }

        return dtoList;
    }

}
