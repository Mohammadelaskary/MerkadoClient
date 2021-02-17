package com.merkado.merkadoclient.Model;

public class SubDeparment {
    private String depName;
    private String subdepName;
    private String imageFileName;
    private String imageUrl;
    private String discount_unit;
    private int discount;

    public SubDeparment() {
    }

    public SubDeparment(String depName, String subdepName) {
        this.depName = depName;
        this.subdepName = subdepName;
    }

    public SubDeparment(String depName, String subdepName, String imageFileName, String imageUrl, String discount_unit, int discount) {
        this.depName = depName;
        this.subdepName = subdepName;
        this.imageFileName = imageFileName;
        this.imageUrl = imageUrl;
        this.discount_unit = discount_unit;
        this.discount = discount;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDiscount_unit() {
        return discount_unit;
    }

    public void setDiscount_unit(String discount_unit) {
        this.discount_unit = discount_unit;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public String getSubdepName() {
        return subdepName;
    }

    public void setSubdepName(String subdepName) {
        this.subdepName = subdepName;
    }
}
