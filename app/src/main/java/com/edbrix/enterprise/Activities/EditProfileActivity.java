package com.edbrix.enterprise.Activities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;

public class EditProfileActivity extends BaseActivity {

    Spinner _edit_profile_name_title;
    Spinner _edit_profile_timezone;
    TextInputEditText _edit_profile_first_name;
    TextInputEditText _edit_profile_last_name;
    TextInputEditText _edit_profile_dob;
    TextInputEditText _edit_profile_about_you;
    CheckBox _edit_profile_check_1;
    CheckBox _edit_profile_check_2;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        user = SettingsMy.getActiveUser();

        _edit_profile_timezone = findViewById(R.id.edit_profile_timezone);
        _edit_profile_name_title = findViewById(R.id.edit_profile_name_title);
        _edit_profile_first_name = findViewById(R.id.edit_profile_first_name);
        _edit_profile_last_name = findViewById(R.id.edit_profile_last_name);
        _edit_profile_dob = findViewById(R.id.edit_profile_dob);
        _edit_profile_about_you = findViewById(R.id.edit_profile_about_you);
        _edit_profile_check_1 = findViewById(R.id.edit_profile_check_1);
        _edit_profile_check_2 = findViewById(R.id.edit_profile_check_2);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.name_title, R.layout.custom_text_layout);
        adapter.setDropDownViewResource(R.layout.custom_text_layout);
        _edit_profile_name_title.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.name_title, R.layout.custom_text_layout);
        adapter2.setDropDownViewResource(R.layout.custom_text_layout);
        _edit_profile_timezone.setAdapter(adapter2);

        setValues();

    }

    private void setValues() {
        _edit_profile_first_name.setText(user.getFirstName());
        _edit_profile_last_name.setText(user.getLastName());
        // _edit_profile_dob.setText(user.getGender());
        _edit_profile_about_you.setText(user.getAboutMe());

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
