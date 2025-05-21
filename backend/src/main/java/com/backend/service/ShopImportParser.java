package com.backend.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

import org.glassfish.jaxb.runtime.v2.runtime.IllegalAnnotationsException;

import com.backend.service.dto.ShopData;

// Klasse zum Parsen einer XML-Shop Datei
@Slf4j // Lombok-Annotation, stellt ein log-Objekt zur Verfügung
public class ShopImportParser {

    /**
     * Lädt die entsprechende XML-Datei und gibt die geparste {@link ShopData} zurück.
     * Gibt null zurück, wenn die Datei nicht geparst werden konnte und loggt diesen
     * Fehler.
     * 
     * @param xmlFile die Datei zum Parsen
     * @return die geparste Shop Daten oder bei einem Fehler null
     */
    public ShopData parseShopFile(File xmlFile) {
        try {
            JAXBContext context = JAXBContext.newInstance(ShopData.class); // Erstellen eines neuen Contexts mit ShopData
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (ShopData) unmarshaller.unmarshal(xmlFile); // Deserialisierung der XML-Datei zu ShopData Objekt
        } catch (JAXBException e) {
            if (e instanceof IllegalAnnotationsException ex) {
                ex.getErrors().forEach(err -> log.error(err.toString()));
            }
            log.error("Fehler beim Parsen der Datei: " + xmlFile.getName(), e); // Logging des Fehlers
            return null;
        }
    }

}
