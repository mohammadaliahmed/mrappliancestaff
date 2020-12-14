package com.appsinventiv.mrappliancestaff.Activities;

import android.os.Bundle;

import com.appsinventiv.mrappliancestaff.Adapters.MainSliderAdapter;
import com.appsinventiv.mrappliancestaff.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


public class PicturesSlider extends AppCompatActivity {
    ViewPager viewPager;
    ArrayList<String> pics = new ArrayList<>();
    MainSliderAdapter mViewPagerAdapter;
    private int currentPic;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_slider);
        viewPager = findViewById(R.id.viewPager);

        pics = getIntent().getStringArrayListExtra("list");
        position = getIntent().getIntExtra("position", 0);

        initViewPager();

    }

    private void initViewPager() {
        mViewPagerAdapter = new MainSliderAdapter(this, pics);
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPic = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(position);

        mViewPagerAdapter.notifyDataSetChanged();
    }
}
