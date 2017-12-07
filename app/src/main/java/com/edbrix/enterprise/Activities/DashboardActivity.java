package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.DashBoardCourseListAdapter;
import com.edbrix.enterprise.Adapters.DashBoardMeetingListAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Interfaces.DashboardListInterface;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.Meeting;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Conditions;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Utils.SessionManager;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;

public class DashboardActivity extends BaseActivity {

    Context context;

    RelativeLayout layout;

    RecyclerView _dashboard_recycler_meetings;
    RecyclerView _dashboard_recycler_courses;

    TextView _dashboard_text_all_meetings;
    TextView _dashboard_text_all_course;
    FloatingActionsMenu _floatingActionMenu;
    FloatingActionButton _fab_course;
    FloatingActionButton _fab_meeting;

    ProgressBar _dashboard_progress;

    DashBoardCourseListAdapter courseAdapter;
    DashBoardMeetingListAdapter meetingAdapter;

    private ArrayList<Meeting> meetings;
    private ArrayList<Courses> courses;

    private String deviceType;
    private String dataType = "all";
    private int pageNo = 1;

    User activeUser;
    private SessionManager sessionManager;

    private int val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setElevation(0);

        context = DashboardActivity.this;

        sessionManager = new SessionManager(context);
        // if ()
            deviceType = sessionManager.getSessionDeviceType();
        // else
            // deviceType = "tab";

        meetings = new ArrayList<>();
        courses = new ArrayList<>();

        activeUser = SettingsMy.getActiveUser();

        _dashboard_recycler_meetings = findViewById(R.id.dashboard_recycler_meetings);
        _dashboard_text_all_meetings = findViewById(R.id.dashboard_text_all_meetings);
        _dashboard_recycler_courses = findViewById(R.id.dashboard_recycler_courses);
        _dashboard_text_all_course = findViewById(R.id.dashboard_text_all_course);
        _dashboard_progress = findViewById(R.id.dashboard_progress);
        _floatingActionMenu = findViewById(R.id.floatingActionMenu);
        _fab_course = findViewById(R.id.fab_course);
        _fab_meeting = findViewById(R.id.fab_meeting);

        courseAdapter = new DashBoardCourseListAdapter(context, courses, new DashboardListInterface() {
            @Override
            public void onListSelected(String id, String type) {
                Intent intent = new Intent(DashboardActivity.this, CreateLiveCourseActivity.class);
                intent.putExtra("courseId", id);
                intent.putExtra("courseTitle", type);
                startActivity(intent);
            }
        });

        meetingAdapter = new DashBoardMeetingListAdapter(context, meetings, new DashboardListInterface() {
            @Override
            public void onListSelected(String id, String type) {

            }
        });

        _dashboard_text_all_meetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BottomTabMenuActivity.class);
                val = 0;
                intent.putExtra(BottomTabMenuActivity.tabIndexKey, val);
                startActivity(intent);
            }
        });

        _dashboard_text_all_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BottomTabMenuActivity.class);
                val = 1;
                intent.putExtra(BottomTabMenuActivity.tabIndexKey, val);
                startActivity(intent);
            }
        });

        _fab_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _floatingActionMenu.collapse();
                Intent intent = new Intent(DashboardActivity.this, CreateLiveCourseActivity.class);
                startActivity(intent);
            }
        });

        _fab_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _floatingActionMenu.collapse();
                Intent intent = new Intent(DashboardActivity.this, CreateScheduleActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        assert _dashboard_recycler_meetings != null;
        _dashboard_recycler_meetings.setHasFixedSize(true);
        _dashboard_recycler_meetings.setLayoutManager(linearLayoutManager1);
        registerForContextMenu(_dashboard_recycler_meetings);
        _dashboard_recycler_meetings.setAdapter(meetingAdapter);


        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
        assert _dashboard_recycler_courses != null;
        _dashboard_recycler_courses.setHasFixedSize(true);
        _dashboard_recycler_courses.setLayoutManager(linearLayoutManager2);
        registerForContextMenu(_dashboard_recycler_courses);
        _dashboard_recycler_courses.setAdapter(courseAdapter);

        if (activeUser.getUserType().equals("L")) {
            _floatingActionMenu.setVisibility(View.GONE);
        } else {
            _floatingActionMenu.setVisibility(View.VISIBLE);
        }

        if (Conditions.isNetworkConnected(DashboardActivity.this)) {
            getDashBoardList();
        }
        else {
            try {
                Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(DashboardActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void getDashBoardList() {

        _dashboard_progress.setVisibility(View.VISIBLE);
        if (activeUser!=null) {

            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", activeUser.getId());
                jo.put("AccessToken", activeUser.getAccessToken());
                jo.put("UserType", activeUser.getUserType());
                jo.put("DeviceType", deviceType);
                jo.put("DataType", dataType);
                jo.put("Page", pageNo);
            } catch (JSONException e) {
                Timber.e(e, "Parse getCourseList exception");
                return;
            }

            if (BuildConfig.DEBUG) Timber.d("getCourseList Request Param: %s", jo.toString());

            GsonRequest<ResponseData> getDashboardCourseSchedulesRequest = new GsonRequest<>(Request.Method.POST, Constants.getDashboardCourseSchedules, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {
                            Timber.d("response: %s", response.toString());
                            _dashboard_progress.setVisibility(View.INVISIBLE);
                            if (response.getErrorCode()==null) {

                                if (response.getCoursesList()!=null) {
                                    courseAdapter.refresh(response.getCoursesList());
                                    courseAdapter.notifyDataSetChanged();
                                } if (response.getMeetings()!=null){
                                    meetingAdapter.refresh(response.getMeetings());
                                    meetingAdapter.notifyDataSetChanged();
                                } else {
                                    _dashboard_text_all_meetings.setText("No meetings available ");
                                }
                            }
                            else {
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
                    _dashboard_progress.setVisibility(View.INVISIBLE);
                    Timber.d("Error: %s", error.getMessage());
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            getDashboardCourseSchedulesRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getDashboardCourseSchedulesRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getDashboardCourseSchedulesRequest, "dashboard_requests");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settingsOption:

                Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
