package com.merkado.merkadoclient.Database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Total cost")
public class OrderCost {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private float sum;
    private float discount;
    private float overAllDiscount;
    private float totalCost;
    private float shippingFee;

    public OrderCost() {
    }

    @Ignore
    public OrderCost(float sum, float discount, float overAllDiscount, float shippingFee, float totalCost) {
        this.sum = sum;
        this.discount = discount;
        this.overAllDiscount = overAllDiscount;
        this.shippingFee = shippingFee;
        this.totalCost = totalCost;
    }

    public float getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(float shippingFee) {
        this.shippingFee = shippingFee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getOverAllDiscount() {
        return overAllDiscount;
    }

    public void setOverAllDiscount(float overAllDiscount) {
        this.overAllDiscount = overAllDiscount;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }
}
