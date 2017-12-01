package com.edbrix.enterprise.Interfaces;


import com.edbrix.enterprise.Models.Courses;

public interface CourseContentButtonListener {

    void onCourseDeleteClick(String id, int position);

    void onCoursePreviewClick(String id, String path);

}
