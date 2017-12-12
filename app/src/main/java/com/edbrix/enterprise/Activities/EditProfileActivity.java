package com.edbrix.enterprise.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.JsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import timber.log.Timber;

public class EditProfileActivity extends BaseActivity {

    Context context;
    ConstraintLayout layout;
    Spinner _edit_profile_name_title;
    Spinner _edit_profile_timezone;
    TextInputEditText _edit_profile_first_name;
    TextInputEditText _edit_profile_last_name;
    TextInputEditText _edit_profile_dob;
    TextInputEditText _edit_profile_about_you;
    CheckBox _edit_profile_check_1;
    CheckBox _edit_profile_check_2;
    User user;
    private String firstName;
    private String lastName;
    private String aboutYou;
    private String dob;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = EditProfileActivity.this;
        user = SettingsMy.getActiveUser();

        layout = findViewById(R.id.edit_profile_layout);
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


        _edit_profile_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                Log.d("TAG", " DatePickerDialog-1 ");
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                if (view.isShown()) {
                                    String mDate = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                    _edit_profile_dob.setText(mDate);
                                    _edit_profile_dob.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                                }
                            }
                        }, mYear, mMonth, mDay);
                // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

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

    private void checkValidations() {

        firstName = _edit_profile_first_name.getText().toString().trim();
        lastName = _edit_profile_last_name.getText().toString().trim();
        dob = _edit_profile_dob.getText().toString().trim();
        aboutYou = _edit_profile_about_you.getText().toString().trim();


        if (firstName.isEmpty()) {
            _edit_profile_first_name.setError(getString(R.string.error_edit_text));
        } else if (lastName.isEmpty()) {
            _edit_profile_first_name.setError(null);
            _edit_profile_last_name.setError(getString(R.string.error_edit_text));
        } else if (dob.isEmpty()) {
            _edit_profile_last_name.setError(null);
            _edit_profile_dob.setError(getString(R.string.error_edit_text));
        } else if (aboutYou.isEmpty()) {
            _edit_profile_dob.setError(null);
            _edit_profile_about_you.setError(getString(R.string.error_edit_text));

        } else {

            // saveProfile();
        }
    }

    private void saveProfile() {

        User user = SettingsMy.getActiveUser();
        if (user != null) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());

                _edit_profile_check_1.isChecked();
                _edit_profile_check_2.isChecked();

            } catch (JSONException e) {
                Timber.e(e, "Parse profile exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Profile: %s", jo.toString());

            JsonRequest req = new JsonRequest(Request.Method.POST, Constants.setCreateCourse, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    Timber.d("Response : %s", response.toString());
                    try {

                        if (response.getString("ErrorCode").equals("0")) {

                            /*Intent intent = new Intent(CreateScheduleActivity.this, DashboardActivity.class);
                            startActivity(intent);*/

                        } else {
                            try {
                                Snackbar.make(layout, response.getString("ErrorMessage"), Snackbar.LENGTH_LONG).show();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                Toast.makeText(context, response.getString("ErrorMessage"), Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            });
            req.setRetryPolicy(Application.getDefaultRetryPolice());
            req.setShouldCache(false);
            Application.getInstance().addToRequestQueue(req, "edit_profile_requests");

        }
    }

}
