package com.edbrix.enterprise.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.edbrix.enterprise.R;

/**
 * Created by rajk on 16/11/17.
 */

public class CoursePlayImagePagerAdapter extends PagerAdapter {

    private Context aContext;
    private LayoutInflater layoutInflater;

    public CoursePlayImagePagerAdapter(Context aContext) {
        this.aContext = aContext;
//        this.images = images;
        layoutInflater = (LayoutInflater) aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.item_image_pager, container, false);
        ImageView imgContent = (ImageView) itemView.findViewById(R.id.imgContent);
//        imgContent.setImageResource(images[position]);

        container.addView(itemView);

        //listening to image click
        imgContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(aContext, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
