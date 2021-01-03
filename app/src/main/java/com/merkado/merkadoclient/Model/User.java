package com.merkado.merkadoclient.Model;



public class User {
    private String customerName;
    private String mobileNumber;
    private String phoneNumber;
    private String streetName;
    private String buildingNo;
    private String appartmentNo;
    private String neighborhood;
    private String city;
    private String governorate;
    private String promoCode;
    private String userId;
    private int count;
    private String messagingToken;
    private String email;
    private String famousMark;

    public User(String customerName, String mobileNumber, String phoneNumber, String streetName, String neighborhood, String city, String governorate, String promoCode, String userId, String email) {
        this.customerName = customerName;
        this.mobileNumber = mobileNumber;
        this.phoneNumber = phoneNumber;
        this.streetName = streetName;
        this.neighborhood = neighborhood;
        this.city = city;
        this.governorate = governorate;
        this.promoCode = promoCode;
        this.userId = userId;
        this.email = email;
    }

    public String getFamousMark() {
        return famousMark;
    }

    public void setFamousMark(String famousMark) {
        this.famousMark = famousMark;
    }

    public String getMessagingToken() {
        return messagingToken;
    }

    public void setMessagingToken(String messagingToken) {
        this.messagingToken = messagingToken;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }



    public User() {
    }

    public User(String customerName, String mobileNumber, String phoneNumber, String streetName, String buildingNo, String appartmentNo, String neighborhood, String city, String governorate, String promoCode, String userId, String email, String famousMark) {
        this.customerName = customerName;
        this.mobileNumber = mobileNumber;
        this.phoneNumber = phoneNumber;
        this.streetName = streetName;
        this.buildingNo = buildingNo;
        this.appartmentNo = appartmentNo;
        this.neighborhood = neighborhood;
        this.city = city;
        this.governorate = governorate;
        this.promoCode = promoCode;
        this.userId = userId;
        this.email = email;
        this.famousMark = famousMark;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getAppartmentNo() {
        return appartmentNo;
    }

    public void setAppartmentNo(String appartmentNo) {
        this.appartmentNo = appartmentNo;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }


}
