package com.edbrix.enterprise.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.CategorySpinnerAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.Models.CourseContents;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.JsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditCourseActivity extends BaseActivity {

    static final String courseIDKEY = "courseID";

    private TextView txtCourseCode;
    private TextView txtCourseImageName;

    private EditText edtCourseName;
    private EditText edtCoursePrice;

    private Spinner spnrCategory;

    private Button btnSave;
    private Button btnBrowseFile;

    private String courseId;
    private String courseName;
    private String coursePrice;
    private String categoryId;

    private ArrayList<CourseContents> categoryDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        txtCourseCode = findViewById(R.id.txtCourseCode);
        txtCourseImageName = findViewById(R.id.txtCourseImageName);

        edtCourseName = findViewById(R.id.edtCourseName);
        edtCoursePrice = findViewById(R.id.edtCoursePrice);

        spnrCategory = findViewById(R.id.spnrCategory);

        btnSave = findViewById(R.id.btnSave);
        btnBrowseFile = findViewById(R.id.btnBrowseFile);

        courseId = "";
        courseId = getIntent().getStringExtra(courseIDKEY);

        categoryDataList = new ArrayList<>();

        setListeners();

        getCourseDetails(courseId);

    }

    private void setListeners() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidationsWithCourseId();
            }
        });

        btnBrowseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void getCourseDetails(String courseId) {
        showBusyProgress();
        User activeUser = SettingsMy.getActiveUser();
        if (activeUser != null) {

            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", activeUser.getId());
                jo.put("AccessToken", activeUser.getAccessToken());
                jo.put("UserType", activeUser.getUserType());
                jo.put("CourseId", courseId);
            } catch (JSONException e) {
                return;
            }

            GsonRequest<Courses> getCourseDetailsRequest = new GsonRequest<>(Request.Method.POST, Constants.getCourseDetails, jo.toString(), Courses.class,
                    new Response.Listener<Courses>() {
                        @Override
                        public void onResponse(@NonNull Courses response) {

                            if (response.getErrorCode() == null) {
                                setCourseData(response);
                                hideBusyProgress();
                            } else {
                                hideBusyProgress();
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
            getCourseDetailsRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getCourseDetailsRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getCourseDetailsRequest, "course_details_requests");
        }
    }

    private void setCourseData(Courses courses) {
        txtCourseCode.setText(courses.getCode());
        edtCourseName.setText(courses.getTitle());
        edtCoursePrice.setText(courses.getPrice());
        categoryDataList.addAll(courses.getCourseCategory());

        CategorySpinnerAdapter categorySpinnerAdapter = new CategorySpinnerAdapter(EditCourseActivity.this);
        categorySpinnerAdapter.addItems(courses.getCourseCategory());
        spnrCategory.setAdapter(categorySpinnerAdapter);

        spnrCategory.setSelection(categorySpinnerAdapter.getCategoryListPosition(courses.getCoursecategoryId()));
    }

    private void checkValidationsWithCourseId() {
        courseName = edtCourseName.getText().toString().trim();
        coursePrice = edtCoursePrice.getText().toString().trim();
        if (!categoryDataList.isEmpty()) {
            categoryId = categoryDataList.get(spnrCategory.getSelectedItemPosition()).getId();
        } else {
            categoryId = "0";
        }
        Log.d("TAG", "categoryId - " + categoryId);
        edtCourseName.setError(null);
        edtCoursePrice.setError(null);

        if (courseName.isEmpty()) {
            edtCourseName.setError(getString(R.string.error_edit_text));
        } else if (coursePrice.isEmpty()) {
            coursePrice = "0";
            if (!courseId.isEmpty() && !courseId.equals("0")) {
                saveCourseDetails(courseId);
            } else {
                showToast("Course ID not found. Unable to save.");
            }
        } else {
            if (!courseId.isEmpty() && !courseId.equals("0")) {
                saveCourseDetails(courseId);
            } else {
                showToast("Course ID not found. Unable to save.");
            }
        }

    }

    private void saveCourseDetails(String courseID) {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            showBusyProgress();
            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("Title", courseName);
                jo.put("Price", coursePrice);
                jo.put("CourseId", courseID);
                jo.put("CategoryId", categoryId);

                // Course Image =-------------------------------------= jo.put("Image", path..);


            } catch (JSONException e) {
                Log.v("Exception", e.getMessage());
                e.printStackTrace();
                return;
            }

            JsonRequest req = new JsonRequest(Request.Method.POST, Constants.setCreateCourse, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        hideBusyProgress();
                        if (response.getString("ErrorCode").equals("0")) {

                            showToast("Course updated successfully.");

//                            Intent intent = new Intent(CreateLiveCourseActivity.this, CreateVideoCourseActivity.class);
//                            intent.putExtra("courseId", courseId);
//                            startActivityForResult(intent, 1);
                            setResult(RESULT_OK);
                            finish();

                        } else {
                            showToast(response.getString("ErrorMessage"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            req.setRetryPolicy(Application.getDefaultRetryPolice());
            req.setShouldCache(false);
            Application.getInstance().addToRequestQueue(req, "create_course_requests");

        }
    }
}
