package com.merkado.merkadoclient.Model;

public class Cart {
    private String productName;
    private float numberOfProducts;

    public Cart(String productName, float numberOfProducts) {
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

    public float getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(float numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }
}
