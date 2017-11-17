package com.edbrix.enterprise.Interfaces;

import com.edbrix.enterprise.Models.Courses;

/**
 * Created by rajk on 14/11/17.
 */

public interface CourseListActionListener {
    void onCourseItemSelected(Courses courses);

    void onCoursePlayClick(Courses courses);

    void onCourseMessageClick(String mobNo);

    void onCourseCallClick(String mobNo);
}
