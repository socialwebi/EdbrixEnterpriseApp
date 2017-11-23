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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.MainActivity;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Conditions;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class ChangePasswordActivity extends BaseActivity {

    Context context;

    RelativeLayout layout;
    TextInputEditText _change_password_edit_text_password;
    TextInputEditText _change_password_edit_text_confirm_password;
    Button _change_password_button_submit;
    ProgressBar _change_password_progress_bar;
    boolean checkPassword = false, checkConfirmPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        _change_password_edit_text_password = findViewById(R.id.change_password_edit_text_password);
        _change_password_edit_text_confirm_password = findViewById(R.id.change_password_edit_text_confirm_password);
        _change_password_button_submit = findViewById(R.id.change_password_button_submit);
        _change_password_progress_bar = findViewById(R.id.change_password_progress_bar);

        _change_password_button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });

        _change_password_edit_text_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && charSequence.toString().trim().length()>3) {
                    checkConfirmPassword = true;
                } else {
                    checkConfirmPassword = false;
                }
                if (checkPassword && checkConfirmPassword) {
                    _change_password_button_submit.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                } else {
                    _change_password_button_submit.setBackgroundColor(context.getResources().getColor(R.color.colorDisableBtn));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        _change_password_edit_text_confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && charSequence.toString().trim().length()>3) {
                    checkPassword = true;
                } else {
                    checkPassword = false;
                }
                if (checkPassword && checkConfirmPassword) {
                    _change_password_button_submit.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                } else {
                    _change_password_button_submit.setBackgroundColor(context.getResources().getColor(R.color.colorDisableBtn));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

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

        Conditions.hideKeyboard(ChangePasswordActivity.this);

        String password = _change_password_edit_text_password.getText().toString().trim();
        String cPassword = _change_password_edit_text_confirm_password.getText().toString().trim();

        if (password.isEmpty()) {
            _change_password_edit_text_password.setError(getString(R.string.error_edit_text));
        }
        else if (cPassword.isEmpty()) {
            _change_password_edit_text_password.setError(null);
            _change_password_edit_text_confirm_password.setError(getString(R.string.error_edit_text));
        }
        else if (!password.equals(cPassword)) {
            _change_password_edit_text_confirm_password.setError(null);
            try {
                Snackbar.make(layout, getString(R.string.error_password_not_match), Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.error_password_not_match), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (Conditions.isNetworkConnected(ChangePasswordActivity.this)) {
                setChangePassword(password);
                // ((MainActivity) getActivity()).onMeetingListSelected();
            }
            else {
                try {
                    Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void setChangePassword(String password) {

        _change_password_progress_bar.setVisibility(View.VISIBLE);

        User user = SettingsMy.getActiveUser();

        if (user!=null) {

            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("UserType", user.getUserType());
                jo.put("Password", password);

            } catch (JSONException e) {
                Timber.e(e, "Parse logInWithEmail exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Login user: %s", jo.toString());

            GsonRequest<ResponseData> userChangePasswordRequest = new GsonRequest<>(Request.Method.POST, Constants.changePassword, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {
                            _change_password_progress_bar.setVisibility(View.INVISIBLE);
                            Timber.d("response: %s", response.toString());
                            if (response.getErrorCode()==null) {
                                Toast.makeText(context, "Success, Please login with new password ", Toast.LENGTH_SHORT).show();
                                SettingsMy.setActiveUser(null);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }
                            else {

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
                    _change_password_progress_bar.setVisibility(View.INVISIBLE);
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            userChangePasswordRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            userChangePasswordRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(userChangePasswordRequest, "change_password_requests");
        }
    }

}
