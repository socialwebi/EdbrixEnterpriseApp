package com.edbrix.enterprise.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edbrix.enterprise.Activities.VideoPlayerActivity;
import com.edbrix.enterprise.Models.FileData;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.commons.VideoThumbLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by rajk on 30/01/18.
 */

public class VideoPlayerListAdapter extends RecyclerView.Adapter<VideoPlayerListAdapter.VideoViewHolder> {
    private Context adContext;
    private List<FileData> fileDataList;
    private OnButtonActionListener onButtonActionListener;
    private final VideoThumbLoader mVideoThumbLoader;

    public interface OnButtonActionListener {

        public void onCardViewClicked(FileData fileData, int position);

    }

    public VideoPlayerListAdapter(Context adContext, List<FileData> filesList, OnButtonActionListener onButtonActionListener) {
        this.fileDataList = filesList;
        this.adContext = adContext;
        this.mVideoThumbLoader = new VideoThumbLoader();
        this.onButtonActionListener = onButtonActionListener;
    }

    @Override
    public VideoPlayerListAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoPlayerListAdapter.VideoViewHolder holder, final int position) {
        holder.fileDataAdpt = fileDataList.get(position);
        holder.thumbnail.setTag(holder.fileDataAdpt.getFileObject().getPath());// binding imageview

//        holder.thumbnail.setImageResource(R.drawable.play_circle_grey); //default image
        mVideoThumbLoader.showThumbByAsynctack(holder.fileDataAdpt.getFileObject().getPath(), holder.thumbnail);
        String fName = holder.fileDataAdpt.getFileName().replaceAll(".mp4", "");
        holder.title.setText(fName);
        holder.duration.setText(getVideoDuration(adContext, holder.fileDataAdpt.getFileObject()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonActionListener.onCardViewClicked(holder.fileDataAdpt,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return fileDataList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public TextView title, duration;
        public ImageView thumbnail;
        public FileData fileDataAdpt;
        public View mView;

        public VideoViewHolder(View view) {
            super(view);
            mView =view;
            title = (TextView) view.findViewById(R.id.videoName);
            duration = (TextView) view.findViewById(R.id.videoDuration);
            thumbnail = (ImageView) view.findViewById(R.id.videoThumbnail);

        }
    }

    /**
     * Return video file duration from File Object
     *
     * @param context   Context
     * @param uriOfFile URI of File
     * @return String with min:sec format
     */
    public String getVideoDuration(Context context, File uriOfFile) {
        int duration = 0;
        MediaPlayer mp = MediaPlayer.create(context, Uri.parse(uriOfFile.getPath()));
        try {
            duration = mp.getDuration();
            mp.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

/*convert millis to appropriate time*/
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public void removeItem(int pos) {
        if (fileDataList != null && fileDataList.size() > 0) {
            fileDataList.remove(pos);
            notifyDataSetChanged();
        }
    }

    public void updateList(List<FileData> list) {
        if (fileDataList != null && fileDataList.size() > 0) {
            fileDataList = list;
            notifyDataSetChanged();
        }
    }

    public void refresh(ArrayList<FileData> list) {
        this.fileDataList = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            this.fileDataList = list;
        }
        notifyDataSetChanged();

    }

}
