package com.edbrix.enterprise.Models;


import java.util.ArrayList;

public class Meetings {

    private ArrayList<Meeting> meeting;

    private String date;

    public ArrayList<Meeting> getMeeting() {
        return meeting;
    }

    public void setMeeting(ArrayList<Meeting> meeting) {
        this.meeting = meeting;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MEETINGS [ date = " + date + ", meeting = " + meeting + "]";
    }


}
