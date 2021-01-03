package com.merkado.merkadoclient.Model;

public class SubDeparment {
    private String depName;
    private String subdepName;

    public SubDeparment() {
    }

    public SubDeparment(String depName, String subdepName) {
        this.depName = depName;
        this.subdepName = subdepName;
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
