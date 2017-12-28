package com.edbrix.enterprise.Models;

/**
 * Created by rajk on 28/12/17.
 */

public class TrainingSessionEventContentData {

    private String title;

    private String location;

    private String description;

    private String connectType;

    private String connectURL;

    private String sessionEvtDay;

    private String sessionEvtMonth;

    private String sessionEvtYear;

    private String sessionEvtDate;

    private String startDateTime;

    private String endDateTime;

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

    @Override
    public String toString() {
        return "ClassPojo [title = " + title + ", startDateTime = " + startDateTime + ", location = " + location + ", description = " + description + ", endDateTime = " + endDateTime + ", connectType = " + connectType + ", connectURL = " + connectURL + "]";
    }
}
