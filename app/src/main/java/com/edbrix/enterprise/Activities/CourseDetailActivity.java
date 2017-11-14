package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class CourseDetailActivity extends BaseActivity {

    public static final String courseDetailBundleKey = "courseDetailItem";

    private Courses courseDetailItem;

    private TextView title;
    private TextView courseDesc;

    private ImageView courseImage;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        context = CourseDetailActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (TextView) toolbar.findViewById(R.id.title);
        courseDesc = (TextView) findViewById(R.id.txtCourseDesc);
        courseImage = (ImageView) findViewById(R.id.courseImage);

        courseDetailItem = (Courses) getIntent().getSerializableExtra(courseDetailBundleKey);
        if (courseDetailItem != null) {
            //set Course Details
            setCourseDetails();
        } else {
            //show message and finish activity
        }
    }


    private void setCourseDetails() {

        title.setText(courseDetailItem.getTitle());
        courseDesc.setText(courseDetailItem.getDescription());
        if (courseDetailItem.getCourse_image_url() != null && !courseDetailItem.getCourse_image_url().isEmpty()) {
            Picasso.with(context)
                    .load(courseDetailItem.getCourse_image_url())
                    .error(R.drawable.edbrix_logo)
                    .into(courseImage);
        }
    }
}
