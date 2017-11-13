package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Conditions;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class SignupActivity extends AppCompatActivity {

    Context context;

    ScrollView layout;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        _register_edit_text_first_name = findViewById(R.id.register_edit_text_first_name);
        _register_edit_text_last_name = findViewById(R.id.register_edit_text_last_name);
        _register_edit_text_email = findViewById(R.id.register_edit_text_email);
        _register_edit_text_number = findViewById(R.id.register_edit_text_number);
        _register_button_register = findViewById(R.id.register_button_register);
        _register_text_view_login = findViewById(R.id.register_text_view_login);
        _register_progress_bar = findViewById(R.id.register_progress_bar);


        _register_button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });

        _register_text_view_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

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

    private void checkValidations() {

        Conditions.hideKeyboard(SignupActivity.this);

        String fName = _register_edit_text_first_name.getText().toString().trim();
        String lName = _register_edit_text_last_name.getText().toString().trim();
        String email = _register_edit_text_email.getText().toString().trim();
        String number = _register_edit_text_number.getText().toString().trim();

        if (fName.isEmpty()) {
            _register_edit_text_first_name.setError(getString(R.string.error_edit_text));
        }
        else if (lName.isEmpty()) {
            _register_edit_text_first_name.setError(null);
            _register_edit_text_last_name.setError(getString(R.string.error_edit_text));
        }
        else if (email.isEmpty()) {
            _register_edit_text_last_name.setError(null);
            _register_edit_text_email.setError(getString(R.string.error_edit_text));
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _register_edit_text_email.setError(getString(R.string.error_email_not_valid));
        }
        else if (number.isEmpty()) {
            _register_edit_text_email.setError(null);
            _register_edit_text_number.setError(getString(R.string.error_edit_text));
        }
        else if (number.length()!=10) {
            _register_edit_text_number.setError(getString(R.string.error_number_not_valid));
        }
        else {
            _register_edit_text_number.setError(null);

            if (Conditions.isNetworkConnected(SignupActivity.this)) {
                register(email, number, fName, lName);
                // ((MainActivity) getActivity()).onMeetingListSelected();
            }
            else {
                try {
                    Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SignupActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


    private void register(String email, String number, String firstName, String lastName) {

        _register_progress_bar.setVisibility(View.VISIBLE);
        JSONObject jo = new JSONObject();
        try {
            jo.put("Email", email);
            jo.put("Number", number);
            jo.put("FirstName", firstName);
            jo.put("LastName", lastName);

        } catch (JSONException e) {
            Timber.e(e, "Parse logInWithEmail exception");
            return;
        }
        if (BuildConfig.DEBUG) Timber.d("Register user: %s", jo.toString());

        GsonRequest<ResponseData> userLoginEmailRequest = new GsonRequest<>(Request.Method.POST, Constants.userRegister , jo.toString(), ResponseData.class,
                new Response.Listener<ResponseData>() {
                    @Override
                    public void onResponse(@NonNull ResponseData response) {
                        _register_progress_bar.setVisibility(View.INVISIBLE);
                        Timber.d("response: %s", response.toString());
                        if (response.getErrorCode()==null) {
                            SettingsMy.setActiveUser(response.getUser());

                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            try {
                                Timber.d("Error: %s", response.getErrorCode());
                                Snackbar.make(layout, response.getErrorMessage(), Snackbar.LENGTH_LONG).show();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                Timber.d("Error: %s", response.getErrorCode());
                                Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _register_progress_bar.setVisibility(View.INVISIBLE);
                try {
                    Timber.d("Error: %s", error.getMessage());
                    Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                } catch (Exception e2) {
                    e2.printStackTrace();

                    Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                }
            }
        });
        userLoginEmailRequest.setRetryPolicy(Application.getDefaultRetryPolice());
        userLoginEmailRequest.setShouldCache(false);
        Application.getInstance().addToRequestQueue(userLoginEmailRequest, "register_requests");

    }

}
