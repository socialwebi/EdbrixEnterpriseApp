package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.Models.FileData;
import com.edbrix.enterprise.Models.Meeting;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Utils.VideoPlayerWithListDialog;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.app.Config;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.commons.AlertDialogManager;
import com.edbrix.enterprise.commons.DialogManager;
import com.edbrix.enterprise.commons.GlobalMethods;
import com.edbrix.enterprise.commons.ToastMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class TokBoxActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks,
        Publisher.PublisherListener,
        Session.SessionListener {
    private static final String TAG = "TokBoxActivity";//"simple-multiparty " + MainActivity.class.getSimpleName();

    private final int MAX_NUM_SUBSCRIBERS = 15; //Max Number Of TokBoxSubscribers

    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private Session mSession;
    private Publisher mPublisher;
    public DialogManager dialogManager;
    public ToastMessage toastMessage;

    private ArrayList<Subscriber> mSubscribers = new ArrayList<Subscriber>();
    private HashMap<Stream, Subscriber> mSubscriberStreams = new HashMap<Stream, Subscriber>();

    private RelativeLayout mPublisherViewContainer, mfullViewContainer;
    public ImageView swapCamImageView, toggleAudioImageView, toggleVideoImageView, publisherFullViewImageView, zoomImageView;
    public ImageView fullViewSwapCamImageView, fullViewToggleAudioImageView, fullViewToggleVideoImageView;
    public ImageView waitingImageView;

    private LinearLayout mSubscriberlistLinearLayout, mPublisherControlsLinearLayout, mRightSideLinearLayout;
    public LinearLayout mFullViewControlsLinearLayout;
    public LinearLayout loaderLayout;
    public FrameLayout mpublisherScreenFrame;
    public Toolbar toolbar;
    public TextView publisherNameTextView, userNameTextview, leaveMeetingTextView, txtVideoList, subscriberwaitTextView;

    boolean swapSubscriberToFullView = false;
    boolean swapPublisherToFullView = false;
    boolean togglePublisherFullScreen = false;
    boolean isMeetingStarted = false;
    public static int swapPos;
    Meeting meeting;

    //public String Role="Host";

    // ScreenRecording
    private static final int REQUEST_CODE = 1000;
    public static final int RESULT_CODE = 1234;
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private int DISPLAY_WIDTH = 1920;
    private int DISPLAY_HEIGHT = 1080;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;
    private MediaRecorder mMediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_PERMISSIONS = 10;
    private int countDownTime;
    private int countDownInterval;
    private TextView countDownTimerText;
    private TextView infoTimerText;
    private TextView currentTimeText;
    private TextView txtMeetingName;
    private TextView txtLoading;
    private TextView progressText;
    private ImageView stopBtn;
    private ImageView startBtn;
    private MyCountDownTimer myCountDownTimer;
    private SessionEndCountDownTimer sessionEndCountDownTimer;
    private boolean isScreenRecordingRunning = false;
    private File screenRecordOutputFile;
    private Handler handler;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private String meetingId;
    private String meetingName;
    private String meetingType;
    private int recordTime;
    private static final int slideMaxTimeDuration = 10; // 600 sec i.e. 10 min

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tok_box);

        dialogManager = DialogManager.getInstance(TokBoxActivity.this);
        toastMessage = ToastMessage.getInstance(TokBoxActivity.this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mPublisherViewContainer = (RelativeLayout) findViewById(R.id.publisherviewLayout);
        mfullViewContainer = (RelativeLayout) findViewById(R.id.fullViewLayout);
        mpublisherScreenFrame = (FrameLayout) findViewById(R.id.publisherScreenFrame);
        mSubscriberlistLinearLayout = (LinearLayout) findViewById(R.id.subscriberListLinear);
        mPublisherControlsLinearLayout = (LinearLayout) findViewById(R.id.publisherControlLayout);
        mRightSideLinearLayout = (LinearLayout) findViewById(R.id.rightLinearLayout);
        mFullViewControlsLinearLayout = (LinearLayout) findViewById(R.id.fullviewControlLayout);
        loaderLayout = (LinearLayout) findViewById(R.id.loaderLayout);

        swapCamImageView = (ImageView) findViewById(R.id.swapCamera);
        toggleAudioImageView = (ImageView) findViewById(R.id.toggleAudio);
        toggleVideoImageView = (ImageView) findViewById(R.id.toggleVideo);
        publisherFullViewImageView = (ImageView) findViewById(R.id.fullView);
        zoomImageView = (ImageView) findViewById(R.id.zoomView);
        waitingImageView = (ImageView) findViewById(R.id.waitImageView);

        fullViewSwapCamImageView = (ImageView) findViewById(R.id.fullviewSwapCamera);
        fullViewToggleAudioImageView = (ImageView) findViewById(R.id.fullviewToggleAudio);
        fullViewToggleVideoImageView = (ImageView) findViewById(R.id.fullviewToggleVideo);

        publisherNameTextView = (TextView) findViewById(R.id.textViewPubliishername);
        userNameTextview = (TextView) findViewById(R.id.textViewUsername);
        txtLoading = (TextView) findViewById(R.id.txtLoading);
        leaveMeetingTextView = (TextView) findViewById(R.id.textViewLeaveMeeting);
        txtVideoList = (TextView) findViewById(R.id.txtVideoList);
        subscriberwaitTextView = (TextView) findViewById(R.id.textViewSubscriberWait);
        txtMeetingName = (TextView) findViewById(R.id.txtMeetingName);

        startBtn = (ImageView) findViewById(R.id.startBtn);
        stopBtn = (ImageView) findViewById(R.id.stopBtn);
        countDownTimerText = (TextView) findViewById(R.id.countDownTimerText);
        infoTimerText = (TextView) findViewById(R.id.infoTimerText);
        currentTimeText = (TextView) findViewById(R.id.currentTimeText);
        progressText = (TextView) findViewById(R.id.progressText);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://edbrixcbuilder.appspot.com");
        meetingId = getIntent().getStringExtra(Constants.TolkBox_MeetingId);
        meetingName = getIntent().getStringExtra(Constants.TolkBox_MeetingName);
        meetingType = getIntent().getStringExtra(Constants.TolkBox_MeetingType);

        txtMeetingName.setText(meetingName);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);/////Handle ScreenOut Time

        swapCamImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchPublisherCam();
            }
        });
        fullViewSwapCamImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swapPublisherToFullView) {
                    switchPublisherCam();
                } else {

                }
            }
        });


        // mPublisher.setPublishAudio(true);
        toggleAudioImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //togglePublisherAudio();
                if (mPublisher == null) {
                    return;
                }
                if (mPublisher.getPublishAudio() == true) {
                    mPublisher.setPublishAudio(false);
                    toggleAudioImageView.setImageResource(R.drawable.micoff);
                } else {
                    mPublisher.setPublishAudio(true);
                    toggleAudioImageView.setImageResource(R.drawable.micon);
                }
            }
        });
        fullViewToggleAudioImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swapPublisherToFullView) {
                    togglePublisherAudio();
                } else {
                    togglefullviewsubscriberAudio(mSubscribers.get(swapPos));
                }
            }
        });

        // mPublisher.setPublishVideo(true);
        toggleVideoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //togglePublisherVideo();
                if (mPublisher == null) {
                    return;
                }
                if (mPublisher.getPublishVideo() == true) {
                    mPublisher.setPublishVideo(false);
                    toggleVideoImageView.setImageResource(R.drawable.videooff);
                } else {
                    mPublisher.setPublishVideo(true);
                    toggleVideoImageView.setImageResource(R.drawable.videoon);
                }
            }
        });
        fullViewToggleVideoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swapPublisherToFullView) {
                    togglePublisherVideo();
                } else {
                    togglefullviewsubscriberVideo(mSubscribers.get(swapPos));
                }
            }
        });
        requestPermissions();

        zoomImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!togglePublisherFullScreen) {
                    mRightSideLinearLayout.setVisibility(View.GONE);
                    zoomImageView.setImageResource(R.drawable.minimise);
                    toolbar.setVisibility(View.GONE);
                    mFullViewControlsLinearLayout.setVisibility(View.GONE);
                    togglePublisherFullScreen = true;
                } else if (togglePublisherFullScreen) {
                    mRightSideLinearLayout.setVisibility(View.VISIBLE);
                    zoomImageView.setImageResource(R.drawable.maximize);
                    toolbar.setVisibility(View.VISIBLE);
                    mFullViewControlsLinearLayout.setVisibility(View.VISIBLE);
                    togglePublisherFullScreen = false;
                }
            }
        });

        // screen recording
        handler = new Handler();

        countDownTime = 5; // count down time in sec
        countDownInterval = 1000; //count down interval in sec

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mScreenDensity = metrics.densityDpi;

        mMediaRecorder = new MediaRecorder();

        mProjectionManager = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);

