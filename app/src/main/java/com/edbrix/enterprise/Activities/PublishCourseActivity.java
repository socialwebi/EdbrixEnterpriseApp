package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.FileListAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Interfaces.CourseContentButtonListener;
import com.edbrix.enterprise.Models.CourseContents;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.AddContentDialog;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.commons.GlobalMethods;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import permissions.dispatcher.NeedsPermission;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class PublishCourseActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    static final String courseIDKEY = "courseID";
    static final String courseTitleKEY = "courseTitle";
    static final String coursePriceKEY = "coursePrice";

    private TextView txtTitle;

    private TextView txtCourseCode;

    private RecyclerView contentListRecycler;

    private Button btnPublish;

    private String courseId = "";

    private String courseTitle;

    private String coursePrice;

    private FileListAdapter fileListAdapter;

    private FloatingActionsMenu fabAddContent;

    private FloatingActionButton fabRecordVideo;

    private FloatingActionButton fabAddVideo;

    private FloatingActionButton fabAddDoc;

    static final int REQUEST_PERMISSION_EXTERNAL = 1004;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    private String fileType;

    private ArrayList<String> photoPaths = new ArrayList<>();

    private ArrayList<String> docPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_course);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        courseId = getIntent().getStringExtra(courseIDKEY);
        courseTitle = getIntent().getStringExtra(courseTitleKEY);
        coursePrice = getIntent().getStringExtra(coursePriceKEY);

        txtTitle = toolbar.findViewById(R.id.title);
        txtCourseCode = findViewById(R.id.txtCourseCode);
        contentListRecycler = findViewById(R.id.contentListRecycler);
        contentListRecycler.setLayoutManager(new LinearLayoutManager(PublishCourseActivity.this));
        btnPublish = findViewById(R.id.btnPublish);

        fabAddContent = findViewById(R.id.fabAddContent);
        fabRecordVideo = findViewById(R.id.fabRecordVideo);
        fabAddVideo = findViewById(R.id.fabAddVideo);
        fabAddDoc = findViewById(R.id.fabAddDoc);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        txtTitle.setText(courseTitle);

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        fileListAdapter = new FileListAdapter(PublishCourseActivity.this, new ArrayList<CourseContents>(), new CourseContentButtonListener() {

            @Override
            public void onCourseDeleteClick(String id, int position) {
                setCourseContentDelete(id, position);
            }

            @Override
            public void onCoursePreviewClick(String id, String path) {

            }
        });

        contentListRecycler.setAdapter(fileListAdapter);

        getCourseDetails(courseId);

        fabRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabAddContent.collapse();
                fileType = AddContentDialog.OPT_RECORD_VIDEO;
                openFileChooser();
            }
        });

        fabAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabAddContent.collapse();
                fileType = AddContentDialog.OPT_ADD_VIDEO;
                openFileChooser();
            }
        });

        fabAddDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabAddContent.collapse();
                fileType = AddContentDialog.OPT_ADD_DOCUMENT;
                openFileChooser();
            }
        });

    }

    private void getCourseDetails(String courseId) {
        showBusyProgress();
        User activeUser = SettingsMy.getActiveUser();
        if (activeUser != null) {

            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", activeUser.getId());
                jo.put("AccessToken", activeUser.getAccessToken());
                jo.put("UserType", activeUser.getUserType());
                jo.put("CourseId", courseId);
            } catch (JSONException e) {
                return;
            }

            GsonRequest<Courses> getCourseDetailsRequest = new GsonRequest<>(Request.Method.POST, Constants.getCourseDetails, jo.toString(), Courses.class,
                    new Response.Listener<Courses>() {
                        @Override
                        public void onResponse(@NonNull Courses response) {

                            if (response.getErrorCode() == null) {
//                                courseDetailItem = response;
//                                if (courseDetailItem != null) {
//                                    //set Course Details
//                                    setCourseDetails();
//                                }else{
//                                    showToast(getString(R.string.error_something_wrong));
//                                }
//                                hideBusyProgress();
                                txtCourseCode.setText(response.getCode());
                                getCourseContent();
                            } else {
                                hideBusyProgress();
                                showToast(response.getErrorMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            getCourseDetailsRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getCourseDetailsRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getCourseDetailsRequest, "course_details_requests");
        }
    }

    private void getCourseContent() {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
//            showBusyProgress();
            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("CourseId", courseId);

            } catch (JSONException e) {
                Timber.e(e, "Parse create course exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Course: %s", jo.toString());

            GsonRequest<ResponseData> courseContentRequest = new GsonRequest<>(Request.Method.POST, Constants.getCourseContent, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {
                            hideBusyProgress();
                            if (response.getErrorCode() == null) {

                                if (response.getCourseContents() != null && response.getCourseContents().size() > 0) {
                                    fileListAdapter.refresh(response.getCourseContents());
                                    fileListAdapter.notifyDataSetChanged();
                                }

                            } else {
                                showToast(response.getErrorMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            courseContentRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            courseContentRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(courseContentRequest, "get_course_content_requests");

        }
    }

    private void setCourseContentDelete(String id, final int position) {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            showBusyProgress();
            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("CourseId", courseId);
                jo.put("CourseContentId", id);

            } catch (JSONException e) {
                Timber.e(e, "Parse create course exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Course: %s", jo.toString());

            GsonRequest<ResponseData> courseContentDeleteRequest = new GsonRequest<>(Request.Method.POST, Constants.setDeleteCourseContent, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {

                            hideBusyProgress();
                            if (response.getErrorCode() == null || response.getErrorCode().equals("0")) {
                                showToast("Content deleted successfully.");
                                fileListAdapter.deleteItemFromList(position);
                                fileListAdapter.notifyDataSetChanged();
                            } else {
                                showToast(response.getErrorMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            courseContentDeleteRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            courseContentDeleteRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(courseContentDeleteRequest, "get_course_content_requests");

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
        if (GlobalMethods.isCameraHardwareAvailable(PublishCourseActivity.this)) {

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

        Uri fileUri = Uri.fromFile(GlobalMethods.getOutputMediaFile(PublishCourseActivity.this,
                new File(Environment.getExternalStorageDirectory(), "/" + getResources().getString(R.string.app_name) + "/Video/")));

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3600);
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
                        Intent courseContent = new Intent(PublishCourseActivity.this, CreateCourseContentActivity.class);
                        courseContent.putExtra(CreateCourseContentActivity.contentTypeKEY, CreateCourseContentActivity.contentTypeVideo);
                        courseContent.putExtra(CreateCourseContentActivity.contentDataKEY, photoPaths.get(0));
                        courseContent.putExtra(CreateCourseContentActivity.courseIDKEY, courseId);
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
                        Intent courseContent = new Intent(PublishCourseActivity.this, CreateCourseContentActivity.class);
                        courseContent.putExtra(CreateCourseContentActivity.contentTypeKEY, CreateCourseContentActivity.contentTypeDoc);
                        courseContent.putExtra(CreateCourseContentActivity.contentDataKEY, docPaths.get(0));
                        courseContent.putExtra(CreateCourseContentActivity.courseIDKEY, courseId);
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

                        Intent courseContent = new Intent(PublishCourseActivity.this, CreateCourseContentActivity.class);
                        courseContent.putExtra(CreateCourseContentActivity.contentTypeKEY, CreateCourseContentActivity.contentTypeVideo);
                        courseContent.putExtra(CreateCourseContentActivity.contentDataKEY, data.getData().getPath());
                        courseContent.putExtra(CreateCourseContentActivity.courseIDKEY, courseId);
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
