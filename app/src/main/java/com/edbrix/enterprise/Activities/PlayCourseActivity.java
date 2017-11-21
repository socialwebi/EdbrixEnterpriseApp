package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.Models.ChoicesData;
import com.edbrix.enterprise.Models.CourseListResponseData;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.PlayCourseContentResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Utils.CustomViewPager;
import com.edbrix.enterprise.Utils.CustomWebView;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.commons.GlobalMethods;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zipow.videobox.confapp.GLImage;

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
    private LinearLayout radioGroupLayout;

    private TextView title;
    private TextView txtContentType;
    private TextView txtContentDesc;
    private TextView txtQuestion;
    private TextView txtTimer;
    private TextView txtSubmitBtn;

    private ImageView imgPrevBtn;
    private ImageView imgNextBtn;
    private ImageView imgPreview;

    private CheckBox checkSubmit;
    private CustomViewPager imgViewPager;

    private CustomWebView mediaWebView;

    private CustomWebView contentDescWebView;

    private CountDownTimer countDownTimer;

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
        txtTimer = (TextView) findViewById(R.id.txtTimer);

        imgPrevBtn = (ImageView) findViewById(R.id.imgPrevBtn);
        imgNextBtn = (ImageView) findViewById(R.id.imgNextBtn);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        checkSubmit = (CheckBox) findViewById(R.id.checkSubmit);

        imgViewPager = (CustomViewPager) findViewById(R.id.imgViewPager);
        mediaWebView = (CustomWebView) findViewById(R.id.mediaWebView);
        contentDescWebView = (CustomWebView) findViewById(R.id.contentDescWebView);

        mediaWebView.getSettings().setJavaScriptEnabled(true);
        mediaWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mediaWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mediaWebView.setWebChromeClient(new WebChromeClient());

        contentDescWebView.getSettings().setJavaScriptEnabled(true);
        contentDescWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        contentDescWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        contentDescWebView.setWebChromeClient(new WebChromeClient());