//        countDownTimerText.setVisibility(View.VISIBLE);
        countDownTimerText.setText("" + countDownTime);
        myCountDownTimer = new MyCountDownTimer(countDownTime * 1000, countDownInterval);
        sessionEndCountDownTimer = new SessionEndCountDownTimer(countDownTime * 1000, countDownInterval);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);

                if (!togglePublisherFullScreen)
                    zoomImageView.callOnClick();

                zoomImageView.setVisibility(View.GONE);
                infoTimerText.setText("Starting to record meeting...");
                infoTimerText.setVisibility(View.VISIBLE);
                countDownTimerText.setVisibility(View.VISIBLE);
                myCountDownTimer.start();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                startBtn.setVisibility(View.VISIBLE);
                zoomImageView.setVisibility(View.VISIBLE);
                mFullViewControlsLinearLayout.setVisibility(View.VISIBLE);

                if (togglePublisherFullScreen)
                    zoomImageView.callOnClick();

                onToggleScreenShare(false);
                recordTime = 0;
                currentTimeText.setVisibility(View.GONE);
            }
        });

        leaveMeetingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveMeetingDialog();
            }
        });

        txtVideoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoList();
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        User activeUser = SettingsMy.getActiveUser();
        String uId = activeUser.getId();
        String acsToken = activeUser.getAccessToken();
        String uType = activeUser.getUserType();
        isMeetingStarted = true;
        if (uType.equals("I")) {
            //update meeting status by host as meeting is started
            updateTokboxMeetingStatus(uId, acsToken, uType, meetingId, meetingType, "1");
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        super.onResume();

        if (mSession == null) {
            return;
        }
        mSession.onResume();
    }

    private BroadcastReceiver mNotificationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                // new push notification is received

                final String fileName = intent.getStringExtra("filename");
                final String ownerName = intent.getStringExtra("owner");
                final String ownerId = intent.getStringExtra("ownerId");
                final String meetingIdNoti = intent.getStringExtra("meetingId");
                if (!ownerId.isEmpty()) {
                    if (!SettingsMy.getActiveUser().getId().equals(ownerId) && meetingId.equals(meetingIdNoti)) {
//                showToast("Push notification: " + fileName);
                        dialogManager.getAlertDialogManager().setAlertDialogCancellable(false);
                        dialogManager.getAlertDialogManager().Dialog("Download Video", ownerName + " shared video.\nContinue to download video?", "Continue", "Exit", new AlertDialogManager.onTwoButtonClickListner() {
                            @Override
                            public void onNegativeClick() {

                            }

                            @Override
                            public void onPositiveClick() {
                                downloadVideoFromNotification(fileName);
                            }
                        }).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onPause");
        super.onStop();

        if (mSession == null) {
            return;
        }
        mSession.onPause();

        if (isFinishing()) {
            disconnectSession();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        disconnectSession();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            String API_KEY = Constants.TolkBox_APIKey;
            String SESSION_ID = getIntent().getStringExtra(Constants.TolkBox_SessionId);
            String TOKEN = getIntent().getStringExtra(Constants.TolkBox_Token);
            Log.i(TAG, "TokBoxActivity :\nApi : " + API_KEY + "\nSession : " + SESSION_ID + "\n Token :" + TOKEN);

            mSession = new Session.Builder(TokBoxActivity.this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onConnected(Session session) {
        Log.d(TAG, "onConnected: Connected to session " + session.getSessionId());
        mPublisher = new Publisher.Builder(TokBoxActivity.this).name(SettingsMy.getActiveUser().getFirstName() + " " + SettingsMy.getActiveUser().getLastName()).build();
        mPublisher.setPublisherListener(this);
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

        ///New
        if (!SettingsMy.getActiveUser().getUserType().equals("L")) {
            startBtn.setVisibility(View.VISIBLE);
            fullViewSwapCamImageView.setVisibility(View.VISIBLE);
            fullViewToggleVideoImageView.setVisibility(View.VISIBLE);
            fullViewToggleAudioImageView.setVisibility(View.VISIBLE);

            waitingImageView.setImageResource(R.drawable.wait);
            txtLoading.setText(getString(R.string.please_wait));

            mpublisherScreenFrame.setVisibility(View.GONE);
            mfullViewContainer.addView(mPublisher.getView());
            userNameTextview.setText("" + mPublisher.getName());
            userNameTextview.setVisibility(View.VISIBLE);
            swapPublisherToFullView = true;

            if (Constants.meetingUserCount == 1) {
                subscriberwaitTextView.setVisibility(View.VISIBLE);
                //subscriberwaitTextView.setText("Waiting for "+(Constants.meetingUserCount - mSubscribers.size())+" participants..");
                subscriberwaitTextView.setText("Waiting for participant..");
            } else {
                subscriberwaitTextView.setVisibility(View.VISIBLE);
                //subscriberwaitTextView.setText("Waiting for "+(Constants.meetingUserCount - mSubscribers.size())+" participants..");
                subscriberwaitTextView.setText("Waiting for other participants..");
            }
        } else {
            fullViewToggleAudioImageView.setVisibility(View.VISIBLE);
            publisherFullViewImageView.setVisibility(View.GONE);
            waitingImageView.setImageResource(R.drawable.wait_host);
            txtLoading.setText(getString(R.string.plz_wait_host_start_meeting));
            mPublisherViewContainer.addView(mPublisher.getView());

            userNameTextview.setVisibility(View.GONE);
            mFullViewControlsLinearLayout.setVisibility(View.GONE);

            if (Constants.meetingUserCount == 1) {
                subscriberwaitTextView.setVisibility(View.VISIBLE);
                //subscriberwaitTextView.setText("Waiting for "+(Constants.meetingUserCount - mSubscribers.size())+" participants..");
                subscriberwaitTextView.setText("Waiting for participant..");
            } else {
                subscriberwaitTextView.setVisibility(View.VISIBLE);
                //subscriberwaitTextView.setText("Waiting for "+(Constants.meetingUserCount - mSubscribers.size())+" participants..");
                subscriberwaitTextView.setText("Waiting for other participants..");
            }
        }
        mSession.publish(mPublisher);
        publisherNameTextView.setText("" + mPublisher.getName().toString());
    }

    @Override
    public void onDisconnected(Session session) {
        Log.d(TAG, "onDisconnected: disconnected from session " + session.getSessionId());
        mSession = null;
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.d(TAG, "onError: Error (" + opentokError.getMessage() + ") in session " + session.getSessionId());

        // Toast.makeText(this, "Session error. See the logcat please.", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.d(TAG, "onStreamCreated: Own stream " + stream.getStreamId() + " created");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.d(TAG, "onStreamDestroyed: Own stream " + stream.getStreamId() + " destroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.d(TAG, "onError: Error (" + opentokError.getMessage() + ") in publisher");
        //Toast.makeText(this, "Session error. See the logcat please.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void disconnectSession() {
        if (mSession == null) {
            return;
        }

        if (mSubscribers.size() > 0) {
            for (Subscriber subscriber : mSubscribers) {
                if (subscriber != null) {
                    mSession.unsubscribe(subscriber);
                    subscriber.destroy();
                }
            }
        }

        if (mPublisher != null) {
            mPublisherViewContainer.removeView(mPublisher.getView());
            mSession.unpublish(mPublisher);
            mPublisher.destroy();
            mPublisher = null;
        }
        mSession.disconnect();
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.d(TAG, "onStreamReceived: New stream " + stream.getStreamId() + " in session " + session.getSessionId());
        Log.e(TAG, "Stream Data : " + stream.getConnection().getData() + "\nPos : " + (mSubscribers.size()));
        if (mSubscribers.size() + 1 > MAX_NUM_SUBSCRIBERS) {
            Toast.makeText(this, "New subscriber ignored. MAX_NUM_SUBSCRIBERS limit reached : " + MAX_NUM_SUBSCRIBERS, Toast.LENGTH_LONG).show();
            return;
        }
        if (!SettingsMy.getActiveUser().getUserType().equals("L")) {
            hostView(session, stream);
        } else {
            userView(session, stream);
        }
        if (mSubscribers.size() == Constants.meetingUserCount) {
            subscriberwaitTextView.setVisibility(View.GONE);
        } else {
            if (Constants.meetingUserCount == 1) {
                subscriberwaitTextView.setVisibility(View.VISIBLE);
                //subscriberwaitTextView.setText("Waiting for "+(Constants.meetingUserCount - mSubscribers.size())+" participants..");
                subscriberwaitTextView.setText("Waiting for participant..");
            } else {
                subscriberwaitTextView.setVisibility(View.VISIBLE);
                //subscriberwaitTextView.setText("Waiting for "+(Constants.meetingUserCount - mSubscribers.size())+" participants..");
                subscriberwaitTextView.setText("Waiting for other participants..");
            }
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.d(TAG, "onStreamDropped: Stream " + stream.getStreamId() + " dropped from session " + session.getSessionId());
        Log.e(TAG, "onStreamDropped: Stream Data : " + stream.getConnection().getData() + "\nSubscribers Size : " + (mSubscribers.size()));

        Subscriber subscriber = mSubscriberStreams.get(stream);
        if (subscriber == null) {
            return;
        }

        try {
            if (stream.getConnection().getData().equals("Host")) {
                mfullViewContainer.removeView(subscriber.getView());
                txtLoading.setText(getString(R.string.plz_wait_host_unavailable));
                User activeUser = SettingsMy.getActiveUser();
                String uId = activeUser.getId();
                String acsToken = activeUser.getAccessToken();
                String uType = activeUser.getUserType();
                getTokboxMeetingStatus(uId, acsToken, uType, meetingId, meetingType);
                return;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error :" + ex.getMessage().toString());
        }
        try {
            if (swapSubscriberToFullView) {
                mfullViewContainer.removeView(mSubscribers.get(swapPos).getView());
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error :" + ex.getMessage().toString());
        }

        int position = mSubscribers.indexOf(subscriber);
        mSubscribers.remove(subscriber);
        mSubscriberStreams.remove(stream);
        mSubscriberlistLinearLayout.removeViewAt(position);
        if (mSubscribers.size() == Constants.meetingUserCount) {
            subscriberwaitTextView.setVisibility(View.GONE);
        } else {
            if (Constants.meetingUserCount == 1) {
                subscriberwaitTextView.setVisibility(View.VISIBLE);
                //subscriberwaitTextView.setText("Waiting for "+(Constants.meetingUserCount - mSubscribers.size())+" participants..");
                subscriberwaitTextView.setText("Waiting for participant..");
            } else {
                subscriberwaitTextView.setVisibility(View.VISIBLE);
                //subscriberwaitTextView.setText("Waiting for "+(Constants.meetingUserCount - mSubscribers.size())+" participants..");
                subscriberwaitTextView.setText("Waiting for other participants..");
            }
        }
    }

    public void hostView(Session session, Stream stream) {
        final Subscriber subscriber = new Subscriber.Builder(TokBoxActivity.this, stream).build();
        mSession.subscribe(subscriber);
        mSubscribers.add(subscriber);
        mSubscriberStreams.put(stream, subscriber);

        final int position = mSubscribers.size() - 1;

        final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.row_host, null);

        final FrameLayout subscriberFrame = (FrameLayout) addView.findViewById(R.id.subscriberFrame);
        //final LinearLayout subscriberControls = (LinearLayout)addView.findViewById(R.id.subscriberControlsLinear);
        final RelativeLayout subscriberViewContainer = (RelativeLayout) addView.findViewById(R.id.subscriberview);
        final TextView subscriberName = (TextView) addView.findViewById(R.id.textViewSubscribername);
        final ImageView switchView = (ImageView) addView.findViewById(R.id.toggleSubscriberView);
        final ImageView switchAudio = (ImageView) addView.findViewById(R.id.toggleAudioSubscriber);

        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        subscriber.setSubscribeToAudio(true);

        subscriberViewContainer.addView(subscriber.getView());
        subscriberViewContainer.setTag(position);
        subscriberName.setText("" + subscriber.getStream().getName().toString());
        switchAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subscriber.getSubscribeToAudio() == true) {
                    subscriber.setSubscribeToAudio(false);
                    switchAudio.setImageResource(R.drawable.soundoff);
                } else {
                    subscriber.setSubscribeToAudio(true);
                    switchAudio.setImageResource(R.drawable.soundon);
                }
            }
        });

        switchView.setTag(position);
        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemposition = (Integer) v.getTag();
                Log.e("swap", "SwapPos : " + swapPos);

                if (!swapSubscriberToFullView) {
                    Log.e("swap", "SubscriberToFullView = " + swapSubscriberToFullView + " SwapPos : " + swapPos + "ItemPos : " + itemposition);
                    if (swapPublisherToFullView) {
                        Log.e("swap", "swapPublisherToFullView = " + swapPublisherToFullView + " SwapPos : " + swapPos + "ItemPos : " + itemposition);
                        mfullViewContainer.removeView(mPublisher.getView());
                        mpublisherScreenFrame.setVisibility(View.VISIBLE);
                        publisherFullViewImageView.setVisibility(View.VISIBLE);
                        mPublisherViewContainer.addView(mPublisher.getView());
                        publisherNameTextView.setText("" + mPublisher.getName().toString());
                        swapPublisherToFullView = false;

                        subscriberViewContainer.removeView(subscriber.getView());
                        addView.setVisibility(View.GONE);
                        mfullViewContainer.addView(subscriber.getView());
                        userNameTextview.setText("" + subscriber.getStream().getName().toString());
                        swapPos = itemposition;
                        swapSubscriberToFullView = true;

                        startBtn.setVisibility(View.VISIBLE);
                        fullViewSwapCamImageView.setVisibility(View.GONE);
                        fullViewToggleVideoImageView.setVisibility(View.GONE);
                        fullViewToggleAudioImageView.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        subscriberViewContainer.removeView(subscriber.getView());
                        addView.setVisibility(View.GONE);
                        mfullViewContainer.addView(subscriber.getView());
                        userNameTextview.setText("" + subscriber.getStream().getName().toString());
                        swapPos = itemposition;
                        swapSubscriberToFullView = true;

                        startBtn.setVisibility(View.VISIBLE);
                        fullViewSwapCamImageView.setVisibility(View.GONE);
                        fullViewToggleVideoImageView.setVisibility(View.GONE);
                        fullViewToggleAudioImageView.setVisibility(View.VISIBLE);
                        return;
                    }
                } else if (swapSubscriberToFullView) {
                    Log.e("swap", "SubscriberToFullView = " + swapSubscriberToFullView + " SwapPos : " + swapPos + "ItemPos : " + itemposition);
                    if (swapPos == itemposition) {
                        mfullViewContainer.removeView(subscriber.getView());
                        addView.setVisibility(View.VISIBLE);
                        subscriberViewContainer.addView(subscriber.getView());
                        userNameTextview.setText("Name");
                        swapSubscriberToFullView = false;
                        return;
                    } else if (swapPos != itemposition) {
                        mfullViewContainer.removeView(mSubscribers.get(swapPos).getView());

                        final Subscriber tmpsubscriber = mSubscribers.get(swapPos);
                        tmpsubscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

                        if (tmpsubscriber.getSubscribeToAudio() == true) {
                            tmpsubscriber.setSubscribeToAudio(false);
                            switchAudio.setImageResource(R.drawable.soundoff);
                        } else {
                            tmpsubscriber.setSubscribeToAudio(true);
                            switchAudio.setImageResource(R.drawable.soundon);
                        }
                        //tmpsubscriber.setSubscribeToAudio(true);
                        View tmpView = mSubscriberlistLinearLayout.getChildAt(swapPos);
                        RelativeLayout tmpSubView = (RelativeLayout) tmpView.findViewById(R.id.subscriberview);
                        TextView subscriberNameTextView = (TextView) tmpView.findViewById(R.id.textViewSubscribername);
                        subscriberNameTextView.setText("" + tmpsubscriber.getStream().getName().toString());
                        tmpSubView.addView(tmpsubscriber.getView());
                        tmpView.setVisibility(View.VISIBLE);

                        swapPos = itemposition;
                        subscriberViewContainer.removeView(subscriber.getView());
                        addView.setVisibility(View.GONE);
                        mfullViewContainer.addView(subscriber.getView());
                        userNameTextview.setText("" + subscriber.getStream().getName().toString());

                        swapSubscriberToFullView = true;

                        startBtn.setVisibility(View.VISIBLE);
                        fullViewSwapCamImageView.setVisibility(View.GONE);
                        fullViewToggleVideoImageView.setVisibility(View.GONE);
                        fullViewToggleAudioImageView.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }
        });

        addView.setTag(position);
        mSubscriberlistLinearLayout.addView(addView);
        LayoutTransition transition = new LayoutTransition();
        mSubscriberlistLinearLayout.setLayoutTransition(transition);

        publisherFullViewImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swapSubscriberToFullView) {
                    mPublisherViewContainer.removeView(mPublisher.getView());
                    mfullViewContainer.removeView(mSubscribers.get(swapPos).getView());
                    mpublisherScreenFrame.setVisibility(View.GONE);
                    publisherFullViewImageView.setVisibility(View.GONE);
                    mfullViewContainer.addView(mPublisher.getView());
                    userNameTextview.setText("" + mPublisher.getName().toString());
                    swapPublisherToFullView = true;

                    startBtn.setVisibility(View.VISIBLE);
                    fullViewSwapCamImageView.setVisibility(View.VISIBLE);
                    fullViewToggleVideoImageView.setVisibility(View.VISIBLE);
                    fullViewToggleAudioImageView.setVisibility(View.VISIBLE);

                    final Subscriber tmpsubscriber = mSubscribers.get(swapPos);
                    tmpsubscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                    tmpsubscriber.setSubscribeToAudio(true);
                    View tmpView = mSubscriberlistLinearLayout.getChildAt(swapPos);
                    RelativeLayout tmpSubView = (RelativeLayout) tmpView.findViewById(R.id.subscriberview);
                    TextView subscriberNameTextView = (TextView) tmpView.findViewById(R.id.textViewSubscribername);
                    subscriberNameTextView.setText("" + tmpsubscriber.getStream().getName().toString());
                    tmpSubView.addView(tmpsubscriber.getView());
                    tmpView.setVisibility(View.VISIBLE);

                    swapSubscriberToFullView = false;
                    return;
                } else {
                    mPublisherViewContainer.removeView(mPublisher.getView());
                    mpublisherScreenFrame.setVisibility(View.GONE);
                    publisherFullViewImageView.setVisibility(View.GONE);
                    mfullViewContainer.addView(mPublisher.getView());
                    userNameTextview.setText("" + mPublisher.getName().toString());
                    swapPublisherToFullView = true;

                    startBtn.setVisibility(View.VISIBLE);
                    fullViewSwapCamImageView.setVisibility(View.VISIBLE);
                    fullViewToggleVideoImageView.setVisibility(View.VISIBLE);
                    fullViewToggleAudioImageView.setVisibility(View.VISIBLE);
                    return;
                }
            }
        });
    }

    public void userView(Session session, Stream stream) {

        try {
            if (stream.getConnection().getData().equals("Host")) {
                userNameTextview.setVisibility(View.VISIBLE);
                mFullViewControlsLinearLayout.setVisibility(View.VISIBLE);

                Subscriber mSubscriber = new Subscriber.Builder(TokBoxActivity.this, stream).build();
                mSubscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                mSession.subscribe(mSubscriber);
                mSubscribers.add(mSubscriber);
                mSubscriberStreams.put(stream, mSubscriber);
                mfullViewContainer.addView(mSubscriber.getView());
                userNameTextview.setText("" + mSubscriber.getStream().getName().toString());
                return;
            }
        } catch (Exception ex) {
        }

        //Origin
        final Subscriber subscriber = new Subscriber.Builder(TokBoxActivity.this, stream).build();
        mSession.subscribe(subscriber);
        mSubscribers.add(subscriber);
        mSubscriberStreams.put(stream, subscriber);

        final int position = mSubscribers.size() - 1;

        final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.row_subscribers, null);

        final FrameLayout subscriberFrame = (FrameLayout) addView.findViewById(R.id.subscriberFrame);
        // final LinearLayout subscriberControls = (LinearLayout)addView.findViewById(R.id.subscriberControlsLinear);
        final RelativeLayout subscriberViewContainer = (RelativeLayout) addView.findViewById(R.id.subscriberview);
        final TextView subscriberName = (TextView) addView.findViewById(R.id.textViewSubscribername);
        final ImageView switchView = (ImageView) addView.findViewById(R.id.toggleSubscriberView);
        final ImageView switchAudio = (ImageView) addView.findViewById(R.id.toggleAudioSubscriber);

        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        subscriber.setSubscribeToAudio(true);

        subscriberViewContainer.addView(subscriber.getView());
        subscriberName.setText("" + subscriber.getStream().getName().toString());
        switchAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subscriber.getSubscribeToAudio() == true) {
                    subscriber.setSubscribeToAudio(false);
                    switchAudio.setImageResource(R.drawable.soundoff);
                } else {
                    subscriber.setSubscribeToAudio(true);
                    switchAudio.setImageResource(R.drawable.soundon);
                }
            }
        });

        mSubscriberlistLinearLayout.addView(addView);
        LayoutTransition transition = new LayoutTransition();
        mSubscriberlistLinearLayout.setLayoutTransition(transition);
    }

