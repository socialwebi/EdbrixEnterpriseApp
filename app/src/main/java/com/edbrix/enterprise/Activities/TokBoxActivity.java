package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Utils.TokBoxWebServiceCoordinator;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class TokBoxActivity extends AppCompatActivity
        implements  EasyPermissions.PermissionCallbacks,
        TokBoxWebServiceCoordinator.Listener,
        Session.SessionListener,
        PublisherKit.PublisherListener,
        SubscriberKit.SubscriberListener,
        Session.ArchiveListener {

    private static final String LOG_TAG = "TokBox";//MainActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    // Suppressing this warning. mWebServiceCoordinator will get GarbageCollected if it is local.
    @SuppressWarnings("FieldCanBeLocal")
    private TokBoxWebServiceCoordinator mWebServiceCoordinator;

    private String mApiKey;
    private String mSessionId;
    private String mToken;
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private String mCurrentArchiveId;
    private String mPlayableArchiveId;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private ImageView mArchivingIndicatorView;
    private Button swapCamera,leavemeetButton;
    private ToggleButton toggleVideo,toggleAudio;

    private Menu mMenu;

    public boolean flag = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(LOG_TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(LOG_TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

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
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms))
        {
            // initialize view objects from your layout
            mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
            mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);
            mArchivingIndicatorView = (ImageView) findViewById(R.id.archiving_indicator_view);

            // initialize WebServiceCoordinator and kick off request for session data
            // session initialization occurs once data is returned, in onSessionConnectionDataReady
            mWebServiceCoordinator = new TokBoxWebServiceCoordinator(this, this);
            // mWebServiceCoordinator.fetchSessionConnectionData("/session");

            String API_KEY = Constants.TolkBox_APIKey;
            String SESSION_ID = getIntent().getStringExtra(Constants.TolkBox_SessionId);
            String TOKEN = getIntent().getStringExtra(Constants.TolkBox_Token);
            Log.i(LOG_TAG, "TokBoxActivity :\nApi : "+API_KEY+"\nSession : "+SESSION_ID+"\n Token :"+TOKEN);

            TokBoxWebServiceCoordinator.delegate.onSessionConnectionDataReady(API_KEY, SESSION_ID, TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tok_box);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        swapCamera = (Button) findViewById(R.id.swapCamera);
        leavemeetButton =(Button)findViewById(R.id.leaveMeet);
        leavemeetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {   finish();   }
        });

        requestPermissions();
        swapCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mPublisher == null) {
                    return;
                }
                mPublisher.cycleCamera();
            }
        });

        toggleAudio = (ToggleButton) findViewById(R.id.toggleAudio);
        toggleAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mPublisher == null) {
                    return;
                }
                if (isChecked) {
                    mPublisher.setPublishAudio(true);
                } else {
                    mPublisher.setPublishAudio(false);
                }
            }
        });

        toggleVideo = (ToggleButton) findViewById(R.id.toggleVideo);
        toggleVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mPublisher == null) {
                    return;
                }
                if (isChecked) {
                    mPublisher.setPublishVideo(true);
                } else {
                    mPublisher.setPublishVideo(false);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setStopArchiveEnabled(false);
        mPublisher.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_start_archive:
                startArchive();
                return true;
            case R.id.action_stop_archive:
                stopArchive();
                return true;
            case R.id.action_play_archive:
                playArchive();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeSession(String apiKey, String sessionId, String token) {
        mSession = new Session.Builder(this, apiKey, sessionId).build();
        mSession.setSessionListener(this);
        mSession.setArchiveListener(this);
        mSession.connect(token);
    }

    private void initializePublisher() {
        // initialize Publisher and set this object to listen to Publisher events
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        // set publisher video style to fill view
        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        mPublisherViewContainer.addView(mPublisher.getView(), 0);
    }

    private void logOpenTokError(OpentokError opentokError) {
        Log.e(LOG_TAG, "Error Domain: " + opentokError.getErrorDomain().name());
        Log.e(LOG_TAG, "Error Code: " + opentokError.getErrorCode().name());
    }

    /* methods calling mWebServiceCoordinator to control Archiving */

    private void startArchive() {
        if(mSession != null) {
            mWebServiceCoordinator.startArchive(mSessionId);
            setStartArchiveEnabled(false);
        }
    }

    private void stopArchive() {
        mWebServiceCoordinator.stopArchive(mCurrentArchiveId);
        setStopArchiveEnabled(false);
    }

    private void playArchive() {
        Uri playArchiveUri = mWebServiceCoordinator.archivePlaybackUri(mPlayableArchiveId);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, playArchiveUri);
        startActivity(browserIntent);
    }

    /* Activity lifecycle methods */

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");

        if (mSession != null) {
            mSession.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");

        if (mSession != null) {
            mSession.onResume();
        }
    }

    /* Web Service Coordinator delegate methods */

    @Override
    public void onSessionConnectionDataReady(String apiKey, String sessionId, String token) {
        Log.i(LOG_TAG, apiKey);
        Log.i(LOG_TAG, sessionId);
        Log.i(LOG_TAG, token);

        mApiKey = apiKey;
        mSessionId = sessionId;
        mToken = token;

        initializeSession(apiKey, sessionId, token);
        initializePublisher();
    }

    @Override
    public void onWebServiceCoordinatorError(Exception error) {
        Log.e(LOG_TAG, "Web Service error: " + error);
        Log.e(LOG_TAG, "Web Service error message: " + error.getMessage());
    }

    /* Session Listener methods */

    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");

        Log.d(LOG_TAG, "onConnected: Connected to session: "+session.getSessionId());

        // initialize Publisher and set this object to listen to Publisher events
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        // set publisher video style to fill view
        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        mPublisherViewContainer.addView(mPublisher.getView());
        if (mPublisher.getView() instanceof GLSurfaceView) {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        if (mPublisher != null) {
            mSession.publish(mPublisher);
        }

        setStartArchiveEnabled(true);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");

        setStartArchiveEnabled(false);
        setStopArchiveEnabled(false);
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSubscriber.setSubscriberListener(this);
            mSubscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FILL);
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "onError: "+ opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() + " - "+opentokError.getMessage() + " in session: "+ session.getSessionId());

        logOpenTokError(opentokError);
    }

    /* Publisher Listener methods */

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher Stream Created");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher Stream Destroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        logOpenTokError(opentokError);
    }

    /* Subscriber Listener methods */

    @Override
    public void onConnected(SubscriberKit subscriberKit) {
        Log.i(LOG_TAG, "Subscriber Connected");

        // mSubscriberViewContainer.addView(mSubscriber.getView());
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {
        Log.i(LOG_TAG, "Subscriber Disconnected");
    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "onError: "+opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() +  " - "+opentokError.getMessage());

        logOpenTokError(opentokError);
    }

    /* Archive Listener methods */

    @Override
    public void onArchiveStarted(Session session, String archiveId, String archiveName) {
        mCurrentArchiveId = archiveId;
        setStopArchiveEnabled(true);
        mArchivingIndicatorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onArchiveStopped(Session session, String archiveId) {
        mPlayableArchiveId = archiveId;
        mCurrentArchiveId = null;
        setPlayArchiveEnabled(true);
        setStartArchiveEnabled(true);
        mArchivingIndicatorView.setVisibility(View.INVISIBLE);
    }

    /* Options menu helpers */

    private void setStartArchiveEnabled(boolean enabled) {
       /* mMenu.findItem(R.id.action_start_archive1)
                .setEnabled(enabled)
                .setVisible(enabled);*/
    }

    private void setStopArchiveEnabled(boolean enabled) {
       /* mMenu.findItem(R.id.action_stop_archive1)
                .setEnabled(enabled)
                .setVisible(enabled);*/
    }

    private void setPlayArchiveEnabled(boolean enabled) {
      /*  mMenu.findItem(R.id.action_play_archive1)
                .setEnabled(enabled)
                .setVisible(enabled);*/


    }
}
