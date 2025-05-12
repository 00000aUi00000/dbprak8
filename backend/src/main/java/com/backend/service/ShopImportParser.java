package com.backend.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

import org.glassfish.jaxb.runtime.v2.runtime.IllegalAnnotationsException;

import com.backend.service.dto.ShopData;

@Slf4j
public class ShopImportParser {

    public ShopData parseShopFile(File xmlFile) {
        try {
            JAXBContext context = JAXBContext.newInstance(ShopData.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (ShopData) unmarshaller.unmarshal(xmlFile);
        } catch (JAXBException e) {
                if (e instanceof IllegalAnnotationsException ex) {
                    ex.getErrors().forEach(err -> log.error(err.toString()));
                }
                log.error("Fehler beim Parsen der Datei: " + xmlFile.getName(), e);
                return null;
                
        }
    }
}
