package com.edbrix.enterprise.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.edbrix.enterprise.Fragments.ImagePageFragment;
import com.edbrix.enterprise.Models.ImageContentData;
import com.edbrix.enterprise.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rajk on 16/11/17.
 */

public class CoursePlayImagePagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<ImageContentData> imageList;

    public CoursePlayImagePagerAdapter(FragmentManager fm,ArrayList<ImageContentData> imageList) {
        super(fm);
        this.imageList = imageList;
    }

    @Override
    public Fragment getItem(int position) {
        return ImagePageFragment.getInstance(imageList.get(position));
    }

    @Override
    public int getCount() {
        return imageList == null ? 0 : imageList.size();
    }
}
