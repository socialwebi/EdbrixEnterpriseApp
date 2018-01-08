package com.edbrix.enterprise.Models;


import java.io.Serializable;

public class CourseContents implements Serializable{

    private String id;
    private String title;
    private String type;
    // private String preview;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    /*public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }*/

    @Override
    public String toString() {
        return "CourseContents { " +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
//                ", preview='" + preview + '\'' +
                '}';
    }

}
