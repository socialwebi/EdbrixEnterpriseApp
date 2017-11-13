package com.edbrix.enterprise.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edbrix.enterprise.Fragments.CourseListFragment;
import com.edbrix.enterprise.Fragments.MeetingListFragment;
import com.edbrix.enterprise.R;

public class BottomTabMenuActivity extends AppCompatActivity {

    private TabLayout menuTab;

    private TextView titleText;
    private TextView tabSchdlText;
    private TextView tabCoursesText;
    private TextView tabSettingText;

    private ImageView tabSchdlIcon;
    private ImageView tabCoursesIcon;
    private ImageView tabSettingIcon;

    private Fragment menuFragment;

    public static final String tabIndexKey = "tabIndex";

    private int tabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_tab_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleText = (TextView) toolbar.findViewById(R.id.title);
        menuTab = (TabLayout) findViewById(R.id.tabLayout);

        tabIndex = getIntent().getIntExtra(tabIndexKey, 0);
        setMenuTabs();
        setListeners();
        menuTab.getTabAt(tabIndex).select();
        tabSelected();
    }

    private void setMenuTabs() {

        menuTab.addTab(menuTab.newTab().setText(getResources().getString(R.string.schedules)));
        menuTab.addTab(menuTab.newTab().setText(getResources().getString(R.string.courses)));
        menuTab.addTab(menuTab.newTab().setText(getResources().getString(R.string.settings)));

        LinearLayout tabSchdl = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabSchdlText = (TextView) tabSchdl.findViewById(R.id.tabText);
        tabSchdlIcon = (ImageView) tabSchdl.findViewById(R.id.tabIcon);
        tabSchdlIcon.setImageResource(R.mipmap.footer_calendar_menu);
        tabSchdlText.setText(getResources().getString(R.string.schedules));
        menuTab.getTabAt(0).setCustomView(tabSchdl);

        LinearLayout tabCourses = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabCoursesText = (TextView) tabCourses.findViewById(R.id.tabText);
        tabCoursesIcon = (ImageView) tabCourses.findViewById(R.id.tabIcon);
        tabCoursesIcon.setImageResource(R.mipmap.footer_course_menu);
        tabCoursesText.setText(getResources().getString(R.string.courses));
        menuTab.getTabAt(1).setCustomView(tabCourses);

        LinearLayout tabSetting = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabSettingText = (TextView) tabSetting.findViewById(R.id.tabText);
        tabSettingIcon = (ImageView) tabSetting.findViewById(R.id.tabIcon);
        tabSettingIcon.setImageResource(R.mipmap.footer_schedule_menu);
        tabSettingText.setText(getResources().getString(R.string.settings));
        menuTab.getTabAt(2).setCustomView(tabSetting);

    }

    private void setListeners() {
        menuTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabSelected();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (menuTab.getSelectedTabPosition()) {
                    case 0:
                        tabSchdlIcon.setImageResource(R.mipmap.footer_calendar_menu);
                        tabSchdlText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorFooterMenuText));
                        break;
                    case 1:
                        tabCoursesIcon.setImageResource(R.mipmap.footer_course_menu);
                        tabCoursesText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorFooterMenuText));
                        break;
                    case 2:
                        tabSettingIcon.setImageResource(R.mipmap.footer_schedule_menu);
                        tabSettingText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorFooterMenuText));
                        break;

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void tabSelected() {

        switch (menuTab.getSelectedTabPosition()) {
            case 0:
                tabSchdlIcon.setImageResource(R.mipmap.footer_calendar_menu_active);
                tabSchdlText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAppOrange));
                titleText.setText(getText(R.string.meetings));
                menuFragment = new MeetingListFragment();
//                Bundle bundleMsg = new Bundle();
//                bundleMsg.putSerializable(MessagesFragment.INFO, stringInfo);
//                fragment.setArguments(bundleMsg);
                break;
            case 1:
                tabCoursesIcon.setImageResource(R.mipmap.footer_course_menu_active);
                tabCoursesText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAppOrange));
                titleText.setText(tabCoursesText.getText());
                menuFragment = new CourseListFragment();
//                Bundle bundleSchdl = new Bundle();
//                bundleSchdl.putSerializable(ScheduleFragment.INFO, stringInfo);
//                fragment.setArguments(bundleSchdl);
                break;
            case 2:
                tabSettingIcon.setImageResource(R.mipmap.footer_schedule_menu_active);
                tabSettingText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAppOrange));
                titleText.setText(tabSettingText.getText());
//                menuFragment = new SettingsFragment();
//                Bundle bundleLogs = new Bundle();
//                bundleLogs.putSerializable(LogFragment.INFO, stringInfo);
//                fragment.setArguments(bundleLogs);
                break;

        }
        if (menuFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_container, menuFragment).commit();
        }
    }

}
