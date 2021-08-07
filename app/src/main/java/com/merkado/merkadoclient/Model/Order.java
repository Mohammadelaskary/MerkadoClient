package com.merkado.merkadoclient.Model;

import java.util.List;

public class Order {
    private int id;
    private FullOrder fullOrder;
    private List<PharmacyOrder> pharmacyOrders;

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
}