//ScreenRecording added on 1st feb///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode != RESULT_OK) {
                    toastMessage.showToast("Recording declined");
                    stopBtn.setVisibility(View.GONE);
                    startBtn.setVisibility(View.VISIBLE);

                    togglePublisherFullScreen = true;
                    zoomImageView.callOnClick();
                    return;
                }
                try {
                    mMediaProjectionCallback = new MediaProjectionCallback();
                    mMediaProjection = mProjectionManager.getMediaProjection(resultCode, intent);
                    mMediaProjection.registerCallback(mMediaProjectionCallback, null);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide navigation bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    mVirtualDisplay = createVirtualDisplay();
                    mMediaRecorder.start();
                    handler.post(UpdateRecordTime);
                    stopBtn.setVisibility(View.VISIBLE);
                } catch (RuntimeException stopRuntimeException) {
                    mMediaRecorder.reset();
                    stopScreenSharing();
                    setResult(REQUEST_CODE);
                    toastMessage.showToast("Something went wrong. Please try again later..!");
                    finish();
                }

                break;
        }
    }

    /**
     * Toggle for Recording start or stop
     *
     * @param isShare
     */
    public void onToggleScreenShare(boolean isShare) {
        isScreenRecordingRunning = isShare;
        try {
            if (isShare) {
                initRecorder();
                shareScreen();
            } else {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                Log.v(TAG, "Stopping Recording");
                stopScreenSharing();
//                showToast("Recording is done. Showing recorded video preview.", Toast.LENGTH_SHORT);
                final MaterialDialog dialog = new MaterialDialog.Builder(TokBoxActivity.this)
                        .customView(R.layout.share_preview_dialog, true)
                        .cancelable(false)
                        .build();

                TextView dialogTitle = (TextView) dialog.getCustomView().findViewById(R.id.dialogTitle);
                TextView dialogMsg = (TextView) dialog.getCustomView().findViewById(R.id.dialogMsg);
                dialogTitle.setText("Video");
                dialogMsg.setText("Recording done successfully.");
                Button btnPreview = (Button) dialog.getCustomView().findViewById(R.id.btnPreview);
                btnPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        VideoPlayerWithListDialog videoPlayerDialog = new VideoPlayerWithListDialog(TokBoxActivity.this, R.style.DialogAnimation, new FileData(screenRecordOutputFile));
                        videoPlayerDialog.setOnActionButtonListener(new VideoPlayerWithListDialog.OnActionButtonListener() {
                            @Override
                            public void onShareFile(FileData fileData) {

                                dialogManager.getAlertDialogManager().setAlertDialogCancellable(false);
                                dialogManager.getAlertDialogManager().Dialog("Video Recording", "Continue to share video recording?", "Continue", "Cancel", new AlertDialogManager.onTwoButtonClickListner() {
                                    @Override
                                    public void onNegativeClick() {

                                    }

                                    @Override
                                    public void onPositiveClick() {
                                        uploadToEdbrixMyFiles();

                                    }
                                }).show();
                            }
                        });
                        videoPlayerDialog.showMe();
                    }
                });

                Button btnShare = (Button) dialog.getCustomView().findViewById(R.id.btnShare);
                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        uploadToEdbrixMyFiles();
                    }
                });

                Button btnCancel = (Button) dialog.getCustomView().findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();


            }
        } catch (RuntimeException stopRuntimeException) {
            mMediaRecorder.reset();
            stopScreenSharing();
//            toastMessage.showToast("Meeting recording is not done successfully.");
        }
    }

    /**
     * create virtual display for screen recording
     */
    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("MainActivity",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    /**
     * initialize media recorder object and other stuffs
     */
    private void initRecorder() {
        try {
            long mills = System.currentTimeMillis();
            screenRecordOutputFile = new File(GlobalMethods.getAppVideoStorageDirectory(TokBoxActivity.this) + "/VD_" + mills + ".mp4");
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setOutputFile(screenRecordOutputFile.getPath());
//            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, 888);
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setVideoFrameRate(30);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * share screen by creating media projection object
     */
    private void shareScreen() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide navigation bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
        // Post the record progress
        handler.post(UpdateRecordTime);
        stopBtn.setVisibility(View.VISIBLE);
    }

    /**
     * MediaProjectionCallback class for callback listener
     */
    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaProjection = null;
            stopScreenSharing();
        }
    }

    /**
     * stop screen sharing by releasing virtual display and destroying media projection object
     */
    private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        //mMediaRecorder.release(); //If used: mMediaRecorder object cannot
        // be reused again
        destroyMediaProjection();
    }

    /**
     * destroy mediaProjection object
     */
    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.v(TAG, "MediaProjection Stopped");
    }

    /**
     * Count down timer to indicate that video recording going to be start
     */
    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished / 1000);
            countDownTimerText.setText("" + (progress - 1));

        }

        @Override
        public void onFinish() {
            countDownTimerText.setText("0");
            countDownTimerText.setVisibility(View.GONE);
            infoTimerText.setVisibility(View.GONE);
            stopBtn.setVisibility(View.VISIBLE);
            onToggleScreenShare(true);
        }

    }

    /**
     * upload file to Edbrix cloud storage
     */
    private void uploadToEdbrixMyFiles() {
        try {
            dialogManager.showBusyProgress();
            final FileData fileData = new FileData(screenRecordOutputFile);
            final String userId;
            final String accessToken;
            final String userType;
            final String userFullName;
            Uri fileUri = Uri.fromFile(fileData.getFileObject());
            if (fileUri != null) {
//                btnUpload.setVisibility(View.GONE);
//                btnCancel.setVisibility(View.VISIBLE);
//
//                mProgressBar.setVisibility(View.VISIBLE);
//                textPercentage.setVisibility(View.VISIBLE);
//                textPercentage.setText("");
                User activeUser = SettingsMy.getActiveUser();
                userId = activeUser.getId();
                accessToken = activeUser.getAccessToken();
                userType = activeUser.getUserType();
                userFullName = activeUser.getFirstName() + " " + activeUser.getLastName();

                StorageReference childRef = storageRef.child("meetingrecordings/" + meetingId + "/" + fileData.getFileName());
                //uploading the image
                uploadTask = childRef.putFile(fileUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        btnUpload.setVisibility(View.VISIBLE);
//                        btnUpload.setEnabled(false);
//                        btnCancel.setVisibility(View.GONE);
//
//                        mProgressBar.setVisibility(View.GONE);
                        shareRecordWithNotification(userId, accessToken, userType, userFullName, meetingId, fileData.getFileName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("Upload", "Fail Exception :" + e.getMessage());
                        dialogManager.hideBusyProgress();
//                        btnUpload.setEnabled(true);
//                        btnUpload.setVisibility(View.VISIBLE);
//                        btnCancel.setVisibility(View.GONE);
//
//                        mProgressBar.setVisibility(View.GONE);
//                        textPercentage.setText("Upload Failed.");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                        mProgressBar.setProgress((int) progress);
//                        textPercentage.setText("Uploading completed " + (int) progress + "%");
                    }
                });

            } else {
                toastMessage.showToast("No file found");
                dialogManager.hideBusyProgress();
            }
        } catch (Exception e) {
            Log.v("VideoDetailsActivity", e.getMessage());
            dialogManager.hideBusyProgress();
        }

    }


    /**
     * Upload files to Edbrix Instructor's  my files
     *
     * @param userId
     * @param accessToken
     * @param userType
     * @param meetingId
     * @param fileName
     */
    private void shareRecordWithNotification(final String userId, final String accessToken, final String userType, final String userFullName, final String meetingId, final String fileName) {

        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("UserId", userId);
        requestMap.put("AccessToken", accessToken);
        requestMap.put("UserType", userType);
        requestMap.put("UserFullName", userFullName);
        requestMap.put("FileName", fileName);
        requestMap.put("MeetingId", meetingId);


        try {
            JsonObjectRequest shareRecording = new JsonObjectRequest(Request.Method.POST, Constants.sendMeetingNotification, new JSONObject(requestMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialogManager.hideBusyProgress();
                    Log.v("Volley Response", response.toString());
                    try {
                        if (response != null) {
                            if (response.has("success")) {
                                toastMessage.showToast("Video recording shared successfully.");
                            } else if (response.has("Error")) {
                                toastMessage.showToast("Error occurred while sharing video recording.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialogManager.hideBusyProgress();
                        Log.v("Volley Excep", e.getMessage());
                        toastMessage.showToast(getResources().getString(R.string.error_something_wrong));
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialogManager.hideBusyProgress();
                    toastMessage.showToast(SettingsMy.getErrorMessage(error));
                }

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

            };

            shareRecording.setRetryPolicy(Application.getDefaultRetryPolice());
            shareRecording.setShouldCache(false);
            Application.getInstance().addToRequestQueue(shareRecording, "share_recording");

        } catch (Exception e) {
            Log.v("Excep", e.getMessage());
            toastMessage.showToast(getResources().getString(R.string.error_something_wrong));
        }
    }

    /**
     * download file from Edbrix bucket once notification is received and file name received from notification
     *
     * @param fileName
     */
    public void downloadVideoFromNotification(String fileName) {
        try {
//            dialogManager.showBusyProgress();
            loaderLayout.setVisibility(View.VISIBLE);
            progressText.setText("Downloading video...");
            final File localFile = new File(GlobalMethods.getAppVideoStorageDirectory(TokBoxActivity.this).getPath() + "/" + fileName);
            boolean isFileExist = localFile.exists();
            if (!isFileExist)
                isFileExist = localFile.createNewFile();


            if (isFileExist) {
                String userId;
                User activeUser = SettingsMy.getActiveUser();
                userId = activeUser.getId();

                StorageReference riversRef = storageRef.child("meetingrecordings/" + meetingId + "/" + fileName);
                riversRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                dialogManager.hideBusyProgress();
//                                showToast("File downloaded successfully..");
                                progressText.setText("Downloading completed");
                                loaderLayout.setVisibility(View.GONE);
                                dialogManager.getAlertDialogManager().setAlertDialogCancellable(false);
                                dialogManager.getAlertDialogManager().Dialog("Play", "Continue to play video?", "Continue", "Exit", new AlertDialogManager.onTwoButtonClickListner() {
                                    @Override
                                    public void onNegativeClick() {

                                    }

                                    @Override
                                    public void onPositiveClick() {
                                        if (localFile.exists()) {
                                           /* Intent videoDetail = new Intent(mContext, VideoPlayerActivity.class);
                                            videoDetail.putExtra("FileData", new FileData(localFile));
                                            startActivity(videoDetail);*/

                                            VideoPlayerWithListDialog videoPlayerDialog = new VideoPlayerWithListDialog(TokBoxActivity.this, R.style.DialogAnimation, new FileData(localFile));
                                            videoPlayerDialog.setOnActionButtonListener(new VideoPlayerWithListDialog.OnActionButtonListener() {
                                                @Override
                                                public void onShareFile(FileData fileData) {

                                                    dialogManager.getAlertDialogManager().setAlertDialogCancellable(false);
                                                    dialogManager.getAlertDialogManager().Dialog("Video Recording", "Continue to share video recording?", "Continue", "Cancel", new AlertDialogManager.onTwoButtonClickListner() {
                                                        @Override
                                                        public void onNegativeClick() {

                                                        }

                                                        @Override
                                                        public void onPositiveClick() {
                                                            uploadToEdbrixMyFiles();

                                                        }
                                                    }).show();
                                                }
                                            });
                                            videoPlayerDialog.showMe();
                                        }
                                    }
                                }).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
//                        dialogManager.hideBusyProgress();
                        loaderLayout.setVisibility(View.GONE);
                        toastMessage.showToast("Error found..");
                        // Handle failed download
                        // ...
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressText.setText("Downloading video... " + (int) progress + "%");
                    }
                });
            }

        } catch (Exception e) {
            dialogManager.hideBusyProgress();
            toastMessage.showToast("Exception found.." + e.getMessage());
        }
    }

    /**
     * Runnable thread to update record time
     */
    Runnable UpdateRecordTime = new Runnable() {
        public void run() {
            if (isScreenRecordingRunning) {
                int seconds = 0;
                int minutes = 0;

                if (recordTime >= 60) {
                    seconds = recordTime % 60;
                    minutes = recordTime / 60;
                } else {
                    seconds = recordTime;
                }

                String str = String.format("%02d:%02d", minutes, seconds);
//                currentTimeText.setText(String.valueOf(recordTime));
                currentTimeText.setVisibility(View.VISIBLE);
                currentTimeText.setText(str);
                if (recordTime == slideMaxTimeDuration) {
                    stopBtn.callOnClick();
                } else {
                    recordTime += 1;
                    // Delay 1s before next call
                    handler.postDelayed(this, 1000);
                }
            }
        }
    };

    public void leaveMeetingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to leave the meeting?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        User activeUser = SettingsMy.getActiveUser();
                        String uId = activeUser.getId();
                        String acsToken = activeUser.getAccessToken();
                        String uType = activeUser.getUserType();
                        isMeetingStarted = false;
                        updateTokboxMeetingStatus(uId, acsToken, uType, meetingId, meetingType, "0");
//                        disconnectSession();
//                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Leave ");
        alert.show();
    }

    private void showVideoList() {
        VideoPlayerWithListDialog videoPlayerDialog = new VideoPlayerWithListDialog(TokBoxActivity.this, R.style.DialogAnimation, null);
        videoPlayerDialog.setOnActionButtonListener(new VideoPlayerWithListDialog.OnActionButtonListener() {
            @Override
            public void onShareFile(final FileData fileData) {

                dialogManager.getAlertDialogManager().setAlertDialogCancellable(false);
                dialogManager.getAlertDialogManager().Dialog("Video Recording", "Continue to share video recording?", "Continue", "Cancel", new AlertDialogManager.onTwoButtonClickListner() {
                    @Override
                    public void onNegativeClick() {
                    }

                    @Override
                    public void onPositiveClick() {
                        screenRecordOutputFile = fileData.getFileObject();
                        uploadToEdbrixMyFiles();
                    }
                }).show();
            }
        });
        videoPlayerDialog.showMe();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        leaveMeetingDialog();
    }

    public void switchPublisherCam() {
        if (mPublisher == null) {
            return;
        }
        mPublisher.cycleCamera();
    }

    public void togglePublisherAudio() {
        if (mPublisher == null) {
            return;
        }
        if (mPublisher.getPublishAudio() == true) {
            mPublisher.setPublishAudio(false);
            fullViewToggleAudioImageView.setImageResource(R.drawable.micoff);
        } else {
            mPublisher.setPublishAudio(true);
            fullViewToggleAudioImageView.setImageResource(R.drawable.micon);
        }
    }

    public void togglePublisherVideo() {
        if (mPublisher == null) {
            return;
        }
        if (mPublisher.getPublishVideo() == true) {
            mPublisher.setPublishVideo(false);
            fullViewToggleVideoImageView.setImageResource(R.drawable.videooff);
        } else {
            mPublisher.setPublishVideo(true);
            fullViewToggleVideoImageView.setImageResource(R.drawable.videoon);
        }
    }

    public void togglefullviewsubscriberAudio(Subscriber sub) {
        if (sub == null) {
            return;
        }
        if (sub.getSubscribeToAudio() == true) {
            sub.setSubscribeToAudio(false);
            fullViewToggleAudioImageView.setImageResource(R.drawable.micoff);
        } else {
            sub.setSubscribeToAudio(true);
            fullViewToggleAudioImageView.setImageResource(R.drawable.micon);
        }
    }

    public void togglefullviewsubscriberVideo(Subscriber sub) {
        if (sub == null) {
            return;
        }
        if (sub.getSubscribeToVideo() == true) {
            sub.setSubscribeToVideo(false);
            fullViewToggleVideoImageView.setImageResource(R.drawable.videooff);
        } else {
            sub.setSubscribeToVideo(true);
            fullViewToggleVideoImageView.setImageResource(R.drawable.videoon);
        }
    }

    /**
     * Get Tokbox Meeting status
     *
     * @param userId
     * @param accessToken
     * @param userType
     * @param meetingId
     * @param meetingType
     */
    private void getTokboxMeetingStatus(final String userId, final String accessToken, final String userType, final String meetingId, final String meetingType) {

        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("UserId", userId);
        requestMap.put("AccessToken", accessToken);
        requestMap.put("UserType", userType);
        requestMap.put("MeetingId", meetingId);
        requestMap.put("MeetingType", meetingType);


        try {
            JsonObjectRequest tokBoxMeetingStatus = new JsonObjectRequest(Request.Method.POST, Constants.getTalkboxMeetingStatus, new JSONObject(requestMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialogManager.hideBusyProgress();
                    Log.v("Volley Response", response.toString());
                    try {
                        if (response != null) {
                            if (response.has("ismeetingstarted")) {
                                String isMeetingStarted = response.getString("ismeetingstarted");
                                if (isMeetingStarted != null && !isMeetingStarted.isEmpty()) {
                                    if (isMeetingStarted.equals("0")) {
                                        // meeting started is false then forcefully close meeting session
                                        infoTimerText.setText("Meeting has been ended by host");
                                        infoTimerText.setVisibility(View.VISIBLE);
                                        countDownTimerText.setVisibility(View.VISIBLE);
                                        sessionEndCountDownTimer.start();
                                    } else {
                                        // meeting started is true then keep meeting as it is
                                    }
                                }
                            } else if (response.has("ErrorMessage")) {
                                //  toastMessage.showToast(response.getString("ErrorMessage"));
                            } else {
//                                toastMessage.showToast(getResources().getString(R.string.error_something_wrong));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialogManager.hideBusyProgress();
                        Log.v("Volley Excep", e.getMessage());
                        toastMessage.showToast(getResources().getString(R.string.error_something_wrong));
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialogManager.hideBusyProgress();
                    toastMessage.showToast(SettingsMy.getErrorMessage(error));
                }

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

            };

            tokBoxMeetingStatus.setRetryPolicy(Application.getDefaultRetryPolice());
            tokBoxMeetingStatus.setShouldCache(false);
            Application.getInstance().addToRequestQueue(tokBoxMeetingStatus, "get_tokbox_meeting_status");

        } catch (Exception e) {
            Log.v("Excep", e.getMessage());
            toastMessage.showToast(getResources().getString(R.string.error_something_wrong));
        }
    }

    /**
     * Update Tokbox Meeting status
     *
     * @param userId
     * @param accessToken
     * @param userType
     * @param meetingId
     * @param meetingType
     */
    private void updateTokboxMeetingStatus(final String userId, final String accessToken, final String userType, final String meetingId, final String meetingType, final String meetingStatus) {

        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("UserId", userId);
        requestMap.put("AccessToken", accessToken);
        requestMap.put("UserType", userType);
        requestMap.put("MeetingId", meetingId);
        requestMap.put("MeetingType", meetingType);
        requestMap.put("MeetingStatus", meetingStatus);


        try {
            JsonObjectRequest updateTokBoxMeetingStatus = new JsonObjectRequest(Request.Method.POST, Constants.updateTalkboxMeetingStatus, new JSONObject(requestMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialogManager.hideBusyProgress();
                    Log.v("Volley Response", response.toString());
                    try {
                        if (response != null) {
                            if (response.has("success")) {
//                                toastMessage.showToast("Video recording shared successfully.");
                                if (!isMeetingStarted) {
                                    disconnectSession();
                                    finish();
                                }
                            } else if (response.has("Error")) {
//                                toastMessage.showToast("Error occurred while sharing video recording.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialogManager.hideBusyProgress();
                        Log.v("Volley Excep", e.getMessage());
                        toastMessage.showToast(getResources().getString(R.string.error_something_wrong));
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialogManager.hideBusyProgress();
                    toastMessage.showToast(SettingsMy.getErrorMessage(error));
                }

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

            };

            updateTokBoxMeetingStatus.setRetryPolicy(Application.getDefaultRetryPolice());
            updateTokBoxMeetingStatus.setShouldCache(false);
            Application.getInstance().addToRequestQueue(updateTokBoxMeetingStatus, "update_tokbox_meeting_status");

        } catch (Exception e) {
            Log.v("Excep", e.getMessage());
            toastMessage.showToast(getResources().getString(R.string.error_something_wrong));
        }
    }

    // This countdown will show to learner only once meeting is end by host and session will disconnect automatically
    public class SessionEndCountDownTimer extends CountDownTimer {

        public SessionEndCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished / 1000);
            countDownTimerText.setText("" + (progress - 1));

        }

        @Override
        public void onFinish() {
            infoTimerText.setText("Meeting end");
            countDownTimerText.setText("0");
            countDownTimerText.setVisibility(View.GONE);
            infoTimerText.setVisibility(View.GONE);
            disconnectSession();
            finish();
        }

    }
}
