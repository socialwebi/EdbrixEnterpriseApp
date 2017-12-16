package com.edbrix.enterprise.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.ParticipantListAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.Models.Meeting;
import com.edbrix.enterprise.Models.MeetingUsers;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Conditions;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingEvent;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MeetingDetailActivity extends BaseActivity implements ZoomSDKInitializeListener, MeetingServiceListener {

    private final static int STYPE = MeetingService.USER_TYPE_API_USER;
    private static String DISPLAY_NAME = "User";
    Context context;
    ArrayList<MeetingUsers> list;
    User user;
    Meeting meeting;
    private LinearLayout layout;
    private TextView day;
    private TextView date;
    private TextView title;
    private TextView time;
    private TextView des;
    private Button _meeting_detail_button_connect;
    private ParticipantListAdapter adapter;
    private String meetingID;
    private String id;
    private String type;
    private TextView toolBarTitle;
    private boolean mbPendingStartMeeting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_detail);

        context = MeetingDetailActivity.this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolBarTitle = toolbar.findViewById(R.id.title);

        assert user != null;
        user = SettingsMy.getActiveUser();

        list = new ArrayList<>();

        Intent intent = getIntent();
        id = intent.getStringExtra("meetingId");
        type = intent.getStringExtra("meetingType");
        String meetingTitle = intent.getStringExtra("meetingTitle");

        toolBarTitle.setText(meetingTitle);

        day = findViewById(R.id.meeting_detail_day);
        date = findViewById(R.id.meeting_detail_date);
        title = findViewById(R.id.meeting_detail_name);
        time = findViewById(R.id.meeting_detail_time);
        des = findViewById(R.id.meeting_detail_des);
        _meeting_detail_button_connect = findViewById(R.id.meeting_detail_button_connect);
        RecyclerView _meeting_detail_recycler = findViewById(R.id.meeting_detail_recycler);


        adapter = new ParticipantListAdapter(MeetingDetailActivity.this, list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        assert _meeting_detail_recycler != null;
        _meeting_detail_recycler.setHasFixedSize(true);
        _meeting_detail_recycler.setLayoutManager(linearLayoutManager);
        registerForContextMenu(_meeting_detail_recycler);
        _meeting_detail_recycler.setAdapter(adapter);

        _meeting_detail_button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (meeting.getConnect().equals("1")) {
                    if (meeting.getConnectType().equals("ZOOM")) {
                        meetingID = meeting.getMeetingId();

                        assert user != null;
                        if (user.getUserType().equals("L")) {
                            Log.d("TAG", "----JOIN----");
                            onClickBtnJoinMeeting();
                        } else {
                            if (meeting.getIsPaid().equals("1")) {
                                Log.d("TAG", "----START----");
                                onClickBtnStartMeeting();
                            } else {
                                Log.d("TAG", "----Login START----");
                                Intent intent = new Intent(MeetingDetailActivity.this, ZoomLoginActivity.class);
                                intent.putExtra("meetingId", meetingID);
                                startActivity(intent);
                            }
                        }
                    } else {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(meeting.getConnectURL()));
                        context.startActivity(i);
                    }
                }

            }
        });

        //if ()

        if (savedInstanceState == null) {
            ZoomSDK sdk = ZoomSDK.getInstance();
            sdk.initialize(context, Constants.APP_KEY, Constants.APP_SECRET, Constants.WEB_DOMAIN, this);
        } else {
            registerMeetingServiceListener();
        }

        if (Conditions.isNetworkConnected(context)) {
            showBusyProgress();
            getMeetingList();
        } else {
            try {
                Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        }

    }


    void getMeetingList() {

        User user = SettingsMy.getActiveUser();

        if (user != null) {

            if (user.getUserType().equals("L")) {
                // meetingDetailParticipant.setText("Organizer ");
            } else {
                // meetingDetailParticipant.setText("Participant list ");
            }

            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("UserType", user.getUserType());
                jo.put("MeetingId", id);
                jo.put("MeetingType", type);

            } catch (JSONException e) {
                Timber.e(e, "Parse logInWithEmail exception");
                return;
            }
            // if (BuildConfig.DEBUG)
            Timber.d("Meeting detail request: %s", jo.toString());

            GsonRequest<Meeting> userMeetingDetailRequest = new GsonRequest<>(Request.Method.POST, Constants.getMeetingDetails, jo.toString(), Meeting.class,
                    new Response.Listener<Meeting>() {
                        @Override
                        public void onResponse(@NonNull Meeting response) {

                            Timber.d("response: %s", response.toString());
                            meeting = response;

                            if (response.getMeetingUsers() != null) {

                                Log.d("TAG", " Size:  " + response.getMeetingUsers().size());

                                meetingID = response.getMeetingId();

                                day.setText(response.getMeetingDay());
                                title.setText(response.getTitle());
                                String month = response.getMeetingMonth() + ", " + response.getMeetingYear();
                                date.setText(month);
                                time.setText(response.getStartDateTime() + " - " + response.getEndDateTime());
                                des.setText(response.getDescription());
                                des.setMovementMethod(new ScrollingMovementMethod());


                                if (meeting.getConnect().equals("1")) {
                                    _meeting_detail_button_connect.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                                } else {
                                    _meeting_detail_button_connect.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
                                }

                                list = response.getMeetingUsers();
                                adapter.refreshList(list);
                                adapter.notifyDataSetChanged();
                            }

                            hideBusyProgress();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    Timber.d("Error: %s", error.getMessage());
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            userMeetingDetailRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            userMeetingDetailRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(userMeetingDetailRequest, "meeting_detail_requests");
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
    public void onDestroy() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (zoomSDK.isInitialized()) {
            MeetingService meetingService = zoomSDK.getMeetingService();
            meetingService.removeListener(this);
        }

        super.onDestroy();
    }

    @Override
    public void onMeetingEvent(int meetingEvent, int errorCode,
                               int internalErrorCode) {

        Log.i("TAG", "onMeetingEvent, meetingEvent=" + meetingEvent + ", errorCode=" + errorCode
                + ", internalErrorCode=" + internalErrorCode);

        if (meetingEvent == MeetingEvent.MEETING_CONNECT_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
            Toast.makeText(context, "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
        }

        if (mbPendingStartMeeting && meetingEvent == MeetingEvent.MEETING_DISCONNECTED) {
            mbPendingStartMeeting = false;
            onClickBtnStartMeeting();
        }
    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.i("TAG", "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

        if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Toast.makeText(context, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG).show();
        } else {
            // Toast.makeText(context, "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();

            registerMeetingServiceListener();
        }
    }


    public void onClickBtnJoinMeeting() {

        String meetingPassword = "";

        if (meetingID.length() == 0) {
            Toast.makeText(context, "You need to enter a meeting number which you want to join.", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(context, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
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
        int ret = meetingService.joinMeeting(context, meetingID, DISPLAY_NAME, meetingPassword, opts);
        Log.i("TAG", "onClickBtnJoinMeeting, ret=" + ret);

    }

    public void onClickBtnStartMeeting() {

        if (meetingID.length() == 0) {
            Toast.makeText(context, "You need to enter a scheduled meeting number.", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(context, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        final MeetingService meetingService = zoomSDK.getMeetingService();

        if (meetingService.getMeetingStatus() != MeetingStatus.MEETING_STATUS_IDLE) {
            long lMeetingNo = 0;
            try {
                lMeetingNo = Long.parseLong(meetingID);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid meeting number: " + meetingID, Toast.LENGTH_LONG).show();
                return;
            }

            if (meetingService.getCurrentMeetingID() == lMeetingNo) {
                meetingService.returnToMeeting(context);
                return;
            }

            new AlertDialog.Builder(context)
                    .setMessage("Do you want to leave current meeting and start another?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mbPendingStartMeeting = true;
                            meetingService.leaveCurrentMeeting(false);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }

        StartMeetingOptions opts = new StartMeetingOptions();
//		opts.no_driving_mode = true;
//		opts.no_invite = true;
//		opts.no_meeting_end_message = true;
//		opts.no_titlebar = true;
//		opts.no_bottom_toolbar = true;
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;
//		opts.no_share = true;
//		opts.invite_options = InviteOptions.INVITE_ENABLE_ALL;
//		opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE + MeetingViewsOptions.NO_BUTTON_VIDEO;
//		opts.no_audio = true;
//		opts.no_video = true;
//		opts.no_meeting_error_message = true;

        assert user != null;
        int ret = meetingService.startMeeting(context, user.getZoomUserId(), user.getZoomUserToken(), STYPE, meetingID, user.getFirstName(), opts);
        /*int ret = meetingService.startMeeting(context, "xch6jAJ-Tiqcf7ct-LDxEw",
                        "eRr1c1RQuIlqAIyqiactTFf1_oghkN8-cgTXTyy2rq0.BgMYaUE4UzJtK2VUREZsVGJ1WXdPMzQrZz09AAAMM0NCQXVvaVlTM3M9",
                        STYPE, "469520738", "USER NAME", opts);*/

        Log.i("TAG", "onClickBtnStartMeeting, ret=" + ret);
    }

}
