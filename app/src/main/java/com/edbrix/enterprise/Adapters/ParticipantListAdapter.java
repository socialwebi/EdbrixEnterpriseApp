package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.edbrix.enterprise.Models.MeetingUsers;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MeetingUsers> meeting;

    public ParticipantListAdapter(Context context, ArrayList<MeetingUsers> meeting) {

        this.context = context;
        this.meeting = meeting;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_participant_list, parent, false);

        return new ParticipantListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhiteSmoke));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorActionBar));
        }

        holder.name.setText(meeting.get(position).getName());

        if (meeting.get(position).getProfileImageURL() != null && !meeting.get(position).getProfileImageURL().isEmpty()) {
            Picasso.with(context)
                    .load(meeting.get(position).getProfileImageURL())
                    .fit()
                    .error(R.mipmap.user_profile)
                    .into(holder.imageView);
        }

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + meeting.get(holder.getAdapterPosition()).getPhoneNo()));
                    context.startActivity(dialIntent);
                } catch (Exception e) {
                    Toast.makeText(context, "This feature not supported ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

        holder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:" + meeting.get(holder.getAdapterPosition()).getPhoneNo()));
                    context.startActivity(sendIntent);
                } catch (Exception e) {
                    Toast.makeText(context, "This feature not supported ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return meeting.size();
    }

    public void refreshList(ArrayList<MeetingUsers> meeting) {
        this.meeting = meeting;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private RoundedImageView imageView;
        private ImageButton callButton;
        private ImageButton messageButton;


        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.meetingUserName);
            imageView = itemView.findViewById(R.id.meetingUserImage);
            callButton = itemView.findViewById(R.id.meetingUserCall);
            messageButton = itemView.findViewById(R.id.meetingUserMessage);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*orgRecyclerInterface.onOrgProductSelected(view, list.get(getLayoutPosition()).getId(),
                            list.get(getLayoutPosition()).getOrganizationName(), list.get(getLayoutPosition()).getOrganizationImage());*/
                }
            });
        }
    }

}
