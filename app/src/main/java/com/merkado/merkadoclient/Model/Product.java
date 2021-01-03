package com.merkado.merkadoclient.Model;


public class Product {
    private String imageUrl;
    private String imageFileName;
    private String dep;
    private String subDep;
    private String productName;
    private String price;
    private String unitWeight;
    private String discount;
    private String discountUnit;
    private float availableAmount;
    private String count;
    private boolean todaysOffer;
    private boolean mostSold;
    private float minimumOrderAmount;

    public Product(String dep, String subDep, String productName, String price, String unitWeight, String discount, String discountUnit, float availableAmount, boolean todaysOffer, boolean mostSold,float minimumOrderAmount) {
        this.dep = dep;
        this.subDep = subDep;
        this.productName = productName;
        this.price = price;
        this.unitWeight = unitWeight;
        this.discount = discount;
        this.discountUnit = discountUnit;
        this.availableAmount = availableAmount;
        this.todaysOffer = todaysOffer;
        this.mostSold = mostSold;
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public Product() {

    }

    public float getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(float minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public String getSubDep() {
        return subDep;
    }

    public void setSubDep(String subDep) {
        this.subDep = subDep;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public boolean isTodaysOffer() {
        return todaysOffer;
    }

    public void setTodaysOffer(boolean todaysOffer) {
        this.todaysOffer = todaysOffer;
    }

    public boolean isMostSold() {
        return mostSold;
    }

    public void setMostSold(boolean mostSold) {
        this.mostSold = mostSold;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(String unitWeight) {
        this.unitWeight = unitWeight;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountUnit() {
        return discountUnit;
    }

    public void setDiscountUnit(String discountUnit) {
        this.discountUnit = discountUnit;
    }

    public float getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(int availableAmount) {
        this.availableAmount = availableAmount;
    }

    @Override
    public String toString() {
        return productName;
    }
}
