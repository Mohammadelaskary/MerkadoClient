package com.merkado.merkadoclient.Model;

public class SubDeparment {
    private String depName;
    private String subdepName;
    private String imageFileName;
    private String imageUrl;


    public SubDeparment() {
    }

    public SubDeparment(String depName, String subdepName) {
        this.depName = depName;
        this.subdepName = subdepName;
    }

    public SubDeparment(String depName, String subdepName, String imageFileName, String imageUrl) {
        this.depName = depName;
        this.subdepName = subdepName;
        this.imageFileName = imageFileName;
        this.imageUrl = imageUrl;

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
