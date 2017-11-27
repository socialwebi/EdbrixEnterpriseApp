package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.OrganizationListAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Interfaces.OrganizationListInterface;
import com.edbrix.enterprise.MainActivity;
import com.edbrix.enterprise.Models.Organizations;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Conditions;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

public class OrganizationListActivity extends BaseActivity {

    Context context;

    RelativeLayout layout;
    RecyclerView _organization_recycler;
    ProgressBar _organization_progress_bar;

    private OrganizationListAdapter organizationListAdapter;

    private Boolean key;
    private String email;
    private String password;
    private String orgId;
    private String orgName;
    private String orgImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_list);
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
        key = intent.getBooleanExtra("Key",false);
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");

        ArrayList<Organizations> list = new ArrayList<>();

        _organization_recycler = findViewById(R.id.organization_recycler);
        _organization_progress_bar = findViewById(R.id.organization_progress_bar);

        organizationListAdapter = new OrganizationListAdapter(OrganizationListActivity.this, list, new OrganizationListInterface() {
            @Override
            public void onOrgSelected(String id, String name, String image) {
                orgId = id;
                orgName = name;
                orgImage = image;

                if (!key) {
                    if (Conditions.isNetworkConnected(OrganizationListActivity.this)) {
                        signIn();
                    }
                    else {
                        try {
                            Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(OrganizationListActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    if (Conditions.isNetworkConnected(OrganizationListActivity.this)) {
                        forgotPassword();
                    }
                    else {
                        try {
                            Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(OrganizationListActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        if (Conditions.isNetworkConnected(OrganizationListActivity.this)) {
            getOrg();
        }
        else {
            try {
                Snackbar.make(layout, getString(R.string.error_network), Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(OrganizationListActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        assert _organization_recycler != null;
        _organization_recycler.setHasFixedSize(true);
        _organization_recycler.setLayoutManager(linearLayoutManager);
        registerForContextMenu(_organization_recycler);
        _organization_recycler.setAdapter(organizationListAdapter);

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

    private void getOrg() {

        JSONObject jo = new JSONObject();
        try {

            jo.put("Email", email);

        } catch (JSONException e) {
            Timber.e(e, "Parse logInWithEmail exception");
            return;
        }
        if (BuildConfig.DEBUG) Timber.d("Login user: %s", jo.toString());

        GsonRequest<ResponseData> userOrganizationRequest = new GsonRequest<>(Request.Method.POST, Constants.getSchoolList, jo.toString(), ResponseData.class,
                new Response.Listener<ResponseData>() {
                    @Override
                    public void onResponse(@NonNull ResponseData response) {

                        _organization_progress_bar.setVisibility(View.INVISIBLE);
                        Timber.d("response: %s", response.toString());
                        if (response.getErrorCode()==null) {
                            organizationListAdapter.refreshList(response.getOrganizations());
                            organizationListAdapter.notifyDataSetChanged();
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
                _organization_progress_bar.setVisibility(View.INVISIBLE);
                try {
                    Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                }
            }
        });
        userOrganizationRequest.setRetryPolicy(Application.getDefaultRetryPolice());
        userOrganizationRequest.setShouldCache(false);
        Application.getInstance().addToRequestQueue(userOrganizationRequest, "organization_requests");

    }

    private void signIn() {

        _organization_progress_bar.setVisibility(View.VISIBLE);
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
                        _organization_progress_bar.setVisibility(View.INVISIBLE);
                        if (response.getErrorCode()==null) {

                            SettingsMy.setActiveUser(response.getUser());

                            // editor.putBoolean("first", false);
                            // editor.commit();

                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        }
                        else {

                            Intent intent = new Intent(context, PasswordActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("name", orgName);
                            intent.putExtra("id", orgId);
                            intent.putExtra("image", orgImage);
                            startActivity(intent);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _organization_progress_bar.setVisibility(View.INVISIBLE);
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
        Application.getInstance().addToRequestQueue(userLoginEmailRequest, "login_dialog_requests");

    }

    private void forgotPassword() {

        JSONObject jo = new JSONObject();
        try {

            jo.put("OrganizationId", orgId);
            jo.put("Email", email);

        } catch (JSONException e) {
            Timber.e(e, "Parse logInWithEmail exception");
            return;
        }
        if (BuildConfig.DEBUG) Timber.d("Forgot user: %s", jo.toString());

        GsonRequest<ResponseData> userForgotPasswordRequest = new GsonRequest<>(Request.Method.POST, Constants.forgotPassword, jo.toString(), ResponseData.class,
                new Response.Listener<ResponseData>() {
                    @Override
                    public void onResponse(@NonNull ResponseData response) {
                        _organization_progress_bar.setVisibility(View.INVISIBLE);
                        Timber.d("response: %s", response.toString());
                        if (response.getErrorCode()==null) {

                            if (response.getIsOrganizationListShow().equals("0")){
                                Toast.makeText(context, "Success, Please login with new password ", Toast.LENGTH_SHORT).show();
                                SettingsMy.setActiveUser(null);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

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
                _organization_progress_bar.setVisibility(View.INVISIBLE);
                try {
                    Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                }
            }
        });
        userForgotPasswordRequest.setRetryPolicy(Application.getDefaultRetryPolice());
        userForgotPasswordRequest.setShouldCache(false);
        Application.getInstance().addToRequestQueue(userForgotPasswordRequest, "org_forgot_password_requests");

    }

}
