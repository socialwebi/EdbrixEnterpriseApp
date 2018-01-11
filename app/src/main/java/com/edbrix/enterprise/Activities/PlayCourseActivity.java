package com.edbrix.enterprise.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.CoursePlayImagePagerAdapter;
import com.edbrix.enterprise.Adapters.DrawerContentListAdapter;
import com.edbrix.enterprise.Adapters.ImageChoiceListAdapter;
import com.edbrix.enterprise.Adapters.ImageDrawerAdapter;
import com.edbrix.enterprise.Adapters.SessionEventListAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.Interfaces.ImageChoiceActionListener;
import com.edbrix.enterprise.Models.ChoicesData;
import com.edbrix.enterprise.Models.ChoicesInputData;
import com.edbrix.enterprise.Models.CourseContentData;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.GetCourseContentListResponseData;
import com.edbrix.enterprise.Models.ImageContentData;
import com.edbrix.enterprise.Models.PlayCourseContentResponseData;
import com.edbrix.enterprise.Models.TrainingSessionEventContentData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Utils.CustomViewPager;
import com.edbrix.enterprise.Utils.CustomWebView;
import com.edbrix.enterprise.Utils.RoundedImageView;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.commons.AlertDialogManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import permissions.dispatcher.NeedsPermission;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingEvent;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class PlayCourseActivity extends BaseActivity implements ZoomSDKInitializeListener, MeetingServiceListener {

    public static final String courseItemBundleKey = "courseItem";

    static final int REQUEST_PERMISSION_EXTERNAL = 1004;

    private Courses courseItem;

    private LinearLayout checkboxGroupLayout;
    private LinearLayout surveyProgressLayout;
    private LinearLayout imageContentLayout;
    private LinearLayout timerLayout;
    private LinearLayout audioContentLayout;
    private LinearLayout assignmentLayout;
    private RelativeLayout imageChoiceGroupLayout;
    private RelativeLayout videoLayout;
    private RadioGroup radioGroupLayout;

    private TextView title;
    private TextView drawerCourseTitle;
    private TextView achivmntText;
    private TextView txtContentType;
    private TextView txtContentDesc;
    private TextView txtQuestion;
    private TextView txtTimer;
    private TextView txtSubmitBtn;
    private TextView txtSkipBtn;
    private TextView txtSurveyProgress;
    private TextView edtAssignmentFile;
    private TextView btnDownloadASContent;

    private Button btnBrowseFile;

    private EditText editTxtLongAns;

    private ImageView imgContentPrevBtn;
    private ImageView imgContentNextBtn;

    private ImageView imgPrevBtn;
    private ImageView imgNextBtn;

    private ImageView imgPreview;

    private ImageView fullScreenBtn;

    private ProgressBar pbarSurvey;
    private CheckBox checkSubmit;
    private CustomViewPager imgViewPager;
    private RecyclerView imageChoiceListView;
    private RecyclerView imgDrawerRecyclerView;

    private EasyVideoPlayer vdPlayer;

    private CustomWebView questionWebView;

    private CustomWebView mediaWebView;

    private CustomWebView docWebView;

    private CustomWebView audioWebView;

    private CustomWebView contentDescWebView;

    private CountDownTimer countDownTimer;

    private ArrayList<ChoicesInputData> choiceInput;

    private PlayCourseContentResponseData playCourseContentResponseData;

    private CoursePlayImagePagerAdapter imagePagerAdapter;

    private int questionIndex = 0;

    private JSONArray mJSONArray;

    private boolean isAnswerRequired;

    private DrawerLayout drawer;

    private RecyclerView menuListDrawerRecylerView;

    private RecyclerView sessionEventRecyclerView;

    private Button btnBack;

    private ScrollView playScrollView;

    private Toolbar toolbar;

    private ArrayList<CourseContentData> courseContentDataList;

    private boolean mbPendingStartMeeting = false;

    private String sessionID;
    private String sessionTOKEN;

    private ArrayList<String> assignmentFilePaths = new ArrayList<>();

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private UploadTask uploadTask;

    private File assignmentFile;

//    private ImageLoader imageLoader; // Get singleton instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_drawer);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://edbrixcbuilder.appspot.com");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.title);
        drawerCourseTitle = (TextView) findViewById(R.id.drawerCourseTitle);
        achivmntText = (TextView) findViewById(R.id.achivmntText);
        txtContentType = (TextView) findViewById(R.id.txtContentType);
        txtContentDesc = (TextView) findViewById(R.id.txtContentDesc);
        txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        txtSubmitBtn = (TextView) findViewById(R.id.txtSubmitBtn);
        txtSkipBtn = (TextView) findViewById(R.id.txtSkipBtn);
        editTxtLongAns = (EditText) findViewById(R.id.editTxtLongAns);
        edtAssignmentFile = (TextView) findViewById(R.id.edtAssignmentFile);
        btnDownloadASContent = (TextView) findViewById(R.id.btnDownloadASContent);
        txtTimer = (TextView) findViewById(R.id.txtTimer);
        txtSurveyProgress = (TextView) findViewById(R.id.txtSurveyProgress);

        imgPrevBtn = (ImageView) findViewById(R.id.imgPrevBtn);
        imgNextBtn = (ImageView) findViewById(R.id.imgNextBtn);

        fullScreenBtn = (ImageView) findViewById(R.id.fullScreenBtn);
        vdPlayer = (EasyVideoPlayer) findViewById(R.id.vdPlayer);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBrowseFile = (Button) findViewById(R.id.btnBrowseFile);

        imgContentPrevBtn = (ImageView) findViewById(R.id.imgContentPrevBtn);
        imgContentNextBtn = (ImageView) findViewById(R.id.imgContentNextBtn);

        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        checkSubmit = (CheckBox) findViewById(R.id.checkSubmit);

        imgViewPager = (CustomViewPager) findViewById(R.id.imgViewPager);

        questionWebView = (CustomWebView) findViewById(R.id.questionWebView);
        mediaWebView = (CustomWebView) findViewById(R.id.mediaWebView);
        docWebView = (CustomWebView) findViewById(R.id.docWebView);
        audioWebView = (CustomWebView) findViewById(R.id.audioWebView);
        contentDescWebView = (CustomWebView) findViewById(R.id.contentDescWebView);

        pbarSurvey = (ProgressBar) findViewById(R.id.pbarSurvey);

        questionWebView.getSettings().setJavaScriptEnabled(true);
        questionWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        questionWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        questionWebView.setWebChromeClient(new WebChromeClient());

        mediaWebView.getSettings().setJavaScriptEnabled(true);
        mediaWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mediaWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mediaWebView.setWebChromeClient(new WebChromeClient());

        docWebView.getSettings().setJavaScriptEnabled(true);
        docWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        docWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        docWebView.setWebChromeClient(new WebChromeClient());

        audioWebView.getSettings().setJavaScriptEnabled(true);
        audioWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        audioWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        audioWebView.setWebChromeClient(new WebChromeClient());

        contentDescWebView.getSettings().setJavaScriptEnabled(true);
        contentDescWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        contentDescWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        contentDescWebView.setWebChromeClient(new WebChromeClient());

//        setupWebView();
        checkboxGroupLayout = (LinearLayout) findViewById(R.id.checkboxGroupLayout);
        surveyProgressLayout = (LinearLayout) findViewById(R.id.surveyProgressLayout);
        imageContentLayout = (LinearLayout) findViewById(R.id.imageContentLayout);
        timerLayout = (LinearLayout) findViewById(R.id.timerLayout);
        audioContentLayout = (LinearLayout) findViewById(R.id.audioContentLayout);
        assignmentLayout = (LinearLayout) findViewById(R.id.assignmentLayout);
        imageChoiceGroupLayout = (RelativeLayout) findViewById(R.id.imageChoiceGroupLayout);
        videoLayout = (RelativeLayout) findViewById(R.id.videoLayout);
        radioGroupLayout = (RadioGroup) findViewById(R.id.radioGroupLayout);
        imageChoiceListView = (RecyclerView) findViewById(R.id.imageChoiceListView);
        imgDrawerRecyclerView = (RecyclerView) findViewById(R.id.imgDrawerRecyclerView);
        menuListDrawerRecylerView = (RecyclerView) findViewById(R.id.menuListDrawerRecylerView);
        sessionEventRecyclerView = (RecyclerView) findViewById(R.id.sessionEventRecyclerView);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        courseItem = (Courses) getIntent().getSerializableExtra(courseItemBundleKey);

        sessionID = "";
        sessionTOKEN = "";

        if (courseItem != null) {
            title.setText(courseItem.getTitle());
            drawerCourseTitle.setText(courseItem.getTitle());
            //set Course Details
//            setCourseDetails();
            showBusyProgress();
            getCourseContentList(SettingsMy.getActiveUser(), courseItem.getId());
        } else {
            //show message and finish activity
        }

        playScrollView = (ScrollView) findViewById(R.id.playScrollView);
        playScrollView.smoothScrollTo(0, 0);
        setListeners();

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


    private void showDrawerContentList(final ArrayList<CourseContentData> contentDataArrayList) {
        courseContentDataList = contentDataArrayList;
        DrawerContentListAdapter drawerContentListAdapter = new DrawerContentListAdapter(PlayCourseActivity.this, contentDataArrayList, new DrawerContentListAdapter.ContentListActionListener() {
            @Override
            public void onListItemSelected(int position, CourseContentData contentData) {
                achivmntText.setText("" + (position + 1) + "/" + contentDataArrayList.size());
                drawer.closeDrawer(GravityCompat.START);
                showBusyProgress();
                getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), contentData.getId(), "0", "0");
            }
        });
        menuListDrawerRecylerView.setLayoutManager(new LinearLayoutManager(PlayCourseActivity.this));
        menuListDrawerRecylerView.setAdapter(drawerContentListAdapter);
    }

    /**
     * Get course content list from server and load data
     *
     * @param activeUser Object of User class ie. logged active user.
     * @param courseId   CourseId i.e. Id of selected Course
     */
    private void getCourseContentList(final User activeUser, String courseId) {
        try {

            JSONObject jo = new JSONObject();

            jo.put("UserId", activeUser.getId());
            jo.put("AccessToken", activeUser.getAccessToken());
            jo.put("CourseId", courseId);


//        if (BuildConfig.DEBUG) Timber.d("getCourseList Request Param: %s", jo.toString());

            GsonRequest<GetCourseContentListResponseData> getCourseContentListRequest = new GsonRequest<>(Request.Method.POST, Constants.getCourseContentList, jo.toString(), GetCourseContentListResponseData.class,
                    new Response.Listener<GetCourseContentListResponseData>() {
                        @Override
                        public void onResponse(@NonNull final GetCourseContentListResponseData response) {
//                        Timber.d("response: %s", response.toString());
                            Log.v("ResponseData", response.toString());

                            if (response.getErrorMessage() != null && response.getErrorMessage().length() > 0) {
//                            Timber.d("Error: %s", response.getErrorCode());
                                hideBusyProgress();
                                showToast(response.getErrorMessage());
                            } else {
                                if (response.isShowDrawerContents()) {

                                    if (response.getCourseContentList() != null)
                                        showDrawerContentList(response.getCourseContentList());

                                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                } else {
                                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            onBackPressed();
                                        }
                                    });
                                }

                                if (SettingsMy.getActiveUser().getUserType().equals("L")) {
                                    if (response.getJumpContentId().equals("0")) {
                                        if (response.getCourseContentList() != null && !response.getCourseContentList().isEmpty())
                                            setQuestionAchievementIndex("0", response.getCourseContentList());

                                        getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), response.getJumpContentId(), "0", "0");
                                    } else {
                                        new AlertDialog.Builder(PlayCourseActivity.this)
                                                .setTitle(courseItem.getTitle())
                                                .setMessage("Resume or start over course?")
                                                .setCancelable(false)
                                                .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        if (response.getCourseContentList() != null && !response.getCourseContentList().isEmpty())
                                                            setQuestionAchievementIndex(response.getJumpContentId(), response.getCourseContentList());

                                                        getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), response.getJumpContentId(), "0", "0");
                                                    }
                                                })
                                                .setNegativeButton("Start Over", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int i) {
                                                        dialog.dismiss();

                                                        if (response.getCourseContentList() != null && !response.getCourseContentList().isEmpty())
                                                            setQuestionAchievementIndex("0", response.getCourseContentList());

                                                        // set start over as 1 for starting course from start
                                                        getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), "0", "0", "1");

                                                    }
                                                })
                                                .show();
                                    }
                                } else {

                                    if (response.getCourseContentList() != null && !response.getCourseContentList().isEmpty())
                                        setQuestionAchievementIndex("0", response.getCourseContentList());

                                    getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), response.getJumpContentId(), "0", "0");
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    Timber.d("Error: %s", error.getMessage());
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            getCourseContentListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getCourseContentListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getCourseContentListRequest, "getcoursecontentlist");
        } catch (JSONException e) {
            hideBusyProgress();
            Timber.e(e, "Parse getCourseContentList exception");
            showToast("Something went wrong. Please try again later.");
        }
    }

    private void setQuestionAchievementIndex(String contentId, ArrayList<CourseContentData> contentDataArrayList) {
        int index = 0;

        if (contentId.equals("0")) {
            achivmntText.setText("1/" + contentDataArrayList.size());
            setSelectedCheckedItem(0);
        } else {
            for (int i = 0; i < contentDataArrayList.size(); i++) {
                if (contentDataArrayList.get(i).getId().equalsIgnoreCase(contentId)) {
                    achivmntText.setText("" + (i + 1) + "/" + contentDataArrayList.size());
                    setSelectedCheckedItem(i);
                    break;
                }
            }
        }

    }

    private void setSelectedCheckedItem(int position) {
        if (menuListDrawerRecylerView.getAdapter() != null) {
            ((DrawerContentListAdapter) menuListDrawerRecylerView.getAdapter()).setChecked(position);
            ((DrawerContentListAdapter) menuListDrawerRecylerView.getAdapter()).setSelected(position);
        }
    }

    /**
     * Get course list from server and load data
     *
     * @param activeUser Object of User class ie. logged active user.
     * @param courseId   CourseId i.e. Id of selected Course
     * @param contentId  ContentId i.e. Id of content from Course
     * @param questionId QuestionId i.e. default question id of Course
     */
    private void getPlayCourseContent(final User activeUser, String courseId, String contentId, String questionId, String startOver) {
        try {

            JSONObject jo = new JSONObject();

            jo.put("UserId", activeUser.getId());
            jo.put("AccessToken", activeUser.getAccessToken());
            jo.put("courseId", courseId);
            jo.put("contentId", contentId);
            jo.put("questionId", questionId);
            jo.put("startOver", startOver);


//            jo.put("UserId", "1");
//            jo.put("AccessToken", "sdfsdf");
//            jo.put("courseId", "1774");
////            jo.put("contentId", "27659");
//            jo.put("contentId", "23236");
////            jo.put("contentId", "23236");
//            jo.put("questionId", "0");

//        if (BuildConfig.DEBUG) Timber.d("getCourseList Request Param: %s", jo.toString());

            GsonRequest<PlayCourseContentResponseData> getPlayCourseContentRequest = new GsonRequest<>(Request.Method.POST, Constants.playCourseContent, jo.toString(), PlayCourseContentResponseData.class,
                    new Response.Listener<PlayCourseContentResponseData>() {
                        @Override
                        public void onResponse(@NonNull PlayCourseContentResponseData response) {
//                        Timber.d("response: %s", response.toString());
                            Log.v("ResponseData", response.toString());

                            if (response.getErrorCode() != null && response.getErrorCode().length() > 0) {
//                            Timber.d("Error: %s", response.getErrorCode());
                                hideBusyProgress();
                                showToast(response.getErrorMessage());
                            } else {
                                playCourseContentResponseData = response;
                                clearData();
                                setContentData(response);
                                hideBusyProgress();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    Timber.d("Error: %s", error.getMessage());
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            getPlayCourseContentRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getPlayCourseContentRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getPlayCourseContentRequest, "playcoursecontent");
        } catch (JSONException e) {
            hideBusyProgress();
            Timber.e(e, "Parse getCourseList exception");
            showToast("Something went wrong. Please try again later.");
        }
    }

    private void submitPlayCourseContent(final User activeUser, final String courseId, String contentId, String questionId, String contentType, String contentCompleteTypeId, String longAnswer, String assignmentFileName, JSONArray choiceJsonArray) {
        try {
            showBusyProgress();

            JSONObject jo = new JSONObject();

            jo.put("UserId", activeUser.getId());
            jo.put("AccessToken", activeUser.getAccessToken());
            jo.put("courseId", courseId);
            jo.put("contentId", contentId);
            jo.put("questionId", questionId);
            jo.put("contentType", contentType);
            if (contentCompleteTypeId != null && !contentCompleteTypeId.isEmpty()) {
                jo.put("contentcomplete_type_id", contentCompleteTypeId);
            } else {
                if (contentType.equals(Constants.contentType_TrainingSession) || contentType.equals(Constants.contentType_Event))
                    jo.put("contentcomplete_type_id", "1");
            }
            jo.put("choiceId", choiceJsonArray);
            if (longAnswer != null && longAnswer.length() > 0) {
                jo.put("longAnswer", longAnswer);
            }

            if (assignmentFileName != null && assignmentFileName.length() > 0) {
                jo.put("fileName", assignmentFileName);
            }

/*            jo.put("UserId", "1");
            jo.put("AccessToken", "sdfsdf");
            jo.put("courseId", "1774");
            jo.put("contentId", "0");
            jo.put("questionId", "0");
            jo.put("contentType", "C");
            jo.put("contentcomplete_type_id", "0");
            jo.put("choiceId", choiceJsonArray);*/


//        if (BuildConfig.DEBUG) Timber.d("getCourseList Request Param: %s", jo.toString());

            GsonRequest<PlayCourseContentResponseData> submitPlayCourseContentRequest = new GsonRequest<>(Request.Method.POST, Constants.playCourseContentSubmit, jo.toString(), PlayCourseContentResponseData.class,
                    new Response.Listener<PlayCourseContentResponseData>() {
                        @Override
                        public void onResponse(@NonNull PlayCourseContentResponseData response) {
//                        Timber.d("response: %s", response.toString());
                            Log.v("ResponseData", response.toString());

                            if (response.getErrorCode() != null && response.getErrorCode().length() > 0) {
//                            Timber.d("Error: %s", response.getErrorCode());
                                hideBusyProgress();
                                showToast(response.getErrorMessage());
                            } else {

                                if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("2")) {
                                    // course end
                                    showToast("Course completed successfully...");
                                    finish();
                                } else {

                                    if (courseContentDataList != null && !courseContentDataList.isEmpty())
                                        setQuestionAchievementIndex(response.getNext_content_id(), courseContentDataList);

                                    getPlayCourseContent(SettingsMy.getActiveUser(), courseId, response.getNext_content_id(), response.getQuestion_id(), "0");
                                }

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    Timber.d("Error: %s", error.getMessage());
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            submitPlayCourseContentRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            submitPlayCourseContentRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(submitPlayCourseContentRequest, "playcoursecontentsubmit");
        } catch (JSONException e) {
            hideBusyProgress();
            Timber.e(e, "Parse submitPlayCourseContentRequest exception");
            showToast("Something went wrong. Please try again later.");
        }
    }

    private void clearData() {

        sessionID = "";
        sessionTOKEN = "";

        // questionIndex = 0;
        contentDescWebView.setVisibility(View.GONE);
        contentDescWebView.reload();

        surveyProgressLayout.setVisibility(View.GONE);
        checkSubmit.setVisibility(View.GONE);

        txtQuestion.setText("");
        txtQuestion.setVisibility(View.GONE);

        questionWebView.setVisibility(View.GONE);
        questionWebView.reload();

        checkboxGroupLayout.removeAllViewsInLayout();
        checkboxGroupLayout.setVisibility(View.GONE);

        radioGroupLayout.removeAllViewsInLayout();
        radioGroupLayout.setVisibility(View.GONE);
        imageContentLayout.setVisibility(View.GONE);

        videoLayout.setVisibility(View.GONE);

        mediaWebView.setVisibility(View.GONE);
        mediaWebView.reload();

        docWebView.setVisibility(View.GONE);
        docWebView.reload();

        audioContentLayout.setVisibility(View.GONE);
        audioWebView.setVisibility(View.GONE);
        audioWebView.reload();

        editTxtLongAns.setText("");
        editTxtLongAns.setVisibility(View.GONE);

        imageChoiceGroupLayout.setVisibility(View.GONE);
        imageChoiceListView.setAdapter(null);
        imageChoiceListView.setVisibility(View.GONE);

        imgDrawerRecyclerView.setAdapter(null);
        imgDrawerRecyclerView.setVisibility(View.GONE);

        timerLayout.setVisibility(View.GONE);

        txtSubmitBtn.setText(R.string.submit);
        txtSubmitBtn.setVisibility(View.VISIBLE);
        txtSubmitBtn.setEnabled(true);

        txtSkipBtn.setVisibility(View.GONE);

        sessionEventRecyclerView.setVisibility(View.GONE);

        assignmentLayout.setVisibility(View.GONE);
        edtAssignmentFile.setText("");
        assignmentFile = null;
        assignmentFilePaths = null;
        btnDownloadASContent.setVisibility(View.GONE);
    }

    private void setContentData(PlayCourseContentResponseData response) {
        if (response.getPrev_content_id().equalsIgnoreCase("0")) {
            imgContentPrevBtn.setVisibility(View.INVISIBLE);
        } else {
            // if Test or Survey Content
            if (playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Test) ||
                    playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Survey)) {
                // if Submit data not null and previous question id == 0

                if (playCourseContentResponseData.getCourse_content().getSubmit_data() != null &&
                        playCourseContentResponseData.getCourse_content().getSubmit_data().getPrev_question_id().equalsIgnoreCase("0")) {
                    imgContentPrevBtn.setVisibility(View.VISIBLE);
                } else {
                    imgContentPrevBtn.setVisibility(View.INVISIBLE);
                }

            } else {
                imgContentPrevBtn.setVisibility(View.VISIBLE);
            }
        }

        if (SettingsMy.getActiveUser().getUserType().equals("L")) {
            imgContentNextBtn.setVisibility(View.INVISIBLE);
        } else {
            imgContentNextBtn.setVisibility(View.VISIBLE);
        }

        choiceInput = new ArrayList<>();
//        txtContentDesc.setText(Html.fromHtml(response.getCourse_content().getDescription()));
        if (response.getCourse_content().getDescription() != null && !response.getCourse_content().getDescription().isEmpty())
            loadContentDescWebView(response.getCourse_content().getDescription());
//        if(response.getContent_type().equalsIgnoreCase(Constants.contentType_WC)){
//            txtContentType.setText(getString(R.string.web_content));
//            mediaWebView.setVisibility(View.VISIBLE);
//        }else{
//            mediaWebView.setVisibility(View.GONE);
//        }

        // Course content load

        txtContentType.setText(response.getCourse_content().getTitle());
        switch (response.getContent_type()) {
            case Constants.contentType_C:
//                txtContentType.setText(getString(R.string.simple_content));
                break;
            case Constants.contentType_Audio:
//                txtContentType.setText(getString(R.string.audio_content));
                loadAudioContent(response.getCourse_content().getAudio_content());
                break;
            case Constants.contentType_Video:
//                txtContentType.setText(getString(R.string.video_content));
                loadVideoContent(response.getCourse_content().getVideo_content());
                break;
            case Constants.contentType_Iframe:
//                txtContentType.setText(getString(R.string.iframe_content));
                loadWebContent("<html><body>" + response.getCourse_content().getIframe_content() + "</body></html>");
                break;
            case Constants.contentType_Doc:
//                txtContentType.setText(getString(R.string.document_content));
                if (response.getCourse_content().getDocument_content_type().equalsIgnoreCase(Constants.docContentType_Img)) {
                    loadPdfImageContent(response.getCourse_content().getDoc_content());
                } else {
                    if (response.getCourse_content().getDoc_content()[0].contains("www.slideshare.net")) {
                        loadWebContent(response.getCourse_content().getDoc_content()[0]);
                    } else {
                        loadDocWebContent(response.getCourse_content().getDoc_content()[0]);
                    }

                }
                break;
            case Constants.contentType_WC:
//                txtContentType.setText(getString(R.string.web_content));
                loadWebContent(response.getCourse_content().getWebContent());
                break;
            case Constants.contentType_IMG:
//                txtContentType.setText(getString(R.string.image_content));
                loadImageContent(PlayCourseActivity.this, response.getCourse_content().getImg_content(), true);
                break;
            case Constants.contentType_Survey:
//                txtContentType.setText(getString(R.string.survey));
                showSurveyProgress(questionIndex, playCourseContentResponseData.getCourse_content().getSubmit_data().getTotal_question_count());
                break;
            case Constants.contentType_Test:
//                txtContentType.setText(getString(R.string.test));
                showSurveyProgress(questionIndex, playCourseContentResponseData.getCourse_content().getSubmit_data().getTotal_question_count());
                break;
            case Constants.contentType_TrainingSession:
//                txtContentType.setText(getString(R.string.test));
                if (SettingsMy.getActiveUser().getUserType().equals("L")) {
                    txtSubmitBtn.setVisibility(View.VISIBLE);
                    txtSubmitBtn.setText(R.string.complete_continue);
                } else {
                    txtSubmitBtn.setVisibility(View.GONE);
                }

                if (response.getCourse_content().getTrainingSessionEventContentDataList() != null && response.getCourse_content().getTrainingSessionEventContentDataList().size() > 0) {
                    showSessionEventList(response.getCourse_content().getTrainingSessionEventContentDataList());
                }
                break;
            case Constants.contentType_Event:
                if (SettingsMy.getActiveUser().getUserType().equals("L")) {
                    txtSubmitBtn.setVisibility(View.VISIBLE);
                    txtSubmitBtn.setText(R.string.complete_continue);
                } else {
                    txtSubmitBtn.setVisibility(View.GONE);
                }

                break;

            case Constants.contentType_Assignment:
//                new DownloadFileFromURL().execute("");
                if (response.getCourse_content().getAssignment_content() != null && !response.getCourse_content().getAssignment_content().isEmpty()) {
                    loadDocWebContent(response.getCourse_content().getAssignment_content());
                    btnDownloadASContent.setVisibility(View.VISIBLE);
                } else {
                    btnDownloadASContent.setVisibility(View.GONE);
                }

                assignmentLayout.setVisibility(View.VISIBLE);

                break;
        }

//        loadWebContent(response.getCourse_content().getWebContent());

        if (response.getCourse_content().getSubmit_type().equalsIgnoreCase(Constants.submitType_Check)) {
//            checkSubmit.setVisibility(View.VISIBLE);
        } else if (response.getCourse_content().getSubmit_type().equalsIgnoreCase(Constants.submitType_Timer)) {
            checkSubmit.setVisibility(View.GONE);
            startTimer(Integer.parseInt(response.getCourse_content().getSubmit_data().getTime()));
        } else if (response.getCourse_content().getSubmit_type().equalsIgnoreCase(Constants.submitType_Question)) {
            checkSubmit.setVisibility(View.GONE);
            txtSubmitBtn.setEnabled(false);
//            txtQuestion.setVisibility(View.VISIBLE);

            isAnswerRequired = response.getCourse_content().getSubmit_data().isAnswerRequired();

            if (isAnswerRequired) {
//                txtSubmitBtn.setEnabled(false);
            } else {
//                txtSubmitBtn.setEnabled(true);

                if (SettingsMy.getActiveUser().getUserType().equals("L")) {
//                    txtSkipBtn.setVisibility(View.VISIBLE);
                }
            }
            txtQuestion.setText("Q. " + response.getCourse_content().getSubmit_data().getTitle());
            loadQuestionTextInWebView("<b>Q. " + response.getCourse_content().getSubmit_data().getTitle() + "</b>");
            if (response.getCourse_content().getSubmit_data().getChoices() != null && response.getCourse_content().getSubmit_data().getChoices().size() > 0) {
                if (response.getCourse_content().getSubmit_data().getType().equalsIgnoreCase(Constants.submitDataType_TrueFalse)) {
                    radioGroupLayout.setVisibility(View.VISIBLE);
                    addSingleChoiceRadioButton(response.getCourse_content().getSubmit_data().getChoices());
                } else if (response.getCourse_content().getSubmit_data().getType().equalsIgnoreCase(Constants.submitDataType_SingleChoice)) {
                    radioGroupLayout.setVisibility(View.VISIBLE);
                    addSingleChoiceRadioButton(response.getCourse_content().getSubmit_data().getChoices());
                } else if (response.getCourse_content().getSubmit_data().getType().equalsIgnoreCase(Constants.submitDataType_MultiChoice)) {
                    checkboxGroupLayout.setVisibility(View.VISIBLE);
                    addMultiChoiceCheckBox(response.getCourse_content().getSubmit_data().getChoices());
                } else if (response.getCourse_content().getSubmit_data().getType().equalsIgnoreCase(Constants.submitDataType_ImageChoice)) {
                    imageChoiceGroupLayout.setVisibility(View.VISIBLE);
                    addImageChoiceRadioButton(response.getCourse_content().getSubmit_data().getChoices());
                }
            } else if (response.getCourse_content().getSubmit_data().getType().equalsIgnoreCase(Constants.submitDataType_LongAnswer)) {
                editTxtLongAns.setVisibility(View.VISIBLE);
            }
        }

        if (response.getCourse_content().getSubmit_data() != null && response.getCourse_content().getSubmit_data().getNext_question_id() != null) {
            if (response.getCourse_content().getSubmit_data().getNext_question_id().equals("0") && response.getNext_content_id().equals("0")) {
                txtSubmitBtn.setVisibility(View.VISIBLE);
                txtSubmitBtn.setText(R.string.submit_course);
                imgContentNextBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            if (response.getNext_content_id().equals("0")) {
                imgContentNextBtn.setVisibility(View.INVISIBLE);
                txtSubmitBtn.setVisibility(View.VISIBLE);
                txtSubmitBtn.setText(R.string.submit_course);
            }
        }

    }

    private void addTrueFalseRadioButton(ArrayList<ChoicesData> choiceList) {
        radioGroupLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < choiceList.size(); i++) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(Integer.parseInt(choiceList.get(i).getId()));
            rdbtn.setText((char) (65 + i) + ". " + choiceList.get(i).getChoice());
            radioGroupLayout.addView(rdbtn);
        }
    }

    private void addSingleChoiceRadioButton(ArrayList<ChoicesData> choiceList) {
        radioGroupLayout.setOrientation(LinearLayout.VERTICAL);
        RadioGroup.LayoutParams params
                = new RadioGroup.LayoutParams(PlayCourseActivity.this, null);
        params.setMargins(10, 10, 10, 10);
        for (int i = 0; i < choiceList.size(); i++) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(Integer.parseInt(choiceList.get(i).getId()));
            rdbtn.setText((char) (65 + i) + ". " + choiceList.get(i).getChoice());
            rdbtn.setLayoutParams(params);
            radioGroupLayout.addView(rdbtn);
        }
    }

    private void addImageChoiceRadioButton(ArrayList<ChoicesData> choiceList) {
        ImageChoiceListAdapter imageChoiceListAdapter = new ImageChoiceListAdapter(PlayCourseActivity.this, choiceList, new ImageChoiceActionListener() {
            @Override
            public void onImageChoiceSelected(ChoicesData choicesData) {
                choiceInput.clear();
                choiceInput.add(ChoicesInputData.addChoiceData(choicesData.getId()));
                txtSubmitBtn.setEnabled(true);
            }

            @Override
            public void onImageClick(ChoicesData choicesData) {
                Intent photo = new Intent(PlayCourseActivity.this, PhotoPopUpActivity.class);
                photo.putExtra("IMGURL", choicesData.getChoice());
                startActivity(photo);
            }
        });
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(PlayCourseActivity.this);
        assert imageChoiceListView != null;
        imageChoiceListView.setHasFixedSize(true);
        imageChoiceListView.setLayoutManager(linearLayoutManager1);
        registerForContextMenu(imageChoiceListView);
        imageChoiceListView.setAdapter(imageChoiceListAdapter);
        imageChoiceListView.setVisibility(View.VISIBLE);
    }

    private void addMultiChoiceCheckBox(final ArrayList<ChoicesData> choiceList) {
        checkboxGroupLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        for (int i = 0; i < choiceList.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setLayoutParams(params);
            checkBox.setId(Integer.parseInt(choiceList.get(i).getId()));
            checkBox.setText((char) (65 + i) + ". " + choiceList.get(i).getChoice());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    int choiceSize = choiceInput.size();
                    if (isChecked) {
//                        showToast("Added Id :" + compoundButton.getId());
                        txtSubmitBtn.setEnabled(true);
                        if (choiceSize > 0) {
                            boolean isDuplicateChoice = false;
                            for (int i = 0; i < choiceSize; i++) {
                                if (choiceInput.get(i).getId().equalsIgnoreCase("" + compoundButton.getId())) {
                                    isDuplicateChoice = true;
                                    break;
                                }
                            }
                            if (!isDuplicateChoice) {
                                choiceInput.add(ChoicesInputData.addChoiceData("" + compoundButton.getId()));
                            }
                        } else {
                            choiceInput.add(ChoicesInputData.addChoiceData("" + compoundButton.getId()));
                        }
                    } else {
//                        showToast("Removed Id :" + compoundButton.getId());
                        for (int j = 0; j < choiceSize; j++) {
                            if (choiceInput.get(j).getId().equalsIgnoreCase("" + compoundButton.getId())) {
                                choiceInput.remove(j);
                                break;
                            }
                        }
                        if (choiceInput.size() == 0) {
                            if (isAnswerRequired) {
                                txtSubmitBtn.setEnabled(false);
                            } else {
                                txtSubmitBtn.setEnabled(true);
                            }
                        }
                    }

                }
            });
            checkboxGroupLayout.addView(checkBox);
        }
    }

    private void loadWebContent(String webContent) {
        if (webContent != null && webContent.length() > 0) {
            mediaWebView.setVisibility(View.VISIBLE);
            mediaWebView.loadData(webContent, "text/html", "utf-8");
            if (!isTablet()) {
                mediaWebView.getSettings().setLoadWithOverviewMode(true);
                mediaWebView.getSettings().setUseWideViewPort(true);
            }
        } else {
            mediaWebView.setVisibility(View.GONE);
        }
    }

    private void loadVideoContent(final String videoContent) {
        if (videoContent != null && videoContent.length() > 0) {

            if (videoContent.contains("cdn.video.playwire.com")) {
                videoLayout.setVisibility(View.VISIBLE);
                vdPlayer.setSource(Uri.parse(videoContent));

                fullScreenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!videoContent.isEmpty()) {
                            vdPlayer.stop();
                            Intent videoPlayer = new Intent(PlayCourseActivity.this, VideoPlayerActivity.class);
                            videoPlayer.putExtra("FileUrl", videoContent);
                            startActivity(videoPlayer);
                        }
                    }
                });
            } else if (!videoContent.contains("iframe")) {
                mediaWebView.setVisibility(View.VISIBLE);
                String dt = "\u003Ciframe width=\"100%\" height=\"100%\" src=\"" + videoContent + "\" frameborder=\"0\"allowfullscreen\u003E\u003C/iframe\u003E";
                mediaWebView.loadData(dt, "text/html", "UTF-8");
            } else {
                mediaWebView.setVisibility(View.VISIBLE);
                mediaWebView.loadData(videoContent, "text/html", "UTF-8");
            }

            if (!isTablet()) {
                mediaWebView.getSettings().setLoadWithOverviewMode(true);
                mediaWebView.getSettings().setUseWideViewPort(true);
            }
        }
    }

    private void loadDocWebContent(String docWebContent) {
        if (docWebContent != null && docWebContent.length() > 0) {
            docWebView.setVisibility(View.VISIBLE);
            docWebView.loadUrl(docWebContent);
        } else {
            docWebView.setVisibility(View.GONE);
        }
    }

    private void loadAudioContent(String webContent) {
        if (webContent != null && webContent.length() > 0) {
            audioContentLayout.setVisibility(View.VISIBLE);
            audioWebView.setVisibility(View.VISIBLE);
            String dt = "\u003Cdiv\n" +
                    "class=\"row margin_zero\"\u003E\u003Ciframe width=\"100%\" height=\"100%\"\n" +
                    "src=\"https://edbrixcbuilder.storage.googleapis.com/storage/uploads/coursecontent/audio/23233/59ad27e3adc0a.mp3\" frameborder=\"0\"allowfullscreen\u003E\u003C/iframe\u003E\u003C/div\u003E";
            audioWebView.loadData(webContent, "text/html", "utf-8");
        } else {
            audioContentLayout.setVisibility(View.GONE);
            audioWebView.setVisibility(View.GONE);
        }
    }


    private void loadQuestionTextInWebView(String questionContent) {
        if (questionContent != null && questionContent.length() > 0) {
            questionWebView.setVisibility(View.VISIBLE);
            questionWebView.loadData(questionContent, "text/html", "utf-8");
        } else {
            questionWebView.setVisibility(View.GONE);
        }
    }

    private void loadContentDescWebView(String webContent) {
        if (webContent != null && webContent.length() > 0) {
            contentDescWebView.setVisibility(View.VISIBLE);
            contentDescWebView.loadData(webContent, "text/html", "utf-8");
//        contentDescWebView.loadUrl("https://www.tutorialspoint.com/java/java_basic_syntax.htm");
//        contentDescWebView.getSettings().setLoadWithOverviewMode(true);
//        contentDescWebView.getSettings().setUseWideViewPort(true);
        } else {
            contentDescWebView.setVisibility(View.GONE);
        }
    }

    private void loadPdfImageContent(String[] pdfImageUrls) {
        if (pdfImageUrls != null && pdfImageUrls.length > 0) {
            ArrayList<ImageContentData> temp = new ArrayList<>();
            for (String imgUrl : pdfImageUrls) {
                temp.add(ImageContentData.addImages(imgUrl));
            }
            loadImageContent(PlayCourseActivity.this, temp, false);
        }
    }

    private void loadImageContent(Context context, ArrayList<ImageContentData> imageContentList, boolean showThumbnailList) {
        if (imageContentList != null && imageContentList.size() > 0) {
            if (imageContentList.size() == 1) {
                imgPrevBtn.setVisibility(View.INVISIBLE);
                imgNextBtn.setVisibility(View.INVISIBLE);
            } else {
                imgPrevBtn.setVisibility(View.INVISIBLE);
                imgNextBtn.setVisibility(View.VISIBLE);
            }
            //load image slider at upper side
            imagePagerAdapter = new CoursePlayImagePagerAdapter(getSupportFragmentManager(), imageContentList);
            if (imageContentList.get(0).getImg_url() != null && !imageContentList.get(0).getImg_url().isEmpty()) {
                Picasso.with(context)
                        .load(imageContentList.get(0).getImg_url())
                        .error(R.drawable.edbrix_logo)
                        .into(imgPreview);
            }
            imgPreview.setVisibility(View.GONE);
            imgViewPager.setAdapter(imagePagerAdapter);
            imageContentLayout.setVisibility(View.VISIBLE);

            if (showThumbnailList) {
                //load horizontal image drawer
                ImageDrawerAdapter imageDrawerAdapter = new ImageDrawerAdapter(context, imageContentList, new ImageDrawerAdapter.ImageSelectionListener() {
                    @Override
                    public void onSelect(ImageContentData imageContentData, int position) {
                        imgViewPager.setCurrentItem(position);
                    }
                });

                imgDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                imgDrawerRecyclerView.setAdapter(imageDrawerAdapter);
                imgDrawerRecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            imageContentLayout.setVisibility(View.GONE);
        }
    }

    private void showSurveyProgress(int qIndex, int qCount) {

        int percentage = (qIndex * 100) / qCount;

        surveyProgressLayout.setVisibility(View.VISIBLE);
        txtSurveyProgress.setText(percentage + "% Completed");
        pbarSurvey.setProgress(percentage);
    }

    //Stop Countdown method
    private void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    //Start Countdown method
    private void startTimer(int seconds) {
        timerLayout.setVisibility(View.VISIBLE);
        txtSubmitBtn.setVisibility(View.GONE);
        countDownTimer = new CountDownTimer((seconds * 1000), 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                txtTimer.setText(hms);//set text
            }

            public void onFinish() {
                txtTimer.setText("00:00:00");//set text
                countDownTimer = null;//set CountDownTimer to null
                timerLayout.setVisibility(View.GONE);
                txtSubmitBtn.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void setListeners() {
        txtSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mJSONArray = new JSONArray();
                for (int i = 0; i < choiceInput.size(); i++) {
                    mJSONArray.put(choiceInput.get(i).getJSONObject());
                }
               /* showToast(mJSONArray.toString());
                JSONObject po = new JSONObject();
                try {
                    po.put("choiceId", mJSONArray);
                    showToast(po.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                stopCountdown();
                if (playCourseContentResponseData != null) {

                    if (playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Test) ||
                            playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Survey)) {
                        if (playCourseContentResponseData.getCourse_content().getSubmit_data().getNext_question_id().equalsIgnoreCase("0")) {
                            questionIndex = 0;
                        } else {
                            questionIndex++;
                        }

                    }

                    String questionId = "0";
                    if (playCourseContentResponseData.getCourse_content().getSubmit_data() != null) {
                        if (playCourseContentResponseData.getCourse_content().getSubmit_data().getQuestion_id() != null && playCourseContentResponseData.getCourse_content().getSubmit_data().getQuestion_id().length() > 0) {
                            questionId = playCourseContentResponseData.getCourse_content().getSubmit_data().getQuestion_id();
                        }
                    }

                    if (playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Assignment)) {

                        if (assignmentFile != null && !edtAssignmentFile.getText().toString().isEmpty()) {
                            uploadAssignmentSubmit(assignmentFile);
                        } else {
                            if (playCourseContentResponseData != null) {
                                submitPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(),
                                        playCourseContentResponseData.getContent_id(),
                                        playCourseContentResponseData.getQuestion_id(),
                                        playCourseContentResponseData.getContent_type(),
                                        playCourseContentResponseData.getContentcomplete_type_id(),
                                        null,
                                        null,
                                        mJSONArray);
                            }
                        }

                    } else {
                        submitPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(),
                                playCourseContentResponseData.getContent_id(),
                                questionId,
                                playCourseContentResponseData.getContent_type(),
                                playCourseContentResponseData.getContentcomplete_type_id(),
                                editTxtLongAns.getText().toString(),
                                null,
                                mJSONArray);
                    }
                }
            }
        });

        imgPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgViewPager.getCurrentItem() > 0) {
                    imgViewPager.setCurrentItem(imgViewPager.getCurrentItem() - 1);
                }
            }
        });

        imgNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgViewPager.getCurrentItem() < imgViewPager.getAdapter().getCount() - 1) {
                    imgViewPager.setCurrentItem(imgViewPager.getCurrentItem() + 1);
                }
            }
        });

        imgContentNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playCourseContentResponseData.getErrorCode() != null && playCourseContentResponseData.getErrorCode().length() > 0) {
//                            Timber.d("Error: %s", response.getErrorCode());
                    showToast(playCourseContentResponseData.getErrorMessage());
                } else {
                    if ((playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Test) ||
                            playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Survey)) &&
                            !playCourseContentResponseData.getCourse_content().getSubmit_data().getNext_question_id().equalsIgnoreCase("0")) {
                        showBusyProgress();

                        if (courseContentDataList != null && !courseContentDataList.isEmpty())
                            setQuestionAchievementIndex(playCourseContentResponseData.getContent_id(), courseContentDataList);

                        getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), playCourseContentResponseData.getContent_id(), playCourseContentResponseData.getCourse_content().getSubmit_data().getNext_question_id(), "0");
                        questionIndex++;
                    } else {
                        showBusyProgress();

                        if (courseContentDataList != null && !courseContentDataList.isEmpty())
                            setQuestionAchievementIndex(playCourseContentResponseData.getNext_content_id(), courseContentDataList);

                        getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), playCourseContentResponseData.getNext_content_id(), "0", "0");
                        questionIndex = 0;
                    }
                }
            }
        });

        txtSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgContentNextBtn.callOnClick();
            }
        });

        imgContentPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playCourseContentResponseData.getErrorCode() != null && playCourseContentResponseData.getErrorCode().length() > 0) {
//                            Timber.d("Error: %s", response.getErrorCode());
                    showToast(playCourseContentResponseData.getErrorMessage());
                } else {
                    showBusyProgress();

                    if (courseContentDataList != null && !courseContentDataList.isEmpty())
                        setQuestionAchievementIndex(playCourseContentResponseData.getPrev_content_id(), courseContentDataList);

                    getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), playCourseContentResponseData.getPrev_content_id(), "0", "0");
                }
            }
        });

        radioGroupLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                choiceInput.clear();
                choiceInput.add(ChoicesInputData.addChoiceData("" + id));
                txtSubmitBtn.setEnabled(true);
            }
        });

        imgViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.v("ImageContent", "onPageScrolled : position :" + position);

            }

            @Override
            public void onPageSelected(int position) {
                Log.v("ImageContent", "onPageSelected : position : " + position);

                if (imgDrawerRecyclerView.getAdapter() != null)
                    ((ImageDrawerAdapter) imgDrawerRecyclerView.getAdapter()).setSelectedIndex(position);

                if (position == 0 && imagePagerAdapter.getCount() > 1) {
                    imgPrevBtn.setVisibility(View.INVISIBLE);
                    imgNextBtn.setVisibility(View.VISIBLE);
                } else if (position == (imagePagerAdapter.getCount() - 1)) {
                    imgPrevBtn.setVisibility(View.VISIBLE);
                    imgNextBtn.setVisibility(View.INVISIBLE);
                } else {
                    imgPrevBtn.setVisibility(View.VISIBLE);
                    imgNextBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.v("AudioRecorder", "onPageScrollStateChanged : state : " + state);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAlertDialogManager().Dialog(courseItem.getTitle(), "Confirm to discontinue..?", "YES", "CANCEL", new AlertDialogManager.onTwoButtonClickListner() {
                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onPositiveClick() {
                        finish();
                    }
                }).show();
            }
        });

        editTxtLongAns.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && charSequence.toString().trim().length() > 0) {
                    txtSubmitBtn.setEnabled(true);
                } else {
                    txtSubmitBtn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnDownloadASContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playCourseContentResponseData != null && playCourseContentResponseData.getCourse_content() != null) {

                    if (playCourseContentResponseData.getCourse_content().getAssignment_content() != null && !playCourseContentResponseData.getCourse_content().getAssignment_content().isEmpty()) {
                        downloadAssignmentContent(playCourseContentResponseData.getCourse_content().getAssignment_content());
                    } else {
                        showToast("No content url found");
                    }

                }
            }
        });

        btnBrowseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseFileForAssignmentUpload();
            }
        });

    }

    private void showSessionEventList(ArrayList<TrainingSessionEventContentData> trainingSessionEventContentDataList) {
//        courseContentDataList = contentDataArrayList;
        SessionEventListAdapter sessionEventListAdapter = new SessionEventListAdapter(PlayCourseActivity.this, trainingSessionEventContentDataList, new SessionEventListAdapter.SessionEventItemListener() {
            @Override
            public void onSessionEventSelected(final TrainingSessionEventContentData sessionEventContentData) {
                final MaterialDialog dialog = new MaterialDialog.Builder(PlayCourseActivity.this)
                        .customView(R.layout.custom_dialog_view_training_session, true)
                        .positiveText(R.string.connect)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                if (sessionEventContentData.getSessionId() != null)
                                    sessionID = sessionEventContentData.getSessionId();

                                if (sessionEventContentData.getSessionToken() != null)
                                    sessionTOKEN = sessionEventContentData.getSessionToken();

                                if (sessionEventContentData.getConnectType().equals("ZOOM")) {
                                    if (SettingsMy.getActiveUser() != null && SettingsMy.getActiveUser().getUserType().equals("L")) {
                                        joinZoomMeeting(PlayCourseActivity.this, sessionID);
                                    } else {
                                        startZoomMeeting(PlayCourseActivity.this, sessionID);
                                    }
                                } else if (sessionEventContentData.getConnectType().equals(Constants.availabilityType_TrainingSession)) {
                                    Intent tokboxIntent = new Intent(PlayCourseActivity.this, TokBoxActivity.class);
                                    tokboxIntent.putExtra(Constants.TolkBox_SessionId, sessionID);
                                    tokboxIntent.putExtra(Constants.TolkBox_Token, sessionTOKEN);
                                    startActivity(tokboxIntent);
                                } else {
                                    if (sessionEventContentData.getConnectURL() != null && sessionEventContentData.getConnectURL().length() > 0) {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(sessionEventContentData.getConnectURL()));
                                        startActivity(i);
                                    } else {
                                        showToast("Connection URL not found. Please try again later.");
                                    }
                                }
                            }
                        })
                        .build();

                TextView title = (TextView) dialog.getCustomView().findViewById(R.id.session_name);
                title.setText(sessionEventContentData.getTitle());

                TextView day = (TextView) dialog.getCustomView().findViewById(R.id.session_day);
                day.setText(sessionEventContentData.getSessionEvtDay());

                TextView date = (TextView) dialog.getCustomView().findViewById(R.id.session_date);
                date.setText(sessionEventContentData.getSessionEvtMonth() + ", " + sessionEventContentData.getSessionEvtYear());

                TextView time = (TextView) dialog.getCustomView().findViewById(R.id.session_time);
                time.setText(sessionEventContentData.getStartDateTime() + " - " + sessionEventContentData.getEndDateTime());

                TextView place = (TextView) dialog.getCustomView().findViewById(R.id.session_place);
                place.setText(sessionEventContentData.getLocation());

                TextView instructorName = (TextView) dialog.getCustomView().findViewById(R.id.instructor_name);
                instructorName.setText("By " + sessionEventContentData.getInstructorName());

                RoundedImageView instructorImg = (RoundedImageView) dialog.getCustomView().findViewById(R.id.instructor_pic);

                if (sessionEventContentData.getInstructorPicUrl() != null && !sessionEventContentData.getInstructorPicUrl().isEmpty()) {
                    Picasso.with(PlayCourseActivity.this)
                            .load(sessionEventContentData.getInstructorPicUrl())
                            .fit()
                            .error(R.mipmap.user_profile)
                            .into(instructorImg);
                }


                ImageButton imageButton = (ImageButton) dialog.getCustomView().findViewById(R.id.custom_cancle);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });
        sessionEventRecyclerView.setVisibility(View.VISIBLE);
        sessionEventRecyclerView.setLayoutManager(new LinearLayoutManager(PlayCourseActivity.this));
        sessionEventRecyclerView.setAdapter(sessionEventListAdapter);

//        if(SettingsMy.getActiveUser().getUserType().equals("L")){
//            txtSubmitBtn.setVisibility(View.GONE);
//            txtSkipBtn.setVisibility(View.VISIBLE);
//            txtSkipBtn.setText("Complete");
//        }
    }

    @Override
    public void onBackPressed() {
        btnBack.callOnClick();
    }

    @Override
    public void onDestroy() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (zoomSDK.isInitialized()) {
            MeetingService meetingService = zoomSDK.getMeetingService();
            meetingService.removeListener(this);
        }

        super.onDestroy();
    }


    @Override
    public void onMeetingEvent(int meetingEvent, int errorCode,
                               int internalErrorCode) {

        Log.i("TAG", "onMeetingEvent, meetingEvent=" + meetingEvent + ", errorCode=" + errorCode
                + ", internalErrorCode=" + internalErrorCode);

        if (meetingEvent == MeetingEvent.MEETING_CONNECT_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
            showToast("Version of ZoomSDK is too low!");
        }

        if (mbPendingStartMeeting && meetingEvent == MeetingEvent.MEETING_DISCONNECTED) {
            mbPendingStartMeeting = false;

            if (SettingsMy.getActiveUser() != null && !SettingsMy.getActiveUser().getUserType().equals("L")) {
                startZoomMeeting(PlayCourseActivity.this, sessionID);
            }
        }
    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.i("TAG", "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

        if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            showToast("Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode);
        } else {
            // Toast.makeText(context, "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();
            registerMeetingServiceListener();
        }
    }

    private void registerMeetingServiceListener() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        MeetingService meetingService = zoomSDK.getMeetingService();
        if (meetingService != null) {
            meetingService.addListener(this);
        }
    }


    public void joinZoomMeeting(Context context, String meetingID) {

        String meetingPassword = "";
        String DISPLAY_NAME = "User";

        if (meetingID.length() == 0) {
            showToast("Zoom meeting ID not found. Unable to connect meeting.");
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            showToast("ZoomSDK has not been initialized successfully. Please try again later.");
            return;
        }


        MeetingService meetingService = zoomSDK.getMeetingService();

        JoinMeetingOptions opts = new JoinMeetingOptions();
//        opts.no_meeting_end_message = true;
//		opts.no_driving_mode = true;
//		opts.no_invite = true;
//		opts.no_meeting_end_message = true;
//		opts.no_titlebar = true;
//		opts.no_bottom_toolbar = true;
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;
//		opts.no_share = true;
//		opts.invite_options = InviteOptions.INVITE_VIA_EMAIL + InviteOptions.INVITE_VIA_SMS;
//		opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE;
//		opts.no_audio = true;
//		opts.no_video = true;
//		opts.no_meeting_error_message = true;
//		opts.participant_id = "participant id";


        DISPLAY_NAME = SettingsMy.getActiveUser() != null ? SettingsMy.getActiveUser().getFirstName() : "User";
        int ret = meetingService.joinMeeting(context, meetingID, DISPLAY_NAME, meetingPassword, opts);
        Log.i("TAG", "onClickBtnJoinMeeting, ret=" + ret);

    }

    public void startZoomMeeting(Context context, String meetingID) {

        User user = SettingsMy.getActiveUser();

        if (meetingID.length() == 0) {
            showToast("Zoom meeting number not found. Unable to connect meeting.");
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            showToast("ZoomSDK has not been initialized successfully. Please try again later.");
            return;
        }

        final MeetingService meetingService = zoomSDK.getMeetingService();

        if (meetingService.getMeetingStatus() != MeetingStatus.MEETING_STATUS_IDLE) {
            long lMeetingNo = 0;
            try {
                lMeetingNo = Long.parseLong(meetingID);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid meeting number: " + meetingID, Toast.LENGTH_LONG).show();
                return;
            }

            if (meetingService.getCurrentMeetingID() == lMeetingNo) {
                meetingService.returnToMeeting(context);
                return;
            }

            new android.app.AlertDialog.Builder(context)
                    .setMessage("Do you want to leave current meeting and start another?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mbPendingStartMeeting = true;
                            meetingService.leaveCurrentMeeting(false);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }

        StartMeetingOptions opts = new StartMeetingOptions();
//		opts.no_driving_mode = true;
//		opts.no_invite = true;
//		opts.no_meeting_end_message = true;
//		opts.no_titlebar = true;
//		opts.no_bottom_toolbar = true;
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;
//		opts.no_share = true;
//		opts.invite_options = InviteOptions.INVITE_ENABLE_ALL;
//		opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE + MeetingViewsOptions.NO_BUTTON_VIDEO;
//		opts.no_audio = true;
//		opts.no_video = true;
//		opts.no_meeting_error_message = true;

        assert user != null;
        int ret = meetingService.startMeeting(context, user.getZoomUserId(), user.getZoomUserToken(), MeetingService.USER_TYPE_API_USER, meetingID, user.getFirstName(), opts);
        /*int ret = meetingService.startMeeting(context, "xch6jAJ-Tiqcf7ct-LDxEw",
                        "eRr1c1RQuIlqAIyqiactTFf1_oghkN8-cgTXTyy2rq0.BgMYaUE4UzJtK2VUREZsVGJ1WXdPMzQrZz09AAAMM0NCQXVvaVlTM3M9",
                        STYPE, "469520738", "USER NAME", opts);*/

        Log.i("TAG", "onClickBtnStartMeeting, ret=" + ret);
    }

    private void browseFileForAssignmentUpload() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            onPickFile();
//            onPickImages();
        } else {
            EasyPermissions.requestPermissions(this,
                    "This app needs to access your Documents.",
                    REQUEST_PERMISSION_EXTERNAL,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickFile() {
        String[] ppts = {".ppt", ".pptx"};
        String[] pdfs = {".pdf"};
        String[] docs = {".doc", ".docx"};
        String[] xls = {".xls", ".xlsx"};
        String[] jpg = {".jpg", ".jpeg", ".png"};
        String[] gif = {".gif"};
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(assignmentFilePaths)
                .setActivityTheme(R.style.AppTheme)
                .addFileSupport("DOC", docs, R.mipmap.doc_icon)
                .addFileSupport("PDF", pdfs, R.mipmap.pdf_icon)
                .addFileSupport("PPT", ppts, R.mipmap.ppt_icon)
                .addFileSupport("XLS", xls, R.mipmap.xls_icon)
                .addFileSupport("JPG/JPEG/PNG", jpg, R.drawable.image_placeholder)
                .addFileSupport("GIF", gif, R.drawable.image_placeholder)
                .enableDocSupport(false)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickFile(this);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickImages() {
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(assignmentFilePaths)
                .setActivityTheme(R.style.AppTheme)
                .enableVideoPicker(false)
                .enableCameraSupport(false)
                .enableImagePicker(true)
                .showGifs(true)
                .showFolderView(true)
                .enableSelectAll(false)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickPhoto(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    assignmentFilePaths = new ArrayList<>();
                    assignmentFilePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));


                    if (!assignmentFilePaths.isEmpty()) {
                        assignmentFile = new File(assignmentFilePaths.get(0));
                        edtAssignmentFile.setText(assignmentFile.getName());
                    }

//                    filePath = Uri.fromFile(new File(assignmentFilePaths.get(0)));
//                    Glide.with(context).load(filePath)
//                            .apply(RequestOptions
//                                    .centerCropTransform()
//                                    .dontAnimate()
//                                    .override(imageSize, imageSize)
//                                    .placeholder(R.mipmap.document_icon))
//                            .thumbnail(0.5f)
//                            .into(imageView);
//
//                    imageView.setClickable(false);
//                    fileExtension = docPaths.get(0).substring(docPaths.get(0).lastIndexOf("."));
//                    Log.d("TAG", docPaths.toString() + " _-_ " + fileExtension);
                    // uploadToEdbrix(uri);
                }
                break;
        }
    }

    private void uploadAssignmentSubmit(final File fileObject) {
        try {

            if (fileObject != null) {
//                showBusyProgress("Uploading files..");
                showBusyProgress();

                String userId = SettingsMy.getActiveUser().getId();// get user Id from active user
                StorageReference childRef = storageRef.child("enterprisecoursecontent/" + userId + "/" + fileObject.getName());

                uploadTask = childRef.putFile(Uri.fromFile(fileObject));

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        hideBusyProgress();
                        if (playCourseContentResponseData != null) {
                            submitPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(),
                                    playCourseContentResponseData.getContent_id(),
                                    playCourseContentResponseData.getQuestion_id(),
                                    playCourseContentResponseData.getContent_type(),
                                    playCourseContentResponseData.getContentcomplete_type_id(),
                                    null,
                                    fileObject.getName(),
                                    mJSONArray);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideBusyProgress();
                        Log.v("Upload", "Fail Exception :" + e.getMessage());
                        showToast(e.getMessage());
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                        showBusyProgress("Uploading completed " + (int) progress + "%");
                    }
                });

            } else {
                showToast("NO file found");
            }
        } catch (Exception e) {
            showToast(e.getMessage());
            Log.v("Upload", e.getMessage());
        }

    }

    private void downloadAssignmentContent(String assignmentContent) {
        new DownloadFileFromURL().execute(assignmentContent);
    }

    /**
     * https://storage.googleapis.com/edbrixcbuilder/storage/uploads/coursecontent/assignment/24666/59d36d1a5b964.jpg
     * Background Async Task to download file
     */
    private class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showBusyProgress("Downloading files..");
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                File downloadStorageDir = new File(Environment.getExternalStorageDirectory(), "/" + getResources().getString(R.string.app_name) + "/Downloads/");
                if (!downloadStorageDir.exists()) {
                    downloadStorageDir.mkdirs();
                }

                //https://docs.google.com/viewer?embedded=true&url=
                String urlString = f_url[0];
                if (urlString.contains("https://docs.google.com/viewer?embedded=true&url=")) {
                    urlString = urlString.replace("https://docs.google.com/viewer?embedded=true&url=", "");
                }

                URL url = new URL(urlString);
//                URL url = new URL("https://docs.google.com/viewer?embedded=true&url=https://storage.googleapis.com/edbrixcbuilder/storage/uploads/coursecontent/assignment/28228/5a4a14683eea5.docx");
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream(downloadStorageDir.getPath() + "/" + urlString.substring(urlString.lastIndexOf('/') + 1));

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                return downloadStorageDir.getPath() + "/" + urlString.substring(urlString.lastIndexOf('/') + 1);

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
//            pDialog.setProgress(Integer.parseInt(progress[0]));
//            showBusyProgress("Downloading completed " + progress[0] + "%");
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
//            dismissDialog(progress_bar_type);
            hideBusyProgress();
            showToast("Download Completed : file path : " + file_url);
//            openFolder(file_url);

            // Displaying downloaded image into image view
            // Reading image path from sdcard
//            String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
            // setting downloaded into image view
//            my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        }

    }

    public void openFolder(String path) {
        File file = new File(path);
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(fileExt(path).substring(1));
        newIntent.setDataAndType(Uri.fromFile(file), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            showToast("No handler for this type of file.");
        }
    }

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }
}


