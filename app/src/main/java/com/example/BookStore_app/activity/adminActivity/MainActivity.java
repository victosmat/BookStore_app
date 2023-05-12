package com.example.BookStore_app.activity.adminActivity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;


import com.example.BookStore_app.R;
import com.example.BookStore_app.adapter.ViewPagerAdapterAdmin;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavi;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavi = findViewById(R.id.bottomNavi);
        viewPager = findViewById(R.id.viewPager);


        ViewPagerAdapterAdmin adapter = new ViewPagerAdapterAdmin(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavi.getMenu().findItem(R.id.mBook).setChecked(true);
                        break;
                    case 1:
                        bottomNavi.getMenu().findItem(R.id.mUser).setChecked(true);
                        break;
                    case 2:
                        bottomNavi.getMenu().findItem(R.id.mStatistical).setChecked(true);
                        break;
                    case 3:
                        bottomNavi.getMenu().findItem(R.id.mLogout).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavi.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mBook:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.mUser:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.mStatistical:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.mLogout:
                    viewPager.setCurrentItem(3);
                    break;
            }
            return true;
        });

    }
}