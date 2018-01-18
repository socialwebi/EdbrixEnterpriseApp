package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.edbrix.enterprise.baseclass.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class LoginActivity extends BaseActivity {

    Context context;

    RelativeLayout layout;
    TextInputEditText _login_edit_text_email;
    TextInputEditText _login_edit_text_password;
    Button _login_button_login;
    Button _login_text_view_register;
    TextView _login_text_view_forgot_password;
    ProgressBar _login_progress_bar;
    boolean checkEmail = false, checkPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        _login_edit_text_email = findViewById(R.id.login_edit_text_email);
        _login_edit_text_password = findViewById(R.id.login_edit_text_password);
        _login_button_login = findViewById(R.id.login_button_login);
        _login_text_view_register = findViewById(R.id.login_text_view_register);
        _login_text_view_forgot_password = findViewById(R.id.login_text_view_forgot_password);
        _login_progress_bar = findViewById(R.id.login_progress_bar);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        _login_button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });

        _login_text_view_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

        _login_text_view_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPasswordIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
            }
        });

        _login_edit_text_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && charSequence.toString().trim().length() > 3) {
                    checkEmail = true;
                } else {
                    checkEmail = false;
                }
                if (checkEmail && checkPassword) {
                    _login_button_login.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                } else {
                    _login_button_login.setBackgroundColor(context.getResources().getColor(R.color.colorDisableBtn));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        _login_edit_text_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && charSequence.toString().trim().length() > 3) {
                    checkPassword = true;
                } else {
                    checkPassword = false;
                }
                if (checkEmail && checkPassword) {
                    _login_button_login.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                } else {
                    _login_button_login.setBackgroundColor(context.getResources().getColor(R.color.colorDisableBtn));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        _login_edit_text_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(_login_edit_text_password.getWindowToken(), 0);
                    //doLogin
                    checkValidations();
                    return true;
                }
                return false;
            }
        });

        // Conditions.isNetworkConnected(context);
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

        Conditions.hideKeyboard(LoginActivity.this);

        String email = _login_edit_text_email.getText().toString().trim();
        String password = _login_edit_text_password.getText().toString().trim();

        if (email.isEmpty()) {
            _login_edit_text_email.setError(getString(R.string.error_edit_text));
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _login_edit_text_email.setError(getString(R.string.error_email_not_valid));
        } else if (password.isEmpty()) {
            _login_edit_text_email.setError(null);
            _login_edit_text_password.setError(getString(R.string.error_edit_text));
        } else {
            _login_edit_text_password.setError(null);

            if (Conditions.isNetworkConnected(LoginActivity.this)) {
                signIn(email, password);
                // ((MainActivity) getActivity()).onMeetingListSelected();
            } else {
                try {
                    Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void signIn(final String email, final String password) {

        _login_progress_bar.setVisibility(View.VISIBLE);
        JSONObject jo = new JSONObject();
        try {
            jo.put("Email", email);
            jo.put("Password", password);
            jo.put("OrganizationId", "");

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
                        _login_progress_bar.setVisibility(View.INVISIBLE);
                        if (response.getErrorCode() == null) {
                            //((MainActivity) getActivity()).onCategoryListSelected();  //onCategoryMenuSelected
                            if (response.getIsOrganizationListShow().equals("0")) {
                                SettingsMy.setActiveUser(response.getUser());
                                SettingsMy.setZoomCredential(response.getUser().getZoomUserId(),response.getUser().getZoomUserToken());

                                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), OrganizationListActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                startActivity(intent);
                            }
                        } else {
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
                _login_progress_bar.setVisibility(View.INVISIBLE);
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
        Application.getInstance().addToRequestQueue(userLoginEmailRequest, "login_requests");

    }

}
