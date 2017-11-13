package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edbrix.enterprise.Interfaces.DashboardListInterface;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.Meeting;
import com.edbrix.enterprise.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardMeetingListAdapter extends RecyclerView.Adapter<DashBoardMeetingListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Meeting> list;
    private DashboardListInterface dashRecyclerInterface;

    public DashBoardMeetingListAdapter(Context context, ArrayList<Meeting> list, DashboardListInterface dashRecyclerInterface) {
        this.context = context;
        this.list = list;
        this.dashRecyclerInterface = dashRecyclerInterface;
    }


    @Override
    public DashBoardMeetingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_dashboard_meeting_list, parent, false);

        return new DashBoardMeetingListAdapter.ViewHolder(v, dashRecyclerInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.name.setText(list.get(position).getTitle());

        String totalTime = list.get(position).getStartDateTime() +" - "+list.get(position).getEndDateTime();
        holder.time.setText(totalTime);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refresh(ArrayList<Meeting> list)
    {
        this.list = new ArrayList<>();
        this.list = list;
        notifyDataSetChanged();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView time;

        ViewHolder(View itemView, final DashboardListInterface dashRecyclerInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.meeting_schedule_name);
            time = itemView.findViewById(R.id.meeting_schedule_time);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dashRecyclerInterface.onListSelected(list.get(getLayoutPosition()).getId(), list.get(getLayoutPosition()).getType());
                }
            });
        }
    }


}
