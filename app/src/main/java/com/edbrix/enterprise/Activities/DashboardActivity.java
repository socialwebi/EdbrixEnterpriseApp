package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.edbrix.enterprise.commons.GlobalMethods;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingEvent;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class DashboardActivity extends BaseActivity implements ZoomSDKInitializeListener, MeetingServiceListener {

    private static String DISPLAY_NAME = "User";
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
    User user;
    private ArrayList<Meeting> meetings;
    private ArrayList<Courses> courses;
    private String deviceType;
    private String dataType = "all";
    private int pageNo = 1;
    private String meetingNo;
    private View positiveAction;
    private boolean mbPendingStartMeeting = false;
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

        assert user != null;
        user = SettingsMy.getActiveUser();

        _dashboard_recycler_meetings = findViewById(R.id.dashboard_recycler_meetings);
        _dashboard_text_all_meetings = findViewById(R.id.dashboard_text_all_meetings);
        _dashboard_recycler_courses = findViewById(R.id.dashboard_recycler_courses);
        _dashboard_text_all_course = findViewById(R.id.dashboard_text_all_course);
        _dashboard_progress = findViewById(R.id.dashboard_progress);
        _floatingActionMenu = findViewById(R.id.floatingActionMenu);
        _fab_course = findViewById(R.id.fab_course);
        _fab_meeting = findViewById(R.id.fab_meeting);

        _dashboard_text_all_meetings.setText("No meetings available ");
        _dashboard_text_all_course.setText("No courses available ");

        if (savedInstanceState == null) {
            ZoomSDK sdk = ZoomSDK.getInstance();
            sdk.initialize(this, Constants.APP_KEY, Constants.APP_SECRET, Constants.WEB_DOMAIN, this);
        } else {
            registerMeetingServiceListener();
        }

        courseAdapter = new DashBoardCourseListAdapter(context, courses, new DashBoardCourseListAdapter.DashboardListInterface() {
            @Override
            public void onListSelected(Courses course) {
                if (course != null) {
                    Intent courseDetail = new Intent(context, CourseDetailActivity.class);
                    courseDetail.putExtra(CourseDetailActivity.courseDetailBundleKey, course);
                    startActivityForResult(courseDetail, 205);
                }
              /*  Intent intent = new Intent(DashboardActivity.this, CreateLiveCourseActivity.class);
                intent.putExtra("courseId", course.getId());
                intent.putExtra("courseTitle", course.getTitle());
                startActivity(intent);*/
            }
        });

        meetingAdapter = new DashBoardMeetingListAdapter(context, meetings, new DashboardListInterface() {
            @Override
            public void onListSelected(Object object) {

                final Meeting meeting = (Meeting) object;
                meetingNo = meeting.getId();

                Intent intent = new Intent(context, MeetingDetailActivity.class);
                intent.putExtra("meetingId", meeting.getId());
                intent.putExtra("meetingType", meeting.getType());
                intent.putExtra("meetingTitle", meeting.getTitle());
                context.startActivity(intent);

                assert user != null;
               /* if (!user.getUserType().equals("L")) {
                    Intent intent = new Intent(context, MeetingDetailActivity.class);
                    intent.putExtra("meetingId", meeting.getId());
                    intent.putExtra("meetingType", meeting.getType());
                    intent.putExtra("meetingTitle", meeting.getTitle());
                    context.startActivity(intent);
                } else {

                    String title = meeting.getTitle();
                    String message = "Time: " + meeting.getStartDateTime() + " - "
                            + meeting.getEndDateTime() + "\n";

                    if (meeting.getIsFree().equals("1")) {

                        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                                .customView(R.layout.custom_view, true)
                                .positiveText(R.string.join)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Log.d("TAG", "ID: " + meeting.getMeetingId());
                                        if (meeting.getConnectType().equals("ZOOM")) {
                                            meetingNo = meeting.getMeetingId();

                                            assert user != null;
                                            if (user.getUserType().equals("L")) {
                                                Log.d("TAG", "----JOIN----");
                                                onClickBtnJoinMeeting();
                                            }
                                        } else if (meeting.getConnectType().equals(Constants.availabilityType_TrainingSession)) {
                                            Intent tokboxIntent = new Intent(DashboardActivity.this, TokBoxActivity.class);
                                            tokboxIntent.putExtra(Constants.TolkBox_SessionId, meeting.getMeetingId());
                                            tokboxIntent.putExtra(Constants.TolkBox_Token, meeting.getMeetingToken());
                                            context.startActivity(tokboxIntent);
                                        } else {
                                            if (meeting.getConnectURL() != null && meeting.getConnectURL().length() > 0) {
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(meeting.getConnectURL()));
                                                context.startActivity(i);
                                            } else {
                                                showToast("Connection URL not found. Please try again later.");
                                            }
                                        }
                                    }
                                })
                                .build();
                        Log.d("TAG", "setOnClickListener - 2");
                        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

                        if (meeting.getConnect().equals("1")) {
                            positiveAction.setEnabled(true);
                        } else {
                            positiveAction.setEnabled(false);
                        }

                        Button callButton = (Button) dialog.getCustomView().findViewById(R.id.custom_call_button);
                        callButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                try {
//                                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
//                                    dialIntent.setData(Uri.parse("tel:" + meeting.getMeetingUsers().get(0).getPhoneNo()));
//                                    context.startActivity(dialIntent);

                                    if (meeting.getMeetingUsers().get(0).getPhoneNo() != null && !meeting.getMeetingUsers().get(0).getPhoneNo().isEmpty()) {
                                        GlobalMethods.dialContactPhone(context, meeting.getMeetingUsers().get(0).getPhoneNo());
                                    } else {
                                        showToast("Phone number not available");
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(context, "This feature not supported ", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });

                        Button messageButton = (Button) dialog.getCustomView().findViewById(R.id.custom_message_button);
                        messageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                try {
//                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                                    sendIntent.setData(Uri.parse("sms:" + meeting.getMeetingUsers().get(0).getPhoneNo()));
//                                    context.startActivity(sendIntent);
                                    if (meeting.getMeetingUsers().get(0).getPhoneNo() != null && !meeting.getMeetingUsers().get(0).getPhoneNo().isEmpty()) {
                                        GlobalMethods.sendSMS(context, meeting.getMeetingUsers().get(0).getPhoneNo(), "");
                                    } else {
                                        showToast("Phone number not available");
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(context, "This feature not supported ", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });


                        TextView textViewTitle = (TextView) dialog.getCustomView().findViewById(R.id.custom_title);
                        textViewTitle.setText(title);

                        TextView textViewDate = (TextView) dialog.getCustomView().findViewById(R.id.custom_date);
                        textViewDate.setText("Date: " + meeting.getMeetingDate());

                        TextView textViewTime = (TextView) dialog.getCustomView().findViewById(R.id.custom_time);
                        textViewTime.setText(message);

                        TextView textViewDes = (TextView) dialog.getCustomView().findViewById(R.id.custom_des);
                        if (meeting.getDescription() != null)
                            textViewDes.setText(meeting.getDescription());

                        ImageButton imageButton = (ImageButton) dialog.getCustomView().findViewById(R.id.custom_cancle);
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.cancel();
                            }
                        });

                        if (meeting.getMeetingUsers() != null && meeting.getMeetingUsers().size() > 0) {

                            TextView textViewName = (TextView) dialog.getCustomView().findViewById(R.id.custom_user_name);
                            textViewName.setText(meeting.getMeetingUsers().get(0).getName());

                            ImageView imageView = (ImageView) dialog.getCustomView().findViewById(R.id.custom_user_image);
                            Picasso.with(context)
                                    .load(meeting.getMeetingUsers().get(0).getProfileImageURL())
                                    .error(R.mipmap.user_profile)
                                    .into(imageView);

                        } else {

                        }
                        dialog.show();
                    } else {

                        new MaterialDialog.Builder(context)
                                .title(title)
                                .content(R.string.pay_content)
                                .positiveText(R.string.ok)
                                .show();

                    }
                }*/

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
                Intent intent = new Intent(DashboardActivity.this, CreateCourseActivity.class);
                startActivityForResult(intent, 205);
            }
        });

        _fab_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _floatingActionMenu.collapse();
                Intent intent = new Intent(DashboardActivity.this, CreateScheduleActivity.class);
                startActivityForResult(intent, 205);
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

        if (user.getUserType().equals("L")) {
            _floatingActionMenu.setVisibility(View.GONE);
        } else {
            _floatingActionMenu.setVisibility(View.VISIBLE);
        }

        if (Conditions.isNetworkConnected(DashboardActivity.this)) {
            getDashBoardList();
        } else {
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
        if (user != null) {

            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("UserType", user.getUserType());
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
                            if (response.getErrorCode() == null) {

                                if (response.getCoursesList() != null && !response.getCoursesList().isEmpty()) {
                                    courseAdapter.refresh(response.getCoursesList());
                                    courseAdapter.notifyDataSetChanged();

                                    _dashboard_text_all_course.setText(R.string.all_courses);
                                    _dashboard_text_all_course.setEnabled(true);

                                } else {
                                    _dashboard_text_all_course.setEnabled(false);
                                    _dashboard_text_all_course.setText("No courses available ");
                                }
                                if (response.getMeetings() != null && !response.getMeetings().isEmpty()) {
                                    meetingAdapter.refresh(response.getMeetings());
                                    meetingAdapter.notifyDataSetChanged();
                                    _dashboard_text_all_meetings.setEnabled(true);
                                    _dashboard_text_all_meetings.setText(R.string.all_meetings);
                                } else {
                                    _dashboard_text_all_meetings.setEnabled(false);
                                    _dashboard_text_all_meetings.setText("No meetings available ");
                                }
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

            case R.id.refreshOption:
                refreshDashboard();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (zoomSDK.isInitialized()) {
            MeetingService meetingService = zoomSDK.getMeetingService();
            meetingService.removeListener(this);
        }

        super.onDestroy();
    }

    private void refreshDashboard() {
        if (Conditions.isNetworkConnected(DashboardActivity.this)) {
            meetingAdapter.refresh(null);
            courseAdapter.refresh(null);
            getDashBoardList();
        } else {
            try {
                Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(DashboardActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerMeetingServiceListener() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        MeetingService meetingService = zoomSDK.getMeetingService();
        if (meetingService != null) {
            meetingService.addListener(this);
        }
    }

    @Override
    public void onMeetingEvent(int meetingEvent, int errorCode,
                               int internalErrorCode) {

        Log.i("TAG", "onMeetingEvent, meetingEvent=" + meetingEvent + ", errorCode=" + errorCode
                + ", internalErrorCode=" + internalErrorCode);

        if (meetingEvent == MeetingEvent.MEETING_CONNECT_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
            Toast.makeText(context, "App version is too low!", Toast.LENGTH_LONG).show();
        }

        if (mbPendingStartMeeting && meetingEvent == MeetingEvent.MEETING_DISCONNECTED) {
            mbPendingStartMeeting = false;
        }
    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.i("TAG", "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

        if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Toast.makeText(context, "Something went wrong, Please try again", Toast.LENGTH_LONG).show();
        } else {
            // Toast.makeText(context, "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();
            registerMeetingServiceListener();
        }
    }


    public void onClickBtnJoinMeeting() {

        String meetingPassword = "";

        if (meetingNo.length() == 0) {
            Toast.makeText(context, "You need a meeting number which you want to join.", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(context, "Something went wrong, Please try again", Toast.LENGTH_LONG).show();
            return;
        }

        MeetingService meetingService = zoomSDK.getMeetingService();

        JoinMeetingOptions opts = new JoinMeetingOptions();
//        opts.no_meeting_end_message = true;
//		opts.no_driving_mode = true;
//		opts.no_invite = true;
//		opts.no_meeting_end_message = true;
//		opts.no_titlebar = true;
//		opts.no_bottom_toolbar = true;
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;
//		opts.no_share = true;
//		opts.invite_options = InviteOptions.INVITE_VIA_EMAIL + InviteOptions.INVITE_VIA_SMS;
//		opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE;
//		opts.no_audio = true;
//		opts.no_video = true;
//		opts.no_meeting_error_message = true;
//		opts.participant_id = "participant id";

        DISPLAY_NAME = user != null ? user.getFirstName() : "User";
        int ret = meetingService.joinMeeting(context, meetingNo, DISPLAY_NAME, meetingPassword, opts);
        Log.i("TAG", "onClickBtnJoinMeeting, ret=" + ret);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 205 && resultCode == RESULT_OK) {
            if (Conditions.isNetworkConnected(DashboardActivity.this)) {
                getDashBoardList();
            } else {
                try {
                    Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DashboardActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}
