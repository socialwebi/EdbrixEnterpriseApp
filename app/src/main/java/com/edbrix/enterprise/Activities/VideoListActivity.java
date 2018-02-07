package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.edbrix.enterprise.Adapters.VideoListAdapter;
import com.edbrix.enterprise.Models.FileData;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Conditions;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.commons.GlobalMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import permissions.dispatcher.NeedsPermission;
import pub.devrel.easypermissions.EasyPermissions;


public class VideoListActivity extends BaseActivity implements VideoListAdapter.OnButtonActionListener {

    static final int REQUEST_PERMISSION_EXTERNAL = 1004;

    private RecyclerView recyclerViewVideoList;
    private ArrayList<FileData> fileDataList;

    private VideoListAdapter videoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerViewVideoList = findViewById(R.id.recyclerViewVideoList);

        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            getRecordedVideoListFromLocalStorage();
        } else {
            EasyPermissions.requestPermissions(this,
                    "This app needs to access your Videos.",
                    REQUEST_PERMISSION_EXTERNAL,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.refreshOption:
                refreshVideoList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshVideoList() {
        if (Conditions.isNetworkConnected(VideoListActivity.this)) {
            videoListAdapter.refresh(null);
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                getRecordedVideoListFromLocalStorage();
            } else {
                EasyPermissions.requestPermissions(this,
                        "This app needs to access your Videos.",
                        REQUEST_PERMISSION_EXTERNAL,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        } else {
            showToast(getString(R.string.error_network));
        }
    }


    /**
     * Get recorded video list from apps local storage
     */
    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    private void getRecordedVideoListFromLocalStorage() {
        fileDataList = new ArrayList<>();
        File yourDir = GlobalMethods.getAppVideoStorageDirectory(VideoListActivity.this);
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
            videoListAdapter = new VideoListAdapter(VideoListActivity.this, fileDataList, this);
            recyclerViewVideoList.setLayoutManager(new LinearLayoutManager(VideoListActivity.this));
            recyclerViewVideoList.setAdapter(videoListAdapter);
            recyclerViewVideoList.setVisibility(View.VISIBLE);
//            swipeRefreshLayout.setRefreshing(false);
        } else {
            recyclerViewVideoList.setVisibility(View.GONE);
//            swipeRefreshLayout.setRefreshing(false);
            showToast("No recorded video found", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onDeleteButtonPressed(FileData fileData, int position) {

    }

    @Override
    public void onCardViewClicked(FileData fileData, int position) {
        Intent videoDetail = new Intent(VideoListActivity.this, VideoDetailsActivity.class);
        videoDetail.putExtra("FileData",fileData);
        startActivity(videoDetail);

    }
}
