package com.merkado.merkadoclient.Model;

import java.math.BigDecimal;

public class PointsDiscount {
    private boolean freeShipping;
    private int numberOfPoints;
    private String discountValue;

    public PointsDiscount() {
    }

    public PointsDiscount(boolean freeShipping, int numberOfPoints, String discountValue) {
        this.freeShipping = freeShipping;
        this.numberOfPoints = numberOfPoints;
        this.discountValue = discountValue;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }

    public void setFreeShipping(boolean freeShipping) {
        this.freeShipping = freeShipping;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }
}
