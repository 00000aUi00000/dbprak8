package com.backend.service.parser;

import com.backend.service.dto.CategoriesData;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

import org.glassfish.jaxb.runtime.v2.runtime.IllegalAnnotationsException;

// Klasse zum Parsen einer XML-Kategorien Datei
@Slf4j
public class CategoriesFileParser {

     /**
     * Lädt die entsprechende XML-Datei und gibt die geparste {@link CategoriesData} zurück.
     * Gibt null zurück, wenn die Datei nicht geparst werden konnte und loggt diesen
     * Fehler.
     * 
     * @param xmlFile die Datei zum Parsen
     * @return die geparste Kategorie Daten oder null bei einem Fehler 
     */
    public CategoriesData parseCategoriesFile(File xmlFile) {
        try {
            JAXBContext context = JAXBContext.newInstance(CategoriesData.class); // Erstellen eines neuen Contexts mit CategoriesData
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (CategoriesData) unmarshaller.unmarshal(xmlFile); // Deserialisierung der XML-Datei zu ShopData Objekt
        } catch (JAXBException e) {
            if (e instanceof IllegalAnnotationsException ex) {
                ex.getErrors().forEach(err -> log.error(err.toString()));
            }
            log.error("Fehler beim Parsen der Datei: " + xmlFile.getName(), e); // Logging des Fehlers
            return null;
        }
    }

}
