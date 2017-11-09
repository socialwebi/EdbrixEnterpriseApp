package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;


public class Organizations {

    @SerializedName("Id")
    private String id;

    @SerializedName("OrganizationName")
    private String organizationName;

    @SerializedName("OrganizationImage")
    private String organizationImage;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationImage() {
        return organizationImage;
    }

    public void setOrganizationImage(String organizationImage) {
        this.organizationImage = organizationImage;
    }


    @Override
    public String toString() {
        return "Organizations { " +
                "id=" + id +
                ", organizationName='" + organizationName + '\'' +
                ", organizationImage='" + organizationImage + '\'' +
                '}';
    }

}
