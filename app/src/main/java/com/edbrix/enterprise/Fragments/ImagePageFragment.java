package com.edbrix.enterprise.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.edbrix.enterprise.Activities.PhotoPopUpActivity;
import com.edbrix.enterprise.Models.ImageContentData;
import com.edbrix.enterprise.R;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImagePageFragment extends Fragment {

    private ImageContentData imageContent;

    public ImagePageFragment() {
        // Required empty public constructor
    }

    public static ImagePageFragment getInstance(ImageContentData imageContentData) {
        ImagePageFragment f = new ImagePageFragment();
        Bundle args = new Bundle();
        args.putSerializable("image_source", imageContentData);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageContent = (ImageContentData) getArguments().getSerializable("image_source");
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageThumb = (ImageView) view.findViewById(R.id.img_thumb);
        if (imageContent.getImg_url() != null && !imageContent.getImg_url().isEmpty()) {
            Picasso.with(getContext())
                    .load(imageContent.getImg_url())
                    .error(R.drawable.image_placeholder)
                    .into(imageThumb);
        }
        imageThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photo = new Intent(getContext(), PhotoPopUpActivity.class);
                photo.putExtra("IMGURL", imageContent.getImg_url());
                startActivity(photo);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_page, container, false);
    }

}
