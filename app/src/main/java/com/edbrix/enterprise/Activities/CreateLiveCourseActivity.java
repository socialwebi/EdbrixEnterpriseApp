package com.edbrix.enterprise.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.edbrix.enterprise.R;

public class CreateLiveCourseActivity extends AppCompatActivity {

    NumberPicker _number_picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live_course);

    }
}
