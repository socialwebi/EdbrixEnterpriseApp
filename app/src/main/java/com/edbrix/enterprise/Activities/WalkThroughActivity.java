package com.edbrix.enterprise.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edbrix.enterprise.Adapters.WalkthroughAdapter;
import com.edbrix.enterprise.MainActivity;
import com.edbrix.enterprise.R;
import com.edbrix.enterprise.baseclass.BaseActivity;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class WalkThroughActivity extends BaseActivity {

    WalkthroughAdapter mAdapter;
    private ViewPager mPager;
    private RelativeLayout indicatorLayout;
    private TextView gotItText;
    private TextView nextText;
    private TextView skipText;
    private int itemCount;
    private Intent intent;
    private String isCouch = "";
    private boolean isMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_through);

        gotItText = findViewById(R.id.gotitTextView);
        nextText = findViewById(R.id.nextTextView);
        skipText = findViewById(R.id.skipTextView);
        indicatorLayout = findViewById(R.id.indicatorLayout);

        gotItText.setVisibility(View.GONE);

        intent = WalkThroughActivity.this.getIntent();
        if (intent != null) {
            isMain = intent.getBooleanExtra("ismain", false);
            isCouch = intent.getStringExtra("isCouch");
        }


        final int[] mResources = new int[]{
                R.drawable.walkthroughs_1,
                /*R.drawable.walkthroughs_2,
                R.drawable.walkthroughs_3,
                R.drawable.walkthroughs_4,
                R.drawable.walkthroughs_5*/

        };

        final int[] mResources1 = new int[]{
                R.drawable.walkthroughs_1,
                /*R.drawable.walkthroughs_2,
                R.drawable.walkthroughs_3,
                R.drawable.walkthroughs_4*/

        };

        final int[] mResources2 = new int[]{
                R.drawable.walkthroughs_1,
                /*R.drawable.walkthroughs_learner_2,
                R.drawable.walkthroughs_learner_3*/

        };

        mPager = findViewById(R.id.pager);
        mPager.setClipToPadding(false);
        mPager.setPadding(0, 100, 0, 0);
        mPager.setPageMargin(0);

        if (isCouch != null) {
            if (isCouch.equals("i"))
                mAdapter = new WalkthroughAdapter(this, mResources1);
            else if (isCouch.equals("l"))
                mAdapter = new WalkthroughAdapter(this, mResources2);
        } else
            mAdapter = new WalkthroughAdapter(this, mResources);

        PageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager);

        itemCount = mAdapter.getCount();


        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if ((position + 1) == itemCount) {
                    indicatorLayout.setVisibility(View.INVISIBLE);
                    skipText.setVisibility(View.GONE);
                    nextText.setVisibility(View.GONE);
                    gotItText.setVisibility(View.VISIBLE);
                } else {
                    indicatorLayout.setVisibility(View.VISIBLE);
                    skipText.setVisibility(View.VISIBLE);
                    nextText.setVisibility(View.VISIBLE);
                    gotItText.setVisibility(View.GONE);
                }
                Log.v("Walkthrough", "onPageScrolled position : " + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.v("Walkthrough", "onPageSelected position : ");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.v("Walkthrough", "onPageScrollStateChanged position : ");
            }
        });


        if (isMain) {
            gotItText.setText(getString(R.string.start));
        } else {
            gotItText.setText(getString(R.string.done));
        }

        gotItText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intent != null && intent.getBooleanExtra("ismain", false)) {
                        /*if (!managerSession.getWalkthroughSkipValue()) {
                            managerSession.addUpdateSkipWalkthroughPref(true);
                        }*/
                    startMainActivity();
                } else {
                    finish();
                }
            }
        });

        nextText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(getItem(+1), true);
                if (getItem(+1) == itemCount) {
                    indicatorLayout.setVisibility(View.INVISIBLE);
                    skipText.setVisibility(View.GONE);
                    nextText.setVisibility(View.GONE);
                    gotItText.setVisibility(View.VISIBLE);
                }
            }
        });
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent != null && intent.getBooleanExtra("ismain", false)) {
                        /*if (!managerSession.getWalkthroughSkipValue()) {
                            managerSession.addUpdateSkipWalkthroughPref(true);
                        }*/
                    startMainActivity();
                } else {
                    finish();
                }
            }
        });

    }

    private int getItem(int i) {
        return mPager.getCurrentItem() + i;
    }

    private void startMainActivity() {
        Intent mainIntent = new Intent();
        mainIntent.setClass(WalkThroughActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }
}
