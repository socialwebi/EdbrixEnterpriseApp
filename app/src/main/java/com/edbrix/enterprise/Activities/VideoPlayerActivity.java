package com.edbrix.enterprise.Activities;

import android.net.Uri;
import android.os.Bundle;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.edbrix.enterprise.Models.FileData;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.baseclass.BaseActivity;

public class VideoPlayerActivity extends BaseActivity implements EasyVideoCallback {

    EasyVideoPlayer player;
    String fileName;
    String fileUrl;
    FileData fData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        fData = (FileData) getIntent().getSerializableExtra("FileData");
        fileName = getIntent().getStringExtra("FileName");
        fileUrl = getIntent().getStringExtra("FileUrl");
        // Grabs a reference to the player view
        player = (EasyVideoPlayer) findViewById(R.id.player);

        // Sets the callback to this Activity, since it inherits EasyVideoCallback
        player.setCallback(this);

        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
//        player.setSource(Uri.parse("https://cdn.video.playwire.com/1010450/videos/5449631/video-sd.mp4"));

        if (fData != null) {
            player.setSource(Uri.fromFile(fData.getFileObject()));
            player.setBottomLabelText(fData.getFileName());
        } else if (fileUrl != null && !fileUrl.isEmpty()) {
            player.setSource(Uri.parse(fileUrl));
        } else {
            showToast("No file data found.");
        }
//        player.setSource(Uri.fromFile(fData.getFileObject()));

//        player.setBottomLabelText(fileName);

        // From here, the player view will show a progress indicator until the player is prepared.
        // Once it's prepared, the progress indicator goes away and the controls become enabled for the user to begin playback.

    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }
}
