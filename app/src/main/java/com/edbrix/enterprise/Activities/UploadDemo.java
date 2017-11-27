package com.edbrix.enterprise.Activities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UploadDemo extends BaseActivity {

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private Button uploadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_demo);
        uploadBtn = findViewById(R.id.btnUploadFile);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://edbrixcbuilder.appspot.com");

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call upload method
            }
        });
    }

    private void uploadToEdbrix(File uploadFile) {
        try {

            Uri fileUri = Uri.fromFile(uploadFile);
            if (fileUri != null) {
//                btnUpload.setVisibility(View.GONE);
//                btnCancel.setVisibility(View.VISIBLE);
//
//                mProgressBar.setVisibility(View.VISIBLE);
//                textPercentage.setVisibility(View.VISIBLE);
//                textPercentage.setText("");
//                userId = sessionManager.getLoggedUserData().getId();
//                accessToken = sessionManager.getSessionProfileToken();

                String userId=""+ SettingsMy.getActiveUser().getId();// get user Id from active user
                StorageReference childRef = storageRef.child("enterprisecoursecontent/" + userId + "/" + uploadFile.getName());
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
//                        uploadVideoToMyFiles(userId, accessToken, fileData.getFileName());
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
                showToast("NO file found");
            }
        } catch (Exception e) {
            Log.v("Upload", e.getMessage());
        }

    }
}
