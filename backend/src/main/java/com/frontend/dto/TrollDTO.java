package com.frontend.dto;

public class TrollDTO {

    private String username;
    private double avgPunkte;

    public TrollDTO(String username, double avgPunkte) {
        this.username = username;
        this.avgPunkte = avgPunkte;
    }

    public String getUsername() {
        return username;
    }

    public double getAvgPunkte() {
        return avgPunkte;
    }
}
