package com.edbrix.enterprise.Utils;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.edbrix.enterprise.Adapters.VideoPlayerListAdapter;
import com.edbrix.enterprise.Models.FileData;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.commons.GlobalMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import permissions.dispatcher.NeedsPermission;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by rajk on 09/02/18.
 */

public class VideoPlayerWithListDialog extends Dialog implements EasyVideoCallback {

    static final int REQUEST_PERMISSION_EXTERNAL = 1004;

    private EasyVideoPlayer videoPlayer;
    private FileData fData;

    private RecyclerView recyclerViewVideoList;
    private ArrayList<FileData> fileDataList;

    private VideoPlayerListAdapter videoListAdapter;

    private Context dContext;

    private OnActionButtonListener onActionButtonListener;

    public interface OnActionButtonListener {
        void onOptionPressed(String optionType);
    }

    public void setOnActionButtonListener(OnActionButtonListener listener) {
        this.onActionButtonListener = listener;
    }

    public VideoPlayerWithListDialog(@NonNull Context context, @StyleRes int themeResId, FileData fileData) {
        super(context, themeResId);
        setContentView(R.layout.dialog_video_player_with_list);
        setCancelable(true);
        this.fData = fileData;
        this.dContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoPlayer = (EasyVideoPlayer) findViewById(R.id.videoPlayer);
        recyclerViewVideoList = (RecyclerView) findViewById(R.id.videoList);

        // Sets the callback to this Activity, since it inherits EasyVideoCallback
        videoPlayer.setCallback(this);

        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
//        player.setSource(Uri.parse("https://cdn.video.playwire.com/1010450/videos/5449631/video-sd.mp4"));

        getRecordedVideoListFromLocalStorage();
        setVideoPlayer();
    }

    private void setVideoPlayer() {

        if (fData != null) {
            videoPlayer.setVisibility(View.VISIBLE);
            videoPlayer.setSource(Uri.fromFile(fData.getFileObject()));
            videoPlayer.setBottomLabelText(fData.getFileName());
        } else {
            videoPlayer.setVisibility(View.INVISIBLE);
            /*if (!fileDataList.isEmpty()) {
                fData = fileDataList.get(0);
                videoPlayer.setSource(Uri.fromFile(fData.getFileObject()));
                videoPlayer.setBottomLabelText(fData.getFileName());
            }*/
        }
    }

    /**
     * Get recorded video list from apps local storage
     */
    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    private void getRecordedVideoListFromLocalStorage() {
        fileDataList = new ArrayList<>();
        File yourDir = GlobalMethods.getAppVideoStorageDirectory(dContext);
        for (File file : yourDir.listFiles()) {
            Log.v("VideoList", "File MIME Type:" + GlobalMethods.getMimeType(file));
            Log.v("VideoList", "File MIME Ext:" + GlobalMethods.getExtension(file.getPath()));
            if (file.isFile()) {
                if (GlobalMethods.getMimeType(file).equals("video/mp4") && GlobalMethods.getExtension(file.getPath()).equals(".mp4")) {
                    fileDataList.add(new FileData(file));
                }
            }
        }

        Collections.reverse(fileDataList);

        if (fileDataList != null && fileDataList.size() > 0) {
            videoListAdapter = new VideoPlayerListAdapter(dContext, fileDataList, new VideoPlayerListAdapter.OnButtonActionListener() {
                @Override
                public void onCardViewClicked(FileData fileData, int position) {
                    fData = fileData;
                    setVideoPlayer();
                }
            });
            recyclerViewVideoList.setLayoutManager(new LinearLayoutManager(dContext));
            recyclerViewVideoList.setAdapter(videoListAdapter);
            recyclerViewVideoList.setVisibility(View.VISIBLE);
//            swipeRefreshLayout.setRefreshing(false);
        } else {
            recyclerViewVideoList.setVisibility(View.GONE);
//            swipeRefreshLayout.setRefreshing(false);
//            showToast("No recorded video found", Toast.LENGTH_SHORT);
        }
    }


    @Override
    public void onAttachedToWindow() {
        getWindow().setBackgroundDrawableResource(R.color.ColorBlackTransparent);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        onActionButtonListener.onOptionPressed("");
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
