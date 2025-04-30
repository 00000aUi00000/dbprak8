# DB Praktikum Gruppe 8
Repo für Datenbankpraktikum Gruppe 8

# Done
- Github Umgebumg für schnelle Programmierung aufbauen
- Datenbank PostgresSQL Container in Docker-Compose einbinden
- Backend mit Springboot und Maven konfigurieren, Testdaten einspielen, mittels Postman abrufen
GET - http://localhost:8080/api/products <br>
POST - http://localhost:8080/api/products mit JSON: 
```{"asin": "A100", "title": "Demo via Postman"} ``` <br>


# Todo´s
- ERM erstellen
- Datenbank ableiten und mittels DDL erstellen lassen
- ...


# HowTo´s
- Docker-Container <br>
Starten mit ```docker-compose up -d``` <br>
Beenden mit ```docker-compose down``` <br>

- Postgres Datenbank <br>
Verbindung zur DB via PostgresSQL-Client ```psql -h localhost -p 5432 -U postgres -d mediastore``` <br>
Anzeigen von Tabellen ```\dt``` <br>
Anzeigen von Inhalten einer Tabelle "product ```select * from product;```

- Backend -> Springboot mit Maven <br>
Testen ohne Docker - mit der Konsole in Ordner Backend ```cd ./backend``` <br>
ausführen von ```mvnw spring-boot:run``` <br>
