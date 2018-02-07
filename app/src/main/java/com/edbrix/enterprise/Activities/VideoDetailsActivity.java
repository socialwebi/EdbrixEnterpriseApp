package com.edbrix.enterprise.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edbrix.enterprise.Models.FileData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Utils.SessionManager;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.commons.GlobalMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VideoDetailsActivity extends BaseActivity {

    private ImageView videoPreview;
    private ImageView playBtn;
    private ProgressBar vProgressBar;

    private TextView txtFileName;
    private TextView noContentText;

    private TextView saveBtn;
    private TextView renameBtn;
    private TextView shareBtn;
    private TextView uploadBtn;
    private TextView deleteBtn;

    private FileData fileData;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private String userId;
    private String accessToken;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);

        sessionManager = new SessionManager(VideoDetailsActivity.this);
        Bundle bundle = getIntent().getExtras();
//        title = (TextView) findViewById(R.id.title);
        fileData = (FileData) bundle.getSerializable("FileData");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://edbrixcbuilder.appspot.com");

        videoPreview = findViewById(R.id.videoPreview);
        playBtn = findViewById(R.id.playBtn);
        vProgressBar = findViewById(R.id.vProgressBar);

        txtFileName = findViewById(R.id.txtFileName);
        noContentText = findViewById(R.id.noContentText);

        saveBtn = findViewById(R.id.saveBtn);
        renameBtn = findViewById(R.id.renameBtn);
        shareBtn = findViewById(R.id.shareBtn);
        uploadBtn = findViewById(R.id.uploadBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        //set video uri to video view
        if (fileData != null && fileData.getFileObject() != null) {
            String fName = fileData.getFileName().replaceAll(".mp4", "");
//            title.setText(fName);
            txtFileName.setText(fName);
            noContentText.setVisibility(View.GONE);
            Bitmap thumb = GlobalMethods.getVideoThumbnailFromPath(fileData.getFileObject().getPath());
            videoPreview.setImageBitmap(thumb);
            playBtn.setVisibility(View.VISIBLE);
        } else {
            noContentText.setVisibility(View.VISIBLE);
        }

        setListener();

    }

    private void setListener(){
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVideoPlayer();
            }
        });
    }

    private void openVideoPlayer() {
        Intent videoPlayer = new Intent(VideoDetailsActivity.this, VideoPlayerActivity.class);
        videoPlayer.putExtra("FileData", fileData);
        startActivity(videoPlayer);
    }

    /**
     * upload file to Edbrix cloud storage
     */
    private void uploadToEdbrix() {
        try {

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

                StorageReference childRef = storageRef.child("media/" + userId + "/" + fileData.getFileName());
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
                        uploadVideoToMyFiles(userId, accessToken, fileData.getFileName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("Upload", "Fail Exception :" + e.getMessage());
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
                showToast("No file found");
            }
        } catch (Exception e) {
            Log.v("VideoDetailsActivity", e.getMessage());
        }

    }

    /**
     * Upload files to Edbrix Instructor's  my files
     *
     * @param userId
     * @param accessToken
     * @param fileName
     */
    private void uploadVideoToMyFiles(final String userId, final String accessToken, final String fileName) {

        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("UserId", userId);
        requestMap.put("AccessToken", accessToken);
        requestMap.put("FileName", fileName);


        try {
            JsonObjectRequest uploadVideo = new JsonObjectRequest(Request.Method.POST, Constants.uploadVideoToMyFiles, new JSONObject(requestMap), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    btnUpload.setEnabled(true);
//                    btnUpload.setVisibility(View.VISIBLE);
//                    btnCancel.setVisibility(View.GONE);
//
//                    mProgressBar.setVisibility(View.GONE);
//                    textPercentage.setText("File Uploaded successfully.");
//                    sessionManager.updateVideoUploadCount(sessionManager.getVideoUploadCount() + 1);
                    Log.v("Volley Response", response.toString());
                    try {
                        if (response != null) {
                            if (response.has("Success")) {
//                                textPercentage.setText("Upload successful");
                            } else if (response.has("Error")) {
//                                textPercentage.setText("Error in web service.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.v("Volley Excep", e.getMessage());
//                        textPercentage.setText(getResources().getString(R.string.error_message));
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    btnUpload.setEnabled(true);
//                    btnUpload.setVisibility(View.VISIBLE);
//                    btnCancel.setVisibility(View.GONE);
//
//                    mProgressBar.setVisibility(View.GONE);
                    showToast(SettingsMy.getErrorMessage(error));

//                    textPercentage.setText(VolleySingleton.getErrorMessage(error));
                }

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

            };

//            VolleySingleton.getInstance().addToRequestQueue(uploadVideo);
        } catch (Exception e) {
//            btnUpload.setEnabled(true);
//            btnUpload.setVisibility(View.VISIBLE);
//            btnCancel.setVisibility(View.GONE);
//
//            mProgressBar.setVisibility(View.GONE);
            Log.v("Volley Excep", e.getMessage());
//            textPercentage.setText(getResources().getString(R.string.error_message));
        }
    }
}
