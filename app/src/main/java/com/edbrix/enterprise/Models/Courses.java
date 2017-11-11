package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rajk on 11/11/17.
 */

public class Courses {
    @SerializedName("id")
    private String id;

    @SerializedName("access_code")
    private String access_code;

    @SerializedName("title")
    private String title;

    @SerializedName("instructor_email")
    private String instructor_email;

    @SerializedName("instructor_name")
    private String instructor_name;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("description")
    private String description;

    @SerializedName("instructor_mobileno")
    private String instructor_mobileno;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getAccess_code ()
    {
        return access_code;
    }

    public void setAccess_code (String access_code)
    {
        this.access_code = access_code;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getInstructor_email ()
    {
        return instructor_email;
    }

    public void setInstructor_email (String instructor_email)
    {
        this.instructor_email = instructor_email;
    }

    public String getInstructor_name ()
    {
        return instructor_name;
    }

    public void setInstructor_name (String instructor_name)
    {
        this.instructor_name = instructor_name;
    }

    public String getImage_url ()
    {
        return image_url;
    }

    public void setImage_url (String image_url)
    {
        this.image_url = image_url;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getInstructor_mobileno ()
    {
        return instructor_mobileno;
    }

    public void setInstructor_mobileno (String instructor_mobileno)
    {
        this.instructor_mobileno = instructor_mobileno;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", access_code = "+access_code+", title = "+title+", instructor_email = "+instructor_email+", instructor_name = "+instructor_name+", image_url = "+image_url+", description = "+description+", instructor_mobileno = "+instructor_mobileno+"]";
    }
}
