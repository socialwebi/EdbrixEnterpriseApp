package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rajk on 16/11/17.
 */

public class GetCourseContentListResponseData {

    @SerializedName("ErrorCode")
    private String ErrorCode;

    @SerializedName("ErrorMessage")
    private String ErrorMessage;

    @SerializedName("jumpContentId")
    private String jumpContentId;

    @SerializedName("showDrawerContents")
    private int showDrawerContents;

    private boolean isShowDrawerContents;

    @SerializedName("courseContentList")
    private ArrayList<CourseContentData> courseContentList;

    public ArrayList<CourseContentData> getCourseContentList() {
        return courseContentList;
    }

    public void setCourseContentList(ArrayList<CourseContentData> courseContentList) {
        this.courseContentList = courseContentList;
    }

    public String getJumpContentId() {
        return jumpContentId;
    }

    public void setJumpContentId(String jumpContentId) {
        this.jumpContentId = jumpContentId;
    }

    public int getShowDrawerContents() {
        return showDrawerContents;
    }

    public void setShowDrawerContents(int showDrawerContents) {
        this.showDrawerContents = showDrawerContents;
    }

    public boolean isShowDrawerContents() {
        return isShowDrawerContents = (showDrawerContents == 1) ? true : false;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ClassPojo [ jumpContentId = " + jumpContentId + ", showDrawerContents = " + showDrawerContents + ", ErrorCode = " + ErrorCode + ", ErrorMessage = " + ErrorMessage + "]";
    }
}
