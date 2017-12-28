package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

public class CreateVideoCourseActivity extends BaseActivity {

    private Context context;
    private RelativeLayout layout;

    private CardView _create_card_video;
    private CardView _create_card_document;
    private RecyclerView _create_recycler;
    private ProgressBar _create_progress;

    private TextView create_text_content;

    private FileListAdapter adapter;

    private String title;
    private String price;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video_course);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = CreateVideoCourseActivity.this;

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        price = intent.getStringExtra("price");
        courseId = intent.getStringExtra("courseId");

        _create_card_video = findViewById(R.id.create_card_video);
        _create_card_document = findViewById(R.id.create_card_document);
        _create_recycler = findViewById(R.id.create_recycler);
        _create_progress = findViewById(R.id.create_progress);
        create_text_content = findViewById(R.id.create_text_content);

        if (!courseId.equals("0")) {
            getCourseContent();
        }

        _create_card_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateVideoCourseActivity.this, AddFilesActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("price", price);
                intent.putExtra("type", "1");
                intent.putExtra("courseId", courseId);
                startActivityForResult(intent, 1);
            }
        });

        _create_card_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateVideoCourseActivity.this, AddFilesActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("price", price);
                intent.putExtra("type", "2");
                intent.putExtra("courseId", courseId);
                startActivityForResult(intent, 1);
            }
        });

        adapter = new FileListAdapter(context, new ArrayList<CourseContents>(), new CourseContentButtonListener() {

            @Override
            public void onCourseDeleteClick(String id, int position) {
                setCourseContentDelete(id, position);
            }

            @Override
            public void onCoursePreviewClick(String id, String path) {

            }
        });

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        assert _create_recycler != null;
        _create_recycler.setHasFixedSize(true);
        _create_recycler.setLayoutManager(linearLayoutManager1);
        registerForContextMenu(_create_recycler);
        _create_recycler.setAdapter(adapter);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("newCourseId", courseId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    courseId = data.getStringExtra("newCourseId");
                    getCourseContent();
                    break;
                }

            case 2:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    courseId = data.getStringExtra("newCourseId");
                    getCourseContent();
                    break;
                }
        }
    }

    private void getCourseContent() {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            _create_progress.setVisibility(View.VISIBLE);
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

            GsonRequest<ResponseData> userChangePasswordRequest = new GsonRequest<>(Request.Method.POST, Constants.getCourseContent, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {
                            _create_progress.setVisibility(View.GONE);
                            Timber.d("response: %s", response.toString());
                            if (response.getErrorCode() == null) {

                                if (response.getCourseContents() != null && response.getCourseContents().size() > 0) {
                                    create_text_content.setVisibility(View.VISIBLE);
                                } else {
                                    create_text_content.setVisibility(View.INVISIBLE);
                                }
                                adapter.refresh(response.getCourseContents());
                                adapter.notifyDataSetChanged();

                            } else {

                                try {
                                    Timber.d("Error: %s", response.getErrorMessage());
                                    Snackbar.make(layout, response.getErrorMessage(), Snackbar.LENGTH_LONG).show();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    Timber.d("Error: %s", response.getErrorMessage());
                                    Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    _create_progress.setVisibility(View.GONE);
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            userChangePasswordRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            userChangePasswordRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(userChangePasswordRequest, "get_course_content_requests");

        }
    }

    private void setCourseContentDelete(String id, final int position) {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            _create_progress.setVisibility(View.VISIBLE);
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

            GsonRequest<ResponseData> userChangePasswordRequest = new GsonRequest<>(Request.Method.POST, Constants.setDeleteCourseContent, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {
                            _create_progress.setVisibility(View.GONE);
                            Timber.d("response: %s", response.toString());
                            if (response.getErrorCode() == null || response.getErrorCode().equals("0")) {

                                try {
                                    Timber.d("Error: %s", "Content deleted successfully ");
                                    Snackbar.make(layout, response.getErrorMessage(), Snackbar.LENGTH_LONG).show();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    Timber.d("Error: %s", "Content deleted successfully ");
                                    Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_LONG).show();
                                }

                                adapter.deleteItemFromList(position);
                                adapter.notifyDataSetChanged();

                            } else {

                                try {
                                    Timber.d("Error: %s", response.getErrorMessage());
                                    Snackbar.make(layout, response.getErrorMessage(), Snackbar.LENGTH_LONG).show();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    Timber.d("Error: %s", response.getErrorMessage());
                                    Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    _create_progress.setVisibility(View.GONE);
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            userChangePasswordRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            userChangePasswordRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(userChangePasswordRequest, "get_course_content_requests");

        }
    }

}
