package com.merkado.merkadoclient.Model;

public class CanceledOrder {
    private String CustomerName;

    public CanceledOrder(String customerName) {
        CustomerName = customerName;
    }

    public CanceledOrder() {
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }
}
