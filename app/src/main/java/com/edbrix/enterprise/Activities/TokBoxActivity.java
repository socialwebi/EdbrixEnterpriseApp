package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.opentok.android.BaseVideoRenderer;
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
        Session.SessionListener
{
    private static final String TAG = "TokBoxActivity";//"simple-multiparty " + MainActivity.class.getSimpleName();

    private final int MAX_NUM_SUBSCRIBERS = 5; //Max Number Of TokBoxSubscribers

    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private Session mSession;
    private Publisher mPublisher;

    private ArrayList<Subscriber> mSubscribers = new ArrayList<Subscriber>();
    private HashMap<Stream, Subscriber> mSubscriberStreams = new HashMap<Stream, Subscriber>();

    private RelativeLayout mPublisherViewContainer,mfullViewContainer;
    public ImageView swapCamImageView,toggleAudioImageView,toggleVideoImageView,restoreFullViewImageView,publisherFullViewImageView,zoomImageView;

    private LinearLayout mSubscriberlistLinearLayout,mPublisherControlsLinearLayout,mRightSideLinearLayout;
    public FrameLayout mpublisherScreenFrame;

    boolean swapSubscriberToFullView= false;
    boolean swapPublisherToFullView=false;
    boolean togglePublisherFullScreen= false;
    public static int swapPos;

    //public String Role="Host";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tok_box);

        mPublisherViewContainer = (RelativeLayout) findViewById(R.id.publisherviewLayout);
        mfullViewContainer = (RelativeLayout) findViewById(R.id.fullViewLayout);
        mpublisherScreenFrame = (FrameLayout)findViewById(R.id.publisherScreenFrame);
        mSubscriberlistLinearLayout = (LinearLayout)findViewById(R.id.subscriberListLinear);
        mPublisherControlsLinearLayout =(LinearLayout)findViewById(R.id.publisherControlLayout);
        mRightSideLinearLayout = (LinearLayout) findViewById(R.id.rightLinearLayout);

        swapCamImageView = (ImageView)findViewById(R.id.swapCamera);
        toggleAudioImageView = (ImageView)findViewById(R.id.toggleAudio);
        toggleVideoImageView = (ImageView)findViewById(R.id.toggleVideo);
        //restoreFullViewImageView = (ImageView)findViewById(R.id.restoreView);
        publisherFullViewImageView = (ImageView)findViewById(R.id.fullView);
        zoomImageView = (ImageView)findViewById(R.id.zoomView);

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

        zoomImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!togglePublisherFullScreen)
                {
                    mRightSideLinearLayout.setVisibility(View.GONE);
                    zoomImageView.setImageResource(R.drawable.minimise);
                    togglePublisherFullScreen = true;
                }else if(togglePublisherFullScreen){
                    mRightSideLinearLayout.setVisibility(View.VISIBLE);
                    zoomImageView.setImageResource(R.drawable.maximize);
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
        mPublisher = new Publisher.Builder(TokBoxActivity.this).name(SettingsMy.getActiveUser().getFirstName()+" "+SettingsMy.getActiveUser().getLastName()).build();
        mPublisher.setPublisherListener(this);
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

        ///New
        if(!SettingsMy.getActiveUser().getUserType().equals("L")){
            mpublisherScreenFrame.setVisibility(View.GONE);
            mfullViewContainer.addView(mPublisher.getView());
            swapPublisherToFullView = true;
        }else{
            mPublisherViewContainer.addView(mPublisher.getView());
        }
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
        Log.e(TAG,"onStreamDropped: Stream Data : "+stream.getConnection().getData()+"\nSubscribers Size : "+(mSubscribers.size()));

        Subscriber subscriber = mSubscriberStreams.get(stream);
        if (subscriber == null) {   return;     }

        try{
            if (stream.getConnection().getData().equals("Host")) {
                mfullViewContainer.removeView(subscriber.getView());
                return;
            }
        }catch(Exception ex){Log.e(TAG,"Error :"+ex.getMessage().toString());}
        try{
            if(swapSubscriberToFullView) {
                mfullViewContainer.removeView(mSubscribers.get(swapPos).getView());
            }
        }catch(Exception ex){Log.e(TAG,"Error :"+ex.getMessage().toString());}

        int position = mSubscribers.indexOf(subscriber);
        mSubscribers.remove(subscriber);
        mSubscriberStreams.remove(stream);
        mSubscriberlistLinearLayout.removeViewAt(position);
    }
    public void hostView(Session session, Stream stream){
        final Subscriber subscriber = new Subscriber.Builder(TokBoxActivity.this, stream).build();
        mSession.subscribe(subscriber);
        mSubscribers.add(subscriber);
        mSubscriberStreams.put(stream, subscriber);

        final int position = mSubscribers.size() - 1;

        final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.row_host, null);

        final FrameLayout subscriberFrame = (FrameLayout)addView.findViewById(R.id.subscriberFrame);
        //final LinearLayout subscriberControls = (LinearLayout)addView.findViewById(R.id.subscriberControlsLinear);
        final RelativeLayout subscriberViewContainer = (RelativeLayout)addView.findViewById(R.id.subscriberview);
        final TextView subscriberName = (TextView)addView.findViewById(R.id.textViewSubscribername);
        final ImageView switchView = (ImageView)addView.findViewById(R.id.toggleSubscriberView);
        final ImageView switchAudio = (ImageView)addView.findViewById(R.id.toggleAudioSubscriber);

        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        subscriber.setSubscribeToAudio(true);

        subscriberViewContainer.addView(subscriber.getView());
        subscriberViewContainer.setTag(position);
        subscriberName.setText(""+subscriber.getStream().getName().toString());
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

        switchView.setTag(position);
        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int itemposition  = (Integer) v.getTag();
                Log.e("swap","SwapPos : "+swapPos);

                if(!swapSubscriberToFullView)
                {   Log.e("swap","SubscriberToFullView = "+swapSubscriberToFullView+" SwapPos : "+swapPos+ "ItemPos : "+itemposition);
                    if(swapPublisherToFullView){
                        Log.e("swap","swapPublisherToFullView = "+swapPublisherToFullView+" SwapPos : "+swapPos+ "ItemPos : "+itemposition);
                        mfullViewContainer.removeView(mPublisher.getView());
                        mpublisherScreenFrame.setVisibility(View.VISIBLE);
                        publisherFullViewImageView.setVisibility(View.VISIBLE);
                        mPublisherViewContainer.addView(mPublisher.getView());
                        swapPublisherToFullView = false;

                        subscriberViewContainer.removeView(subscriber.getView());
                        mfullViewContainer.addView(subscriber.getView());
                        swapPos = itemposition;
                        swapSubscriberToFullView = true;
                        return;
                    }else{
                        subscriberViewContainer.removeView(subscriber.getView());
                        mfullViewContainer.addView(subscriber.getView());
                        swapPos = itemposition;
                        swapSubscriberToFullView = true;
                        return;
                    }
                }else if(swapSubscriberToFullView)
                {   Log.e("swap","SubscriberToFullView = "+swapSubscriberToFullView+" SwapPos : "+swapPos+ "ItemPos : "+itemposition);
                    if(swapPos == itemposition){
                        mfullViewContainer.removeView(subscriber.getView());
                        subscriberViewContainer.addView(subscriber.getView());
                        swapSubscriberToFullView = false;
                        return;
                    }
                    else if(swapPos != itemposition){
                        mfullViewContainer.removeView(mSubscribers.get(swapPos).getView());

                        final Subscriber tmpsubscriber = mSubscribers.get(swapPos);
                        tmpsubscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

                        if(tmpsubscriber.getSubscribeToAudio() == true) {
                            tmpsubscriber.setSubscribeToAudio(false);
                            switchAudio.setImageResource(R.drawable.soundoff);
                        }
                        else {
                            tmpsubscriber.setSubscribeToAudio(true);
                            switchAudio.setImageResource(R.drawable.soundon);
                        }
                        //tmpsubscriber.setSubscribeToAudio(true);
                        View tmpView = mSubscriberlistLinearLayout.getChildAt(swapPos);
                        RelativeLayout tmpSubView = (RelativeLayout) tmpView.findViewById(R.id.subscriberview);
                        tmpSubView.addView(tmpsubscriber.getView());

                        swapPos = itemposition;
                        subscriberViewContainer.removeView(subscriber.getView());
                        mfullViewContainer.addView(subscriber.getView());

                        swapSubscriberToFullView = true;
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
                if(swapSubscriberToFullView)
                {
                    mPublisherViewContainer.removeView(mPublisher.getView());
                    mfullViewContainer.removeView(mSubscribers.get(swapPos).getView());
                    mpublisherScreenFrame.setVisibility(View.GONE);
                    publisherFullViewImageView.setVisibility(View.GONE);
                    mfullViewContainer.addView(mPublisher.getView());
                    swapPublisherToFullView = true;

                    final Subscriber tmpsubscriber = mSubscribers.get(swapPos);
                    tmpsubscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                    tmpsubscriber.setSubscribeToAudio(true);
                    View tmpView = mSubscriberlistLinearLayout.getChildAt(swapPos);
                    RelativeLayout tmpSubView = (RelativeLayout)tmpView.findViewById(R.id.subscriberview);
                    tmpSubView.addView(tmpsubscriber.getView());

                    swapSubscriberToFullView=false;
                    return;
                }else{
                    mPublisherViewContainer.removeView(mPublisher.getView());
                    mpublisherScreenFrame.setVisibility(View.GONE);
                    publisherFullViewImageView.setVisibility(View.GONE);
                    mfullViewContainer.addView(mPublisher.getView());
                    swapPublisherToFullView = true;
                    return;
                }
            }
        });
    }

    public void userView(Session session, Stream stream){

        try{
            if(stream.getConnection().getData().equals("Host"))
            {
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

        final LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.row_subscribers, null);

        final FrameLayout subscriberFrame = (FrameLayout)addView.findViewById(R.id.subscriberFrame);
        // final LinearLayout subscriberControls = (LinearLayout)addView.findViewById(R.id.subscriberControlsLinear);
        final RelativeLayout subscriberViewContainer = (RelativeLayout)addView.findViewById(R.id.subscriberview);
        final TextView subscriberName = (TextView)addView.findViewById(R.id.textViewSubscribername);
        final ImageView switchView = (ImageView)addView.findViewById(R.id.toggleSubscriberView);
        final ImageView switchAudio = (ImageView)addView.findViewById(R.id.toggleAudioSubscriber);

        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        subscriber.setSubscribeToAudio(true);

        subscriberViewContainer.addView(subscriber.getView());
        subscriberName.setText(""+subscriber.getStream().getName().toString());
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

        mSubscriberlistLinearLayout.addView(addView);
        LayoutTransition transition = new LayoutTransition();
        mSubscriberlistLinearLayout.setLayoutTransition(transition);
    }
}
