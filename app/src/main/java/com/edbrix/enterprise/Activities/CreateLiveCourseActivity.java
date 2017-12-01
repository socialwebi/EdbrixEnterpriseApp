package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.MainActivity;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.TypesC;
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
import java.util.List;

import timber.log.Timber;

public class CreateLiveCourseActivity extends BaseActivity {

    LinearLayout layout;
    Context context;
    TextInputLayout _create_text_input_price;
    TextInputEditText _live_course_title;
    TextInputEditText _live_course_price;
    TextInputEditText _live_course_code;
    Button _live_course_button_submit;
    TextView _create_text_category;
    Spinner _create_spinner_category;

    private String courseId;
    private String courseName;
    private String categoryId = "0";
    private ArrayList<String> arrayList;

    private String title;
    private String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live_course);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = CreateLiveCourseActivity.this;

        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        courseName = intent.getStringExtra("courseTitle");

        _create_text_input_price = findViewById(R.id.create_text_input_price);
        _live_course_code = findViewById(R.id.create_course_code);
        _live_course_title = findViewById(R.id.live_course_title);
        _live_course_price = findViewById(R.id.live_course_price);
        _create_text_category = findViewById(R.id.create_text_category);
        _create_spinner_category = findViewById(R.id.create_spinner_category);
        _live_course_button_submit = findViewById(R.id.live_course_button_submit);

        if (courseId==null) {
            courseId = "0";
            visibleCode(false);
        } else {
            visibleCode(true);
        }

        _live_course_price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    _live_course_price.setHint(null);
                } else {
                    _live_course_price.setHint("Leave blank to free ");
                }
            }
        });
        if (courseName!=null) {
            _live_course_title.setText(courseName);
        }

        _live_course_button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });

        arrayList = new ArrayList<>();
        arrayList.add("Category");

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter3.setDropDownViewResource(R.layout.custom_text_layout);
        _create_spinner_category.setAdapter(adapter3);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void checkValidations() {
        title = _live_course_title.getText().toString().trim();
        price = _live_course_price.getText().toString().trim();

        if (title.isEmpty()) {
            _live_course_title.setError(getString(R.string.error_edit_text));
        } else if (price.isEmpty()) {
            _live_course_title.setError(null);
            price = "0";
            Intent intent = new Intent(CreateLiveCourseActivity.this, CreateVideoCourseActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("price", price);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        } else {
            _live_course_title.setError(null);
            Intent intent = new Intent(CreateLiveCourseActivity.this, CreateVideoCourseActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("price", price);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        }

    }

    private void visibleCode(boolean val){

        if (val) {
            _create_text_category.setVisibility(View.VISIBLE);
            _live_course_code.setVisibility(View.VISIBLE);
            _create_spinner_category.setVisibility(View.VISIBLE);
            _live_course_button_submit.setText(" SAVE ");
        } else {
            _create_text_category.setVisibility(View.GONE);
            _live_course_code.setVisibility(View.GONE);
            _create_spinner_category.setVisibility(View.GONE);
            _live_course_button_submit.setText(" NEXT ");
        }

    }

    private void editCourse() {

        User user = SettingsMy.getActiveUser();
        if (user!=null) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("Title", title);
                jo.put("Price", price);
                jo.put("CourseId", courseId);
                jo.put("CategoryId", categoryId);

            } catch (JSONException e) {
                Timber.e(e, "Parse create course exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Course: %s", jo.toString());

            JsonRequest req = new JsonRequest(Request.Method.POST, Constants.setCreateCourse, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    Timber.d("Response : %s", response.toString());
                    try {
                        Timber.d("Disclaimer : %s", response.getString("id"));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            req.setRetryPolicy(Application.getDefaultRetryPolice());
            req.setShouldCache(false);
            Application.getInstance().addToRequestQueue(req, "create_course_requests");

        }
    }

    private void getData(Courses courses) {

        _live_course_code.setText(courses.getCode());
        _live_course_title.setText(courses.getTitle());
        _live_course_price.setText(courses.getPrice());

        arrayList = new ArrayList<>();

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter3.setDropDownViewResource(R.layout.custom_text_layout);
        _create_spinner_category.setAdapter(adapter3);

    }

}
