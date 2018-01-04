package com.edbrix.enterprise.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.FileListAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Interfaces.CourseContentButtonListener;
import com.edbrix.enterprise.Models.CourseContents;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

public class PublishCourseActivity extends BaseActivity {

    static final String courseIDKEY = "courseID";

    private TextView txtCourseCode;

    private RecyclerView contentListRecycler;

    private Button btnPublish;

    private String courseId = "";

    private FileListAdapter fileListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_course);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        courseId = getIntent().getStringExtra(courseIDKEY);

        txtCourseCode = findViewById(R.id.txtCourseCode);
        contentListRecycler = findViewById(R.id.contentListRecycler);
        contentListRecycler.setLayoutManager(new LinearLayoutManager(PublishCourseActivity.this));
        btnPublish = findViewById(R.id.btnPublish);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        fileListAdapter = new FileListAdapter(PublishCourseActivity.this, new ArrayList<CourseContents>(), new CourseContentButtonListener() {

            @Override
            public void onCourseDeleteClick(String id, int position) {
                setCourseContentDelete(id, position);
            }

            @Override
            public void onCoursePreviewClick(String id, String path) {

            }
        });

        contentListRecycler.setAdapter(fileListAdapter);

        getCourseContent();



    }

    private void getCourseContent() {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            showBusyProgress();
            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("CourseId", courseId);

            } catch (JSONException e) {
                Timber.e(e, "Parse create course exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Course: %s", jo.toString());

            GsonRequest<ResponseData> courseContentRequest = new GsonRequest<>(Request.Method.POST, Constants.getCourseContent, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {
                            hideBusyProgress();
                            if (response.getErrorCode() == null) {

                                if (response.getCourseContents() != null && response.getCourseContents().size() > 0) {
                                    fileListAdapter.refresh(response.getCourseContents());
                                    fileListAdapter.notifyDataSetChanged();
                                }

                            } else {
                                showToast(response.getErrorMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            courseContentRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            courseContentRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(courseContentRequest, "get_course_content_requests");

        }
    }

    private void setCourseContentDelete(String id, final int position) {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            showBusyProgress();
            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("CourseId", courseId);
                jo.put("CourseContentId", id);

            } catch (JSONException e) {
                Timber.e(e, "Parse create course exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Course: %s", jo.toString());

            GsonRequest<ResponseData> courseContentDeleteRequest = new GsonRequest<>(Request.Method.POST, Constants.setDeleteCourseContent, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {

                            hideBusyProgress();
                            if (response.getErrorCode() == null || response.getErrorCode().equals("0")) {
                                showToast("Content deleted successfully.");
                                fileListAdapter.deleteItemFromList(position);
                                fileListAdapter.notifyDataSetChanged();
                            } else {
                                showToast(response.getErrorMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            courseContentDeleteRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            courseContentDeleteRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(courseContentDeleteRequest, "get_course_content_requests");

        }
    }
}
