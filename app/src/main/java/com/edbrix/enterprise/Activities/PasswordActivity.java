package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;

import com.edbrix.enterprise.R;

public class PasswordActivity extends AppCompatActivity {

    Context context;

    TextInputEditText _password_edit_text_password;
    Button _password_button_submit;
    ProgressBar _password_progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        context = this;
        getSupportActionBar().setTitle("Password ");

        final ActionBar ab = ((AppCompatActivity) context).getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        _password_edit_text_password = findViewById(R.id.password_edit_text_password);
        _password_button_submit = findViewById(R.id.password_button_submit);
        _password_progress_bar = findViewById(R.id.password_progress_bar);

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
