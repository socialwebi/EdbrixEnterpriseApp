package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edbrix.enterprise.Models.CourseContentData;
import com.edbrix.enterprise.Models.LearnersData;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.Constants;
import com.edbrix.enterprise.Utils.RoundedImageView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DrawerContentListAdapter extends RecyclerView.Adapter<DrawerContentListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CourseContentData> contentList;
    private ContentListActionListener contentListActionListener;

    public interface ContentListActionListener {

        void onListItemSelected(int position, CourseContentData contentData);

    }

    public DrawerContentListAdapter(Context context, ArrayList<CourseContentData> contentList, ContentListActionListener contentListActionListener) {
        this.context = context;
        this.contentList = contentList;
        this.contentListActionListener = contentListActionListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drawer_content_list_item, parent, false);

        return new DrawerContentListAdapter.ViewHolder(v, contentListActionListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (contentList.get(position).getType().equalsIgnoreCase(Constants.contentType_Section)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorActionBar));
            holder.contentName.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
            holder.contentName.setTypeface(Typeface.DEFAULT);
        }

        holder.contentName.setText(contentList.get(position).getTitle());

        if (contentList.get(position).isSelected()) {
            holder.statusImg.setVisibility(View.VISIBLE);
            holder.statusImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_pencil_orange));
        } else if(contentList.get(position).isChecked()) {
            holder.statusImg.setVisibility(View.VISIBLE);
            holder.statusImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.checked_orange));
        }else{
            holder.statusImg.setVisibility(View.GONE);
        }

       /* Picasso.with(context)
                .load("")
                .error(R.mipmap.user_profile)
                .into(holder.learnerImg);*/

    }

    @Override
    public int getItemCount() {
        return contentList == null ? 0 : contentList.size();
    }

    public void refreshList(ArrayList<CourseContentData> list) {
        if (this.contentList != null) {
            this.contentList = list;
            notifyDataSetChanged();
        }
    }

    public void setChecked(int position) {
        if (contentList != null) {
            for (int i = 0; i < contentList.size(); i++) {

                if (i < position) {
                    contentList.get(i).setChecked(true);
                } else {
                    contentList.get(i).setChecked(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setSelected(int position) {
        if (contentList != null) {
            for (int i = 0; i < contentList.size(); i++) {

                if (i == position) {
                    contentList.get(i).setSelected(true);
                } else {
                    contentList.get(i).setSelected(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView contentName;
        private ImageView contentTypeImage;
        private ImageView statusImg;

        ViewHolder(View itemView, final ContentListActionListener contentListActionListener) {
            super(itemView);

            contentName = itemView.findViewById(R.id.contentName);
            statusImg = itemView.findViewById(R.id.statusImg);
            contentTypeImage = itemView.findViewById(R.id.contentTypeImage);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelected(getLayoutPosition());
                    setChecked(getLayoutPosition());

                    if (!contentList.get(getLayoutPosition()).getType().equalsIgnoreCase(Constants.contentType_Section))
                        contentListActionListener.onListItemSelected(getLayoutPosition(), contentList.get(getLayoutPosition()));
                }
            });
        }
    }

}
