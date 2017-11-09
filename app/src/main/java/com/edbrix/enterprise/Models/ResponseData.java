package com.edbrix.enterprise.Models;


import java.util.ArrayList;

public class ResponseData {

    private String code;
    private String message;
    private String accessToken;
    private String isOrganizationListShow;

    private User   user;
    private ArrayList<Organizations> organizations;
    private ArrayList<Meetings> meetings;

    private String ErrorCode;
    private String ErrorMessage;
    private String offset;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIsOrganizationListShow() {
        return isOrganizationListShow;
    }

    public void setIsOrganizationListShow(String isOrganizationListShow) {
        this.isOrganizationListShow = isOrganizationListShow;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Organizations> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(ArrayList<Organizations> organizations) {
        this.organizations = organizations;
    }

    public ArrayList<Meetings> getMeetings() {
        return meetings;
    }

    public void setMeetings(ArrayList<Meetings> meetings) {
        this.meetings = meetings;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String ErrorCode) {
        this.ErrorCode = ErrorCode;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }


    @Override
    public String toString() {
        return "ResponseData { " +
                ", code= '" + code + '\'' +
                ", message= '" + message + '\'' +
                ", accessToken= '" + accessToken + '\'' +
                ", isOrganizationListShow= '" + isOrganizationListShow + '\'' +
                ", user= '" + user + '\'' +
                ", organizations= '" + organizations + '\'' +
                ", Meetings = '" + meetings + '\'' +
                ", ErrorCode= '" + ErrorCode + '\'' +
                ", ErrorMessage= '" + ErrorMessage + '\'' +
                ", offset= '" + offset + '\'' +
                '}';
    }

}
