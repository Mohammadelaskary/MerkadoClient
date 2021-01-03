package com.merkado.merkadoclient.Database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ShippingData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String mobileNumber;
    private String phoneNumber;
    private String address;
    private String city;
    private String famousMark;


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ShippingData() {
    }

    public String getFamousMark() {
        return famousMark;
    }

    public void setFamousMark(String famousMark) {
        this.famousMark = famousMark;
    }

    @Ignore
    public ShippingData(String username, String mobileNumber, String phoneNumber, String address) {
        this.username = username;
        this.mobileNumber = mobileNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
