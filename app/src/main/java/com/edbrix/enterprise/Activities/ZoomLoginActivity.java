package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edbrix.enterprise.R;
import com.edbrix.enterprise.baseclass.BaseActivity;

import us.zoom.sdk.MeetingService;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;

public class ZoomLoginActivity extends BaseActivity implements ZoomSDKAuthenticationListener, View.OnClickListener
{
    Context context;
    String meetingNo;
    private EditText mEdtUserName;
    private EditText mEdtPassord;
    private Button mBtnLogin;
    private View mProgressPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_login);

        context = ZoomLoginActivity.this;

        mEdtUserName = findViewById(R.id.userName);
        mEdtPassord = findViewById(R.id.password);
        TextView mZoomMeetingId = findViewById(R.id.zoomMeetingId);
        mBtnLogin = findViewById(R.id.btnLogin);
        mBtnLogin.setOnClickListener(this);
        mProgressPanel = findViewById(R.id.progressPanel);

        Intent intent = getIntent();
        meetingNo = intent.getStringExtra("meetingId");

        if (meetingNo != null)
            mZoomMeetingId.setText("Meeting ID: " + meetingNo);
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
    protected void onResume() {
        super.onResume();
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (zoomSDK.isInitialized()) {
            zoomSDK.addAuthenticationListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (zoomSDK.isInitialized()) {
            zoomSDK.removeAuthenticationListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {
            onClickBtnLogin();
        }
    }

    public void onClickBtnLogin() {
        String userName = mEdtUserName.getText().toString().trim();
        String password = mEdtPassord.getText().toString().trim();
        if (userName.length() == 0 || password.length() == 0) {
            Toast.makeText(this, "You need to enter user name and password.", Toast.LENGTH_LONG).show();
            return;
        }
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (!(zoomSDK.loginWithZoom(userName, password) == ZoomApiError.ZOOM_API_ERROR_SUCCESS)) {
            Toast.makeText(this, "Something went wrong, Please try again", Toast.LENGTH_LONG).show();
        } else {
            mBtnLogin.setVisibility(View.GONE);
            mProgressPanel.setVisibility(View.VISIBLE);
        }
    }

    public void onClickBtnLoginUserStart() {

        if (meetingNo.length() == 0) {
            Toast.makeText(ZoomLoginActivity.this, "You need a scheduled meeting id.", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(ZoomLoginActivity.this, "Something went wrong, Please try again", Toast.LENGTH_LONG).show();
            return;
        }

        MeetingService meetingService = zoomSDK.getMeetingService();

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
//		opts.no_audio = true;
//		opts.no_video = true;
//		opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE + MeetingViewsOptions.NO_BUTTON_VIDEO;
//		opts.no_meeting_error_message = true;

        int ret = meetingService.startMeeting(ZoomLoginActivity.this, meetingNo, opts);
        Log.i("TAG", "onClickBtnLoginUserStart, ret=" + ret);
    }

    @Override
    public void onZoomSDKLoginResult(long result) {
        if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
            onClickBtnLoginUserStart();
        } else {
            Toast.makeText(this, "Login failed, error code = " + result, Toast.LENGTH_SHORT).show();
        }
        mBtnLogin.setVisibility(View.VISIBLE);
        mProgressPanel.setVisibility(View.GONE);
    }

    @Override
    public void onZoomSDKLogoutResult(long result) {
        if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Logout failed, error code = " + result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
