package com.crycetruly.a4app.models;

public class VHT {
    private String photo,gender,name,profile,village,phone;

    public VHT() {
    }

    public VHT(String photo, String gender, String name, String profile, String village, String phone) {
        this.photo = photo;
        this.gender = gender;
        this.name = name;
        this.profile = profile;
        this.village = village;
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
