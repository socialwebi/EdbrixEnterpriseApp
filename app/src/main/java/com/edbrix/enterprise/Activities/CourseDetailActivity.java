package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.JsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import permissions.dispatcher.NeedsPermission;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class CourseDetailActivity extends BaseActivity {

    public static final String courseDetailBundleKey = "courseDetailItem";

    static final int REQUEST_PERMISSION_EXTERNAL = 1006;

    private Courses courseDetailItem;

    private TextView title;
    private TextView txtCourseBy;
    private WebView courseDesc;

    private ImageView courseImage;
    private ImageView btnCoursePlay;
    private ImageView btnCourseMsg;
    private ImageView btnCourseCall;
    private Button btnCourseStart;
    private android.support.design.widget.FloatingActionButton fabCamera;

    private FloatingActionsMenu fabEditMenu;

    private FloatingActionButton fabEditCourse;

    private FloatingActionButton fabAddContent;

    private Context context;

    private boolean isUpdated;

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
        setContentView(R.layout.activity_course_detail);
        context = CourseDetailActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        title = (TextView) toolbar.findViewById(R.id.title);
        txtCourseBy = (TextView) findViewById(R.id.txtCourseBy);
        courseDesc = (WebView) findViewById(R.id.txtCourseDesc);
        fabCamera =  findViewById(R.id.fabCamera);
        fabEditMenu =findViewById(R.id.fabEditMenu);
        fabEditCourse = findViewById(R.id.fab_edit_course);
        fabAddContent = findViewById(R.id.fab_add_content);

        courseImage = (ImageView) findViewById(R.id.courseImage);
        btnCoursePlay = (ImageView) findViewById(R.id.btnCoursePlay);
        btnCourseMsg = (ImageView) findViewById(R.id.btnCourseMsg);
        btnCourseCall = (ImageView) findViewById(R.id.btnCourseCall);
        btnCourseStart = (Button) findViewById(R.id.btnCourseStart);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://edbrixcbuilder.appspot.com");

        btnCourseStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (courseDetailItem != null) {
                    Intent playCourse = new Intent(context, PlayCourseActivity.class);
                    playCourse.putExtra(PlayCourseActivity.courseItemBundleKey, courseDetailItem);
                    startActivity(playCourse);
                } else {

                }
            }
        });

        btnCoursePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (courseDetailItem != null) {
                    Intent playCourse = new Intent(context, PlayCourseActivity.class);
                    playCourse.putExtra(PlayCourseActivity.courseItemBundleKey, courseDetailItem);
                    startActivity(playCourse);
                } else {

                }
            }
        });

        btnCourseCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialContactPhone((String) view.getTag());
            }
        });

        btnCourseMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS((String) view.getTag(), "Hi..,");
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseClick();
            }
        });

        fabEditCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabEditMenu.collapse();
                goToEditCourse();
            }
        });

        fabAddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabEditMenu.collapse();
                goToCourseContent();
            }
        });

        courseDetailItem = (Courses) getIntent().getSerializableExtra(courseDetailBundleKey);

       /* if (courseDetailItem != null) {
            //set Course Details
            setCourseDetails();
        } else {
            showToast(getString(R.string.error_something_wrong));
        }*/

        getCourseDetails(courseDetailItem.getId());
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.editOption:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

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
                                courseDetailItem = response;
                                if (courseDetailItem != null) {
                                    //set Course Details
                                    setCourseDetails();
                                } else {
                                    showToast(getString(R.string.error_something_wrong));
                                }
                                hideBusyProgress();
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

    private void goToCourseContent() {
        Intent publishIntent = new Intent(CourseDetailActivity.this, PublishCourseActivity.class);
        publishIntent.putExtra(PublishCourseActivity.courseIDKEY, courseDetailItem.getId());
        publishIntent.putExtra(PublishCourseActivity.courseTitleKEY, courseDetailItem.getTitle());
        publishIntent.putExtra(PublishCourseActivity.coursePriceKEY, courseDetailItem.getPrice());
        startActivityForResult(publishIntent, 205);
        setResult(RESULT_OK);
        finish();
    }


    private void goToEditCourse() {
        if (courseDetailItem != null) {
            Intent intent = new Intent(CourseDetailActivity.this, EditCourseActivity.class);
            intent.putExtra(EditCourseActivity.courseIDKEY, courseDetailItem.getId());
            startActivityForResult(intent, 205);
            setResult(RESULT_OK);
            finish();
        }
    }

    private void setCourseDetails() {
        title.setText(courseDetailItem.getTitle());
        txtCourseBy.setText("By " + courseDetailItem.getInstructor_name());
        if (courseDetailItem.getDescription() != null) {
            String justifiedDesc = "<html><body style=\"text-align:justify;font-family:Open Sans;\">" + courseDetailItem.getDescription() + " </body></html>";
            courseDesc.loadData(justifiedDesc, "text/html", "utf-8");
        }
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            courseDesc.setText(Html.fromHtml(justifiedDesc,Html.FROM_HTML_MODE_LEGACY));
//        } else {
//            courseDesc.setText(Html.fromHtml(justifiedDesc));
//        }

//        courseDesc.setText(courseDetailItem.getDescription());
        btnCourseCall.setTag(courseDetailItem.getInstructor_mobileno());
        btnCourseMsg.setTag(courseDetailItem.getInstructor_mobileno());


//        String welcomStr ="\u003Cp style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-stretch: inherit; line-height: 20px; font-family: &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, arial, sans-serif; vertical-align: baseline; color: black !important;\"\u003EThe Internet, linking your computer to other computers around the world, is a way of transporting content. The Web is software that lets you use that content&hellip;or contribute your own. The Web, running on the mostly invisible Internet, is what you see and click on in your computer&rsquo;s browser.\u003C\\/p\u003E\r\n\r\n\u003Cp style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-stretch: inherit; line-height: 20px; font-family: &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, arial, sans-serif; vertical-align: baseline; color: black !important;\"\u003EThe Internet&rsquo;s roots are in the U.S. during the late 1960s. The Web was invented 20 years later by an Englishman working in Switzerland&mdash;though it had many predecessors.\u003C\\/p\u003E\r\n\r\n\u003Cp style=\"margin: 0px 0px 1em; padding: 0px; border: 0px; font-variant-numeric: inherit; font-stretch: inherit; line-height: 20px; font-family: &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, arial, sans-serif; vertical-align: baseline; color: black !important;\\\"\\u003ETo keep things &ldquo;interesting,&rdquo; many people use the term Internet to refer to both.\u003C\\/p\u003E\r\n";
//                courseDesc.setText(Html.fromHtml(welcomStr));
        if (courseDetailItem.getCourse_image_url() != null && !courseDetailItem.getCourse_image_url().isEmpty()) {
            Picasso.with(context)
                    .load(courseDetailItem.getCourse_image_url())
                    .error(R.drawable.edbrix_logo)
                    .into(courseImage);
        }

        if (SettingsMy.getActiveUser().getUserType().equals("L")) {
            fabEditMenu.setVisibility(View.GONE);
            fabCamera.setVisibility(View.GONE);
            btnCourseStart.setVisibility(View.VISIBLE);
            btnCourseMsg.setVisibility(View.GONE);
            btnCourseCall.setVisibility(View.GONE);
            btnCoursePlay.setVisibility(View.GONE);
            btnCourseStart.setEnabled(courseDetailItem.isContentAvailable());

        } else {
            fabEditMenu.setVisibility(View.VISIBLE);
            fabCamera.setVisibility(View.VISIBLE);
            btnCourseStart.setVisibility(View.GONE);
            btnCourseMsg.setVisibility(View.VISIBLE);
            btnCourseCall.setVisibility(View.VISIBLE);
            btnCoursePlay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Send intent to phone dialer
     *
     * @param phoneNumber phone number
     */
    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    /**
     * Send intent to messenger to send sms
     *
     * @param phoneNumber phone number
     * @param smsBody     sms body
     */
    private void sendSMS(String phoneNumber, String smsBody) {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body", smsBody);

        try {
            startActivity(smsIntent);
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("SMS faild, please try again later.");
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 205 && resultCode == RESULT_OK) {
//            getCourseDetails(courseDetailItem.getId());
//            isUpdated = true;
//        }
//
//    }

    @Override
    public void onBackPressed() {
        if (isUpdated) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
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
                .enableCameraSupport(true)
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

            case 205:
                if (resultCode == RESULT_OK) {
                    getCourseDetails(courseDetailItem.getId());
                    isUpdated = true;
                }
                break;
        }
    }

    private void uploadToEdbrix(final Uri fileUri) {
        try {

            if (fileUri != null) {
//                _settings_progress.setVisibility(View.VISIBLE);
//                btnUpload.setVisibility(View.GONE);
//                btnCancel.setVisibility(View.VISIBLE);
//
//                mProgressBar.setVisibility(View.VISIBLE);
//                textPercentage.setVisibility(View.VISIBLE);
//                textPercentage.setText("");
//                userId = sessionManager.getLoggedUserData().getId();
//                accessToken = sessionManager.getSessionProfileToken();
                showBusyProgress();
                String userId = SettingsMy.getActiveUser().getId();// get user Id from active user
                StorageReference childRef = storageRef.child("enterprisecoursecontent/" + userId + "/" + fileName);
                //uploading the image
                uploadTask = childRef.putFile(fileUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        _settings_progress.setVisibility(View.GONE);
                        //showToast(taskSnapshot.getDownloadUrl().toString());
                        Log.d("TAG", taskSnapshot.getDownloadUrl().toString());

                        // save to server

                        isUpdated = true;
                        Picasso.with(context)
                                .load(fileUri)
                                .placeholder(R.mipmap.edbrix)
                                .error(R.mipmap.edbrix)
                                .into(courseImage);

//                        btnUpload.setVisibility(View.VISIBLE);
//                        btnUpload.setEnabled(false);
//                        btnCancel.setVisibility(View.GONE);
//
//                        mProgressBar.setVisibility(View.GONE);
//                        uploadVideoToMyFiles(userId, accessToken, fileData.getFileName());
                        uploadCoursePic(courseDetailItem.getId(), fileName);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        _settings_progress.setVisibility(View.GONE);
                        hideBusyProgress();
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


    private void uploadCoursePic(String courseId, String fileName) {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            JSONObject jo = new JSONObject();
            try {

                jo.put("userid", user.getId());
                jo.put("accesstoken", user.getAccessToken());
                jo.put("UserType", user.getUserType());
                jo.put("courseid", courseId);
                jo.put("filename", fileName);

            } catch (JSONException e) {
                Timber.e(e, "Parse logInWithEmail exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Login user: %s", jo.toString());

            JsonRequest updateCourseImageRequest = new JsonRequest(Request.Method.POST, Constants.updateCoursePic, jo,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(@NonNull JSONObject response) {
                            Timber.d("response: %s", response.toString());
                            hideBusyProgress();
                            try {
                                if (response != null) {
                                    if (response.has("error")) {
                                        int error = response.getInt("error");
                                        if(error==0){
                                            isUpdated = true;
                                            showToast("Course image updated successfully");
                                        }else{
                                            isUpdated = false;
                                            showToast("Course image not updated successfully : "+response.getString("errorMessage"));
                                        }

                                    }
                                }
                            }catch (JSONException e){
                                showToast("Exception :"+e.getMessage());
                                e.printStackTrace();
                            }
                           /* if (response.getErrorCode() == null) {
                                hideBusyProgress();
                                showToast(response.getMessage());
                                isUpdated = true;
//                                Intent publishIntent = new Intent(CreateCourseContentActivity.this, PublishCourseActivity.class);
//                                publishIntent.putExtra(PublishCourseActivity.courseIDKEY, courseId);
//                                publishIntent.putExtra(courseTitleKEY, courseTitle);
//                                publishIntent.putExtra(coursePriceKEY, coursePrice);
//                                startActivity(publishIntent);
//                                finish();
                            } else {
                                hideBusyProgress();
                                isUpdated = false;
                                showToast(response.getErrorMessage());
                            }*/

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    isUpdated = false;
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            updateCourseImageRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            updateCourseImageRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(updateCourseImageRequest, "update_course_picture");
        }
    }

}
