package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.edbrix.enterprise.R;
import com.edbrix.enterprise.baseclass.BaseActivity;

public class CreateScheduleActivity extends BaseActivity {

    Context context;
    TextInputEditText _create_schedule_title;
    TextInputEditText _create_schedule_date;
    TextInputEditText _create_schedule_time;
    TextInputEditText _create_schedule_price;
    TextInputEditText _create_schedule_color;
    TextInputEditText _create_schedule_capacity;
    TextInputEditText _create_schedule_description;
    Spinner _create_schedule_minutes;
    Spinner _create_schedule_availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = CreateScheduleActivity.this;

        _create_schedule_title = findViewById(R.id.create_schedule_title);
        _create_schedule_date = findViewById(R.id.create_schedule_date);
        _create_schedule_time = findViewById(R.id.create_schedule_time);
        _create_schedule_price = findViewById(R.id.create_schedule_price);
        _create_schedule_color = findViewById(R.id.create_schedule_color);
        _create_schedule_capacity = findViewById(R.id.create_schedule_capacity);
        _create_schedule_description = findViewById(R.id.create_schedule_description);
        _create_schedule_minutes = findViewById(R.id.create_schedule_minutes);
        _create_schedule_availability = findViewById(R.id.create_schedule_availability);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.minutes, R.layout.custom_text_layout);
        adapter.setDropDownViewResource(R.layout.custom_text_layout);
        _create_schedule_minutes.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.availability, R.layout.custom_text_layout);
        adapter2.setDropDownViewResource(R.layout.custom_text_layout);
        _create_schedule_availability.setAdapter(adapter2);
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
}
