package com.edbrix.enterprise.Models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Meeting implements Serializable {

    private String id;
    private String title;
    private String description;
    private String courseId;
    private String meetingDate;
    private String meetingDay;
    private String meetingMonth;
    private String meetingYear;
    private String startDateTime;
    private String endDateTime;
    private String connectType;
    private String meetingId;
    private String meetingToken;
    private String connectURL;
    private String type;
    private String connect;

    @SerializedName("meetingUsers")
    private ArrayList<MeetingUsers> meetingUsers;

    private String isPaid;
    private String isFree;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingDay() {
        return meetingDay;
    }

    public void setMeetingDay(String meetingDay) {
        this.meetingDay = meetingDay;
    }

    public String getMeetingMonth() {
        return meetingMonth;
    }

    public void setMeetingMonth(String meetingMonth) {
        this.meetingMonth = meetingMonth;
    }

    public String getMeetingYear() {
        return meetingYear;
    }

    public void setMeetingYear(String meetingYear) {
        this.meetingYear = meetingYear;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
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

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getConnectURL() {
        return connectURL;
    }

    public void setConnectURL(String connectURL) {
        this.connectURL = connectURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {
        this.isFree = isFree;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public ArrayList<MeetingUsers> getMeetingUsers() {
        return meetingUsers;
    }

    public void setMeetingUsers(ArrayList<MeetingUsers> meetingUsers) {
        this.meetingUsers = meetingUsers;
    }


    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }

    public String getMeetingToken() {
        return meetingToken;
    }

    public void setMeetingToken(String meetingToken) {
        this.meetingToken = meetingToken;
    }

    @Override
    public String toString() {
        return "Meeting { " +
                "id=" + id +
                ", title='" + title + '\'' +
                ", isPaid='" + isPaid + '\'' +
                ", isFree='" + isFree + '\'' +
                ", description='" + description + '\'' +
                ", courseId='" + courseId + '\'' +
                ", meetingDate='" + meetingDate + '\'' +
                ", meetingDay='" + meetingDay + '\'' +
                ", meetingMonth='" + meetingMonth + '\'' +
                ", meetingYear='" + meetingYear + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                ", connectType='" + connectType + '\'' +
                ", meetingId='" + meetingId + '\'' +
                ", connectURL='" + connectURL + '\'' +
                ", type='" + type + '\'' +
                ", connect='" + connect + '\'' +
                ", meetingUsers='" + meetingUsers + '\'' +
                '}';
    }
}
