package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rajk on 11/11/17.
 */

public class CourseListResponseData {

    @SerializedName("courses")
    private ArrayList<Courses> coursesList;

    @SerializedName("ErrorCode")
    private String ErrorCode;

    @SerializedName("ErrorMessage")
    private String ErrorMessage;

    public ArrayList<Courses> getCoursesList() {
        return coursesList;
    }

    public void setCoursesList(ArrayList<Courses> coursesList) {
        this.coursesList = coursesList;
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
    public String toString()
    {
        return "ClassPojo [courses = "+coursesList.toString()+", ErrorCode = "+ErrorCode+", ErrorMessage = "+ErrorMessage+"]";
    }
}
