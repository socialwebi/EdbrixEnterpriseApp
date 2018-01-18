package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class PasswordActivity extends BaseActivity {

    Context context;

    RelativeLayout layout;
    ImageView _password_password_image_logo;
    TextInputEditText _password_edit_text_password;
    Button _password_button_submit;
    ProgressBar _password_progress_bar;

    private String orgId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        context = this;

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        orgId = intent.getStringExtra("id");
        String orgName = intent.getStringExtra("name");
        String orgImage = intent.getStringExtra("image");

        getSupportActionBar().setTitle(orgName);

        _password_password_image_logo = findViewById(R.id.password_password_image_logo);
        _password_edit_text_password = findViewById(R.id.password_edit_text_password);
        _password_button_submit = findViewById(R.id.password_button_submit);
        _password_progress_bar = findViewById(R.id.password_progress_bar);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        if (orgImage != null && !orgImage.isEmpty()) {
            Picasso.with(context)
                    .load(orgImage)
                    .error(R.drawable.edbrix_logo)
                    .into(_password_password_image_logo);
        }

        _password_button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
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

        Conditions.hideKeyboard(PasswordActivity.this);

        String password = _password_edit_text_password.getText().toString().trim();

        if (password.isEmpty()) {
            _password_edit_text_password.setError(getString(R.string.error_edit_text));
        } else {
            _password_edit_text_password.setError(null);

            if (Conditions.isNetworkConnected(PasswordActivity.this)) {
                signIn(password);
                // ((MainActivity) getActivity()).onMeetingListSelected();
            } else {
                try {
                    Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PasswordActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void signIn(String password) {

        _password_progress_bar.setVisibility(View.VISIBLE);

        JSONObject jo = new JSONObject();
        try {
            jo.put("Email", email);
            jo.put("Password", password);
            jo.put("OrganizationId", orgId);

        } catch (JSONException e) {
            Timber.e(e, "Parse logInWithEmail exception");
            return;
        }
        if (BuildConfig.DEBUG) Timber.d("Login user: %s", jo.toString());

        GsonRequest<ResponseData> userLoginEmailRequest = new GsonRequest<>(Request.Method.POST, Constants.userLogin, jo.toString(), ResponseData.class,
                new Response.Listener<ResponseData>() {
                    @Override
                    public void onResponse(@NonNull ResponseData response) {
                        Timber.d("response: %s", response.toString());
                        _password_progress_bar.setVisibility(View.INVISIBLE);
                        if (response.getErrorCode() == null) {

                            SettingsMy.setActiveUser(response.getUser());
                            SettingsMy.setZoomCredential(response.getUser().getZoomUserId(),response.getUser().getZoomUserToken());

                            // editor.putBoolean("first", false);
                            // editor.commit();

                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();


                        } else {
                            try {
                                Timber.d("Error: %s", response.getErrorMessage());
                                Snackbar.make(layout, response.getErrorMessage(), Snackbar.LENGTH_LONG).show();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                Timber.d("Error: %s", response.getErrorMessage());
                                Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _password_progress_bar.setVisibility(View.INVISIBLE);
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
        Application.getInstance().addToRequestQueue(userLoginEmailRequest, "login_password_requests");
    }

}
