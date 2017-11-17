package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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

//        String welcomStr ="\u003Cp style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-stretch: inherit; line-height: 20px; font-family: &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, arial, sans-serif; vertical-align: baseline; color: black !important;\"\u003EThe Internet, linking your computer to other computers around the world, is a way of transporting content. The Web is software that lets you use that content&hellip;or contribute your own. The Web, running on the mostly invisible Internet, is what you see and click on in your computer&rsquo;s browser.\u003C\\/p\u003E\r\n\r\n\u003Cp style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-stretch: inherit; line-height: 20px; font-family: &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, arial, sans-serif; vertical-align: baseline; color: black !important;\"\u003EThe Internet&rsquo;s roots are in the U.S. during the late 1960s. The Web was invented 20 years later by an Englishman working in Switzerland&mdash;though it had many predecessors.\u003C\\/p\u003E\r\n\r\n\u003Cp style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-stretch: inherit; line-height: 20px; font-family: &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, arial, sans-serif; vertical-align: baseline; color: black !important;\\\"\\u003ETo keep things &ldquo;interesting,&rdquo; many people use the term Internet to refer to both.\u003C\\/p\u003E\r\n";
//                courseDesc.setText(Html.fromHtml(welcomStr));
        if (courseDetailItem.getCourse_image_url() != null && !courseDetailItem.getCourse_image_url().isEmpty()) {
            Picasso.with(context)
                    .load(courseDetailItem.getCourse_image_url())
                    .error(R.drawable.edbrix_logo)
                    .into(courseImage);
        }
    }
}
