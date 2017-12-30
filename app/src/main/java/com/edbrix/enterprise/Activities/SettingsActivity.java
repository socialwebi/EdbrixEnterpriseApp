package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edbrix.enterprise.MainActivity;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.RoundedImageView;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import permissions.dispatcher.NeedsPermission;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SettingsActivity extends BaseActivity {

    static final int REQUEST_PERMISSION_EXTERNAL = 1006;
    Context context;
    RoundedImageView _settings_image_profile_pic;
    TextView _settings_text_user_name;
    TextView _settings_text_user_email;
    LinearLayout _settings_linear_share;
    LinearLayout _settings_linear_edit_profile;
    LinearLayout _settings_linear_upload_pic;
    LinearLayout _settings_linear_logout;
    LinearLayout _settings_linear_change_password;
    ProgressBar _settings_progress;
    User user;
    private Uri filePath;
    private String fileExtension;
    private String fileName;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private ArrayList<String> photoPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = SettingsActivity.this;
        user = SettingsMy.getActiveUser();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://edbrixcbuilder.appspot.com");

        _settings_image_profile_pic = findViewById(R.id.settings_image_profile_pic);
        _settings_text_user_name = findViewById(R.id.settings_text_user_name);
        _settings_text_user_email = findViewById(R.id.settings_text_user_email);
        _settings_linear_edit_profile = findViewById(R.id.settings_linear_edit_profile);
        _settings_linear_upload_pic = findViewById(R.id.settings_linear_upload_pic);
        _settings_linear_change_password = findViewById(R.id.settings_linear_change_password);
        _settings_linear_logout = findViewById(R.id.settings_linear_logout);
        _settings_linear_share = findViewById(R.id.settings_linear_share);
        _settings_progress = findViewById(R.id.settings_progress);


        _settings_linear_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, EditProfileActivity.class);
//                startActivity(intent);
            }
        });

        _settings_linear_upload_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseClick();
            }
        });

        _settings_linear_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });


        _settings_linear_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.invitation_title);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out, Edbrix app " + "\n" +
                        "  " + "\n");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, " Choose an app "));

            }
        });


        _settings_linear_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout? ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SettingsMy.setActiveUser(null);

                                if (isTablet()) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        }).setNegativeButton("No", null).show();


            }
        });

        if (user != null) {
            String name = user.getFirstName() + " " + user.getLastName();
            _settings_text_user_name.setText(name);
            _settings_text_user_email.setText(user.getOrganizationName());

            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                Picasso.with(context)
                        .load(user.getProfileImageUrl())
                        .placeholder(R.mipmap.user_profile)
                        .error(R.mipmap.user_profile)
                        .into(_settings_image_profile_pic);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void browseClick() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            onPickPhoto();
        } else {
            EasyPermissions.requestPermissions(this,
                    "This app needs to access your Images.",
                    REQUEST_PERMISSION_EXTERNAL,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @AfterPermissionGranted(REQUEST_PERMISSION_EXTERNAL)
    public void onPickPhoto() {

        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.AppTheme)
                .enableVideoPicker(false)
                .enableCameraSupport(false)
                .enableImagePicker(true)
                .showGifs(false)
                .showFolderView(true)
                .enableSelectAll(false)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickPhoto(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    // mOutputText.setText(photoPaths.toString());
                    filePath = Uri.fromFile(new File(photoPaths.get(0)));

                    fileName = photoPaths.get(0).substring(photoPaths.get(0).lastIndexOf("/"));
                    fileName = fileName.replace("/", "");

                    fileExtension = photoPaths.get(0).substring(photoPaths.get(0).lastIndexOf("."));
                    Log.d("TAG", photoPaths.toString() + " _-_ " + fileExtension);
                    uploadToEdbrix(filePath);
                }
                break;
        }
    }

    private void uploadToEdbrix(final Uri fileUri) {
        try {

            if (fileUri != null) {
                _settings_progress.setVisibility(View.VISIBLE);
//                btnUpload.setVisibility(View.GONE);
//                btnCancel.setVisibility(View.VISIBLE);
//
//                mProgressBar.setVisibility(View.VISIBLE);
//                textPercentage.setVisibility(View.VISIBLE);
//                textPercentage.setText("");
//                userId = sessionManager.getLoggedUserData().getId();
//                accessToken = sessionManager.getSessionProfileToken();

                String userId = SettingsMy.getActiveUser().getId();// get user Id from active user
                StorageReference childRef = storageRef.child("enterprisecoursecontent/" + userId + "/" + fileName);
                //uploading the image
                uploadTask = childRef.putFile(fileUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        _settings_progress.setVisibility(View.GONE);
                        //showToast(taskSnapshot.getDownloadUrl().toString());
                        Log.d("TAG", taskSnapshot.getDownloadUrl().toString());

                        // save to server
                        Toast.makeText(context, "Upload successful ", Toast.LENGTH_SHORT).show();

                        Picasso.with(context)
                                .load(fileUri)
                                .placeholder(R.mipmap.user_profile)
                                .error(R.mipmap.user_profile)
                                .into(_settings_image_profile_pic);

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
                        _settings_progress.setVisibility(View.GONE);
                        Log.v("Upload", "Fail Exception :" + e.getMessage());
                        showToast(e.getMessage());
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

}
