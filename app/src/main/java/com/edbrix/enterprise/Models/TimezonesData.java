package com.edbrix.enterprise.Models;

/**
 * Created by rajk on 05/01/18.
 */

public class TimezonesData {
    private String Timezone;

    private String Location;

    private int Id;

    public String getTimezone ()
    {
        return Timezone;
    }

    public void setTimezone (String Timezone)
    {
        this.Timezone = Timezone;
    }

    public String getLocation ()
    {
        return Location;
    }

    public void setLocation (String Location)
    {
        this.Location = Location;
    }

    public int getId ()
    {
        return Id;
    }

    public void setId (int Id)
    {
        this.Id = Id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Timezone = "+Timezone+", Location = "+Location+", Id = "+Id+"]";
    }
}
