package com.edbrix.enterprise.Models;


public class MeetingUsers {

    private String name;
    private String profileImageURL;
    private String phoneNo;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public String toString() {
        return "MeetingUsersList { " +
                "name=" + name +
                ", phoneNo='" + phoneNo + '\'' +
                ", profileImageURL='" + profileImageURL + '\'' +
                '}';
    }

}
