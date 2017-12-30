package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edbrix.enterprise.Interfaces.MeetingListInterface;
import com.edbrix.enterprise.Models.Meeting;
import com.edbrix.enterprise.R;

import java.util.ArrayList;

public class MeetingListAdapter extends RecyclerView.Adapter<MeetingListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Meeting> list;
    private MeetingListInterface meetingListInterface;

    public MeetingListAdapter(Context context, ArrayList<Meeting> list, MeetingListInterface meetingListInterface) {
        this.context = context;
        this.list = list;
        this.meetingListInterface = meetingListInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_meeting_list, parent, false);

        return new ViewHolder(v, meetingListInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position % 2 == 1) {
            holder.linear.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));

        } else {
            holder.linear.setBackgroundColor(context.getResources().getColor(R.color.colorActionBar));
        }

        holder.day.setText(list.get(position).getMeetingDay());
        String month = list.get(position).getMeetingMonth() + ", " + list.get(position).getMeetingYear();
        holder.date.setText(month);
        holder.title.setText(list.get(position).getTitle());
        holder.time.setText(list.get(position).getStartDateTime() + " - " + list.get(position).getEndDateTime());

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void refresh(ArrayList<Meeting> list) {
        this.list = new ArrayList<>();
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linear;
        private View view;
        private TextView day;
        private TextView date;
        private TextView title;
        private TextView time;

        ViewHolder(View itemView, final MeetingListInterface meetingListInterface) {
            super(itemView);

            linear = itemView.findViewById(R.id.meetings_linear);
            view = itemView.findViewById(R.id.meetings_view);
            day = itemView.findViewById(R.id.meetings_day);
            date = itemView.findViewById(R.id.meetings_date);
            title = itemView.findViewById(R.id.meetings_name);
            time = itemView.findViewById(R.id.meetings_time);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    meetingListInterface.onMeetingSelected(list.get(getLayoutPosition()));
                }
            });
        }
    }

}
