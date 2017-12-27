package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rajk on 17/11/17.
 */

public class LearnersData {

    @SerializedName("learnerId")
    private String learnerId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    private boolean checked;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(String learnerId) {
        this.learnerId = learnerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "ClassPojo [learnerId = " + learnerId + ", fullName = " + fullName + ", email = " + email + "]";
    }

}
