package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.edbrix.enterprise.R;
import com.viewpagerindicator.IconPagerAdapter;

public class WalkthroughAdapter extends PagerAdapter implements IconPagerAdapter {


    private LayoutInflater mLayoutInflater;

    private int[] mResources;

    public WalkthroughAdapter(Context context, int[] mResources) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mResources = mResources;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_walkthrough, container, false);

        ImageView imageView = itemView.findViewById(R.id.walkthrough_image_view);
        imageView.setImageResource(mResources[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public int getIconResId(int index) {
        return mResources[index % mResources.length];
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