//        setupWebView();
        checkboxGroupLayout = (LinearLayout) findViewById(R.id.checkboxGroupLayout);
        radioGroupLayout = (RadioGroup) findViewById(R.id.radioGroupLayout);

        courseItem = (Courses) getIntent().getSerializableExtra(courseItemBundleKey);

        txtSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCountdown();
            }
        });

        if (courseItem != null) {
            title.setText(courseItem.getTitle());
            //set Course Details
//            setCourseDetails();
            getPlayCourseContent(SettingsMy.getActiveUser(), "", "", "");
        } else {
            //show message and finish activity
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

//            jo.put("UserId", activeUser.getId());
//            jo.put("AccessToken", activeUser.getAccessToken());
//            jo.put("courseId", courseId);
//            jo.put("contentId", contentId);
//            jo.put("questionId", questionId);

           /* jo.put("UserId", "1");
            jo.put("AccessToken", "sdfsdf");
            jo.put("courseId", "1687");
            jo.put("contentId", "21856");
            jo.put("questionId", "0");*/

            jo.put("UserId", "1");
            jo.put("AccessToken", "sdfsdf");
            jo.put("courseId", "1774");
            jo.put("contentId", "23230");
            jo.put("questionId", "0");

//            {"UserId":"1",
//                    "AccessToken":"sdfsdf",
//                    "courseId":"1774",
//                    "contentId":"23230",
//                    "questionId":"0"
//            }

//            jo.put("UserId", "1");
//            jo.put("AccessToken", "NTI1LTg1REEyUzMtQURTUzVELUVJNUI0QkM1MTE=");
//            jo.put("UserType", "I");
//            jo.put("DeviceType", "mob");
//            jo.put("DataType", "course");
//            jo.put("Page", "1");

//            jo.put("UserId", "3");
//            jo.put("AccessToken", "NTI1LTg1REEyUzMtQURTUzVELUVJNUI0QkM1MTM=");
//            jo.put("UserType", "L");
//            jo.put("DeviceType", deviceType);
//            jo.put("DataType", dataType);
//            jo.put("Page", pageNo);


//        if (BuildConfig.DEBUG) Timber.d("getCourseList Request Param: %s", jo.toString());

            GsonRequest<PlayCourseContentResponseData> getPlayCourseContentRequest = new GsonRequest<>(Request.Method.POST, Constants.playCourseContent, jo.toString(), PlayCourseContentResponseData.class,
                    new Response.Listener<PlayCourseContentResponseData>() {
                        @Override
                        public void onResponse(@NonNull PlayCourseContentResponseData response) {
//                        Timber.d("response: %s", response.toString());
                            Log.v("ResponseData", response.toString());

                            if (response.getErrorCode() != null && response.getErrorCode().length() > 0) {
//                            Timber.d("Error: %s", response.getErrorCode());

                            } else {
                                setContentData(response);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Timber.d("Error: %s", error.getMessage());
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            getPlayCourseContentRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getPlayCourseContentRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getPlayCourseContentRequest, "playcoursecontent");
        } catch (JSONException e) {
            Timber.e(e, "Parse getCourseList exception");
            showToast("Something went wrong. Please try again later.");
        }
    }

    private void setContentData(PlayCourseContentResponseData response) {
//        txtContentDesc.setText(Html.fromHtml(response.getCourse_content().getDescription()));
        loadContentDescWebView(response.getCourse_content().getDescription());
//        if(response.getContent_type().equalsIgnoreCase(Constants.contentType_WC)){
//            txtContentType.setText(getString(R.string.web_content));
//            mediaWebView.setVisibility(View.VISIBLE);
//        }else{
//            mediaWebView.setVisibility(View.GONE);
//        }

        // Course content load
        loadWebContent(response.getCourse_content().getWebContent());

        if (response.getCourse_content().getSubmit_type().equalsIgnoreCase(Constants.submitType_Check)) {
            checkSubmit.setVisibility(View.VISIBLE);
        } else if (response.getCourse_content().getSubmit_type().equalsIgnoreCase(Constants.submitType_Timer)) {
            checkSubmit.setVisibility(View.GONE);
            txtQuestion.setText(response.getCourse_content().getSubmit_data().getTime());
            startTimer(Integer.parseInt(response.getCourse_content().getSubmit_data().getTime()));
        } else if (response.getCourse_content().getSubmit_type().equalsIgnoreCase(Constants.submitType_Question)) {
            checkSubmit.setVisibility(View.GONE);
            txtQuestion.setVisibility(View.VISIBLE);
            txtQuestion.setText("Q. "+response.getCourse_content().getSubmit_data().getTitle());
            if (response.getCourse_content().getSubmit_data().getChoices() != null && response.getCourse_content().getSubmit_data().getChoices().size() > 0) {
                if (response.getCourse_content().getSubmit_data().getType().equalsIgnoreCase(Constants.submitDataType_TrueFalse)) {
                    radioGroupLayout.setVisibility(View.VISIBLE);
                    addTrueFalseRadioButton(response.getCourse_content().getSubmit_data().getChoices());
                } else if (response.getCourse_content().getSubmit_data().getType().equalsIgnoreCase(Constants.submitDataType_SingleChoice)) {
                    radioGroupLayout.setVisibility(View.VISIBLE);
                    addSingleChoiceRadioButton(response.getCourse_content().getSubmit_data().getChoices());
                } else if (response.getCourse_content().getSubmit_data().getType().equalsIgnoreCase(Constants.submitDataType_MultiChoice)) {
                    checkboxGroupLayout.setVisibility(View.VISIBLE);
                    addMultiChoiceCheckBox(response.getCourse_content().getSubmit_data().getChoices());
                }
            }
        }
    }

    private void addTrueFalseRadioButton(ArrayList<ChoicesData> choiceList) {
        radioGroupLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < choiceList.size(); i++) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(Integer.parseInt(choiceList.get(i).getId()));
            rdbtn.setText(choiceList.get(i).getChoice());
            radioGroupLayout.addView(rdbtn);
        }
    }

    private void addSingleChoiceRadioButton(ArrayList<ChoicesData> choiceList) {
        radioGroupLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < choiceList.size(); i++) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(Integer.parseInt(choiceList.get(i).getId()));
            rdbtn.setText(choiceList.get(i).getChoice());
            radioGroupLayout.addView(rdbtn);
        }
    }

  /*  private void addImageChoiceRadioButton(ArrayList<ChoicesData> choiceList) {
        imageLoader = ImageLoader.getInstance();
        radioGroupLayout.setVisibility(View.VISIBLE);
        radioGroupLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < choiceList.size(); i++) {
            final RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(Integer.parseInt(choiceList.get(i).getId()));
// Load image, decode it to Bitmap and return Bitmap to callback
            imageLoader.loadImage("http://placehold.it/120x120&text=image2", new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // Do whatever you want with Bitmap
                    rdbtn.setCompoundDrawables(null, null, new BitmapDrawable(getResources(), loadedImage), null);
                }
            });

            radioGroupLayout.addView(rdbtn);
        }
    }*/

    private void addMultiChoiceCheckBox(ArrayList<ChoicesData> choiceList) {
        checkboxGroupLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < choiceList.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(Integer.parseInt(choiceList.get(i).getId()));
            checkBox.setText(choiceList.get(i).getChoice());
            checkboxGroupLayout.addView(checkBox);
        }
    }

    private void loadWebContent(String webContent) {
        mediaWebView.setVisibility(View.VISIBLE);
        mediaWebView.loadData(webContent, "text/html", "utf-8");
//        mediaWebView.loadUrl("https://www.tutorialspoint.com/java/java_basic_syntax.htm");
        mediaWebView.getSettings().setLoadWithOverviewMode(true);
        mediaWebView.getSettings().setUseWideViewPort(true);
    }

    private void loadContentDescWebView(String webContent) {
        contentDescWebView.setVisibility(View.VISIBLE);
        contentDescWebView.loadData(webContent, "text/html", "utf-8");
//        contentDescWebView.loadUrl("https://www.tutorialspoint.com/java/java_basic_syntax.htm");
        contentDescWebView.getSettings().setLoadWithOverviewMode(true);
        contentDescWebView.getSettings().setUseWideViewPort(true);
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
            }
        }.start();
    }
}
