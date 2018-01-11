package com.edbrix.enterprise.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.edbrix.enterprise.R;

public class VideoWebView extends AppCompatActivity {

    private WebView videoWebView;
    private ProgressBar mProgressBar;
    private String fileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_web_view);

        fileUrl = getIntent().getStringExtra("FileUrl");

        mProgressBar = findViewById(R.id.mProgressBar);
        videoWebView = findViewById(R.id.videoWebView);

        videoWebView.getSettings().setJavaScriptEnabled(true);
        videoWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        videoWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        videoWebView.setWebChromeClient(new WebChromeClient());

        if (fileUrl != null && !fileUrl.isEmpty())
            videoWebView.loadData(fileUrl, "text/html", "UTF-8");

        videoWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);

            }
        });
    }
}
