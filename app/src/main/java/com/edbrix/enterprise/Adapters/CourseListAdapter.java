package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edbrix.enterprise.Interfaces.OrganizationListInterface;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.Organizations;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Courses> courseList;
    private CourseListActionListener courseListActionListener;

    public interface CourseListActionListener{
         void onCourseItemSelected(Courses courses);
         void onCoursePlayClick(String url);
         void onCourseMessageClick(String mobNo);
         void onCourseCallClick(String mobNo);
    }

    public CourseListAdapter(Context context, ArrayList<Courses> courseList, CourseListActionListener courseListActionListener) {

        this.context = context;
        this.courseList = courseList;
        this.courseListActionListener = courseListActionListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_courses, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.txtCourseName.setText(courseList.get(position).getTitle());
        holder.txtCourseBy.setText(courseList.get(position).getInstructor_name());
        holder.txtCourseDesc.setText(courseList.get(position).getDescription());

        if (courseList.get(position).getImage_url()!=null && !courseList.get(position).getImage_url().isEmpty()) {
            Picasso.with(context)
                    .load(courseList.get(position).getImage_url())
                    .error(R.drawable.edbrix_logo)
                    .into(holder.imgCourseBy);
        }

        holder.btnGoDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseListActionListener.onCourseItemSelected(courseList.get(position));
            }
        });

        holder.btnCourseCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseListActionListener.onCourseCallClick(courseList.get(position).getInstructor_mobileno());
            }
        });

        holder.btnCourseMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseListActionListener.onCourseMessageClick(courseList.get(position).getInstructor_mobileno());
            }
        });

        holder.btnCoursePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseListActionListener.onCoursePlayClick("");
            }
        });
    }

    @Override
    public int getItemCount() {
        if(courseList!=null && courseList.size()>0){
            return courseList.size();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView txtCourseName;
        private TextView txtCourseBy;
        private TextView txtCourseDesc;
        private ImageView btnCoursePlay;
        private ImageView btnCourseMsg;
        private ImageView btnCourseCall;
        private ImageView btnGoDetail;
        private RoundedImageView imgCourseBy;


        ViewHolder(View itemView) {
            super(itemView);
            mView =itemView;
            txtCourseName = itemView.findViewById(R.id.txtCourseName);
            txtCourseBy = itemView.findViewById(R.id.txtCourseBy);
            txtCourseDesc = itemView.findViewById(R.id.txtCourseDesc);

            btnCoursePlay = itemView.findViewById(R.id.btnCoursePlay);
            btnCourseMsg = itemView.findViewById(R.id.btnCourseMsg);
            btnCourseCall = itemView.findViewById(R.id.btnCourseCall);
            btnGoDetail = itemView.findViewById(R.id.btnGoDetail);

            imgCourseBy = itemView.findViewById(R.id.imgCourseBy);
        }
    }

}
