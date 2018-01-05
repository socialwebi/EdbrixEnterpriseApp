package com.edbrix.enterprise.Models;


import com.google.gson.annotations.SerializedName;

public class User {

    private String id;

    @SerializedName("userCode")
    private String studentCode;  // userCode

    private String userType;

    private String firstName;

    private String lastName;

    private String gender;

    private String profileImageUrl;

    private String tagline;

    private String aboutMe;

    private String organizationId;

    private String organizationName;

    private String accessToken;

    private String birthYear;

    private String birthMonth;

    private String birthDay;

    private int salutationId;

    private int canCommentOnWall;

    private int canReceiveCourseRequestNotification;

    private int timezoneId;

    private String zoomUserId;

    private String zoomUserToken;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getZoomUserId() {
        return zoomUserId;
    }

    public void setZoomUserId(String zoomUserId) {
        this.zoomUserId = zoomUserId;
    }

    public String getZoomUserToken() {
        return zoomUserToken;
    }

    public void setZoomUserToken(String zoomUserToken) {
        this.zoomUserToken = zoomUserToken;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(String birthMonth) {
        this.birthMonth = birthMonth;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public int getSalutationId() {
        return salutationId;
    }

    public void setSalutationId(int salutationId) {
        this.salutationId = salutationId;
    }

    public int getCanCommentOnWall() {
        return canCommentOnWall;
    }

    public void setCanCommentOnWall(int canCommentOnWall) {
        this.canCommentOnWall = canCommentOnWall;
    }

    public int getCanReceiveCourseRequestNotification() {
        return canReceiveCourseRequestNotification;
    }

    public void setCanReceiveCourseRequestNotification(int canReceiveCourseRequestNotification) {
        this.canReceiveCourseRequestNotification = canReceiveCourseRequestNotification;
    }

    public int getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(int timezoneId) {
        this.timezoneId = timezoneId;
    }

    @Override
    public String toString() {
        return "User { " +
                "id=" + id +
                ", studentCode='" + studentCode + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", userType='" + userType + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", tagline='" + tagline + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", organizationName='" + organizationName + '\'' +

                ", zoomUserId='" + zoomUserId + '\'' +
                ", zoomUserToken='" + zoomUserToken + '\'' +
                '}';
    }
}
