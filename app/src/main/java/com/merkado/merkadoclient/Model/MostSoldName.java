package com.merkado.merkadoclient.Model;

public class MostSoldName {
    private String productName;

    public MostSoldName(String productName) {
        this.productName = productName;
    }

    public MostSoldName() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
