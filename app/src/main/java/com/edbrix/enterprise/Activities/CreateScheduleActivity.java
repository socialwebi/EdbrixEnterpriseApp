package com.edbrix.enterprise.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.RegularSpinnerAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class CreateScheduleActivity extends BaseActivity {

    private Context context;
    private EditText _create_schedule_title;
    private EditText _create_schedule_date;
    private EditText _create_schedule_time;
    private EditText _create_schedule_price;
    private EditText _create_schedule_color;
    private EditText _create_schedule_capacity;
    private EditText _create_schedule_description;
    private LinearLayout layout;
    private Spinner _create_schedule_minutes;
    private Spinner _create_schedule_availability;
    private Button btnCreateAvailability;


    private DatePickerDialog datePicker;

    private static SimpleDateFormat sdfMonthString = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    private String title;
    private String date;
    private String time;
    private String price;
    private String colorCode;
    private String capacity;
    private String description;
    private String connectType;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = CreateScheduleActivity.this;

        _create_schedule_title = findViewById(R.id.create_schedule_title);
        _create_schedule_date = findViewById(R.id.create_schedule_date);
        _create_schedule_time = findViewById(R.id.create_schedule_time);
        _create_schedule_price = findViewById(R.id.create_schedule_price);
        _create_schedule_color = findViewById(R.id.create_schedule_color);
        _create_schedule_capacity = findViewById(R.id.create_schedule_capacity);
        _create_schedule_description = findViewById(R.id.create_schedule_description);
        _create_schedule_minutes = findViewById(R.id.create_schedule_minutes);
        _create_schedule_availability = findViewById(R.id.create_schedule_availability);
        btnCreateAvailability = findViewById(R.id.btnCreateAvailability);
        layout = findViewById(R.id.create_schedule_layout);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        RegularSpinnerAdapter timespanSpinnerAdapter = new RegularSpinnerAdapter(context);
        ArrayList<String> timeSpanList = new ArrayList<>();
        for(int i=0;i<getResources().getStringArray(R.array.minutes).length;i++){
            timeSpanList.add(getResources().getStringArray(R.array.minutes)[i]);
        }
        timespanSpinnerAdapter.addItems(timeSpanList);
        _create_schedule_minutes.setAdapter(timespanSpinnerAdapter);
        _create_schedule_minutes.setSelection(2);

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this, R.array.minutes, R.layout.custom_text_layout);
//        adapter.setDropDownViewResource(R.layout.custom_text_layout);
//        _create_schedule_minutes.setAdapter(adapter);
//        _create_schedule_minutes.setSelection(2);

