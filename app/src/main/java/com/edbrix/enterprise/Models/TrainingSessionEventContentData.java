package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rajk on 28/12/17.
 */

public class TrainingSessionEventContentData {

    private String title;

    private String location;

    private String description;

    private String connectType;

    private String connectURL;

    private String sessionId;

    private String sessionToken;

    private String sessionEvtDay;

    private String sessionEvtMonth;

    private String sessionEvtYear;

    private String sessionEvtDate;

    private String startDateTime;

    private String endDateTime;

    @SerializedName("instructor_name")
    private String instructorName;

    @SerializedName("profileImageURL")
    private String instructorPicUrl;



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getConnectType() {
        return connectType;
    }

    public void setConnectType(String connectType) {
        this.connectType = connectType;
    }

    public String getConnectURL() {
        return connectURL;
    }

    public void setConnectURL(String connectURL) {
        this.connectURL = connectURL;
    }

    public String getSessionEvtDay() {
        return sessionEvtDay;
    }

    public void setSessionEvtDay(String sessionEvtDay) {
        this.sessionEvtDay = sessionEvtDay;
    }

    public String getSessionEvtMonth() {
        return sessionEvtMonth;
    }

    public void setSessionEvtMonth(String sessionEvtMonth) {
        this.sessionEvtMonth = sessionEvtMonth;
    }

    public String getSessionEvtYear() {
        return sessionEvtYear;
    }

    public void setSessionEvtYear(String sessionEvtYear) {
        this.sessionEvtYear = sessionEvtYear;
    }

    public String getSessionEvtDate() {
        return sessionEvtDate;
    }

    public void setSessionEvtDate(String sessionEvtDate) {
        this.sessionEvtDate = sessionEvtDate;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getInstructorPicUrl() {
        return instructorPicUrl;
    }

    public void setInstructorPicUrl(String instructorPicUrl) {
        this.instructorPicUrl = instructorPicUrl;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public String toString() {
        return "ClassPojo [title = " + title + ", startDateTime = " + startDateTime + ", location = " + location + ", description = " + description + ", endDateTime = " + endDateTime + ", connectType = " + connectType + ", connectURL = " + connectURL + "]";
    }
}
