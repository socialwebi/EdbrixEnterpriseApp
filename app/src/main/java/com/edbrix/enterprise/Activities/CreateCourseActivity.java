package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.edbrix.enterprise.Models.FileData;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.AddContentDialog;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.commons.GlobalMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import permissions.dispatcher.NeedsPermission;
import pub.devrel.easypermissions.EasyPermissions;

public class CreateCourseActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    static final int REQUEST_PERMISSION_EXTERNAL = 1004;


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    private EditText edtCourseName;
    private EditText edtCoursePrice;
    private Button btnNext;

    private String fileType;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();

    private String courseTitle;
    private String coursePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        courseTitle = "";
        coursePrice = "";

        edtCourseName = findViewById(R.id.edtCourseName);
        edtCoursePrice = findViewById(R.id.edtCoursePrice);
        btnNext = findViewById(R.id.btnNextCreateCourse);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });

        edtCourseName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (edtCourseName.getText().toString().trim().isEmpty()) {
                        edtCourseName.setError(getString(R.string.error_edit_text));
                        return true;
                    } else {
                        return false;
                    }

                }
                return false;
            }
        });

        edtCoursePrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtCoursePrice.getWindowToken(), 0);
                    //doLogin
                    checkValidations();
                    return true;
                }
                return false;
            }
        });
    }

    private void checkValidations() {
        courseTitle = edtCourseName.getText().toString().trim();
        coursePrice = edtCoursePrice.getText().toString().trim();

        edtCourseName.setError(null);

        if (courseTitle.isEmpty()) {
            edtCourseName.setError(getString(R.string.error_edit_text));
        } else {
            final AddContentDialog addContentDialog = new AddContentDialog(CreateCourseActivity.this, R.style.DialogAnimation);
            addContentDialog.setOnActionButtonListener(new AddContentDialog.OnActionButtonListener() {
                @Override
                public void onOptionPressed(String optionType) {
                    fileType = optionType;
                    openFileChooser();
                }
            });
            addContentDialog.showMe();
        }
    }

    private void openFileChooser() {

        switch (fileType) {
            case AddContentDialog.OPT_RECORD_VIDEO:
                getCamera();
                break;
            case AddContentDialog.OPT_ADD_VIDEO:
                getVideos();
                break;
            case AddContentDialog.OPT_ADD_DOCUMENT:
                getDocuments();
                break;
        }
    }

    private void getCamera() {
        if (GlobalMethods.isCameraHardwareAvailable(CreateCourseActivity.this)) {

            if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                recordVideo();
            } else {
                EasyPermissions.requestPermissions(this,
                        "This app needs to access your Camera.",
                        REQUEST_PERMISSION_EXTERNAL,
                        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        } else {
            showToast(getResources().getString(R.string.camera_not_found_error));
        }
    }

    private void getVideos() {

        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            onPickPhoto();
        } else {
            EasyPermissions.requestPermissions(this,
                    "This app needs to access your Videos.",
                    REQUEST_PERMISSION_EXTERNAL,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void getDocuments() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            onPickDoc();
        } else {
            EasyPermissions.requestPermissions(this,
                    "This app needs to access your Documents.",
                    REQUEST_PERMISSION_EXTERNAL,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }

    /**
     * Launching camera app to record video
     */
    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    private void recordVideo() {

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        Uri fileUri = Uri.fromFile(GlobalMethods.getOutputMediaFile(CreateCourseActivity.this,
                new File(Environment.getExternalStorageDirectory(), "/" + getResources().getString(R.string.app_name) + "/Video/")));

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 1800);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }


    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {

        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.AppTheme)
                .enableVideoPicker(true)
                .enableCameraSupport(true)
                .enableImagePicker(false)
                .showGifs(false)
                .showFolderView(true)
                .enableSelectAll(false)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickPhoto(this);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickDoc() {
        String[] ppts = {".ppt", ".pptx"};
        String[] pdfs = {".pdf"};
        String[] docs = {".doc", ".docx"};
        String[] xls = {".xls", ".xlsx"};
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(docPaths)
                .setActivityTheme(R.style.AppTheme)
                .addFileSupport("DOC", docs, R.mipmap.doc_icon)
                .addFileSupport("PDF", pdfs, R.mipmap.pdf_icon)
                .addFileSupport("PPT", ppts, R.mipmap.ppt_icon)
                .addFileSupport("XLS", xls, R.mipmap.xls_icon)
                .enableDocSupport(false)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickFile(this);
    }


    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    // mOutputText.setText(photoPaths.toString());
                    if (!photoPaths.isEmpty()) {
                        Intent courseContent = new Intent(CreateCourseActivity.this, CreateCourseContentActivity.class);
                        courseContent.putExtra(CreateCourseContentActivity.contentTypeKEY, CreateCourseContentActivity.contentTypeVideo);
                        courseContent.putExtra(CreateCourseContentActivity.contentDataKEY, photoPaths.get(0));
                        courseContent.putExtra(CreateCourseContentActivity.courseIDKEY, "0");
                        courseContent.putExtra(CreateCourseContentActivity.courseTitleKEY, courseTitle);
                        courseContent.putExtra(CreateCourseContentActivity.coursePriceKEY, coursePrice);
                        startActivityForResult(courseContent, 205);
                    }
                }
                break;

            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    //mOutputText.setText(docPaths.toString());
                    if (!docPaths.isEmpty()) {
                        Intent courseContent = new Intent(CreateCourseActivity.this, CreateCourseContentActivity.class);
                        courseContent.putExtra(CreateCourseContentActivity.contentTypeKEY, CreateCourseContentActivity.contentTypeDoc);
                        courseContent.putExtra(CreateCourseContentActivity.contentDataKEY, docPaths.get(0));
                        courseContent.putExtra(CreateCourseContentActivity.courseIDKEY, "0");
                        courseContent.putExtra(CreateCourseContentActivity.courseTitleKEY, courseTitle);
                        courseContent.putExtra(CreateCourseContentActivity.coursePriceKEY, coursePrice);
                        startActivityForResult(courseContent, 205);
                    }
                }
                break;
            case CAMERA_CAPTURE_VIDEO_REQUEST_CODE:

                if (resultCode == RESULT_OK) {

                    // video successfully recorded
                    if (data != null && data.getData() != null) {

//                        FileData fileData = new FileData(new File(data.getData().getPath()));
//                        Intent recordPreviewIntent = new Intent(CreateCourseActivity.this, RecordPreviewActivity.class);
//                        recordPreviewIntent.putExtra("FileUri", data.getData());
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("FileData", fileData);
//                        recordPreviewIntent.putExtras(bundle);
//
//                        startActivityForResult(recordPreviewIntent, RecordPreviewActivity.REQUEST_CODE);

                        Intent courseContent = new Intent(CreateCourseActivity.this, CreateCourseContentActivity.class);
                        courseContent.putExtra(CreateCourseContentActivity.contentTypeKEY, CreateCourseContentActivity.contentTypeVideo);
                        courseContent.putExtra(CreateCourseContentActivity.contentDataKEY, data.getData().getPath());
                        courseContent.putExtra(CreateCourseContentActivity.courseIDKEY, "0");
                        courseContent.putExtra(CreateCourseContentActivity.courseTitleKEY, courseTitle);
                        courseContent.putExtra(CreateCourseContentActivity.coursePriceKEY, coursePrice);
                        startActivityForResult(courseContent, 205);

                    } else {
                        // failed to record video
                        showToast("Sorry! Unable to fetch recorded video data.");
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled recording
                    showToast("User cancelled video recording");

                } else {
                    // failed to record video
                    showToast("Sorry! Failed to record video");
                }

                break;

            case 205:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
        openFileChooser();
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }
}
