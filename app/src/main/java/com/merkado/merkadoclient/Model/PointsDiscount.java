package com.merkado.merkadoclient.Model;

public class PointsDiscount {
    private boolean freeShipping;
    private int numberOfPoints;
    private float discountValue;

    public PointsDiscount() {
    }

    public PointsDiscount(boolean freeShipping, int numberOfPoints, float discountValue) {
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

    public float getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(float discountValue) {
        this.discountValue = discountValue;
    }
}
