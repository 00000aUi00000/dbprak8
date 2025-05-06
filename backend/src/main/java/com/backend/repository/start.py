from pathlib import Path

# Liste aller Entity-Klassen mit ggf. zusammengesetzten Schlüsseln
entities_with_ids = {
    "Aehnlichzu": "AehnlichzuId",
    "Angebot": "Long",
    "Angebotsdetails": "AngebotsdetailsId",
    "Autoren": "AutorenId",
    "Buch": "String",
    "DVD": "String",
    "DVDRollen": "DVDRollenId",
    "Filiale": "Long",
    "Kategorie": "Long",
    "Kauf": "Long",
    "Kaufdetails": "KaufdetailsId",
    "Kuenstler": "KuenstlerId",
    "Kunde": "Long",
    "MusikCD": "String",
    "Person": "Long",
    "Produkt": "String",
    "Produktkategorie": "ProduktkategorieId",
    "Rezension": "Long",
    "Trackliste": "Long"
}

base_package = "com.backend.repository"
output_dir = Path(".")
output_dir.mkdir(parents=True, exist_ok=True)

# Erzeuge Repository-Dateien
for entity, key_type in entities_with_ids.items():
    filename = output_dir / f"{entity}Repository.java"
    content = f"""package {base_package};

import com.backend.entity.{entity};
import org.springframework.data.jpa.repository.JpaRepository;

public interface {entity}Repository extends JpaRepository<{entity}, {key_type}> {{
}}
"""
    filename.write_text(content)

output_dir
