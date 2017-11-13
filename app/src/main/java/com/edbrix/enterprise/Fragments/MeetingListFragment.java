package com.edbrix.enterprise.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
public class MeetingListFragment extends Fragment {

    Context context;
    private RecyclerView _meeting_list_recycler;
    RelativeLayout layout;

    private String deviceType;
    private String dataType;
    private int pageNo = 1;

    public MeetingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meeting_list, container, false);

        context = getActivity();

        _meeting_list_recycler = view.findViewById(R.id.meeting_list_recycler);

        return  view;
    }


    private void getMeetingeList() {

        User activeUser = SettingsMy.getActiveUser();
        if (activeUser!=null) {

            JSONObject jo = new JSONObject();
            try {

                jo.put("UserId", activeUser.getId());
                jo.put("AccessToken", activeUser.getAccessToken());
                jo.put("UserType", activeUser.getUserType());
                jo.put("DeviceType", deviceType);
                jo.put("DataType", dataType);
                jo.put("Page", pageNo);
            } catch (JSONException e) {
                Timber.e(e, "Parse getCourseList exception");
                return;
            }

            if (BuildConfig.DEBUG) Timber.d("getCourseList Request Param: %s", jo.toString());

                GsonRequest<ResponseData> getDashboardCourseSchedulesRequest = new GsonRequest<>(Request.Method.POST, Constants.getDashboardCourseSchedules, jo.toString(), ResponseData.class,
                        new Response.Listener<ResponseData>() {
                            @Override
                            public void onResponse(@NonNull ResponseData response) {
                            Timber.d("response: %s", response.toString());
                                if (response.getErrorCode()==null) {

                                    //
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
                        Timber.d("Error: %s", error.getMessage());
                        try {
                            Snackbar.make(layout, getString(R.string.error_something_wrong), Snackbar.LENGTH_LONG).show();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            Toast.makeText(context, getString(R.string.error_something_wrong), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                getDashboardCourseSchedulesRequest.setRetryPolicy(Application.getDefaultRetryPolice());
                getDashboardCourseSchedulesRequest.setShouldCache(false);
                Application.getInstance().addToRequestQueue(getDashboardCourseSchedulesRequest, "meeting_list_requests");
        }
    }
}
