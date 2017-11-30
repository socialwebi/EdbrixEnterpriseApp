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
import com.edbrix.enterprise.Interfaces.OrganizationListInterface;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.Meeting;
import com.edbrix.enterprise.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardCourseListAdapter extends RecyclerView.Adapter<DashBoardCourseListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Courses> list;
    private DashboardListInterface dashRecyclerInterface;

    public DashBoardCourseListAdapter(Context context, ArrayList<Courses> list, DashboardListInterface dashRecyclerInterface) {
        this.context = context;
        this.list = list;
        this.dashRecyclerInterface = dashRecyclerInterface;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_dashboard_course_list, parent, false);

        return new DashBoardCourseListAdapter.ViewHolder(v, dashRecyclerInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.id.setText(list.get(position).getId());
        holder.name.setText(list.get(position).getTitle());

        Picasso.with(context)
                .load(list.get(position).getCourse_image_url())
                .error(R.drawable.edbrix_logo)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void refresh(ArrayList<Courses> list)
    {
        this.list = new ArrayList<>();
        this.list = list;
        notifyDataSetChanged();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView id;
        private TextView name;
        private ImageView imageView;

        ViewHolder(View itemView, final DashboardListInterface dashRecyclerInterface) {
            super(itemView);

            id = itemView.findViewById(R.id.meeting_course_id);
            name = itemView.findViewById(R.id.meeting_course_name);
            imageView = itemView.findViewById(R.id.meeting_course_image);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dashRecyclerInterface.onListSelected(list.get(getLayoutPosition()).getId(), list.get(getLayoutPosition()).getTitle());
                }
            });
        }
    }

}
