package com.edbrix.enterprise.Models;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rajk on 17/11/17.
 */

public class ChoicesInputData {

    @SerializedName("id")
    private String id;

    public ChoicesInputData(String id) {
        super();
        this.id = id;
    }

    public static ChoicesInputData addChoiceData(String id) {
        return new ChoicesInputData(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + "]";
    }

}
