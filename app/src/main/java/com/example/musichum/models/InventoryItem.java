package com.example.musichum.models;

public class InventoryItem {
    String type;
    String id;
    String did;
    float cost;
    String distName;

    public String getDid() {
        return did;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getDistName() {
        return distName;
    }

    public void setDistName(String distName) {
        this.distName = distName;
    }
}
