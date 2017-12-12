package com.edbrix.enterprise.Models;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseData {

    private String code;
    private String message;
    private String accessToken;
    private String isOrganizationListShow;

    private User user;
    private ArrayList<Organizations> organizations;
    private ArrayList<Meetings> meeting;
    private ArrayList<Meeting> meetings;

    @SerializedName("courses")
    private ArrayList<Courses> coursesList;

    @SerializedName("courseContents")
    private ArrayList<CourseContents> courseContents;

    private ArrayList<TypesC> resourcesTypes;
    private ArrayList<TypesC> courseContentsTypes;
    private ArrayList<TypesC> connectivityTypes;

    private String ErrorCode;
    private String ErrorMessage;
    private String page;
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

    public ArrayList<Meetings> getMeeting() {
        return meeting;
    }

    public void setMeeting(ArrayList<Meetings> meeting) {
        this.meeting = meeting;
    }

    public ArrayList<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(ArrayList<Meeting> meetings) {
        this.meetings = meetings;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public ArrayList<Courses> getCoursesList() {
        return coursesList;
    }

    public void setCoursesList(ArrayList<Courses> coursesList) {
        this.coursesList = coursesList;
    }

    public ArrayList<TypesC> getResourcesTypes() {
        return resourcesTypes;
    }

    public void setResourcesTypes(ArrayList<TypesC> resourcesTypes) {
        this.resourcesTypes = resourcesTypes;
    }

    public ArrayList<CourseContents> getCourseContents() {
        return courseContents;
    }

    public void setCourseContents(ArrayList<CourseContents> courseContents) {
        this.courseContents = courseContents;
    }

    public ArrayList<TypesC> getCourseContentsTypes() {
        return courseContentsTypes;
    }

    public void setCourseContentsTypes(ArrayList<TypesC> courseContentsTypes) {
        this.courseContentsTypes = courseContentsTypes;
    }

    public ArrayList<TypesC> getConnectivityTypes() {
        return connectivityTypes;
    }

    public void setConnectivityTypes(ArrayList<TypesC> connectivityTypes) {
        this.connectivityTypes = connectivityTypes;
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
                ", meeting = '" + meeting + '\'' +
                ", coursesList = '" + coursesList + '\'' +
                ", courseContents = '" + courseContents + '\'' +
                ", ErrorCode= '" + ErrorCode + '\'' +
                ", ErrorMessage= '" + ErrorMessage + '\'' +
                ", page= '" + page + '\'' +
                ", offset= '" + offset + '\'' +
                '}';
    }

}
