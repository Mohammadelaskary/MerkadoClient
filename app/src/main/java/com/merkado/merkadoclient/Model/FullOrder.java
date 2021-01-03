package com.merkado.merkadoclient.Model;

import com.merkado.merkadoclient.Database.ProductOrder;

import java.util.List;

public class FullOrder {
    private String id;
    private String username;
    private String mobilePhone;
    private String phoneNumber;
    private String address;
    private List<ProductOrder> orders;
    private float sum;
    private float discount;
    private float overAllDiscount;
    private float shipping;
    private float totalCost;
    private String date;
    private String time;
    private boolean done;
    private boolean shiped;
    private boolean seen;
    private boolean isStillAvailable;
    private String userId;

    public FullOrder(String date, String time, boolean done, boolean shiped, boolean seen, String username, String mobilePhone, String phoneNumber, String address, List<ProductOrder> orders, float sum, float discount, float overAllDiscount, float shipping, float totalCost, String userId,boolean isStillAvailable) {
        this.username = username;
        this.mobilePhone = mobilePhone;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.orders = orders;
        this.sum = sum;
        this.discount = discount;
        this.overAllDiscount = overAllDiscount;
        this.shipping = shipping;
        this.totalCost = totalCost;
        this.date = date;
        this.time = time;
        this.done = done;
        this.userId = userId;
        this.shiped = shiped;
        this.seen = seen;
        this.isStillAvailable = isStillAvailable;
    }

    public boolean isStillAvailable() {
        return isStillAvailable;
    }

    public void setStillAvailable(boolean stillAvailable) {
        isStillAvailable = stillAvailable;
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public List<ProductOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<ProductOrder> orders) {
        this.orders = orders;
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

    public float getShipping() {
        return shipping;
    }

    public void setShipping(float shipping) {
        this.shipping = shipping;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public FullOrder() {
    }

}
