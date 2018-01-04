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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import timber.log.Timber;

public class EditProfileActivity extends BaseActivity {

    Calendar calendar;
    Context context;
    ConstraintLayout layout;
    Spinner spnrTitle;
    Spinner year_spinner, month_spinner, day_spinner;
    Spinner spnrTimezone;
    EditText edtFirstName;
    EditText edtLastName;
    EditText edtDOB;
    EditText edtAbtUrSelf;
    CheckBox checkEmailNotification;
    CheckBox checkCommentOnWall;
    User user;
    private String firstName;
    private String lastName;
    private String aboutYou;
    private String dob;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_new);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = EditProfileActivity.this;
        user = SettingsMy.getActiveUser();

        layout = findViewById(R.id.edit_profile_layout);
        spnrTimezone = findViewById(R.id.spnrTimezone);
        spnrTitle = findViewById(R.id.spnrTitle);

        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtDOB = findViewById(R.id.edtDOB);
        edtAbtUrSelf = findViewById(R.id.edtAbtUrSelf);
        checkEmailNotification = findViewById(R.id.checkEmailNotification);
        checkCommentOnWall = findViewById(R.id.checkCommentOnWall);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.name_title, R.layout.custom_text_layout);
        adapter.setDropDownViewResource(R.layout.custom_text_layout);
        spnrTitle.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.name_title, R.layout.custom_text_layout);
        adapter2.setDropDownViewResource(R.layout.custom_text_layout);
        spnrTimezone.setAdapter(adapter2);


        edtDOB.setOnClickListener(new View.OnClickListener() {
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
                                    edtDOB.setText(mDate);
                                    edtDOB.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                                }
                            }
                        }, mYear, mMonth, mDay);
                // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        setValues();

        year_spinner = (Spinner) findViewById(R.id.spnrYear);
        month_spinner = (Spinner) findViewById(R.id.spnrMonth);
        day_spinner = (Spinner) findViewById(R.id.spnrDay);

        year_spinner.setOnItemSelectedListener( fixDays );
        month_spinner.setOnItemSelectedListener( fixDays );

        //This option will only allow dates for DOB by min age of 10 and maximum 30
        //This is dynamic, it will always accept DOB for people aged between 10 and 30 depending on current year!
//        populateYears(10, 30);

//        You may also limit your entry by minYear and max Year using this function instead:
        populateYearsByRange(1950,Calendar.getInstance().get(Calendar.YEAR));

    }

    private void setValues() {
        edtFirstName.setText(user.getFirstName());
        edtLastName.setText(user.getLastName());
        // _edit_profile_dob.setText(user.getGender());
        edtAbtUrSelf.setText(user.getAboutMe());

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

        firstName = edtFirstName.getText().toString().trim();
        lastName = edtLastName.getText().toString().trim();
        dob = edtDOB.getText().toString().trim();
        aboutYou = edtAbtUrSelf.getText().toString().trim();

        edtFirstName.setError(null);
        edtLastName.setError(null);
        edtAbtUrSelf.setError(null);
        edtDOB.setError(null);

        if (firstName.isEmpty()) {
            edtFirstName.setError(getString(R.string.error_edit_text));
        } else if (lastName.isEmpty()) {
            edtLastName.setError(getString(R.string.error_edit_text));
        } else if (dob.isEmpty()) {
            edtDOB.setError(getString(R.string.error_edit_text));
        } else if (aboutYou.isEmpty()) {
            edtAbtUrSelf.setError(getString(R.string.error_edit_text));
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

                checkEmailNotification.isChecked();
                checkCommentOnWall.isChecked();

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


    public void setDays()
    {
        int year = Integer.parseInt(year_spinner.getSelectedItem().toString());
        String month = month_spinner.getSelectedItem().toString();

        List<String> months = new ArrayList<>(Arrays.asList(getResources().getStringArray( R.array.months)));

        Calendar mycal = new GregorianCalendar(year ,months.indexOf(month), 1);

        // Get the number of days in that month
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        String[] days_array = new String[daysInMonth];

        for(int k = 0; k < daysInMonth; k++)
            days_array[k] = ""+ (k+1);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, days_array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day_spinner.setAdapter(spinnerArrayAdapter);
    }




    public void populateYears(int minAge, int maxAge)
    {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        String[] years_array = new String[maxAge-minAge];

        for(int i=0; i < maxAge-minAge; i++)
            years_array[i] = ""+ (currentYear - minAge - i);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, years_array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_spinner.setAdapter(spinnerArrayAdapter);

    }

    public void populateYearsByRange(int minYear, int maxYear)
    {

        String[] years_array = new String[(maxYear-minYear)];

        int count =0;
//        for(int i=minYear; i <maxYear; i++) {
//            years_array[count] = "" + i;
//            count++;
//        }

        for(int i=(maxYear-1); i >=minYear; i--) {
            years_array[count] = "" + i;
            count++;
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, years_array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_spinner.setAdapter(spinnerArrayAdapter);

    }

    AdapterView.OnItemSelectedListener fixDays = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setDays();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Another interface callback
        }
    };
}
