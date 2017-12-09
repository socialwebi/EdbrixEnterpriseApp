package com.edbrix.enterprise.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

import timber.log.Timber;

public class CreateScheduleActivity extends BaseActivity {

    Context context;
    TextInputEditText _create_schedule_title;
    TextInputEditText _create_schedule_date;
    TextInputEditText _create_schedule_time;
    TextInputEditText _create_schedule_price;
    TextInputEditText _create_schedule_color;
    TextInputEditText _create_schedule_capacity;
    TextInputEditText _create_schedule_description;
    LinearLayout layout;
    Spinner _create_schedule_minutes;
    Spinner _create_schedule_availability;

    private  String title;
    private  String date;
    private  String time;
    private  String price;
    private  String colorCode;
    private  String capacity;
    private String description;

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
        layout = findViewById(R.id.create_schedule_layout);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.minutes, R.layout.custom_text_layout);
        adapter.setDropDownViewResource(R.layout.custom_text_layout);
        _create_schedule_minutes.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.availability, R.layout.custom_text_layout);
        adapter2.setDropDownViewResource(R.layout.custom_text_layout);
        _create_schedule_availability.setAdapter(adapter2);
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

        title = _create_schedule_title.getText().toString().trim();
        date = _create_schedule_date.getText().toString().trim();
        time = _create_schedule_time.getText().toString().trim();
        price = _create_schedule_price.getText().toString().trim();
        colorCode = _create_schedule_color.getText().toString().trim();
        capacity = _create_schedule_capacity.getText().toString().trim();
        description = _create_schedule_description.getText().toString().trim();

        if (price.isEmpty()) {
            price = "0";
        }
        if (title.isEmpty()) {
            _create_schedule_title.setError(getString(R.string.error_edit_text));
        } else if (date.isEmpty()) {
            _create_schedule_title.setError(null);
            _create_schedule_date.setError(getString(R.string.error_edit_text));
        } else if (time.isEmpty()) {
            _create_schedule_date.setError(null);
            _create_schedule_time.setError(getString(R.string.error_edit_text));
        } else if (colorCode.isEmpty()) {
            _create_schedule_time.setError(null);
            _create_schedule_color.setError(getString(R.string.error_edit_text));
        } else if (description.isEmpty()) {
            _create_schedule_color.setError(null);
            _create_schedule_description.setError(getString(R.string.error_edit_text));
        } else {
            _create_schedule_description.setError(null);

            // createMeeting();

        }
    }

    private void createMeeting() {

        User user = SettingsMy.getActiveUser();
        if (user != null) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("UserId", user.getId());
                jo.put("AccessToken", user.getAccessToken());


            } catch (JSONException e) {
                Timber.e(e, "Parse create meeting exception");
                return;
            }
            if (BuildConfig.DEBUG) Timber.d("Meeting: %s", jo.toString());

            JsonRequest req = new JsonRequest(Request.Method.POST, Constants.setCreateCourse, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    Timber.d("Response : %s", response.toString());
                    try {

                        if (response.getString("ErrorCode").equals("0")) {

                            Toast.makeText(context, "Update successful ", Toast.LENGTH_SHORT).show();

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
            Application.getInstance().addToRequestQueue(req, "create_meeting_requests");

        }
    }

}
