package com.merkado.merkadoclient.Model;

public class OverTotalMoneyDiscount {
    private String minimum;
    private String discount;
    private String discount_unit;
    private String key;

    public OverTotalMoneyDiscount() {
    }

    public OverTotalMoneyDiscount(String minimum, String discount, String discount_unit, String key) {
        this.minimum = minimum;
        this.discount = discount;
        this.discount_unit = discount_unit;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscount_unit() {
        return discount_unit;
    }

    public void setDiscount_unit(String discount_unit) {
        this.discount_unit = discount_unit;
    }
}
