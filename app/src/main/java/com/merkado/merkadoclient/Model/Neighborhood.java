package com.merkado.merkadoclient.Model;

public class Neighborhood {
    private String governorate;
    private String city;
    private String neighborhood;

    public Neighborhood() {
    }

    public Neighborhood(String governorate, String city, String neighborhood) {
        this.governorate = governorate;
        this.city = city;
        this.neighborhood = neighborhood;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
}

