package com.frontend.service;

import java.util.List;
import java.util.Properties;

public interface ApplicationInterface {
    void init(Properties props);
    void finish();

    Object getProduct(String produktId);
    List<Object> getProducts(String pattern);
    Object getCategoryTree();
    List<Object> getProductsByCategoryPath(String pfad);
    List<Object> getTopProducts(int k);
    List<Object> getSimilarCheaperProduct(String produktId);
    void addNewReview(Object review);
    List<Object> getTrolls(double maxRating);
    List<Object> getOffers(String produktId);
}
