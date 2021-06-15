package com.merkado.merkadoclient.Database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;

@Entity(tableName = "Total cost")
public class OrderCost {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String sum;
    private String  discount;
    private String overAllDiscount;
    private String totalCost;
    private String shippingFee;

    public OrderCost() {
    }

    @Ignore
    public OrderCost(String sum, String discount, String overAllDiscount, String shippingFee, String totalCost) {
        this.sum = sum;
        this.discount = discount;
        this.overAllDiscount = overAllDiscount;
        this.shippingFee = shippingFee;
        this.totalCost = totalCost;
    }

    public String getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(String shippingFee) {
        this.shippingFee = shippingFee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOverAllDiscount() {
        return overAllDiscount;
    }

    public void setOverAllDiscount(String overAllDiscount) {
        this.overAllDiscount = overAllDiscount;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }
}
