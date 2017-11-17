package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rajk on 17/11/17.
 */

public class ChoicesData {

    @SerializedName("id")
    private String id;

    @SerializedName("choice")
    private String choice;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getChoice ()
    {
        return choice;
    }

    public void setChoice (String choice)
    {
        this.choice = choice;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", choice = "+choice+"]";
    }

}
