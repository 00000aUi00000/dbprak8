package com.backend.service.parser;

import org.springframework.stereotype.Service;

import com.backend.entity.Produkt;
import com.backend.service.dto.ItemData;
import com.backend.service.util.Result;

@Service
public class BookImportParser extends ProduktImportParser {

    @Override
    public Result<? extends Produkt> parseProdukt(ItemData itemData) {
        return Result.error("Not implemented."); //TBD
    }
    
}
