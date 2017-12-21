package com.edbrix.enterprise.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edbrix.enterprise.Adapters.LearnersListAdapter;
import com.edbrix.enterprise.Application;
import com.edbrix.enterprise.Models.LearnersData;
import com.edbrix.enterprise.Models.ResponseData;
import com.edbrix.enterprise.Models.User;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Volley.GsonRequest;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.commons.AlertDialogManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

public class LearnersListActivity extends BaseActivity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    public static final String scheduleIdBundleKey = "scheduleId";
    public static final String inviteLimitBundleKey = "inviteLimit";

    private Context context;
    private RecyclerView learnerListRecyclerView;
    private LearnersListAdapter learnersListAdapter;
    private ArrayList<LearnersData> learnersDataList;
    private ArrayList<String> learnerIdsList;
    private TextView limitText;
    private Button btnSkip;
    private Button btnInvite;
    private int inviteLimit;
    private String scheduleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learners_list);
        context = LearnersListActivity.this;
        scheduleId = getIntent().getStringExtra(scheduleIdBundleKey);
        inviteLimit = Integer.parseInt(getIntent().getStringExtra(inviteLimitBundleKey));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        learnerListRecyclerView = (RecyclerView) findViewById(R.id.learnerListRecyclerView);
        learnerListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        limitText = (TextView) findViewById(R.id.limitText);
        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnInvite = (Button) findViewById(R.id.btnInvite);
        limitText.setText("Max Capacity : " + inviteLimit);

        showBusyProgress();
        getLearnersList(SettingsMy.getActiveUser(), scheduleId, "");

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (learnerIdsList != null && learnerIdsList.size() > 0) {
                    showBusyProgress();
                    assignLearnerToAvailability(SettingsMy.getActiveUser(), scheduleId, learnerIdsList);
                } else {
                    showToast("Please select learner to invite from list.");
                }

            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        getAlertDialogManager().Dialog(R.string.app_name, "Are you sure want to discontinue?", true, new AlertDialogManager.onTwoButtonClickListner() {
            @Override
            public void onNegativeClick() {

            }

            @Override
            public void onPositiveClick() {
                setResult(RESULT_OK);
                finish();
            }
        }).show();
    }

    /**
     * Get Learners list from server and load data
     *
     * @param activeUser  Object of User class ie. logged active user.
     * @param scheduleId  schedule Id
     * @param learnerName Learner name
     */
    private void getLearnersList(final User activeUser, String scheduleId, final String learnerName) {
        try {
            JSONObject jo = new JSONObject();

            jo.put("UserId", activeUser.getId());
            jo.put("AccessToken", activeUser.getAccessToken());
            jo.put("Id", scheduleId);
            jo.put("LearnerName", learnerName);

            GsonRequest<ResponseData> getAssignAvailabilityLearnersListRequest = new GsonRequest<>(Request.Method.POST, Constants.assignAvailabilityLearnersList, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {
                            hideBusyProgress();
//                            swipeRefreshLayout.setRefreshing(false);
                            if (response.getErrorCode() != null && response.getErrorCode().equals("0")) {
                                if (response.getLearnersList() != null && response.getLearnersList().size() >= 0) {
                                    learnersDataList = new ArrayList<>();
                                    learnersDataList.addAll(response.getLearnersList());
                                    learnerIdsList = new ArrayList<>();
                                    learnersListAdapter = new LearnersListAdapter(context, learnersDataList, new LearnersListAdapter.LearnersListActionListener() {
                                        @Override
                                        public void onListItemSelected(LearnersData learnersData) {
                                            if (learnersData.isChecked()) {
                                                if (learnerIdsList.size() < inviteLimit) {
                                                    learnerIdsList.add(learnersData.getLearnerId());
                                                } else {
                                                    learnersData.setChecked(!learnersData.isChecked());
                                                    learnersListAdapter.notifyDataSetChanged();
                                                    showToast("Limit is over.");
                                                }
                                            } else {
                                                if (learnerIdsList.contains(learnersData.getLearnerId()))
                                                    learnerIdsList.remove(learnersData.getLearnerId());
                                            }
                                        }
                                    });
                                    learnerListRecyclerView.setAdapter(learnersListAdapter);
                                } else {
                                    showToast("No courses found.");
                                }
                            } else {
                                showToast(response.getErrorMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideBusyProgress();
//                    swipeRefreshLayout.setRefreshing(false);
                    Timber.d("Error: %s", error.getMessage());
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            getAssignAvailabilityLearnersListRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            getAssignAvailabilityLearnersListRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(getAssignAvailabilityLearnersListRequest, "assignavailabilitylearnerslist");
        } catch (JSONException e) {
//            swipeRefreshLayout.setRefreshing(false);
            hideBusyProgress();
            Timber.e(e, "Parse getLearnerList exception");
            showToast("Something went wrong. Please try again later.");
        }
    }

    /**
     * Get Learners list from server and load data
     *
     * @param activeUser Object of User class ie. logged active user.
     * @param scheduleId schedule Id
     * @param learnersId Learners Id
     */
    private void assignLearnerToAvailability(final User activeUser, String scheduleId, ArrayList<String> learnersId) {
        try {
            JSONObject jo = new JSONObject();

            jo.put("UserId", activeUser.getId());
            jo.put("AccessToken", activeUser.getAccessToken());
            jo.put("Id", scheduleId);
            jo.put("LearnerIds", learnersId.get(0));

            GsonRequest<ResponseData> assignLearnerToAvailabilityRequest = new GsonRequest<>(Request.Method.POST, Constants.assignLearnerToAvailability, jo.toString(), ResponseData.class,
                    new Response.Listener<ResponseData>() {
                        @Override
                        public void onResponse(@NonNull ResponseData response) {
//                            swipeRefreshLayout.setRefreshing(false);
                            hideBusyProgress();
                            if (response.getErrorMessage() != null && response.getErrorMessage().length() > 0) {
//                            Timber.d("Error: %s", response.getErrorCode());
                                showToast(response.getErrorMessage());
                            } else {
                                showToast(response.getMessage());
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    swipeRefreshLayout.setRefreshing(false);
                    hideBusyProgress();
                    Timber.d("Error: %s", error.getMessage());
                    showToast(SettingsMy.getErrorMessage(error));
                }
            });
            assignLearnerToAvailabilityRequest.setRetryPolicy(Application.getDefaultRetryPolice());
            assignLearnerToAvailabilityRequest.setShouldCache(false);
            Application.getInstance().addToRequestQueue(assignLearnerToAvailabilityRequest, "assignlearnertoavailability");
        } catch (JSONException e) {
//            swipeRefreshLayout.setRefreshing(false);
            hideBusyProgress();
            Timber.e(e, "Parse getLearnerList exception");
            showToast("Something went wrong. Please try again later.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query == null || query.trim().isEmpty()) {
            resetSearch();
            return false;
        }
        filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }
        filter(newText);
        return false;
    }

    public void resetSearch() {
        if (learnerListRecyclerView.getAdapter() != null)
            ((LearnersListAdapter) learnerListRecyclerView.getAdapter()).refreshList(learnersDataList);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    void filter(String text) {
        if (learnersDataList != null && learnersDataList.size() > 0) {
            ArrayList<LearnersData> temp = new ArrayList();
            for (LearnersData learnersData : learnersDataList) {
                //or use .equal(text) with you want equal match
                //use .toLowerCase() for better matches
                if (learnersData.getFullName().toLowerCase().contains(text)) {
                    temp.add(learnersData);
                }
            }
            //update recyclerview
            if (learnerListRecyclerView.getAdapter() != null && learnerListRecyclerView.getAdapter().getItemCount() > 0)
                ((LearnersListAdapter) learnerListRecyclerView.getAdapter()).refreshList(temp);
        }
    }

}