//        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
//                this, R.array.availability, R.layout.custom_text_layout);
//        adapter2.setDropDownViewResource(R.layout.custom_text_layout);

        RegularSpinnerAdapter availabilitySpinnerAdapter = new RegularSpinnerAdapter(context);
        ArrayList<String> availabilityList = new ArrayList<>();

        for(int i=0;i<getResources().getStringArray(R.array.availability).length;i++){
            availabilityList.add(getResources().getStringArray(R.array.availability)[i]);
        }
        availabilitySpinnerAdapter.addItems(availabilityList);
        _create_schedule_availability.setAdapter(availabilitySpinnerAdapter);

        /********* display current time on screen Start ********/

        final Calendar c = Calendar.getInstance();
        // Current Hour
        hour = c.get(Calendar.HOUR_OF_DAY);
        // Current Minute
        minute = c.get(Calendar.MINUTE);

        // set current time into output textview
        updateTime(hour,minute);

        /********* display current time on screen End ********/

        setListeners();
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

    private void setListeners() {
        btnCreateAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndCreate();
//                Intent intent = new Intent(CreateScheduleActivity.this, LearnersListActivity.class);
//                intent.putExtra(LearnersListActivity.scheduleIdBundleKey, "404");
//                intent.putExtra(LearnersListActivity.inviteLimitBundleKey, _create_schedule_capacity.getText().toString().trim());
//                startActivity(intent);
            }
        });

        _create_schedule_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        _create_schedule_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        _create_schedule_time.setText( selectedHour + ":" + selectedMinute);
                        updateTime(selectedHour,selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }


    private void validateAndCreate() {

        title = _create_schedule_title.getText().toString().trim();
        date = _create_schedule_date.getText().toString().trim();
        time = _create_schedule_time.getText().toString().trim();
        price = _create_schedule_price.getText().toString().trim();
        colorCode = _create_schedule_color.getText().toString().trim();
        capacity = _create_schedule_capacity.getText().toString().trim();
        description = _create_schedule_description.getText().toString().trim();


        _create_schedule_title.setError(null);
        _create_schedule_date.setError(null);
        _create_schedule_time.setError(null);
        _create_schedule_color.setError(null);
        _create_schedule_description.setError(null);

        if (price.isEmpty()) {
            price = "0";
        }
        if (title.isEmpty()) {
            _create_schedule_title.setError(getString(R.string.error_edit_text));
        } else if (date.isEmpty()) {
            _create_schedule_date.setError(getString(R.string.error_edit_text));
        } else if (time.isEmpty()) {
            _create_schedule_time.setError(getString(R.string.error_edit_text));
        } else if (colorCode.isEmpty()) {
            _create_schedule_color.setError(getString(R.string.error_edit_text));
        }  else if (_create_schedule_minutes.getSelectedItemPosition() == 0) {
            showToast("Please select Timespan.");
        } else if (_create_schedule_availability.getSelectedItemPosition() == 0) {
            showToast("Please select Availability type.");
        } else {
            if (_create_schedule_availability.getSelectedItemPosition() == 2) {
                connectType = Constants.availabilityType_ZOOM;
            } else {
                connectType = Constants.availabilityType_TrainingSession;
            }
            showBusyProgress();
            createAvailability("0", "0", title, description, colorCode, capacity, price, connectType, "", date, time, "" + _create_schedule_minutes.getSelectedItem());
        }
//        else if (description.isEmpty()) {
//            _create_schedule_description.setError(getString(R.string.error_edit_text));
//        }
    }

    private void createAvailability(String courseId, String Id, String title, String description,
                                    String colorCode, final String capacity, String price, String connectType,
                                    String connectURL, String startDate, String startTime, String timeSpan) {

        User user = SettingsMy.getActiveUser();
        if (user != null) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());
                jo.put("CourseId", courseId);
                jo.put("Id", Id);
                jo.put("Title", title);
                jo.put("Description", description);
                jo.put("ColorCode", colorCode);
                jo.put("Capacity", capacity);
                jo.put("Price", price);
                jo.put("ConnectType", connectType);
                jo.put("ConnectURL", connectURL);
                jo.put("StartDate", startDate);
                jo.put("StartTime", startTime);
                jo.put("TimeSpan", timeSpan);


            } catch (JSONException e) {
                Timber.e(e, "Parse create meeting exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("CreateAvailability Request json: %s", jo.toString());

            JsonRequest req = new JsonRequest(Request.Method.POST, Constants.createAvailability, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideBusyProgress();
                    try {
                        if (response.getString("ErrorCode").equals("0")) {

                            showToast(response.getString("message"));

                            Intent intent = new Intent(CreateScheduleActivity.this, LearnersListActivity.class);
                            intent.putExtra(LearnersListActivity.scheduleIdBundleKey, response.getString("id"));
                            intent.putExtra(LearnersListActivity.inviteLimitBundleKey, capacity);
                            startActivityForResult(intent, 205);
                        } else {
                            showToast(response.getString("ErrorMessage"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast(getString(R.string.error_something_wrong));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            req.setRetryPolicy(Application.getDefaultRetryPolice());
            req.setShouldCache(false);
            Application.getInstance().addToRequestQueue(req, "create_meeting_requests");

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 205 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void showDatePickerDialog() {
        final Calendar newCalendar = Calendar.getInstance();
        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth, 23, 55, 00);
                _create_schedule_date.setText(formatMonthStringDate(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
        datePicker.show();
    }


    public static String formatMonthStringDate(Date date) {
        return sdfMonthString.format(date);
    }


    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        _create_schedule_time.setText(aTime);
    }
}
