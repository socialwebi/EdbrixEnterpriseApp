package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rajk on 16/11/17.
 */

public class CourseContentData {

    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    private String status;

    private String type;

    @SerializedName("submit_type")
    private String submit_type;

    @SerializedName("submit_data")
    private SubmitData submit_data;

    @SerializedName("content")
    private String webContent;

    private ArrayList<ImageContentData> img_content;

    private String audio_content;

    private String video_content;

    @SerializedName("document_content")
    private String[] doc_content;

    @SerializedName("session_event_content")
    private ArrayList<TrainingSessionEventContentData> trainingSessionEventContentDataList;

    private String document_content_type;

    private String iframe_content;

    private String assignment_content;

    private boolean isSelected;

    private boolean isChecked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmit_type() {
        return submit_type;
    }

    public void setSubmit_type(String submit_type) {
        this.submit_type = submit_type;
    }

    public SubmitData getSubmit_data() {
        return submit_data;
    }

    public void setSubmit_data(SubmitData submit_data) {
        this.submit_data = submit_data;
    }

    public String getWebContent() {
        return webContent;
    }

    public void setWebContent(String webContent) {
        this.webContent = webContent;
    }

    public ArrayList<ImageContentData> getImg_content() {
        return img_content;
    }

    public void setImg_content(ArrayList<ImageContentData> img_content) {
        this.img_content = img_content;
    }

    public String getAudio_content() {
        return audio_content;
    }

    public void setAudio_content(String audio_content) {
        this.audio_content = audio_content;
    }

    public String getVideo_content() {
        return video_content;
    }

    public void setVideo_content(String video_content) {
        this.video_content = video_content;
    }

    public String[] getDoc_content() {
        return doc_content;
    }

    public void setDoc_content(String[] doc_content) {
        this.doc_content = doc_content;
    }

    public String getIframe_content() {
        return iframe_content;
    }

    public void setIframe_content(String iframe_content) {
        this.iframe_content = iframe_content;
    }

    public String getAssignment_content() {
        return assignment_content;
    }

    public void setAssignment_content(String assignment_content) {
        this.assignment_content = assignment_content;
    }

    public String getDocument_content_type() {
        return document_content_type;
    }

    public void setDocument_content_type(String document_content_type) {
        this.document_content_type = document_content_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ArrayList<TrainingSessionEventContentData> getTrainingSessionEventContentDataList() {
        return trainingSessionEventContentDataList;
    }

    public void setTrainingSessionEventContentDataList(ArrayList<TrainingSessionEventContentData> trainingSessionEventContentDataList) {
        this.trainingSessionEventContentDataList = trainingSessionEventContentDataList;
    }

    @Override
    public String toString() {
        return "ClassPojo [description = " + description + ", submit_type = " + submit_type + ", submit_data = " + submit_data + "]";
    }
}
