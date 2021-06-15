package com.merkado.merkadoclient.Database;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;

@Entity(tableName = "Orders")
public class ProductOrder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String ImageURL;
    private String productName;
    private String originalPrice;
    private String discount;
    private String discountType;
    private String finalPrice;
    private String totalCost;
    private String unitWeight;
    private String ordered;
    private String available;
    private String minimumOrderAmount;

    public ProductOrder() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFinalPrice() {
        BigDecimal originalPriceValue =  new BigDecimal(originalPrice);


        if (!discount.equals("")) {
             BigDecimal discountBigDecimalValue = new BigDecimal(discount);
            if (discountType.equals("جنيه")) {
                finalPrice = String.valueOf(originalPriceValue.subtract(discountBigDecimalValue));
            } else if (discountType.equals("%")) {
                finalPrice = String.valueOf(originalPriceValue.multiply(BigDecimal.ONE.subtract (discountBigDecimalValue.divide(BigDecimal.valueOf(100)))));
            }
        } else {
            finalPrice = originalPrice;
        }
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    @Ignore
    public ProductOrder(String imageURL, String productName, String ordered, String originalPrice, String discount, String discountType, String available, String unitWeight, String minimumOrderAmount) {

        this.ImageURL = imageURL;
        this.productName = productName;
        this.ordered = ordered;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.discountType = discountType;
        this.available = available;
        this.unitWeight = unitWeight;
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public String getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(String minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public String getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(String unitWeight) {
        this.unitWeight = unitWeight;
    }

    public String getTotalCost() {
        BigDecimal finalPriceValue = new BigDecimal(finalPrice);
        BigDecimal orderedAmountValue = new BigDecimal(ordered);
        totalCost = String.valueOf(finalPriceValue.multiply(orderedAmountValue));
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrdered() {
        return ordered;
    }

    public void setOrdered(String ordered) {
        this.ordered = ordered;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
