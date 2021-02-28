package com.merkado.merkadoclient.Model;

public class CanceledOrder {
    private String customerName;
    private String id;

    public CanceledOrder(String customerName,String id) {
        this.customerName = customerName;
        this.id = id;
    }

    public CanceledOrder() {
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        customerName = customerName;
    }
}
