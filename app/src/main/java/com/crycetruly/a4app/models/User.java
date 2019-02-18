package com.crycetruly.a4app.models;

/**
 * Created by Elia on 2/24/2018.
 */

public class User {
    private String online,device_token,name,image,phone;

    public User() {
    }

    public User(String online, String device_token, String name, String image, String phone) {
        this.online = online;
        this.device_token = device_token;
        this.name = name;
        this.image = image;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "online='" + online + '\'' +
                ", device_token='" + device_token + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
