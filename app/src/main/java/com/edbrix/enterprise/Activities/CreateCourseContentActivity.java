package com.edbrix.enterprise.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.JsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import timber.log.Timber;

public class CreateCourseContentActivity extends BaseActivity {

    static final String contentTypeKEY = "contentType";
    static final String contentDataKEY = "contentData";
    static final String courseTitleKEY = "courseTitle";
    static final String coursePriceKEY = "coursePrice";
    static final String contentTypeVideo = "contentVideo";
    static final String contentTypeDoc = "contentDoc";

    private LinearLayout lnrAddedVideo;
    private LinearLayout lnrAddedDoc;

    private EditText edtContentTitle;

    private TextView txtVideoName;
    private TextView txtDocName;
    private TextView txtPercentage;

    private Button btnSaveCourse;

    private String courseTitle;
    private String coursePrice;
    private String contentType;
    private String filePathString;
    private String fileName;
    private String fileTypeVal;
    private String fileExtention;

    private File contentFile;

    private Uri contentFileUri;

    private ProgressBar mProgressBar;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private UploadTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course_content);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://edbrixcbuilder.appspot.com");

        lnrAddedVideo = findViewById(R.id.lnrAddedVideo);
        lnrAddedDoc = findViewById(R.id.lnrAddedDoc);
        txtVideoName = findViewById(R.id.txtVideoName);
        txtDocName = findViewById(R.id.txtDocName);
        edtContentTitle = findViewById(R.id.edtContentTitle);
        btnSaveCourse = findViewById(R.id.btnSaveCourse);
        mProgressBar = findViewById(R.id.mProgressBar);
        txtPercentage = findViewById(R.id.txtPercentage);

        courseTitle = getIntent().getStringExtra(courseTitleKEY);
        coursePrice = getIntent().getStringExtra(coursePriceKEY);

        contentType = getIntent().getStringExtra(contentTypeKEY);
        filePathString = getIntent().getStringExtra(contentDataKEY);


        contentFile = new File(filePathString);

        contentFileUri = Uri.fromFile(contentFile);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setContent();

        btnSaveCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });
    }


    private void setContent() {
        if (contentFile != null) {
            if (contentType.equals(contentTypeVideo)) {
                lnrAddedVideo.setVisibility(View.VISIBLE);
                txtVideoName.setText(contentFile.getName().substring(0, contentFile.getName().lastIndexOf('.')));
                fileTypeVal = Constants.FileType_Playwire;

            } else {
                lnrAddedDoc.setVisibility(View.VISIBLE);
                txtDocName.setText(contentFile.getName().substring(0, contentFile.getName().lastIndexOf('.')));
                fileTypeVal = Constants.FileType_Document;
            }
            fileExtention = contentFile.getName().substring(contentFile.getName().lastIndexOf('.'));
        }
    }


    private void checkValidations() {
        fileName = edtContentTitle.getText().toString().trim();

        if (fileName.isEmpty()) {
            edtContentTitle.setError(getString(R.string.error_edit_text));
        } else {
            uploadToEdbrix();
        }
    }

    private void uploadToEdbrix() {
        try {

            if (contentFileUri != null) {

                edtContentTitle.setEnabled(false);
                btnSaveCourse.setEnabled(false);
                btnSaveCourse.setText("Please wait..");

                mProgressBar.setVisibility(View.VISIBLE);
                txtPercentage.setVisibility(View.VISIBLE);
                txtPercentage.setText("");

                String userId = SettingsMy.getActiveUser().getId();// get user Id from active user
                StorageReference childRef = storageRef.child("enterprisecoursecontent/" + userId + "/" + fileName + fileExtention);

                uploadTask = childRef.putFile(contentFileUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        mProgressBar.setVisibility(View.GONE);
                        createCourse(courseTitle, coursePrice, 0, 0);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                        btnSaveCourse.setEnabled(true);
                        btnSaveCourse.setText(R.string.save_course);
                        edtContentTitle.setEnabled(true);
                        mProgressBar.setVisibility(View.GONE);
                        txtPercentage.setText("Upload Failed.");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        mProgressBar.setProgress((int) progress);
                        txtPercentage.setText("Uploading completed " + (int) progress + "%");
                    }
                });

            } else {
                showToast("NO file found");
            }
        } catch (Exception e) {
            Log.v("Upload", e.getMessage());
        }

    }

    private void createCourse(String title, String price, int courseId, int categoryId) {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            showBusyProgress();
            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("Title", title);
                jo.put("Price", price);
                jo.put("CourseId", courseId);
                jo.put("CategoryId", categoryId);

            } catch (JSONException e) {
                Timber.e(e, "Parse create course exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Course: %s", jo.toString());

            JsonRequest createCourseRequest = new JsonRequest(Request.Method.POST, Constants.setCreateCourse, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Timber.d("Response : %s", response.toString());
                    try {
                        if (response.getString("ErrorCode").equals("0")) {
                            addCourseContent(response.getString("id"));
                        } else {
                            showToast(response.getString("ErrorMessage"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast(getString(R.string.error_something_wrong));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            createCourseRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            createCourseRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(createCourseRequest, "create_course_requests");

        }
    }

    private void addCourseContent(final String courseId) {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("UserType", user.getUserType());
                jo.put("CourseId", courseId);
                jo.put("Title", fileName);
                jo.put("Type", fileTypeVal);
                jo.put("Content", fileName + fileExtention);

            } catch (JSONException e) {
                Timber.e(e, "Parse logInWithEmail exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Login user: %s", jo.toString());

            GsonRequest<ResponseData> addCourseContentRequest = new GsonRequest<>(Request.Method.POST, Constants.setCreateCourseContent, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {
                            Timber.d("response: %s", response.toString());
                            if (response.getErrorCode() == null) {
                                hideBusyProgress();
                                showToast(response.getMessage());
                                Intent publishIntent = new Intent(CreateCourseContentActivity.this, PublishCourseActivity.class);
                                publishIntent.putExtra(PublishCourseActivity.courseIDKEY, courseId);
                                publishIntent.putExtra(courseTitleKEY, courseTitle);
                                publishIntent.putExtra(coursePriceKEY, coursePrice);
                                startActivity(publishIntent);
                                finish();
                            } else {
                                hideBusyProgress();
                                showToast(response.getErrorMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            addCourseContentRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            addCourseContentRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(addCourseContentRequest, "create_course_content_requests");
        }
    }
}
