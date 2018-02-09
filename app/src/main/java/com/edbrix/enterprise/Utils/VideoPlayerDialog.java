package com.edbrix.enterprise.Utils;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.WindowManager;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.edbrix.enterprise.Models.FileData;
import com.edbrix.enterprise.R;

/**
 * Created by ganeshchaudhari on 09/02/18.
 */

public class VideoPlayerDialog extends Dialog implements EasyVideoCallback {

    private EasyVideoPlayer player;
    private String fileName;
    private String fileUrl;
    private FileData fData;

    private OnActionButtonListener onActionButtonListener;

    public interface OnActionButtonListener {
        void onOptionPressed(String optionType);
    }

    public void setOnActionButtonListener(OnActionButtonListener listener) {
        this.onActionButtonListener = listener;
    }

    public VideoPlayerDialog(@NonNull Context context, @StyleRes int themeResId, FileData fileData) {
        super(context, themeResId);
        setContentView(R.layout.dialog_video_player);
        setCancelable(true);
        this.fData = fileData;
    }

    public VideoPlayerDialog(@NonNull Context context, @StyleRes int themeResId, String fileName, String fileUrl) {
        super(context, themeResId);
        setContentView(R.layout.dialog_video_player);
        setCancelable(true);
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//            showToast("No file data found.");
        }
    }



    @Override
    public void onAttachedToWindow() {
        getWindow().setBackgroundDrawableResource(R.color.ColorBlackTransparent);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onActionButtonListener.onOptionPressed("");
        dismiss();
    }

    public void showMe() {
        this.show();
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
        onActionButtonListener.onOptionPressed("");
        dismiss();
    }
}
