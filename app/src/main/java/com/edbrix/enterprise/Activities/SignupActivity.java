package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edbrix.enterprise.R;

public class SignupActivity extends AppCompatActivity {

    Context context;

    TextInputEditText _register_edit_text_first_name;
    TextInputEditText _register_edit_text_last_name;
    TextInputEditText _register_edit_text_email;
    TextInputEditText _register_edit_text_number;
    Button _register_button_register;
    TextView _register_text_view_login;
    ProgressBar _register_progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        context = this;
        getSupportActionBar().setTitle("SignUp ");

        final ActionBar ab = ((AppCompatActivity) context).getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        _register_edit_text_first_name = findViewById(R.id.register_edit_text_first_name);
        _register_edit_text_last_name = findViewById(R.id.register_edit_text_last_name);
        _register_edit_text_email = findViewById(R.id.register_edit_text_email);
        _register_edit_text_number = findViewById(R.id.register_edit_text_number);
        _register_button_register = findViewById(R.id.register_button_register);
        _register_text_view_login = findViewById(R.id.register_text_view_login);
        _register_progress_bar = findViewById(R.id.register_progress_bar);

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
