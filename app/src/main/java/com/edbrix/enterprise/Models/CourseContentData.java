package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rajk on 16/11/17.
 */

public class CourseContentData {

    @SerializedName("description")
    private String description;

    @SerializedName("submit_type")
    private String submit_type;

    @SerializedName("submit_data")
    private SubmitData submit_data;

    @SerializedName("content")
    private String webContent;

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getSubmit_type ()
    {
        return submit_type;
    }

    public void setSubmit_type (String submit_type)
    {
        this.submit_type = submit_type;
    }

    public SubmitData getSubmit_data ()
    {
        return submit_data;
    }

    public void setSubmit_data (SubmitData submit_data)
    {
        this.submit_data = submit_data;
    }

    public String getWebContent() {
        return webContent;
    }

    public void setWebContent(String webContent) {
        this.webContent = webContent;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [description = "+description+", submit_type = "+submit_type+", submit_data = "+submit_data+"]";
    }
}
