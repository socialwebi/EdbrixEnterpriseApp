package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private TextView txtCourseBy;
    private TextView courseDesc;

    private ImageView courseImage;
    private ImageView btnCoursePlay;
    private ImageView btnCourseMsg;
    private ImageView btnCourseCall;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        context = CourseDetailActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title = (TextView) toolbar.findViewById(R.id.title);
        txtCourseBy = (TextView) findViewById(R.id.txtCourseBy);
        courseDesc = (TextView) findViewById(R.id.txtCourseDesc);

        courseImage = (ImageView) findViewById(R.id.courseImage);
        btnCoursePlay = (ImageView) findViewById(R.id.btnCoursePlay);
        btnCourseMsg = (ImageView) findViewById(R.id.btnCourseMsg);
        btnCourseCall = (ImageView) findViewById(R.id.btnCourseCall);

        btnCoursePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (courseDetailItem != null) {
                    Intent playCourse = new Intent(context, PlayCourseActivity.class);
                    playCourse.putExtra(PlayCourseActivity.courseItemBundleKey, courseDetailItem);
                    startActivity(playCourse);
                } else {

                }
            }
        });

        btnCourseCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialContactPhone((String) view.getTag());
            }
        });

        btnCourseMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS((String) view.getTag(), "Hi..,");
            }
        });

        courseDetailItem = (Courses) getIntent().getSerializableExtra(courseDetailBundleKey);
        if (courseDetailItem != null) {
            //set Course Details
            setCourseDetails();
        } else {
            //show message and finish activity
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.editOption:
                goToEditCourse();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToEditCourse() {
        if (courseDetailItem != null) {
            Intent intent = new Intent(CourseDetailActivity.this, CreateLiveCourseActivity.class);
            intent.putExtra("courseId", courseDetailItem.getId());
            intent.putExtra("courseTitle", courseDetailItem.getTitle());
            startActivity(intent);
            finish();
        }
    }

    private void setCourseDetails() {
        title.setText(courseDetailItem.getTitle());
        txtCourseBy.setText("By " + courseDetailItem.getInstructor_name());
        courseDesc.setText(courseDetailItem.getDescription());
        btnCourseCall.setTag(courseDetailItem.getInstructor_mobileno());
        btnCourseMsg.setTag(courseDetailItem.getInstructor_mobileno());


//        String welcomStr ="\u003Cp style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-stretch: inherit; line-height: 20px; font-family: &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, arial, sans-serif; vertical-align: baseline; color: black !important;\"\u003EThe Internet, linking your computer to other computers around the world, is a way of transporting content. The Web is software that lets you use that content&hellip;or contribute your own. The Web, running on the mostly invisible Internet, is what you see and click on in your computer&rsquo;s browser.\u003C\\/p\u003E\r\n\r\n\u003Cp style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-stretch: inherit; line-height: 20px; font-family: &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, arial, sans-serif; vertical-align: baseline; color: black !important;\"\u003EThe Internet&rsquo;s roots are in the U.S. during the late 1960s. The Web was invented 20 years later by an Englishman working in Switzerland&mdash;though it had many predecessors.\u003C\\/p\u003E\r\n\r\n\u003Cp style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-stretch: inherit; line-height: 20px; font-family: &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, arial, sans-serif; vertical-align: baseline; color: black !important;\\\"\\u003ETo keep things &ldquo;interesting,&rdquo; many people use the term Internet to refer to both.\u003C\\/p\u003E\r\n";
//                courseDesc.setText(Html.fromHtml(welcomStr));
        if (courseDetailItem.getCourse_image_url() != null && !courseDetailItem.getCourse_image_url().isEmpty()) {
            Picasso.with(context)
                    .load(courseDetailItem.getCourse_image_url())
                    .error(R.drawable.edbrix_logo)
                    .into(courseImage);
        }
    }

    /**
     * Send intent to phone dialer
     *
     * @param phoneNumber phone number
     */
    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    /**
     * Send intent to messenger to send sms
     *
     * @param phoneNumber phone number
     * @param smsBody     sms body
     */
    private void sendSMS(String phoneNumber, String smsBody) {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body", smsBody);

        try {
            startActivity(smsIntent);
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("SMS faild, please try again later.");
        }
    }
}
