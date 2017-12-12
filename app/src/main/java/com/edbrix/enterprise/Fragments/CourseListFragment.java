package com.edbrix.enterprise.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Activities.CourseDetailActivity;
import com.edbrix.enterprise.Activities.PlayCourseActivity;
import com.edbrix.enterprise.Adapters.CourseListAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.Interfaces.CourseListActionListener;
import com.edbrix.enterprise.Interfaces.OnLoadMoreListener;
import com.edbrix.enterprise.Models.CourseListResponseData;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Utils.SessionManager;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseListFragment extends BaseFragment {

    private Context context;
    private RecyclerView courseListRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CourseListAdapter courseListAdapter;
    private ArrayList<Courses> coursesArrayList;
    private SessionManager sessionManager;
    private int pageNo;
    private String dataType;


    public CourseListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        context = getActivity();
        sessionManager = new SessionManager(context);
        pageNo = 1;
        dataType = "course";
        coursesArrayList = new ArrayList<>();
        courseListRecyclerView = (RecyclerView) view.findViewById(R.id.courseListRecyclerView);
        courseListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                coursesArrayList.clear();
                pageNo = 1;
                getCourseList(SettingsMy.getActiveUser(), sessionManager.getSessionDeviceType(), dataType, pageNo);

            }
        });
        swipeRefreshLayout.setRefreshing(true);
        courseListAdapter = new CourseListAdapter(context, courseListRecyclerView, coursesArrayList);
        setCourseListAdapter();
        getCourseList(SettingsMy.getActiveUser(), sessionManager.getSessionDeviceType(), dataType, pageNo);
        return view;
    }

    /**
     * Get course list from server and load data
     *
     * @param activeUser Object of User class ie. logged active user.
     * @param deviceType Device type i.e. mob and tablet
     * @param dataType   Data type i.e. course, meeting and all
     * @param pageNo     page no i.e. 1,2,..etc. for pagination
     */
    private void getCourseList(final User activeUser, String deviceType, String dataType, int pageNo) {
        try {
            JSONObject jo = new JSONObject();

            jo.put("UserId", activeUser.getId());
            jo.put("AccessToken", activeUser.getAccessToken());
            jo.put("UserType", activeUser.getUserType());
            jo.put("DeviceType", deviceType);
            jo.put("DataType", dataType);
            jo.put("Page", pageNo);

//            jo.put("UserId", "1");
//            jo.put("AccessToken", "NTI1LTg1REEyUzMtQURTUzVELUVJNUI0QkM1MTE=");
//            jo.put("UserType", "I");
//            jo.put("DeviceType", "mob");
//            jo.put("DataType", "course");
//            jo.put("Page", "1");

//            jo.put("UserId", "3");
//            jo.put("AccessToken", "NTI1LTg1REEyUzMtQURTUzVELUVJNUI0QkM1MTM=");
//            jo.put("UserType", "L");
//            jo.put("DeviceType", deviceType);
//            jo.put("DataType", dataType);
//            jo.put("Page", pageNo);


//        if (BuildConfig.DEBUG) Timber.d("getCourseList Request Param: %s", jo.toString());

            GsonRequest<CourseListResponseData> getDashboardCourseSchedulesRequest = new GsonRequest<>(Request.Method.POST, Constants.getDashboardCourseSchedules, jo.toString(), CourseListResponseData.class,
                    new Response.Listener<CourseListResponseData>() {
                        @Override
                        public void onResponse(@NonNull CourseListResponseData response) {
//                        Timber.d("response: %s", response.toString());
                            swipeRefreshLayout.setRefreshing(false);
                            if (response.getErrorCode() != null && response.getErrorCode().length() > 0) {
//                            Timber.d("Error: %s", response.getErrorCode());
                                showToast(response.getErrorMessage());
                            } else {
                                if (response.getCoursesList() != null && response.getCoursesList().size() >= 0) {
//                                    //Remove loading item
                                    if (coursesArrayList != null && coursesArrayList.size() > 0) {
                                        coursesArrayList.remove(coursesArrayList.size() - 1);
                                        courseListAdapter.notifyItemRemoved(coursesArrayList.size());
                                    }
                                    coursesArrayList.addAll(response.getCoursesList());
                                    courseListAdapter.notifyDataSetChanged();
                                    courseListAdapter.setLoaded();
                                } else {
                                    showToast("No courses found.");
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeRefreshLayout.setRefreshing(false);
                    Timber.d("Error: %s", error.getMessage());
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            getDashboardCourseSchedulesRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getDashboardCourseSchedulesRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getDashboardCourseSchedulesRequest, "getdashboardcourseandschedules");
        } catch (JSONException e) {
            swipeRefreshLayout.setRefreshing(false);
            Timber.e(e, "Parse getCourseList exception");
            showToast("Something went wrong. Please try again later.");
        }
    }

    /**
     * Send intent to phone dialer
     *
     * @param phoneNumber phone number
     */
    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    /**
     * Send intent to messenger to send sms
     *
     * @param phoneNumber phone number
     * @param smsBody     sms body
     */
    private void sendSMS(String phoneNumber, String smsBody) {
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
            showToast("SMS faild, please try again later.");
        }
    }

    private void setCourseListAdapter() {

        courseListAdapter.setCourseListActionListener(new CourseListActionListener() {
            @Override
            public void onCourseItemSelected(Courses courseItem) {

                if (courseItem != null) {
                    Intent courseDetail = new Intent(context, CourseDetailActivity.class);
                    courseDetail.putExtra(CourseDetailActivity.courseDetailBundleKey, courseItem);
                    startActivity(courseDetail);
                } else {

                }
            }

            @Override
            public void onCoursePlayClick(Courses courseItem) {
                if (courseItem != null) {
                    Intent playCourse = new Intent(context, PlayCourseActivity.class);
                    playCourse.putExtra(PlayCourseActivity.courseItemBundleKey, courseItem);
                    startActivity(playCourse);
                } else {

                }
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
        courseListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageNo = pageNo + 1;
                coursesArrayList.add(null);
                courseListAdapter.notifyItemInserted(coursesArrayList.size() - 1);
                getCourseList(SettingsMy.getActiveUser(), sessionManager.getSessionDeviceType(), dataType, pageNo);
            }
        });
        courseListRecyclerView.setAdapter(courseListAdapter);
        courseListRecyclerView.setVisibility(View.VISIBLE);
    }
}
