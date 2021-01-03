package com.merkado.merkadoclient.Model;

public class Complaint {
    private String customerName;
    private String mobileNumber;
    private String complaintText;
    private boolean seen;

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Complaint() {
    }

    public Complaint(String customerName, String mobileNumber, String complaintText, boolean seen) {
        this.customerName = customerName;
        this.mobileNumber = mobileNumber;
        this.complaintText = complaintText;
        this.seen = seen;
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

    public String getComplaintText() {
        return complaintText;
    }

    public void setComplaintText(String complaintText) {
        this.complaintText = complaintText;
    }
}
