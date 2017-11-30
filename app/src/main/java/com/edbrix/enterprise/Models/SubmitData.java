package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rajk on 17/11/17.
 */

public class SubmitData {

    @SerializedName("title")
    private String title;

    @SerializedName("choices")
    private ArrayList<ChoicesData>choices;

    @SerializedName("type")
    private String type;

    @SerializedName("time")
    private String time;

    @SerializedName("progress")
    private String progress;

    private String question_id;

    @SerializedName("total_question_count")
    private String total_question_count;

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public ArrayList<ChoicesData> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<ChoicesData> choices) {
        this.choices = choices;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getTotal_question_count() {
        return total_question_count;
    }

    public void setTotal_question_count(String total_question_count) {
        this.total_question_count = total_question_count;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [title = "+title+", choices = "+choices+", type = "+type+"]";
    }
}
