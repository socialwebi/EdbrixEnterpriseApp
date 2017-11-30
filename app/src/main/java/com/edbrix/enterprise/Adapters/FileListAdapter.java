package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.edbrix.enterprise.Interfaces.CourseContentButtonListener;
import com.edbrix.enterprise.Interfaces.DashboardListInterface;
import com.edbrix.enterprise.Models.CourseContents;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.R;

import java.util.ArrayList;


public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    Context context;
    private ArrayList<CourseContents> list;
    private CourseContentButtonListener courseContentButtonListener;

    public FileListAdapter(Context context, ArrayList<CourseContents> list, CourseContentButtonListener courseContentButtonListener) {
        this.context = context;
        this.list = list;
        this.courseContentButtonListener = courseContentButtonListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_file_list, parent, false);

        return new FileListAdapter.ViewHolder(v, courseContentButtonListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.name.setText(list.get(position).getTitle());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to Delete? ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                courseContentButtonListener.onCourseDeleteClick(list.get(position).getId());
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refresh(ArrayList<CourseContents> newList) {

        list = new ArrayList<>();
        list = newList;
        notifyDataSetChanged();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageButton delete;

        ViewHolder(View itemView, final CourseContentButtonListener courseContentButtonListener) {
            super(itemView);

            name = itemView.findViewById(R.id.file_list_name);
            delete = itemView.findViewById(R.id.file_list_delete);

        }
    }

}
