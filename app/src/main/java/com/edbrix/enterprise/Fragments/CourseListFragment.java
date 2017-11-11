package com.edbrix.enterprise.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Activities.DashboardActivity;
import com.edbrix.enterprise.Activities.OrganizationListActivity;
import com.edbrix.enterprise.Adapters.CourseListAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.BuildConfig;
import com.edbrix.enterprise.Models.CourseListResponseData;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseListFragment extends Fragment {

    private Context context;
    private RecyclerView courseListRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CourseListAdapter courseListAdapter;


    public CourseListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        context = getActivity();
        courseListRecyclerView = (RecyclerView) view.findViewById(R.id.courseListRecyclerView);
        courseListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getCourseList(SettingsMy.getActiveUser(), "mob", "course", 1);

            }
        });
        swipeRefreshLayout.setRefreshing(true);
        getCourseList(SettingsMy.getActiveUser(), "mob", "course", 1);
        return view;
    }

    private void getCourseList(final User activeUser, String deviceType, String dataType, int pageNo) {
        try {
            JSONObject jo = new JSONObject();

          /*  jo.put("UserId", activeUser.getId());
            jo.put("AccessToken", activeUser.getAccessToken());
            jo.put("UserType", activeUser.getUserType());
            jo.put("DeviceType", deviceType);
            jo.put("DataType", dataType);
            jo.put("Page", pageNo);*/

            jo.put("UserId", "1");
            jo.put("AccessToken", "NTI1LTg1REEyUzMtQURTUzVELUVJNUI0QkM1MTE=");
            jo.put("UserType", "I");
            jo.put("DeviceType", "mob");
            jo.put("DataType", "course");
            jo.put("Page", "1");


//        if (BuildConfig.DEBUG) Timber.d("getCourseList Request Param: %s", jo.toString());

            GsonRequest<CourseListResponseData> getDashboardCourseSchedulesRequest = new GsonRequest<>(Request.Method.POST, Constants.getDashboardCourseSchedules, jo.toString(), CourseListResponseData.class,
                    new Response.Listener<CourseListResponseData>() {
                        @Override
                        public void onResponse(@NonNull CourseListResponseData response) {
//                        Timber.d("response: %s", response.toString());

                            if (response.getErrorCode() != null && response.getErrorCode().length() > 0) {
//                            Timber.d("Error: %s", response.getErrorCode());
                                Toast.makeText(context, response.getErrorMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                if (response.getCoursesList() != null && response.getCoursesList().size() >= 0) {
                                    courseListAdapter = new CourseListAdapter(context, response.getCoursesList(), new CourseListAdapter.CourseListActionListener() {
                                        @Override
                                        public void onCourseItemSelected(Courses courses) {

                                        }

                                        @Override
                                        public void onCoursePlayClick(String url) {

                                        }

                                        @Override
                                        public void onCourseMessageClick(String mobNo) {
                                            sendSMS(mobNo, "Hello..,");
                                        }

                                        @Override
                                        public void onCourseCallClick(String mobNo) {
                                            dialContactPhone(mobNo);
                                        }
                                    });

                                    courseListRecyclerView.setAdapter(courseListAdapter);
                                    courseListRecyclerView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Timber.d("Error: %s", error.getMessage());
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            getDashboardCourseSchedulesRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getDashboardCourseSchedulesRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getDashboardCourseSchedulesRequest, "getdashboardcourseandschedules");
        } catch (JSONException e) {
            Timber.e(e, "Parse getCourseList exception");
            return;
        }
    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    protected void sendSMS(String phoneNumber, String smsBody) {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body", smsBody);

        try {
            startActivity(smsIntent);
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }
}
