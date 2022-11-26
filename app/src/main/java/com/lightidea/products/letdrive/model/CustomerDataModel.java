package com.lightidea.products.letdrive.model;

import java.io.Serializable;

public class CustomerDataModel implements Serializable {

    private double lat;
    private double log;
    private String name;
    private String phone;
    private String photo;

    public CustomerDataModel() {
    }

    public CustomerDataModel(double lat, double log, String name, String phone, String photo) {
        this.lat = lat;
        this.log = log;
        this.name = name;
        this.phone = phone;
        this.photo = photo;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
