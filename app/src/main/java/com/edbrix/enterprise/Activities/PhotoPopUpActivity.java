package com.edbrix.enterprise.Activities;

import android.os.Bundle;

import com.edbrix.enterprise.R;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.edbrix.enterprise.commons.TouchImageView;
import com.squareup.picasso.Picasso;

public class PhotoPopUpActivity extends BaseActivity {

    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pop_up);

        imgUrl = getIntent().getStringExtra("IMGURL");

        TouchImageView img = new TouchImageView(this);
        img.setBackgroundColor(getResources().getColor(R.color.colorMainText));
        img.setImageBitmap(null);
        img.destroyDrawingCache();

        Picasso.with(PhotoPopUpActivity.this)
                .load(imgUrl)
                .error(R.drawable.image_placeholder)
                .into(img);

        img.setMaxZoom(4f);
        setContentView(img);
    }
}
