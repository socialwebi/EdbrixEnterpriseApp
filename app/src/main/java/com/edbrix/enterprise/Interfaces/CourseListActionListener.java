package com.edbrix.enterprise.Interfaces;

import com.edbrix.enterprise.Models.Courses;

public interface CourseListActionListener {
    void onCourseItemSelected(Courses courses);

    void onCoursePlayClick(Courses courses);

    void onCourseMessageClick(String mobNo);

    void onCourseCallClick(String mobNo);
}
