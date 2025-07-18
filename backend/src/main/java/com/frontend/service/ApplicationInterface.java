package com.frontend.service;

import java.util.List;
import java.util.Properties;

import com.frontend.dto.ProduktDto;
import com.frontend.dto.RezensionDTO;

public interface ApplicationInterface {
    void init(Properties props);
    void finish();

    Object getProduct(String produktId);
    List<ProduktDto> getProducts(String pattern);
    List<Object> getCategoryTree();
    List<Object> getProductsByCategoryPath(String pfad);
    List<Object> getTopProducts(int k, String typ);
    List<Object> getSimilarCheaperProduct(String produktId);
    void addNewReview(Object review);
    List<Object> getTrolls(double maxRating, boolean asc);
    List<Object> getOffers(String produktId);
    List<RezensionDTO> getRezensionenZuProdukt(String produktId);


}
