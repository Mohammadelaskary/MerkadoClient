package com.merkado.merkadoclient.Model;

public class TodaysOfferName {
    private String productName;

    public TodaysOfferName(String productName) {
        this.productName = productName;
    }

    public TodaysOfferName() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
