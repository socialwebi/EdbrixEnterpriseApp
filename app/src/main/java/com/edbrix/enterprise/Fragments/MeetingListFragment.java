package com.edbrix.enterprise.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.edbrix.enterprise.Activities.MeetingDetailActivity;
import com.edbrix.enterprise.Adapters.MeetingListAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Interfaces.MeetingListInterface;
import com.edbrix.enterprise.Models.Meeting;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Conditions;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Utils.SessionManager;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingListFragment extends Fragment implements SearchView.OnQueryTextListener, ZoomSDKInitializeListener, MeetingServiceListener {

    private static String DISPLAY_NAME = "User";
    Context context;
    RelativeLayout layout;
    MeetingListAdapter adapter;
    SessionManager sessionManager;
    private ProgressBar _meeting_list_progress;
    private ImageView _meeting_list_No_Meetings;
    private ArrayList<Meeting> list;
    private boolean mbPendingStartMeeting = false;
    private boolean isLastPage = false;
    private boolean isLoading = true;
    private String deviceType;
    private String dataType = "meeting";
    private String pageNo = "1";
    private View positiveAction;
    private String meetingNo;
    private User user;

    public MeetingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meeting_list, container, false);

        context = getActivity();
        list = new ArrayList<>();

        assert user != null;
        user = SettingsMy.getActiveUser();

        sessionManager = new SessionManager(context);
        deviceType = sessionManager.getSessionDeviceType();

        RecyclerView _meeting_list_recycler = view.findViewById(R.id.meeting_list_recycler);
        _meeting_list_progress = view.findViewById(R.id.meeting_list_progress);
        _meeting_list_No_Meetings = view.findViewById(R.id.meeting_list_No_Meetings);

        if (savedInstanceState == null) {
            ZoomSDK sdk = ZoomSDK.getInstance();
            sdk.initialize(context, Constants.APP_KEY, Constants.APP_SECRET, Constants.WEB_DOMAIN, this);
        } else {
            registerMeetingServiceListener();
        }

        adapter = new MeetingListAdapter(context, list, new MeetingListInterface() {
            @Override
            public void onMeetingSelected(final Meeting meeting) {
                meetingNo = meeting.getId();

                assert user != null;
                if (!user.getUserType().equals("L")) {
                    Intent intent = new Intent(context, MeetingDetailActivity.class);
                    intent.putExtra("meetingId", meeting.getId());
                    intent.putExtra("meetingType", meeting.getType());
                    intent.putExtra("meetingTitle", meeting.getTitle());
                    context.startActivity(intent);
                } else {

                    String title = meeting.getTitle();
                    String message = " Time: " + meeting.getStartDateTime() + " - "
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
                                        } else {
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(meeting.getConnectURL()));
                                            context.startActivity(i);
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
                                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                                    dialIntent.setData(Uri.parse("tel:" + meeting.getMeetingUsers().get(0).getPhoneNo()));
                                    context.startActivity(dialIntent);
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
                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                    sendIntent.setData(Uri.parse("sms:" + meeting.getMeetingUsers().get(0).getPhoneNo()));
                                    context.startActivity(sendIntent);
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
                        textViewDes.setText(" " + meeting.getDescription());

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
                }
            }
        });


        if (Conditions.isNetworkConnected(context)) {
            _meeting_list_progress.setVisibility(View.VISIBLE);
            getMeetingeList();
        } else {
            try {
                Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        }

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        assert _meeting_list_recycler != null;
        _meeting_list_recycler.setHasFixedSize(true);
        _meeting_list_recycler.setLayoutManager(linearLayoutManager1);
        registerForContextMenu(_meeting_list_recycler);
        _meeting_list_recycler.setAdapter(adapter);

        return view;

    }


    private void getMeetingeList() {

        User activeUser = SettingsMy.getActiveUser();
        if (activeUser != null) {

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
                            _meeting_list_progress.setVisibility(View.INVISIBLE);
                            Timber.d("response: %s", response.toString());
                            if (response.getErrorCode() == null) {

                                if (response.getMeetings() != null) {
                                    _meeting_list_No_Meetings.setVisibility(View.GONE);
                                    adapter.refresh(response.getMeetings());
                                    adapter.notifyDataSetChanged();
                                    pageNo = response.getPage();
                                } else {
                                    _meeting_list_No_Meetings.setVisibility(View.VISIBLE);
                                    // mRecyclerView.setVisibility(View.GONE);
                                }

                                //
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
                    _meeting_list_progress.setVisibility(View.INVISIBLE);
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
            Application.getInstance().addToRequestQueue(getDashboardCourseSchedulesRequest, "meeting_list_requests");
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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

}
