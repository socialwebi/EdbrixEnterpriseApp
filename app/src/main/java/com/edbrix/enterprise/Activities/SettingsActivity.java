package com.edbrix.enterprise.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.edbrix.enterprise.R;
import com.edbrix.enterprise.baseclass.BaseActivity;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
