package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edbrix.enterprise.Interfaces.MeetingListInterface;
import com.edbrix.enterprise.Models.Meeting;
import com.edbrix.enterprise.Models.TrainingSessionEventContentData;
import com.edbrix.enterprise.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SessionEventListAdapter extends RecyclerView.Adapter<SessionEventListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TrainingSessionEventContentData> list;
    private SessionEventItemListener sessionEventItemListener;

    public interface SessionEventItemListener {

        void onSessionEventSelected(TrainingSessionEventContentData sessionEventContentData);

    }

    public SessionEventListAdapter(Context context, ArrayList<TrainingSessionEventContentData> list, SessionEventItemListener sessionEventItemListener) {
        this.context = context;
        this.list = list;
        this.sessionEventItemListener = sessionEventItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_training_session, parent, false);

        return new ViewHolder(v, sessionEventItemListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position % 2 == 1) {
            holder.linear.setBackgroundColor(context.getResources().getColor(R.color.colorActionBar));
        } else {
            holder.linear.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        }

        holder.day.setText(list.get(position).getSessionEvtDay());
        String month = list.get(position).getSessionEvtMonth() + ", " + list.get(position).getSessionEvtYear();
        holder.date.setText(month);
        holder.title.setText(list.get(position).getTitle());
        holder.place.setText(list.get(position).getLocation());
        holder.time.setText(list.get(position).getStartDateTime() + " - " + list.get(position).getEndDateTime());
        if (list.get(position).getDescription() != null && !list.get(position).getDescription().isEmpty()) {
            holder.info.setText(list.get(position).getDescription());
            holder.infoBtn.setVisibility(View.VISIBLE);
        } else {
            holder.infoBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void refresh(ArrayList<TrainingSessionEventContentData> list) {
        this.list = new ArrayList<>();
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linear;
        private TextView day;
        private TextView date;
        private TextView title;
        private TextView time;
        private TextView place;
        private TextView info;
        private ImageButton infoBtn;
        private boolean infoShowing;

        ViewHolder(View itemView, final SessionEventItemListener sessionEventItemListener) {
            super(itemView);

            linear = itemView.findViewById(R.id.training_session_linear);
            day = itemView.findViewById(R.id.training_session_day);
            date = itemView.findViewById(R.id.training_session_date);
            title = itemView.findViewById(R.id.training_session_name);
            time = itemView.findViewById(R.id.training_session_time);
            place = itemView.findViewById(R.id.training_session_place);
            info = itemView.findViewById(R.id.training_session_text_info);
            infoBtn = itemView.findViewById(R.id.training_session_button_info);
            infoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    infoShowing = !infoShowing;
                    if (infoShowing) {
                        info.setVisibility(View.VISIBLE);
                        infoBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.info_active_icon));
                    } else {
                        info.setVisibility(View.GONE);
                        infoBtn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.info_icon));
                    }
                }
            });

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sessionEventItemListener.onSessionEventSelected(list.get(getLayoutPosition()));
                }
            });
        }
    }

}
