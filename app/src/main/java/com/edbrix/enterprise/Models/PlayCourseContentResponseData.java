package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rajk on 16/11/17.
 */

public class PlayCourseContentResponseData {

    @SerializedName("course_content")
    private CourseContentData course_content;

    @SerializedName("nextquestion_id")
    private String nextquestion_id;

    @SerializedName("content_id")
    private String content_id;

    @SerializedName("status")
    private String status;

    @SerializedName("contentcomplete_type_id")
    private String contentcomplete_type_id;

    @SerializedName("html")
    private String html;

    @SerializedName("question_id")
    private String question_id;

    @SerializedName("content_type")
    private String content_type;

    @SerializedName("next_content_id")
    private String next_content_id;

    @SerializedName("ErrorCode")
    private String ErrorCode;

    @SerializedName("ErrorMessage")
    private String ErrorMessage;

    public CourseContentData getCourse_content() {
        return course_content;
    }

    public void setCourse_content(CourseContentData course_content) {
        this.course_content = course_content;
    }

    public String getNextquestion_id() {
        return nextquestion_id;
    }

    public void setNextquestion_id(String nextquestion_id) {
        this.nextquestion_id = nextquestion_id;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContentcomplete_type_id() {
        return contentcomplete_type_id;
    }

    public void setContentcomplete_type_id(String contentcomplete_type_id) {
        this.contentcomplete_type_id = contentcomplete_type_id;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getNext_content_id() {
        return next_content_id;
    }

    public void setNext_content_id(String next_content_id) {
        this.next_content_id = next_content_id;
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
        return "ClassPojo [course_content = " + course_content + ", nextquestion_id = " + nextquestion_id + ", content_id = " + content_id + ", status = " + status + ", contentcomplete_type_id = " + contentcomplete_type_id + ", html = " + html + ", question_id = " + question_id + ", content_type = " + content_type + ", ErrorCode = " + ErrorCode + ", ErrorMessage = " + ErrorMessage + "]";
    }
}
