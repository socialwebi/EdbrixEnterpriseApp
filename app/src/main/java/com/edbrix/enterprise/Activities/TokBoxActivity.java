package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class TokBoxActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks,
        Publisher.PublisherListener,
        Session.SessionListener,
        Session.ArchiveListener
{
    private static final String TAG = "TokBoxActivity";//"simple-multiparty " + MainActivity.class.getSimpleName();

    private final int MAX_NUM_SUBSCRIBERS = 15; //Max Number Of TokBoxSubscribers

    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private Session mSession;
    private Publisher mPublisher;

    private ArrayList<Subscriber> mSubscribers = new ArrayList<Subscriber>();
    private HashMap<Stream, Subscriber> mSubscriberStreams = new HashMap<Stream, Subscriber>();

    private RelativeLayout mPublisherViewContainer,mfullViewContainer;

    private LinearLayout subscriberlistLinearLayout,publisherControlsLinearLayout,rightsideViewLinear;
    public ImageView swapCamImageView,toggleAudioImageView,toggleVideoImageView;
    boolean swapSubscriberToPublisher= false;
    boolean togglePublisherFullScreen= false;
    int swapPos;
    //public String Role="Host";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tok_box);

        mPublisherViewContainer = (RelativeLayout) findViewById(R.id.publisherview);

        mfullViewContainer = (RelativeLayout) findViewById(R.id.fullViewLayout);
        subscriberlistLinearLayout = (LinearLayout)findViewById(R.id.subscriberListLinear);
        rightsideViewLinear = (LinearLayout) findViewById(R.id.rightLinear);
        publisherControlsLinearLayout =(LinearLayout)findViewById(R.id.publisherControls);

        swapCamImageView = (ImageView)findViewById(R.id.swapCam);
        toggleAudioImageView = (ImageView)findViewById(R.id.toggleAudio);
        toggleVideoImageView = (ImageView)findViewById(R.id.toggleVideo);

        swapCamImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mPublisher == null) {
                    return;
                }
                mPublisher.cycleCamera();
            }
        });

        // mPublisher.setPublishAudio(true);
        toggleAudioImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPublisher == null) {
                    return;
                }
                if(mPublisher.getPublishAudio() == true)
                {
                    mPublisher.setPublishAudio(false);
                    toggleAudioImageView.setImageResource(R.drawable.micoff);
                }
                else {
                    mPublisher.setPublishAudio(true);
                    toggleAudioImageView.setImageResource(R.drawable.micon);
                }
            }
        });

        // mPublisher.setPublishVideo(true);
        toggleVideoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPublisher == null) {
                    return;
                }
                if(mPublisher.getPublishVideo() == true)
                {
                    mPublisher.setPublishVideo(false);
                    toggleVideoImageView.setImageResource(R.drawable.videooff);
                }
                else {
                    mPublisher.setPublishVideo(true);
                    toggleVideoImageView.setImageResource(R.drawable.videoon);
                }
            }
        });
        requestPermissions();
       /* mPublisherViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!togglePublisherFullScreen)
                {
                    rightsideViewLinear.setVisibility(View.GONE);
                    togglePublisherFullScreen = true;
                }else if(togglePublisherFullScreen){
                    rightsideViewLinear.setVisibility(View.VISIBLE);
                    togglePublisherFullScreen = false;
                }
            }
        });*/
        mfullViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!togglePublisherFullScreen)
                {
                    rightsideViewLinear.setVisibility(View.GONE);
                    togglePublisherFullScreen = true;
                }else if(togglePublisherFullScreen){
                    rightsideViewLinear.setVisibility(View.VISIBLE);
                    togglePublisherFullScreen = false;
                }
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

        super.onResume();

        if (mSession == null) {
            return;
        }
        mSession.onResume();
    }
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");

        super.onPause();

        if (mSession == null) {
            return;
        }
        mSession.onPause();

        if (isFinishing()) {
            disconnectSession();
        }
    }
    @Override
    protected void onStop() {
        Log.d(TAG, "onPause");

        super.onStop();
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
        if (EasyPermissions.hasPermissions(this, perms))
        {
            String API_KEY = Constants.TolkBox_APIKey;
            String SESSION_ID = getIntent().getStringExtra(Constants.TolkBox_SessionId);
            String TOKEN = getIntent().getStringExtra(Constants.TolkBox_Token);
            Log.i(TAG, "TokBoxActivity :\nApi : "+API_KEY+"\nSession : "+SESSION_ID+"\n Token :"+TOKEN);

            mSession = new Session.Builder(TokBoxActivity.this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, perms);
        }
    }
    @Override
    public void onConnected(Session session)
    {
        Log.d(TAG, "onConnected: Connected to session " + session.getSessionId());

        mPublisher = new Publisher.Builder(TokBoxActivity.this).name("publisher").build();

        mPublisher.setPublisherListener(this);
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

        mPublisherViewContainer.addView(mPublisher.getView());
        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.d(TAG, "onDisconnected: disconnected from session " + session.getSessionId());

        mSession = null;
    }
    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.d(TAG, "onError: Error (" + opentokError.getMessage() + ") in session " + session.getSessionId());

        Toast.makeText(this, "Session error. See the logcat please.", Toast.LENGTH_LONG).show();
        finish();
    }
    @Override
    public void onStreamReceived(Session session, Stream stream)
    {
        Log.d(TAG, "onStreamReceived: New stream " + stream.getStreamId() + " in session " + session.getSessionId());
        Log.e(TAG,"Stream Data : "+stream.getConnection().getData()+"\nPos : "+(mSubscribers.size()));
        if (mSubscribers.size() + 1 > MAX_NUM_SUBSCRIBERS)
        {
            Toast.makeText(this, "New subscriber ignored. MAX_NUM_SUBSCRIBERS limit reached : "+MAX_NUM_SUBSCRIBERS, Toast.LENGTH_LONG).show();
            return;
        }
        if(!SettingsMy.getActiveUser().getUserType().equals("L")){
            hostView(session,stream);
        }else{
            userView(session,stream);
        }
    }
    @Override
    public void onStreamDropped(Session session, Stream stream)
    {
        Log.d(TAG, "onStreamDropped: Stream " + stream.getStreamId() + " dropped from session " + session.getSessionId());
        Log.e(TAG,"onStreamDropped: Stream Data : "+stream.getConnection().getData()+"\nPos : "+(mSubscribers.size()));

        Subscriber subscriber = mSubscriberStreams.get(stream);
        if (subscriber == null) {   return;     }

        try {
            if (stream.getConnection().getData().equals("Host")) {

                mfullViewContainer.removeView(subscriber.getView());
                return;
            }
        }catch (Exception E){Log.e(TAG,"Error :"+E.getMessage().toString());}

        int position = mSubscribers.indexOf(subscriber);
        mSubscribers.remove(subscriber);
        mSubscriberStreams.remove(stream);



        /*if(Role.equals("Host"))
        {*/
        int subscriberFrameID = getResources().getIdentifier("subscriberFrame" + (new Integer(position)).toString(), "id", TokBoxActivity.this.getPackageName());
        final FrameLayout subscriberFrame = (FrameLayout)findViewById(subscriberFrameID);
        subscriberlistLinearLayout.removeView(subscriberFrame);
        //}




       /* int subscriberViewContainerID = getResources().getIdentifier("subscriberview" + (new Integer(position)).toString(), "id", MainActivity.this.getPackageName());
        RelativeLayout subscriberViewContainer = (RelativeLayout) findViewById(subscriberViewContainerID);
        subscriberlistLinearLayout.removeView(subscriberViewContainer);

        int subscriberControlsContainerID = getResources().getIdentifier("subscriberControlsLinear" + (new Integer(position)).toString(), "id", MainActivity.this.getPackageName());
        final LinearLayout subscriberControls = (LinearLayout) findViewById(subscriberControlsContainerID);
        subscriberlistLinearLayout.removeView(subscriberControls);*/

        /*int subscriberFrameID = getResources().getIdentifier("subscriberFrame" + (new Integer(position)).toString(), "id", MainActivity.this.getPackageName());
        final FrameLayout subscriberFrame = (FrameLayout)findViewById(subscriberFrameID);
        subscriberlistLinearLayout.removeView(subscriberFrame);*/


       /*int switchAudioID = getResources().getIdentifier("toggleAudioSubscriber" + (new Integer(position)).toString(), "id", MainActivity.this.getPackageName());
        final ImageView switchAudio = (ImageView)findViewById(switchAudioID);
        subscriberlistLinearLayout.removeView(switchAudio);*/
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

        Toast.makeText(this, "Session error. See the logcat please.", Toast.LENGTH_LONG).show();
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
    public void onArchiveStarted(Session session, String s, String s1) {
        publisherControlsLinearLayout.setVisibility(View.VISIBLE);
    }
    @Override
    public void onArchiveStopped(Session session, String s) {
        publisherControlsLinearLayout.setVisibility(View.INVISIBLE);
    }

    public void hostView(Session session, Stream stream)
    {   //Origin
        final Subscriber subscriber = new Subscriber.Builder(TokBoxActivity.this, stream).build();
        mSession.subscribe(subscriber);
        mSubscribers.add(subscriber);
        mSubscriberStreams.put(stream, subscriber);

        final int position = mSubscribers.size() - 1;

        final FrameLayout subscriberFrame = new FrameLayout(this);
        int subscriberFrameID = getResources().getIdentifier("subscriberFrame" + (new Integer(position)).toString(), "id", TokBoxActivity.this.getPackageName());
        subscriberFrame.setId(subscriberFrameID);
        FrameLayout.LayoutParams subscriberFrameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,500);
        subscriberFrame.setLayoutParams(subscriberFrameParams);

        final LinearLayout subscriberControls = new LinearLayout(this);
        int subscriberControlsContainerID = getResources().getIdentifier("subscriberControlsLinear" + (new Integer(position)).toString(), "id", TokBoxActivity.this.getPackageName());
        subscriberControls.setId(subscriberControlsContainerID);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,80);
        subscriberControls.setOrientation(LinearLayout.HORIZONTAL);
        subscriberControls.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        subscriberControls.setAlpha((float) 0.70);
        subscriberControls.setGravity(Gravity.RIGHT);
        subscriberControls.setLayoutParams(params);

        //Item Subscriber View
        final RelativeLayout subscriberViewContainer = new RelativeLayout(this);
        RelativeLayout.LayoutParams subscriberViewParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,500);
        int subscriberViewContainerID = getResources().getIdentifier("subscriberview" + (new Integer(position)).toString(), "id", TokBoxActivity.this.getPackageName());
        subscriberViewContainer.setId(subscriberViewContainerID);
        subscriberViewContainer.setLayoutParams(subscriberViewParam);
        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        subscriberViewContainer.setPadding(1,1,1,1);
        subscriberViewContainer.addView(subscriber.getView());

        //Item Subscriber Toggle View
        final ImageView switchView = new ImageView(this);
        int switchViewID = getResources().getIdentifier("toggleSubscriberView" + (new Integer(position)).toString(), "id", TokBoxActivity.this.getPackageName());
        switchView.setId(switchViewID);
        switchView.setLayoutParams(new LinearLayout.LayoutParams(70, 70));
        switchView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        switchView.setMaxHeight(90);
        switchView.setMaxWidth(90);
        switchView.setPadding(2,2,2,2);
        switchView.setImageResource(R.drawable.swapview);
        switchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!swapSubscriberToPublisher)
                {
                    mfullViewContainer.removeView(subscriber.getView());
                    subscriberViewContainer.removeView(subscriber.getView());

                    mfullViewContainer.addView(subscriber.getView());
                    //subscriberViewContainer.addView(subscriber.getView());
                    swapPos = position;
                    swapSubscriberToPublisher = true;
                }else if(swapSubscriberToPublisher)
                {
                    if (position == swapPos)
                    {
                        mfullViewContainer.removeView(subscriber.getView());
                        //subscriberViewContainer.removeView(subscriber.getView());
                        subscriberViewContainer.addView(subscriber.getView());

//                        mfullViewContainer.addView(subscriber.getView());
                        //subscriberViewContainer.addView(subscriber.getView());

                        swapSubscriberToPublisher= false;
                    }
                    else{   Toast.makeText(getApplicationContext(),"This Is Not Publisher",Toast.LENGTH_LONG).show();   return; }
                }
                else {return;}
            }
        });
        switchView.setVisibility(View.VISIBLE);
        subscriberControls.addView(switchView);

        //Item Toggle Subscriber Audio
        final ImageView switchAudio = new ImageView(this);
        int switchAudioID = getResources().getIdentifier("toggleAudioSubscriber" + (new Integer(position)).toString(), "id", TokBoxActivity.this.getPackageName());
        switchAudio.setId(switchAudioID);
        subscriber.setSubscribeToAudio(true);
        switchAudio.setLayoutParams(new GridView.LayoutParams(70, 70));
        switchAudio.setScaleType(ImageView.ScaleType.FIT_CENTER);
        switchAudio.setMaxHeight(90);
        switchAudio.setMaxWidth(90);
        switchAudio.setPadding(2,2,2,2);
        switchAudio.setImageResource(R.drawable.soundon);
        switchAudio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(subscriber.getSubscribeToAudio() == true) {
                    subscriber.setSubscribeToAudio(false);
                    switchAudio.setImageResource(R.drawable.soundoff);
                }
                else {
                    subscriber.setSubscribeToAudio(true);
                    switchAudio.setImageResource(R.drawable.soundon);
                }
            }
        });
        switchAudio.setVisibility(View.VISIBLE);
        subscriberControls.addView(switchAudio);
        //subscriberlistLinearLayout.addView(switchAudio);

        subscriberFrame.addView(subscriberViewContainer);
        subscriberFrame.addView(subscriberControls);
        subscriberlistLinearLayout.addView(subscriberFrame);
    }

    public void userView(Session session, Stream stream){

        try{
            if(stream.getConnection().getData().equals("Host")) {
                Subscriber mSubscriber = new Subscriber.Builder(TokBoxActivity.this, stream).build();
                mSubscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                mSession.subscribe(mSubscriber);
                mSubscribers.add(mSubscriber);
                mSubscriberStreams.put(stream, mSubscriber);
                mfullViewContainer.addView(mSubscriber.getView());
                return;
            }
        }catch (Exception ex){}

        //Origin
        final Subscriber subscriber = new Subscriber.Builder(TokBoxActivity.this, stream).build();
        mSession.subscribe(subscriber);
        mSubscribers.add(subscriber);
        mSubscriberStreams.put(stream, subscriber);

        final int position = mSubscribers.size() - 1;

        final FrameLayout subscriberFrame = new FrameLayout(this);
        int subscriberFrameID = getResources().getIdentifier("subscriberFrame" + (new Integer(position)).toString(), "id", TokBoxActivity.this.getPackageName());
        subscriberFrame.setId(subscriberFrameID);
        FrameLayout.LayoutParams subscriberFrameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,500);
        subscriberFrame.setLayoutParams(subscriberFrameParams);

        final LinearLayout subscriberControls = new LinearLayout(this);
        int subscriberControlsContainerID = getResources().getIdentifier("subscriberControlsLinear" + (new Integer(position)).toString(), "id", TokBoxActivity.this.getPackageName());
        subscriberControls.setId(subscriberControlsContainerID);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,80);
        subscriberControls.setOrientation(LinearLayout.HORIZONTAL);
        subscriberControls.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        subscriberControls.setAlpha((float) 0.70);
        subscriberControls.setGravity(Gravity.RIGHT);
        subscriberControls.setLayoutParams(params);

        //Item Subscriber View
        final RelativeLayout subscriberViewContainer = new RelativeLayout(this);
        RelativeLayout.LayoutParams subscriberViewParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,500);
        int subscriberViewContainerID = getResources().getIdentifier("subscriberview" + (new Integer(position)).toString(), "id", TokBoxActivity.this.getPackageName());
        subscriberViewContainer.setId(subscriberViewContainerID);
        subscriberViewContainer.setLayoutParams(subscriberViewParam);
        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        subscriberViewContainer.setPadding(1,1,1,1);
        subscriberViewContainer.addView(subscriber.getView());

        //Item Subscriber Toggle View
        /*final ImageView switchView = new ImageView(this);
        int switchViewID = getResources().getIdentifier("toggleSubscriberView" + (new Integer(position)).toString(), "id", MainActivity.this.getPackageName());
        switchView.setId(switchViewID);
        switchView.setLayoutParams(new LinearLayout.LayoutParams(70, 70));
        switchView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        switchView.setMaxHeight(90);
        switchView.setMaxWidth(90);
        switchView.setPadding(2,2,2,2);
        switchView.setImageResource(R.drawable.swapview);
        switchView.setVisibility(View.VISIBLE);
        subscriberControls.addView(switchView);*/

        //Item Toggle Subscriber Audio
        final ImageView switchAudio = new ImageView(this);
        int switchAudioID = getResources().getIdentifier("toggleAudioSubscriber" + (new Integer(position)).toString(), "id", TokBoxActivity.this.getPackageName());
        switchAudio.setId(switchAudioID);
        subscriber.setSubscribeToAudio(true);
        switchAudio.setLayoutParams(new GridView.LayoutParams(70, 70));
        switchAudio.setScaleType(ImageView.ScaleType.FIT_CENTER);
        switchAudio.setMaxHeight(90);
        switchAudio.setMaxWidth(90);
        switchAudio.setPadding(2,2,2,2);
        switchAudio.setImageResource(R.drawable.soundon);
        switchAudio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(subscriber.getSubscribeToAudio() == true) {
                    subscriber.setSubscribeToAudio(false);
                    switchAudio.setImageResource(R.drawable.soundoff);
                }
                else {
                    subscriber.setSubscribeToAudio(true);
                    switchAudio.setImageResource(R.drawable.soundon);
                }
            }
        });
        switchAudio.setVisibility(View.VISIBLE);
        subscriberControls.addView(switchAudio);

        subscriberFrame.addView(subscriberViewContainer);
        subscriberFrame.addView(subscriberControls);
        subscriberlistLinearLayout.addView(subscriberFrame);
    }
}


/*
if(!swapSubscriberToPublisher)
{
    mPublisherViewContainer.removeView(mPublisher.getView());
    subscriberViewContainer.removeView(subscriber.getView());

    mPublisherViewContainer.addView(subscriber.getView());
    subscriberViewContainer.addView(mPublisher.getView());
    swapPos = position;
    swapSubscriberToPublisher = true;
}else if(swapSubscriberToPublisher)
{
    if (position == swapPos)
    {
        mPublisherViewContainer.removeView(subscriber.getView());
        subscriberViewContainer.removeView(mPublisher.getView());

        mPublisherViewContainer.addView(mPublisher.getView());
        subscriberViewContainer.addView(subscriber.getView());

        swapSubscriberToPublisher= false;
    }
    else{   Toast.makeText(getApplicationContext(),"This Is Not Publisher",Toast.LENGTH_LONG).show();   return; }
}
else {return;}
 */
