package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.CoursePlayImagePagerAdapter;
import com.edbrix.enterprise.Adapters.ImageChoiceListAdapter;
import com.edbrix.enterprise.Adapters.ImageDrawerAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.Interfaces.ImageChoiceActionListener;
import com.edbrix.enterprise.Models.ChoicesData;
import com.edbrix.enterprise.Models.ChoicesInputData;
import com.edbrix.enterprise.Models.CourseListResponseData;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.ImageContentData;
import com.edbrix.enterprise.Models.PlayCourseContentResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Utils.CustomViewPager;
import com.edbrix.enterprise.Utils.CustomWebView;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.commons.GlobalMethods;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.zipow.videobox.confapp.GLImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;


public class PlayCourseActivity extends BaseActivity {

    public static final String courseItemBundleKey = "courseItem";

    private Courses courseItem;

    private LinearLayout checkboxGroupLayout;
    private LinearLayout surveyProgressLayout;
    private LinearLayout imageContentLayout;
    private LinearLayout timerLayout;
    private LinearLayout audioContentLayout;
    private RelativeLayout imageChoiceGroupLayout;
    private RadioGroup radioGroupLayout;

    private TextView title;
    private TextView txtContentType;
    private TextView txtContentDesc;
    private TextView txtQuestion;
    private TextView txtTimer;
    private TextView txtSubmitBtn;
    private TextView txtSurveyProgress;

    private EditText editTxtLongAns;

    private ImageView imgContentPrevBtn;
    private ImageView imgContentNextBtn;

    private ImageView imgPrevBtn;
    private ImageView imgNextBtn;

    private ImageView imgPreview;

    private ProgressBar pbarSurvey;
    private CheckBox checkSubmit;
    private CustomViewPager imgViewPager;
    private RecyclerView imageChoiceListView;
    private RecyclerView imgDrawerRecyclerView;

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

//    private ImageLoader imageLoader; // Get singleton instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_course);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = (TextView) toolbar.findViewById(R.id.title);
        txtContentType = (TextView) findViewById(R.id.txtContentType);
        txtContentDesc = (TextView) findViewById(R.id.txtContentDesc);
        txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        txtSubmitBtn = (TextView) findViewById(R.id.txtSubmitBtn);
        editTxtLongAns = (EditText) findViewById(R.id.editTxtLongAns);
        txtTimer = (TextView) findViewById(R.id.txtTimer);
        txtSurveyProgress = (TextView) findViewById(R.id.txtSurveyProgress);

        imgPrevBtn = (ImageView) findViewById(R.id.imgPrevBtn);
        imgNextBtn = (ImageView) findViewById(R.id.imgNextBtn);

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
        imageChoiceGroupLayout = (RelativeLayout) findViewById(R.id.imageChoiceGroupLayout);
        radioGroupLayout = (RadioGroup) findViewById(R.id.radioGroupLayout);
        imageChoiceListView = (RecyclerView) findViewById(R.id.imageChoiceListView);
        imgDrawerRecyclerView = (RecyclerView) findViewById(R.id.imgDrawerRecyclerView);

        courseItem = (Courses) getIntent().getSerializableExtra(courseItemBundleKey);

