package com.edbrix.enterprise.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.SalutationSpinnerAdapter;
import com.edbrix.enterprise.Adapters.TimezoneSpinnerAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.SalutationsData;
import com.edbrix.enterprise.Models.TimezonesData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
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

    private Calendar calendar;
    private Context context;
    private ConstraintLayout layout;
    private Spinner spnrTitle;
    private Spinner year_spinner, month_spinner, day_spinner;
    private Spinner spnrTimezone;
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtDOB;
    private EditText edtAbtUrSelf;
    private CheckBox checkEmailNotification;
    private CheckBox checkCommentOnWall;
    private User user;
    private int titleId;
    private int timezoneId;
    private String firstName;
    private String lastName;
    private String aboutYou;
    private String dob;
    private int emailNotification;
    private int commentOnWall;
    private Button saveBtn;
    private Button cancelBtn;
    private int mYear, mMonth, mDay;

    private List<String> months;
    private String[] years_array;
    private String[] days_array;

    private boolean isBOBAlreadySet;


    private ArrayList<SalutationsData> salutationList;
    private ArrayList<TimezonesData> timezonesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_new);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

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
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        salutationList = new ArrayList<>();
        timezonesList = new ArrayList<>();

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this, R.array.name_title, R.layout.custom_text_layout);
//        adapter.setDropDownViewResource(R.layout.custom_text_layout);
//        spnrTitle.setAdapter(adapter);

        getSalutationList();

        getTimeZoneList();

//        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
//                this, R.array.name_title, R.layout.custom_text_layout);
//        adapter2.setDropDownViewResource(R.layout.custom_text_layout);
//        spnrTimezone.setAdapter(adapter2);


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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int month = month_spinner.getSelectedItemPosition() + 1;
//                String day = (String) day_spinner.getSelectedItem();
//                String yr = (String) year_spinner.getSelectedItem();
//
//                showToast(yr + "/" + String.format("%02d", month) + "/" + String.format("%02d", Integer.parseInt(day)));

                checkValidations();

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        year_spinner = (Spinner) findViewById(R.id.spnrYear);
        month_spinner = (Spinner) findViewById(R.id.spnrMonth);
        day_spinner = (Spinner) findViewById(R.id.spnrDay);

        year_spinner.setOnItemSelectedListener(fixDays);
        month_spinner.setOnItemSelectedListener(fixDays);

        //This option will only allow dates for DOB by min age of 10 and maximum 30
        //This is dynamic, it will always accept DOB for people aged between 10 and 30 depending on current year!
//        populateYears(10, 30);

