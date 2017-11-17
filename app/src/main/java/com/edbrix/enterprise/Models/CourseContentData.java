package com.edbrix.enterprise.Models;

/**
 * Created by rajk on 16/11/17.
 */

public class CourseContentData {

    private String description;

    private String submit_type;

    private SubmitData submit_data;

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

    @Override
    public String toString()
    {
        return "ClassPojo [description = "+description+", submit_type = "+submit_type+", submit_data = "+submit_data+"]";
    }
}