        if (courseItem != null) {
            title.setText(courseItem.getTitle());
            //set Course Details
//            setCourseDetails();
            showBusyProgress();
            getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), "0", "0");
        } else {
            //show message and finish activity
        }

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

    /**
     * Get course list from server and load data
     *
     * @param activeUser Object of User class ie. logged active user.
     * @param courseId   CourseId i.e. Id of selected Course
     * @param contentId  ContentId i.e. Id of content from Course
     * @param questionId QuestionId i.e. default question id of Course
     */
    private void getPlayCourseContent(final User activeUser, String courseId, String contentId, String questionId) {
        try {

            JSONObject jo = new JSONObject();

            jo.put("UserId", activeUser.getId());
            jo.put("AccessToken", activeUser.getAccessToken());
            jo.put("courseId", courseId);
            jo.put("contentId", contentId);
            jo.put("questionId", questionId);


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

    private void submitPlayCourseContent(final User activeUser, final String courseId, String contentId, String questionId, String contentType, String contentCompleteTypeId, String longAnswer, JSONArray choiceJsonArray) {
        try {
            showBusyProgress();

            JSONObject jo = new JSONObject();

            jo.put("UserId", activeUser.getId());
            jo.put("AccessToken", activeUser.getAccessToken());
            jo.put("courseId", courseId);
            jo.put("contentId", contentId);
            jo.put("questionId", questionId);
            jo.put("contentType", contentType);
            jo.put("contentcomplete_type_id", contentCompleteTypeId);
            jo.put("choiceId", choiceJsonArray);
            if (longAnswer != null && longAnswer.length() > 0) {
                jo.put("longAnswer", longAnswer);
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
                                getPlayCourseContent(SettingsMy.getActiveUser(), courseId, response.getNext_content_id(), response.getQuestion_id());
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
        txtSubmitBtn.setVisibility(View.VISIBLE);
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
        if (response.getNext_content_id().equalsIgnoreCase("0")) {
            imgContentNextBtn.setVisibility(View.INVISIBLE);
        } else {
            imgContentNextBtn.setVisibility(View.VISIBLE);
        }

        choiceInput = new ArrayList<>();
//        txtContentDesc.setText(Html.fromHtml(response.getCourse_content().getDescription()));
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
                loadWebContent(response.getCourse_content().getVideo_content());
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
        }

//        loadWebContent(response.getCourse_content().getWebContent());

        if (response.getCourse_content().getSubmit_type().equalsIgnoreCase(Constants.submitType_Check)) {
            checkSubmit.setVisibility(View.VISIBLE);
        } else if (response.getCourse_content().getSubmit_type().equalsIgnoreCase(Constants.submitType_Timer)) {
            checkSubmit.setVisibility(View.GONE);
            startTimer(Integer.parseInt(response.getCourse_content().getSubmit_data().getTime()));
        } else if (response.getCourse_content().getSubmit_type().equalsIgnoreCase(Constants.submitType_Question)) {
            checkSubmit.setVisibility(View.GONE);
//            txtQuestion.setVisibility(View.VISIBLE);
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
                        showToast("Added Id :" + compoundButton.getId());
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
                        showToast("Removed Id :" + compoundButton.getId());
                        for (int j = 0; j < choiceSize; j++) {
                            if (choiceInput.get(j).getId().equalsIgnoreCase("" + compoundButton.getId())) {
                                choiceInput.remove(j);
                                break;
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
//                    String qId = playCourseContentResponseData.getQuestion_id();
//                    if (playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Test) || playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Survey)) {
//                        qId = playCourseContentResponseData.getCourse_content().getSubmit_data().getQuestion_id();
//                    }

                    if (playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Test) ||
                            playCourseContentResponseData.getContent_type().equalsIgnoreCase(Constants.contentType_Survey)) {
                        if (playCourseContentResponseData.getCourse_content().getSubmit_data().getNext_question_id().equalsIgnoreCase("0")) {
                            questionIndex = 0;
                        } else {
                            questionIndex++;
                        }

                    }

                    submitPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(),
                            playCourseContentResponseData.getContent_id(),
                            playCourseContentResponseData.getCourse_content().getSubmit_data().getQuestion_id(),
                            playCourseContentResponseData.getContent_type(),
                            playCourseContentResponseData.getContentcomplete_type_id(),
                            editTxtLongAns.getText().toString(),
                            mJSONArray);
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
                        getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), playCourseContentResponseData.getContent_id(), playCourseContentResponseData.getCourse_content().getSubmit_data().getNext_question_id());
                        questionIndex++;
                    } else {
                        showBusyProgress();
                        getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), playCourseContentResponseData.getNext_content_id(), "0");
                        questionIndex = 0;
                    }
                }
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
                    getPlayCourseContent(SettingsMy.getActiveUser(), courseItem.getId(), playCourseContentResponseData.getPrev_content_id(), "0");
                }
            }
        });

        radioGroupLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                choiceInput.clear();
                choiceInput.add(ChoicesInputData.addChoiceData("" + id));
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

    }
}
