package com.edbrix.enterprise.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edbrix.enterprise.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingListFragment extends Fragment {

    Context context;
    RecyclerView _meeting_list_recycler;

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

}