//        You may also limit your entry by minYear and max Year using this function instead:
        populateYearsByRange(1950, Calendar.getInstance().get(Calendar.YEAR));

        getUserDetails();

    }

    private void setValues() {
        edtFirstName.setText(user.getFirstName());
        edtLastName.setText(user.getLastName());
        // _edit_profile_dob.setText(user.getGender());
        edtAbtUrSelf.setText(user.getAboutMe());

        if (user.getCanCommentOnWall() == 1) {
            checkCommentOnWall.setChecked(true);
        } else {
            checkCommentOnWall.setChecked(false);
        }

        if (user.getCanReceiveCourseRequestNotification() == 1) {
            checkEmailNotification.setChecked(true);
        } else {
            checkEmailNotification.setChecked(false);
        }

        if ((user.getBirthYear() != null & !user.getBirthYear().isEmpty()) &&
                (user.getBirthMonth() != null & !user.getBirthMonth().isEmpty()) &&
                (user.getBirthDay() != null & !user.getBirthDay().isEmpty())) {
            isBOBAlreadySet = true;
        }
        if (years_array != null && years_array.length > 0) {
            for (int i = 0; i < years_array.length; i++) {
                if (user.getBirthYear().equalsIgnoreCase(years_array[i])) {
                    year_spinner.setSelection(i);
                    break;
                }
            }
        }

        if (months != null && months.size() > 0) {
            for (int p = 0; p < months.size(); p++) {
                if (user.getBirthMonth().equalsIgnoreCase(months.get(p))) {
                    month_spinner.setSelection(p);
                    break;
                }
            }
        }

        if (days_array != null && days_array.length > 0) {
            for (int j = 0; j < days_array.length; j++) {
                if (user.getBirthDay().equalsIgnoreCase(days_array[j])) {
                    day_spinner.setSelection(j);
                    break;
                }
            }
        }

        if (salutationList != null && !salutationList.isEmpty()) {
            for (int s = 0; s < salutationList.size(); s++) {
                if (user.getSalutationId() == salutationList.get(s).getId()) {
                    spnrTitle.setSelection(s);
                    break;
                }
            }
        }

        if (timezonesList != null && !timezonesList.isEmpty()) {
            for (int t = 0; t < timezonesList.size(); t++) {
                if (user.getTimezoneId() == timezonesList.get(t).getId()) {
                    spnrTimezone.setSelection(t);
                    break;
                }
            }
        }

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
        int month = month_spinner.getSelectedItemPosition() + 1;
        String day = (String) day_spinner.getSelectedItem();
        String yr = (String) year_spinner.getSelectedItem();
        dob = yr + "-" + String.format("%02d", month) + "-" + String.format("%02d", Integer.parseInt(day));

        titleId = salutationList.get(spnrTitle.getSelectedItemPosition()).getId();
        timezoneId = timezonesList.get(spnrTimezone.getSelectedItemPosition()).getId();
        firstName = edtFirstName.getText().toString().trim();
        lastName = edtLastName.getText().toString().trim();
        aboutYou = edtAbtUrSelf.getText().toString().trim();
        if (checkCommentOnWall.isChecked()) {
            commentOnWall = 1;
        }

        if (checkEmailNotification.isChecked()) {
            emailNotification = 1;
        }

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
            updateUserDetails(firstName, lastName, titleId, timezoneId, aboutYou, commentOnWall, emailNotification, dob);
//           updateUserDetails(firstName,lastName,);
        }
    }

    public void setDays() {
        int year = Integer.parseInt(year_spinner.getSelectedItem().toString());
        String month = month_spinner.getSelectedItem().toString();

        months = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.months)));

        Calendar mycal = new GregorianCalendar(year, months.indexOf(month), 1);

        // Get the number of days in that month
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        days_array = new String[daysInMonth];

        for (int k = 0; k < daysInMonth; k++)
            days_array[k] = "" + (k + 1);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, days_array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day_spinner.setAdapter(spinnerArrayAdapter);
    }


    public void populateYears(int minAge, int maxAge) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        String[] years_array = new String[maxAge - minAge];

        for (int i = 0; i < maxAge - minAge; i++)
            years_array[i] = "" + (currentYear - minAge - i);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, years_array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_spinner.setAdapter(spinnerArrayAdapter);

    }

    public void populateYearsByRange(int minYear, int maxYear) {

        years_array = new String[(maxYear - minYear)];

        int count = 0;
//        for(int i=minYear; i <maxYear; i++) {
//            years_array[count] = "" + i;
//            count++;
//        }

        for (int i = (maxYear - 1); i >= minYear; i--) {
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
            if (!isBOBAlreadySet) {
                setDays();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Another interface callback
        }
    };

    private void getSalutationList() {
//        showBusyProgress();
        if (user != null) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("Gender", user.getGender());
            } catch (JSONException e) {
                return;
            }

            GsonRequest<ResponseData> getSalutationListRequest = new GsonRequest<>(Request.Method.POST, Constants.getSalutations, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {

                            if (response.getErrorCode() == null) {
                                salutationList.addAll(response.getSalutationsList());
                                SalutationSpinnerAdapter salutationSpinnerAdapter = new SalutationSpinnerAdapter(EditProfileActivity.this);
                                salutationSpinnerAdapter.addItems(salutationList);
                                spnrTitle.setAdapter(salutationSpinnerAdapter);
                                hideBusyProgress();
                            } else {
                                hideBusyProgress();
                                showToast(response.getErrorMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            getSalutationListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getSalutationListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getSalutationListRequest, "salutation_requests");
        }
    }

    private void getTimeZoneList() {
//        showBusyProgress();
        if (user != null) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
            } catch (JSONException e) {
                return;
            }

            GsonRequest<ResponseData> getTimezoneListRequest = new GsonRequest<>(Request.Method.POST, Constants.getTimezoneList, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {

                            if (response.getErrorCode() == null) {
                                timezonesList.addAll(response.getTimezonesList());
                                TimezoneSpinnerAdapter timezoneSpinnerAdapter = new TimezoneSpinnerAdapter(EditProfileActivity.this);
                                timezoneSpinnerAdapter.addItems(timezonesList);
                                spnrTimezone.setAdapter(timezoneSpinnerAdapter);
                                hideBusyProgress();
                            } else {
                                hideBusyProgress();
                                showToast(response.getErrorMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            getTimezoneListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getTimezoneListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getTimezoneListRequest, "timezone_requests");
        }
    }

    private void getUserDetails() {
        showBusyProgress();
        if (user != null) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
            } catch (JSONException e) {
                return;
            }

            GsonRequest<ResponseData> getUserDetailsRequest = new GsonRequest<>(Request.Method.POST, Constants.getUserDetails, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {

                            if (response.getErrorCode() == null) {
                                user = response.getUser();
                                setValues();
                                hideBusyProgress();
                            } else {
                                hideBusyProgress();
                                showToast(response.getErrorMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            getUserDetailsRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getUserDetailsRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getUserDetailsRequest, "user_details_requests");
        }
    }

    private void updateUserDetails(String firstName, String lastName, int titleId, int timezoneId, String aboutMe, int commentOnWall, int receiveNotification, String dateOfBirth) {
        showBusyProgress();
        if (user != null) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("UserType", user.getUserType());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("FirstName", firstName);
                jo.put("LastName", lastName);
                jo.put("TitleId", titleId);
                jo.put("TimezoneId", timezoneId);
                jo.put("AboutMe", aboutMe);
                jo.put("CanCommentOnWall", commentOnWall);
                jo.put("CanReceiveCourseRequestNotification", receiveNotification);
                jo.put("DOB", dateOfBirth);


//                "FirstName":"Victor",
//                        "LastName":"Chang",
//                        "TitleId": 1,
//                        "TimezoneId":2,
//                        "AboutMe":"About me",
//                        "CanCommentOnWall": 1,
//                        "CanReceiveCourseRequestNotification": 1,
//                        "DOB": "2010-01-04"

            } catch (JSONException e) {
                return;
            }

            GsonRequest<ResponseData> updateProfileRequest = new GsonRequest<>(Request.Method.POST, Constants.updateUserProfile, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {

                            if (response.getErrorCode() == null) {
                                hideBusyProgress();
                                if (response.getUser() != null) {

                                    SettingsMy.setActiveUser(response.getUser());
                                    showToast("Your profile is updated successfully.");
                                    finish();
                                } else {
                                    showToast(getString(R.string.error_something_wrong));
                                }
                            } else {
                                hideBusyProgress();
                                showToast(response.getErrorMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            updateProfileRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            updateProfileRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(updateProfileRequest, "update_profile_requests");
        }
    }

}
