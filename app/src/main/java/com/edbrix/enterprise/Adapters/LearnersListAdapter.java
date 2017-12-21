package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edbrix.enterprise.Models.Courses;
import com.edbrix.enterprise.Models.LearnersData;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.Utils.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LearnersListAdapter extends RecyclerView.Adapter<LearnersListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LearnersData> learnersList;
    private LearnersListActionListener learnersListActionListener;

    public interface LearnersListActionListener {

        void onListItemSelected(LearnersData learnersData);

    }

    public LearnersListAdapter(Context context, ArrayList<LearnersData> learnersList, LearnersListActionListener learnersListActionListener) {
        this.context = context;
        this.learnersList = learnersList;
        this.learnersListActionListener = learnersListActionListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.learner_list_item, parent, false);

        return new LearnersListAdapter.ViewHolder(v, learnersListActionListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhiteSmoke));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDivider));
        }

        holder.email.setText(learnersList.get(position).getEmail());
        holder.name.setText(learnersList.get(position).getFullName());
        if (learnersList.get(position).isChecked()) {
            holder.checkImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.checked_orange));
        } else {
            holder.checkImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.unchecked_orange));
        }

       /* Picasso.with(context)
                .load("")
                .error(R.mipmap.user_profile)
                .into(holder.learnerImg);*/

    }

    @Override
    public int getItemCount() {
        return learnersList == null ? 0 : learnersList.size();
    }

    public void refreshList(ArrayList<LearnersData> list) {
        if (this.learnersList != null) {
            this.learnersList = list;
            notifyDataSetChanged();
        }
    }

    public void setChecked(int position) {
        this.learnersList.get(position).setChecked(!learnersList.get(position).isChecked());
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView email;
        private TextView name;
        private ImageView checkImg;
        private RoundedImageView learnerImg;

        ViewHolder(View itemView, final LearnersListActionListener learnersListActionListener) {
            super(itemView);

            email = itemView.findViewById(R.id.learnerEmail);
            name = itemView.findViewById(R.id.learnerName);
            learnerImg = itemView.findViewById(R.id.learnerImage);
            checkImg = itemView.findViewById(R.id.checkImg);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setChecked(getLayoutPosition());
                    learnersListActionListener.onListItemSelected(learnersList.get(getLayoutPosition()));
                }
            });
        }
    }

}
