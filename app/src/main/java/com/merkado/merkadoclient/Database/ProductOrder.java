package com.merkado.merkadoclient.Database;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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
    private float ordered;
    private float available;
    private float minimumOrderAmount;

    public ProductOrder() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFinalPrice() {
        float originalPriceValue = Float.parseFloat(originalPrice);

        float discountFloatValue = 0;
        if (!discount.equals("")) {
            discountFloatValue = Float.parseFloat(discount);
            if (discountType.equals("جنيه")) {
                finalPrice = String.valueOf(originalPriceValue - discountFloatValue);
            } else if (discountType.equals("%")) {
                finalPrice = String.valueOf(originalPriceValue * (1 - (discountFloatValue / 100)));
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
    public ProductOrder(String imageURL, String productName, float ordered, String originalPrice, String discount, String discountType, float available, String unitWeight,float minimumOrderAmount) {

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

    public float getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(float minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public String getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(String unitWeight) {
        this.unitWeight = unitWeight;
    }

    public String getTotalCost() {
        float finalPriceValue = Float.parseFloat(finalPrice);
        float orderedAmountValue = ordered;
        totalCost = String.valueOf(finalPriceValue * orderedAmountValue);
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

    public float getOrdered() {
        return ordered;
    }

    public void setOrdered(float ordered) {
        this.ordered = ordered;
    }

    public float getAvailable() {
        return available;
    }

    public void setAvailable(float available) {
        this.available = available;
    }
}
