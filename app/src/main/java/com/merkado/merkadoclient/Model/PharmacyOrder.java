package com.merkado.merkadoclient.Model;

import com.merkado.merkadoclient.Database.ShippingData;

public class PharmacyOrder {
    private String orderId;
    private String userId;
    private String pharmacyCartOrderId;
    private String imageUrl;
    private String describtion;
    private String therapyName;
    private String numberOfItems;
    private String typeOfItem;
    private String acceptAlternative;
    private String imageFileName;
    private String pharmacyCost;
    private ShippingData shippingData;
    private String time;
    private String date;
    private boolean seen;
    private boolean shiped;
    private boolean done;

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isShiped() {
        return shiped;
    }

    public void setShiped(boolean shiped) {
        this.shiped = shiped;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ShippingData getShippingData() {
        return shippingData;
    }

    public void setShippingData(ShippingData shippingData) {
        this.shippingData = shippingData;
    }

    public PharmacyOrder() {
    }

    public PharmacyOrder(String userId, String imageUrl, String describtion, String productName, String numberOfItems, String typeOfItem, String acceptAlternative, String imageFileName, String pharmacyCartOrderId) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.describtion = describtion;
        this.therapyName = productName;
        this.numberOfItems = numberOfItems;
        this.typeOfItem = typeOfItem;
        this.acceptAlternative = acceptAlternative;
        this.imageFileName = imageFileName;
        this.pharmacyCartOrderId = pharmacyCartOrderId;
    }

    public void setAcceptAlternative(String acceptAlternative) {
        this.acceptAlternative = acceptAlternative;
    }

    public String getPharmacyCost() {
        return pharmacyCost;
    }

    public void setPharmacyCost(String pharmacyCost) {
        this.pharmacyCost = pharmacyCost;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public String getTherapyName() {
        return therapyName;
    }

    public void setTherapyName(String productName) {
        this.therapyName = productName;
    }

    public String getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(String numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public String getTypeOfItem() {
        return typeOfItem;
    }

    public void setTypeOfItem(String typeOfItem) {
        this.typeOfItem = typeOfItem;
    }


    public String getPharmacyCartOrderId() {
        return pharmacyCartOrderId;
    }

    public void setPharmacyCartOrderId(String pharmacyCartOrderId) {
        this.pharmacyCartOrderId = pharmacyCartOrderId;
    }

    public String getAcceptAlternative() {
        return acceptAlternative;
    }
}
