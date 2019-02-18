package com.crycetruly.a4app.models;

/**
 * Created by Elia on 3/1/2018.
 */

public class Village {
    private String id;
    private String name;
    private String district;
    private String created,cover;

    public Village() {
    }

    public Village(String id, String name, String district, String created, String cover) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.created = created;
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Village(String id, String name, String district, String created) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
