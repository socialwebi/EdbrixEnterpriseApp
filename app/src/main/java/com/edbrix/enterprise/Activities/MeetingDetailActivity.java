package com.edbrix.enterprise.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.Models.Meeting;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;

import org.json.JSONException;
import org.json.JSONObject;

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

public class MeetingDetailActivity extends AppCompatActivity implements ZoomSDKInitializeListener, MeetingServiceListener {

    Context context;
    private String meetingID;
    private String id;
    private String type;
    private static String DISPLAY_NAME = "User";
    private final static int STYPE = MeetingService.USER_TYPE_API_USER;

    User user;
    Meeting meeting;
    private boolean mbPendingStartMeeting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_detail);

        context = MeetingDetailActivity.this;
        assert user != null;
        user = SettingsMy.getActiveUser();

        if(savedInstanceState == null) {
            ZoomSDK sdk = ZoomSDK.getInstance();
            sdk.initialize(context, Constants.APP_KEY, Constants.APP_SECRET, Constants.WEB_DOMAIN, this);
        } else {
            registerMeetingServiceListener();
        }

        getMeetingList();

    }


    void getMeetingList() {

        User user = SettingsMy.getActiveUser();

        if (user!=null) {

            if (user.getUserType().equals("L")) {
                // meetingDetailParticipant.setText("Organizer ");
            }
            else {
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

                            /*if (response.getMeetingUsers()!=null ) {

                                Log.d("TAG"," Size:  "+response.getMeetingUsers().size());

                                meetingID = response.getMeetingId();

                                if (meeting.getConnect().equals("1")) {
                                    buttonColorChange(true);
                                } else {
                                    buttonColorChange(false);
                                }

                                list = response.getMeetingUsers();
                                adapter.refreshList(list);
                            }
                            else {

                            }*/

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(context, "Something went wrong, Please try again", Toast.LENGTH_LONG).show();
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
        if(meetingService != null) {
            meetingService.addListener(this);
        }
    }

    @Override
    public void onDestroy() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if(zoomSDK.isInitialized()) {
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

        if(meetingEvent == MeetingEvent.MEETING_CONNECT_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
            Toast.makeText(context, "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
        }

        if(mbPendingStartMeeting && meetingEvent == MeetingEvent.MEETING_DISCONNECTED) {
            mbPendingStartMeeting = false;
            onClickBtnStartMeeting();
        }
    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.i("TAG", "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

        if(errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Toast.makeText(context, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG).show();
        } else {
            // Toast.makeText(context, "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();

            registerMeetingServiceListener();
        }
    }


    public void onClickBtnJoinMeeting() {

        String meetingPassword = "";

        if(meetingID.length() == 0) {
            Toast.makeText(context, "You need to enter a meeting number which you want to join.", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if(!zoomSDK.isInitialized()) {
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


        DISPLAY_NAME = user != null ? user.getFirstName() : "User" ;
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
