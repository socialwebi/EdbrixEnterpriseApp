package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.edbrix.enterprise.Interfaces.ImageChoiceActionListener;
import com.edbrix.enterprise.Models.ChoicesData;
import com.edbrix.enterprise.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageChoiceListAdapter extends RecyclerView.Adapter<ImageChoiceListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ChoicesData> list;
    private ImageChoiceActionListener imageChoiceActionListener;
    private int selectedIndex;
    private boolean isAlreadyLoaded;

    public ImageChoiceListAdapter(Context context, ArrayList<ChoicesData> list, ImageChoiceActionListener imageChoiceActionListener) {
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
                .inflate(R.layout.list_item_image_choice, parent, false);

        return new ImageChoiceListAdapter.ViewHolder(v, imageChoiceActionListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.rdImgChoice.setText((char) (65 + position) + "");
        if (!isAlreadyLoaded) {
            Picasso.with(context)
                    .load(list.get(position).getChoice())
                    .error(R.drawable.edbrix_logo)
                    .into(holder.imgChoice);
        } else {
            if (selectedIndex != -1 && position == selectedIndex) {
                holder.rdImgChoice.setChecked(true);
            } else {
                holder.rdImgChoice.setChecked(false);
            }
        }

        isAlreadyLoaded = true;

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void refresh(ArrayList<ChoicesData> list) {
        this.list = new ArrayList<>();
        this.list = list;
        notifyDataSetChanged();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RadioButton rdImgChoice;
        private ImageView imgChoice;

        ViewHolder(View itemView, final ImageChoiceActionListener imageChoiceActionListener) {
            super(itemView);

            rdImgChoice = itemView.findViewById(R.id.rdImgChoice);
            imgChoice = itemView.findViewById(R.id.imgChoice);

            rdImgChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedIndex(getLayoutPosition());
                    imageChoiceActionListener.onImageChoiceSelected(list.get(getLayoutPosition()));
                }
            });

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

}
