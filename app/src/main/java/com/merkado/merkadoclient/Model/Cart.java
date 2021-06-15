package com.merkado.merkadoclient.Model;

import java.math.BigDecimal;

public class Cart {
    private String productName;
    private String numberOfProducts;

    public Cart(String productName, String numberOfProducts) {
        this.productName = productName;
        this.numberOfProducts = numberOfProducts;
    }

    public Cart() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(String numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }
}
