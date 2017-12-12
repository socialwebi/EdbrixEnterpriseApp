package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.JsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import permissions.dispatcher.NeedsPermission;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class CreateLiveCourseActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    static final int REQUEST_PERMISSION_EXTERNAL = 1006;
    RelativeLayout layout;
    Context context;
    TextInputLayout _create_text_input_price;
    TextInputEditText _live_course_title;
    TextInputEditText _live_course_price;
    TextInputEditText _live_course_code;
    TextInputEditText _live_course_image;
    Button _live_course_button_submit;
    Button _live_course_button_browse;
    Button _live_course_button_next;
    TextView _create_text_category;
    TextView _create_live_title;
    Spinner _create_spinner_category;
    LinearLayout browseLayout;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean mTitle = false, mPrice = false, mImage = false;
    private String courseId;
    private String courseName;
    private String coursePrice;
    private String categoryId = "0";
    private Uri filePath;
    private String fileExtension;
    private String fileName;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private ArrayList<String> arrayListId;
    private ArrayList<String> arrayListTitle;
    private ArrayList<String> photoPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live_course);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = CreateLiveCourseActivity.this;

        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();

        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        courseName = intent.getStringExtra("courseTitle");

        arrayListTitle = new ArrayList<>();
        arrayListId = new ArrayList<>();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://edbrixcbuilder.appspot.com");

        layout = findViewById(R.id.create_live_layout);
        _create_text_input_price = findViewById(R.id.create_text_input_price);
        _live_course_code = findViewById(R.id.create_course_code);
        _live_course_title = findViewById(R.id.live_course_title);
        _live_course_price = findViewById(R.id.live_course_price);
        _live_course_image = findViewById(R.id.live_course_image);
        _create_text_category = findViewById(R.id.create_text_category);
        _create_spinner_category = findViewById(R.id.create_spinner_category);
        _live_course_button_submit = findViewById(R.id.live_course_button_submit);
        _live_course_button_browse = findViewById(R.id.live_course_button_browse);
        _live_course_button_next = findViewById(R.id.live_course_button_next);
        _create_live_title = findViewById(R.id.create_live_title);
        browseLayout = findViewById(R.id.linear_browse_image);

        if (courseId == null) {
            courseId = "0";
            _create_live_title.setText(R.string.create_live_course);
            visibleCode(false);
        } else {
            _create_live_title.setText(R.string.edit_live_course);
            visibleCode(true);
            getCourse();
        }

        _live_course_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTitle = true;
                _live_course_button_submit.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        _live_course_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPrice = true;
                _live_course_button_submit.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        _live_course_image.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mImage = true;
                _live_course_button_submit.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        _live_course_price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    _live_course_price.setHint(null);
                } else {
                    _live_course_price.setHint("Leave blank to free ");
                }
            }
        });
        if (courseName != null) {
            _live_course_title.setText(courseName);
        }

        _live_course_button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTitle | mPrice | mImage) {

                    new AlertDialog.Builder(context)
                            .setTitle("Are you sure ")
                            .setMessage("Are you sure you want to leave without save! ")
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    checkValidationsWithCourseId();
                                }
                            }).setNegativeButton("Don't Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(CreateLiveCourseActivity.this, CreateVideoCourseActivity.class);
                            intent.putExtra("courseId", courseId);
                            startActivity(intent);
                        }
                    }).show();

                } else {
                    checkValidations();
                }

            }
        });

        _live_course_button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidationsWithCourseId();
            }
        });


        _live_course_button_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseClick();
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

    @Override
    protected void onResume() {
        super.onResume();

        String newID = pref.getString("newCourseId", null);
        if (newID != null) {
            editor.putString("newCourseId", null);
            editor.apply();
            finish();
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
                    Log.d("TAG", photoPaths.toString() + " _-_ " + fileName + " _-_ " + fileExtension);
                    // uploadToEdbrix(uri);
                    _live_course_image.setText(fileName);
                }
                break;

        }
    }

    private void checkValidations() {
        courseName = _live_course_title.getText().toString().trim();
        coursePrice = _live_course_price.getText().toString().trim();

        if (courseName.isEmpty()) {
            _live_course_title.setError(getString(R.string.error_edit_text));
        } else if (coursePrice.isEmpty()) {
            _live_course_title.setError(null);
            coursePrice = "0";
            Intent intent = new Intent(CreateLiveCourseActivity.this, CreateVideoCourseActivity.class);
            intent.putExtra("title", courseName);
            intent.putExtra("price", coursePrice);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        } else {
            _live_course_title.setError(null);
            Intent intent = new Intent(CreateLiveCourseActivity.this, CreateVideoCourseActivity.class);
            intent.putExtra("title", courseName);
            intent.putExtra("price", coursePrice);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        }
    }

    private void checkValidationsWithCourseId() {
        courseName = _live_course_title.getText().toString().trim();
        coursePrice = _live_course_price.getText().toString().trim();
        categoryId = arrayListId.get(_create_spinner_category.getSelectedItemPosition());
        Log.d("TAG", "categoryId - " + categoryId);

        if (courseName.isEmpty()) {
            _live_course_title.setError(getString(R.string.error_edit_text));
        } else if (coursePrice.isEmpty()) {
            _live_course_title.setError(null);
            coursePrice = "0";
            saveCourse();
        } else {
            _live_course_title.setError(null);
            saveCourse();
        }

    }

    private void visibleCode(boolean val) {

        if (val) {
            _create_text_category.setVisibility(View.VISIBLE);
            _live_course_code.setVisibility(View.VISIBLE);
            _create_spinner_category.setVisibility(View.VISIBLE);
            browseLayout.setVisibility(View.VISIBLE);
            _live_course_button_submit.setVisibility(View.VISIBLE);
        } else {
            _create_text_category.setVisibility(View.GONE);
            _live_course_code.setVisibility(View.GONE);
            _create_spinner_category.setVisibility(View.GONE);
            browseLayout.setVisibility(View.GONE);
            _live_course_button_submit.setVisibility(View.GONE);
        }

    }

    private void getCourse() {

        // _dashboard_progress.setVisibility(View.VISIBLE);
        User activeUser = SettingsMy.getActiveUser();
        if (activeUser != null) {

            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", activeUser.getId());
                jo.put("AccessToken", activeUser.getAccessToken());
                jo.put("UserType", activeUser.getUserType());
                jo.put("CourseId", courseId);
            } catch (JSONException e) {
                Timber.e(e, "Parse getCourseList exception");
                return;
            }

            if (BuildConfig.DEBUG) Timber.d("getCourseList Request Param: %s", jo.toString());

            GsonRequest<Courses> getDashboardCourseSchedulesRequest = new GsonRequest<>(Request.Method.POST, Constants.getCourseDetails, jo.toString(), Courses.class,
                    new Response.Listener<Courses>() {
                        @Override
                        public void onResponse(@NonNull Courses response) {
                            Timber.d("response: %s", response.toString());
                            // _dashboard_progress.setVisibility(View.INVISIBLE);
                            if (response.getErrorCode() == null) {

                                getData(response);
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
                    // _dashboard_progress.setVisibility(View.INVISIBLE);
                    Timber.d("Error: %s", error.getMessage());
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            getDashboardCourseSchedulesRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getDashboardCourseSchedulesRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getDashboardCourseSchedulesRequest, "dashboard_requests");
        }
    }

    private void saveCourse() {

        User user = SettingsMy.getActiveUser();
        if (user != null) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("Title", courseName);
                jo.put("Price", coursePrice);
                jo.put("CourseId", courseId);
                jo.put("CategoryId", categoryId);

                // Course Image =-------------------------------------= jo.put("Image", path..);


            } catch (JSONException e) {
                Timber.e(e, "Parse create course exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Course: %s", jo.toString());

            JsonRequest req = new JsonRequest(Request.Method.POST, Constants.setCreateCourse, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    Timber.d("Response : %s", response.toString());
                    try {

                        if (response.getString("ErrorCode").equals("0")) {

                            Toast.makeText(context, "Update successful ", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CreateLiveCourseActivity.this, CreateVideoCourseActivity.class);
                            intent.putExtra("courseId", courseId);
                            startActivity(intent);
                            finish();

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

    private void getData(Courses courses) {

        _live_course_code.setText(courses.getCode());
        _live_course_title.setText(courses.getTitle());
        _live_course_price.setText(courses.getPrice());

        for (int i = 0; i < courses.getCourseCategory().size(); i++) {
            arrayListTitle.add(courses.getCourseCategory().get(i).getTitle());
            arrayListId.add(courses.getCourseCategory().get(i).getId());
        }

        /*ArrayAdapter<CourseContents> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter3.setDropDownViewResource(R.layout.custom_text_layout);
        _create_spinner_category.setAdapter(adapter3);*/

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayListTitle);
        adapter3.setDropDownViewResource(R.layout.custom_text_layout);
        _create_spinner_category.setAdapter(adapter3);

    }

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
