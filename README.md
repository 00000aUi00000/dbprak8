# DB Praktikum Gruppe 8
Repo für Datenbankpraktikum Gruppe 8

# Done
- Github Umgebumg für schnelle Programmierung aufbauen
- Datenbank PostgresSQL Container in Docker-Compose einbinden
- Backend mit Springboot und Maven konfiguriert
- UML erstellt
- Relatives Datenbankschema erstellt
- Datenbankaufbau mittels Backend
- DDL Statements
- Backend Importfunktion etabliert
- Fehlerlogausgabe
- Integritätsbedingungen in DB und Backend

# HowTo´s
- Docker-Container <br>
Starten mit ```docker-compose up -d``` <br>
Beenden mit ```docker-compose down``` <br>

- Postgres Datenbank <br>
Verbindung zur DB via PostgresSQL-Client ```psql -h localhost -p 5432 -U postgres -d mediastore``` <br>
Anzeigen von Tabellen ```\dt``` <br>
Anzeigen von Inhalten einer Tabelle "product" ```select * from product;```

- Backend -> Springboot mit Maven <br>
Testen ohne Docker - mit der Konsole in Ordner Backend ```cd ./backend``` <br>
ausführen von ```mvnw spring-boot:run``` <br>
