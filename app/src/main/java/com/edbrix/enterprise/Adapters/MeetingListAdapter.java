package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView imageView;

        ViewHolder(View itemView, final MeetingListInterface meetingListInterface) {
            super(itemView);

            // name = itemView.findViewById(R.id.org_name);
            // imageView = itemView.findViewById(R.id.org_image);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    meetingListInterface.onMeetingSelected(list.get(getLayoutPosition()).getId(),
                            list.get(getLayoutPosition()).getType());
                }
            });
        }
    }

}
