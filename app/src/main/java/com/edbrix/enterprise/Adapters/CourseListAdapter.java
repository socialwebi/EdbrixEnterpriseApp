package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edbrix.enterprise.Interfaces.CourseListActionListener;
import com.edbrix.enterprise.Interfaces.OnLoadMoreListener;
import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.RoundedImageView;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private ArrayList<Courses> courseList;
    private CourseListActionListener courseListActionListener;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;


    public CourseListAdapter(Context context, RecyclerView mRecyclerView, ArrayList<Courses> courseList) {

        this.context = context;
        this.courseList = courseList;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_courses, parent, false);
            return new CourseViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CourseViewHolder) {
            final Courses course = courseList.get(position);
            CourseViewHolder courseViewHolder = (CourseViewHolder) holder;
            courseViewHolder.txtCourseName.setText(course.getTitle());
            courseViewHolder.txtCourseBy.setText("By " + course.getInstructor_name());
            courseViewHolder.txtCourseDesc.setText(course.getDescription());
            courseViewHolder.btnCourseStart.setEnabled(course.isContentAvailable());
//        courseViewHolder.setRating.setRating(Float.valueOf("2.5"));

            if (course.getInstructor_image_url() != null && !course.getInstructor_image_url().isEmpty()) {
                Picasso.with(context)
                        .load(course.getCourse_image_url())
                        .error(R.drawable.edbrix_logo)
                        .into(courseViewHolder.imgCourseBy);
            }

            courseViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    courseListActionListener.onCourseItemSelected(course);
                }
            });
            courseViewHolder.btnGoDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    courseListActionListener.onCourseItemSelected(course);
                }
            });

            courseViewHolder.btnCourseCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    courseListActionListener.onCourseCallClick(course.getInstructor_mobileno());
                }
            });

            courseViewHolder.btnCourseMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    courseListActionListener.onCourseMessageClick(course.getInstructor_mobileno());
                }
            });

            courseViewHolder.btnCourseMsgL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    courseListActionListener.onCourseMessageClick(course.getInstructor_mobileno());
                }
            });

            courseViewHolder.btnCourseStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    courseListActionListener.onCoursePlayClick(course);
                }
            });

            courseViewHolder.btnCoursePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    courseListActionListener.onCoursePlayClick(course);
                }
            });

            if (position % 2 == 1) {
                courseViewHolder.mView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorActionBar));
            } else {
                courseViewHolder.mView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
            }
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return courseList == null ? 0 : courseList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return courseList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setCourseListActionListener(CourseListActionListener courseListActionListener) {
        this.courseListActionListener = courseListActionListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView txtCourseName;
        private TextView txtCourseBy;
        private TextView txtCourseDesc;
        private ImageView btnCoursePlay;
        private ImageView btnCourseMsg;
        private ImageView btnCourseMsgL;
        private ImageView btnCourseCall;
        private ImageView btnGoDetail;
        private Button btnCourseStart;
        private RoundedImageView imgCourseBy;
        private RatingBar setRating;


        CourseViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            txtCourseName = itemView.findViewById(R.id.txtCourseName);
            txtCourseBy = itemView.findViewById(R.id.txtCourseBy);
            txtCourseDesc = itemView.findViewById(R.id.txtCourseDesc);

            btnCoursePlay = itemView.findViewById(R.id.btnCoursePlay);
            btnCourseMsg = itemView.findViewById(R.id.btnCourseMsg);
            btnCourseMsgL = itemView.findViewById(R.id.btnCourseMsgL);
            btnCourseCall = itemView.findViewById(R.id.btnCourseCall);
            btnGoDetail = itemView.findViewById(R.id.btnGoDetail);
            btnCourseStart = itemView.findViewById(R.id.btnCourseStart);

            imgCourseBy = itemView.findViewById(R.id.imgCourseBy);

            setRating = itemView.findViewById(R.id.setRating);

            /*if(SettingsMy.getActiveUser().getUserType().equalsIgnoreCase("L")){
                btnCoursePlay.setVisibility(View.GONE);
                btnCourseMsg.setVisibility(View.GONE);
                btnCourseCall.setVisibility(View.GONE);

                btnCourseMsgL.setVisibility(View.VISIBLE);
                btnCourseStart.setVisibility(View.VISIBLE);

                txtCourseName.setTextColor(ContextCompat.getColor(context,R.color.colorMainText));

            }else{
                btnCoursePlay.setVisibility(View.VISIBLE);
                btnCourseMsg.setVisibility(View.VISIBLE);
                btnCourseCall.setVisibility(View.VISIBLE);

                btnCourseMsgL.setVisibility(View.GONE);
                btnCourseStart.setVisibility(View.GONE);

                txtCourseName.setTextColor(ContextCompat.getColor(context,R.color.colorAppOrange));
            }*/
        }
    }

    // "Loading item" ViewHolder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    public void updateList(ArrayList<Courses> filterList) {
        if (courseList != null) {
            courseList = filterList;
            notifyDataSetChanged();
        }
    }
}
