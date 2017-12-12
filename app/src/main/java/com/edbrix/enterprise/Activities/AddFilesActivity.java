package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

@RuntimePermissions
public class AddFilesActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int REQUEST_PERMISSION_EXTERNAL = 1004;
    private static final String BUTTON_TEXT = "Call YouTube Data API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY};
    private final int RC_CAMERA_AND_LOCATION = 99;
    RelativeLayout layout;
    Context context;
    TextInputEditText _add_file_file_title;
    LinearLayout _add_file_progress_layout;
    TextView _add_file_progress_text;
    Button _add_file_button_youtube;
    Button _add_file_button_browse;
    Button _add_file_button_submit;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;
    private TextView mOutputText;
    private String fileType;
    private String fileTypeVal;
    private Uri filePath;
    private String fileName;
    private String fileExtension;
    private ImageView imageView;
    private int imageSize;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();
    private String title;
    private String price;
    private String courseId = "0";
    private String categoryId = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_files);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        price = intent.getStringExtra("price");
        fileType = intent.getStringExtra("type");
        courseId = intent.getStringExtra("courseId");

        if (fileType.equals("1")) {
            fileTypeVal = "playwire";
        } else {
            fileTypeVal = "document";
        }
        context = AddFilesActivity.this;

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://edbrixcbuilder.appspot.com");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        _add_file_file_title = findViewById(R.id.add_file_file_title);
        mOutputText = findViewById(R.id.add_file_text_message);
        _add_file_button_browse = findViewById(R.id.add_file_button_browse);
        _add_file_button_youtube = findViewById(R.id.add_file_button_youtube);
        imageView = findViewById(R.id.add_file_image_preview);
        _add_file_button_submit = findViewById(R.id.add_file_button_submit);
        _add_file_progress_layout = findViewById(R.id.add_file_progress_layout);
        _add_file_progress_text = findViewById(R.id.add_file_progress_text);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling YouTube Data API ...");

        setProgressBar(false, "Progress..");

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / 2;

        Log.d("TAG", fileType);
        _add_file_button_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileType.equals("1")) {
                    getVideos();
                } else {
                    getDocuments();
                }
            }
        });
        _add_file_button_youtube.setVisibility(View.GONE);
        /*_add_file_button_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _add_file_button_youtube.setEnabled(false);
                mOutputText.setText("");
                getResultsFromApi();
                _add_file_button_youtube.setEnabled(true);
            }
        });*/

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_video_layout);
                //dialog.setTitle("This is my custom dialog box");
                dialog.setCancelable(true);
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                final VideoView cusVideoView = dialog.findViewById(R.id.cus_video_view);
                cusVideoView.setVideoURI(filePath);
                cusVideoView.setZOrderOnTop(true);
                cusVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        cusVideoView.start();
                        mediaPlayer.setLooping(true);
                    }
                });

                //set up button
                ImageButton button = dialog.findViewById(R.id.cus_button_cancel);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it
                dialog.show();
            }
        });

        _add_file_button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });
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

    private void setProgressBar(Boolean val, String text) {
        if (val) {
            _add_file_progress_layout.setVisibility(View.VISIBLE);
            _add_file_progress_text.setText(text);
            _add_file_button_submit.setText("Please wait..");
            _add_file_button_submit.setClickable(false);
        } else {
            _add_file_progress_layout.setVisibility(View.GONE);
            _add_file_button_submit.setText("Submit");
            _add_file_button_submit.setClickable(true);
        }
    }

    private void checkValidations() {
        fileName = _add_file_file_title.getText().toString().trim();

        if (fileName.isEmpty()) {
            _add_file_file_title.setError(getString(R.string.error_edit_text));
        } else if (filePath.toString().isEmpty()) {
            _add_file_file_title.setError(null);
            try {
                Snackbar.make(layout, getString(R.string.error_files), Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddFilesActivity.this, getString(R.string.error_files), Toast.LENGTH_SHORT).show();
            }

        } else {
            /*Intent intent = new Intent(AddFilesActivity.this, CreateVideoCourseActivity.class);
            startActivity(intent);
            finish();*/
            uploadToEdbrix(filePath);
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

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {

        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.AppTheme)
                .enableVideoPicker(true)
                .enableCameraSupport(false)
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


    private void uploadToEdbrix(Uri fileUri) {
        try {

            if (fileUri != null) {

//                btnUpload.setVisibility(View.GONE);
//                btnCancel.setVisibility(View.VISIBLE);
//
//                mProgressBar.setVisibility(View.VISIBLE);
//                textPercentage.setVisibility(View.VISIBLE);
//                textPercentage.setText("");
//                userId = sessionManager.getLoggedUserData().getId();
//                accessToken = sessionManager.getSessionProfileToken();
                setProgressBar(true, "Uploading files..");

                String userId = SettingsMy.getActiveUser().getId();// get user Id from active user
                StorageReference childRef = storageRef.child("enterprisecoursecontent/" + userId + "/" + fileName + fileExtension);
                //uploading the image
                uploadTask = childRef.putFile(fileUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        //showToast(taskSnapshot.getDownloadUrl().toString());
                        setProgressBar(false, "Uploading files..");
                        Log.d("TAG", taskSnapshot.getDownloadUrl().toString());
                        if (courseId.equals("0")) {
                            setCourse();
                        } else {
                            setCourseContent(courseId);
                        }
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
                        setProgressBar(false, "Uploading file..");
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
                        setProgressBar(true, "Uploading completed " + (int) progress + "%");
                    }
                });

            } else {
                showToast("NO file found");
            }
        } catch (Exception e) {
            Log.v("Upload", e.getMessage());
        }

    }

    private void setCourse() {

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            setProgressBar(true, "Creating course..");
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

            JsonRequest req = new JsonRequest(Request.Method.POST, Constants.setCreateCourse, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Timber.d("Response : %s", response.toString());
                    setProgressBar(false, "Creating course..");
                    try {
                        if (response.getString("ErrorCode").equals("0")) {

                            editor.putString("newCourseId", courseId);
                            editor.apply();

                            Timber.d("Disclaimer : %s", response.getString("id"));
                            courseId = response.getString("id");
                            setCourseContent(courseId);
                        } else {
                            try {
                                Snackbar.make(layout, response.getString("ErrorMessage"), Snackbar.LENGTH_LONG).show();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                Toast.makeText(context, response.getString("ErrorMessage"), Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setProgressBar(false, "Creating course..");
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            req.setRetryPolicy(Application.getDefaultRetryPolice());
            req.setShouldCache(false);
            Application.getInstance().addToRequestQueue(req, "create_course_requests");

        }
    }

    private void setCourseContent(final String courseId) {

        User user = SettingsMy.getActiveUser();

        if (user != null) {
            setProgressBar(true, "Updating course..");
            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("UserType", user.getUserType());
                jo.put("CourseId", courseId);
                jo.put("Title", fileName);
                jo.put("Type", fileTypeVal);
                jo.put("Content", fileName + fileExtension);

            } catch (JSONException e) {
                Timber.e(e, "Parse logInWithEmail exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Login user: %s", jo.toString());

            GsonRequest<ResponseData> userChangePasswordRequest = new GsonRequest<>(Request.Method.POST, Constants.setCreateCourseContent, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {
                            Timber.d("response: %s", response.toString());
                            setProgressBar(false, "Updating course..");
                            if (response.getErrorCode() == null) {


                                new AlertDialog.Builder(context)
                                        .setTitle("Confirmation")
                                        .setMessage("Do you want to add more files? ")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                _add_file_file_title.setText(null);
                                                fileName = "";
                                                fileExtension = "";
                                                filePath = Uri.EMPTY;
                                                imageView.setVisibility(View.INVISIBLE);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                Intent intent = new Intent();
                                                intent.putExtra("newCourseId", courseId);
                                                setResult(1, intent);
                                                finish();

                                            }
                                        })
                                        .show();
                            } else {
                                try {
                                    Timber.d("Error: %s", response.getErrorMessage());
                                    Snackbar.make(layout, response.getErrorMessage(), Snackbar.LENGTH_LONG).show();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    Timber.d("Error: %s", response.getErrorMessage());
                                    Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setProgressBar(false, "Updating course..");
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            userChangePasswordRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            userChangePasswordRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(userChangePasswordRequest, "create_course_content_requests");
        } else {
            setProgressBar(false, "Updating course..");
        }
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
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

            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;

            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;

            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    // mOutputText.setText(photoPaths.toString());
                    filePath = Uri.fromFile(new File(photoPaths.get(0)));

                    imageView.setVisibility(View.VISIBLE);

                    Glide.with(context).load(filePath)
                            .apply(RequestOptions
                                    .centerCropTransform()
                                    .dontAnimate()
                                    .override(imageSize, imageSize)
                                    .placeholder(droidninja.filepicker.R.drawable.image_placeholder))
                            .thumbnail(0.5f)
                            .into(imageView);

                    imageView.setClickable(true);
                    fileExtension = photoPaths.get(0).substring(photoPaths.get(0).lastIndexOf("."));
                    Log.d("TAG", photoPaths.toString() + " _-_ " + fileExtension);
                    // uploadToEdbrix(uri);
                }
                break;

            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    //mOutputText.setText(docPaths.toString());
                    filePath = Uri.fromFile(new File(docPaths.get(0)));
                    Glide.with(context).load(filePath)
                            .apply(RequestOptions
                                    .centerCropTransform()
                                    .dontAnimate()
                                    .override(imageSize, imageSize)
                                    .placeholder(R.mipmap.document_icon))
                            .thumbnail(0.5f)
                            .into(imageView);

                    imageView.setClickable(false);
                    fileExtension = docPaths.get(0).substring(docPaths.get(0).lastIndexOf("."));
                    Log.d("TAG", docPaths.toString() + " _-_ " + fileExtension);
                    // uploadToEdbrix(uri);
                }
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
        if (fileType.equals("1")) {
            getVideos();
        } else {
            getDocuments();
        }
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

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                AddFilesActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call YouTube Data API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         *
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> channelInfo = new ArrayList<String>();
            ChannelListResponse result = mService.channels().list("snippet,contentDetails,statistics")
                    .setForUsername("GoogleDevelopers")
                    .execute();
            List<Channel> channels = result.getItems();
            if (channels != null) {
                Channel channel = channels.get(0);
                channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                        "Its title is '" + channel.getSnippet().getTitle() + ", " +
                        "and it has " + channel.getStatistics().getViewCount() + " views.");
            }
            return channelInfo;
        }


        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:");
                mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            AddFilesActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage() + "\n");
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }

}
