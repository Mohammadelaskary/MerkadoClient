package com.merkado.merkadoclient.Model;

import java.util.List;

public class Order {
    private int id;
    private String userId;
    private String date;
    private FullOrder fullOrder;
    private List<PharmacyOrder> pharmacyOrders;

    public Order() {
    }

    public Order(int id, String userId, FullOrder fullOrder, List<PharmacyOrder> pharmacyOrders) {
        this.id = id;
        this.userId = userId;
        this.fullOrder = fullOrder;
        this.pharmacyOrders = pharmacyOrders;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Order(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FullOrder getFullOrder() {
        return fullOrder;
    }

    public void setFullOrder(FullOrder fullOrder) {
        this.fullOrder = fullOrder;
    }

    public List<PharmacyOrder> getPharmacyOrders() {
        return pharmacyOrders;
    }

    public void setPharmacyOrders(List<PharmacyOrder> pharmacyOrders) {
        this.pharmacyOrders = pharmacyOrders;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
