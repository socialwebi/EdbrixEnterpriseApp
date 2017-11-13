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
import android.view.View;
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
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

public class DashboardActivity extends AppCompatActivity {

    Context context;

    RelativeLayout layout;

    RecyclerView _dashboard_recycler_meetings;
    RecyclerView _dashboard_recycler_courses;

    TextView _dashboard_text_all_meetings;
    TextView _dashboard_text_all_course;

    DashBoardCourseListAdapter courseAdapter;
    DashBoardMeetingListAdapter meetingAdapter;

    private ArrayList<Meeting> meetings;
    private ArrayList<Courses> courses;

    private String deviceType;
    private String dataType = "all";
    private int pageNo = 1;

    private int val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        context = DashboardActivity.this;

        // if ()
            deviceType = "mob";
        // else
            // deviceType = "tab";

        meetings = new ArrayList<>();
        courses = new ArrayList<>();

        _dashboard_recycler_meetings = findViewById(R.id.dashboard_recycler_meetings);
        _dashboard_text_all_meetings = findViewById(R.id.dashboard_text_all_meetings);
        _dashboard_recycler_courses = findViewById(R.id.dashboard_recycler_courses);
        _dashboard_text_all_course = findViewById(R.id.dashboard_text_all_course);

        courseAdapter = new DashBoardCourseListAdapter(context, courses, new DashboardListInterface() {
            @Override
            public void onListSelected(String id, String type) {

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

        User activeUser = SettingsMy.getActiveUser();
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

                            if (response.getErrorCode()==null) {
                                courseAdapter.refresh(response.getCoursesList());
                                meetingAdapter.refresh(response.getMeeting());
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
}
