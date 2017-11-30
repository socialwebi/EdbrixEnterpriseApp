package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.edbrix.enterprise.Interfaces.ImageChoiceActionListener;
import com.edbrix.enterprise.Models.ChoicesData;
import com.edbrix.enterprise.Models.ImageContentData;
import com.edbrix.enterprise.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageDrawerAdapter extends RecyclerView.Adapter<ImageDrawerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ImageContentData> list;
    private ImageSelectionListener imageChoiceActionListener;
    private int selectedIndex;
    private boolean isAlreadyLoaded;

    public interface ImageSelectionListener {
        public void onSelect(ImageContentData imageContentData, int position);
    }

    public ImageDrawerAdapter(Context context, ArrayList<ImageContentData> list, ImageSelectionListener imageChoiceActionListener) {
        this.context = context;
        this.list = list;
        this.imageChoiceActionListener = imageChoiceActionListener;
    }

    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_pager, parent, false);

        return new ImageDrawerAdapter.ViewHolder(v, imageChoiceActionListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (!isAlreadyLoaded) {
            Picasso.with(context)
                    .load(list.get(position).getImg_url())
                    .error(R.drawable.image_placeholder)
                    .into(holder.imgChoice);
        } else {
            if (selectedIndex != -1 && position == selectedIndex) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAppOrange));
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
            }
        }

        if (position == (list.size() - 1))
            isAlreadyLoaded = true;

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void refresh(ArrayList<ImageContentData> list) {
        this.list = new ArrayList<>();
        this.list = list;
        notifyDataSetChanged();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgChoice;

        ViewHolder(View itemView, final ImageSelectionListener imageChoiceActionListener) {
            super(itemView);

            imgChoice = itemView.findViewById(R.id.imgContent);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedIndex(getLayoutPosition());
                    imageChoiceActionListener.onSelect(list.get(getLayoutPosition()), getLayoutPosition());
                }
            });
        }
    }

}
