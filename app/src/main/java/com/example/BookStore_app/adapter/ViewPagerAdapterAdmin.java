package com.example.BookStore_app.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.BookStore_app.fragment.LogoutFragment;
import com.example.BookStore_app.fragment.admin.BookFragment;
import com.example.BookStore_app.fragment.admin.StatisticalFragment;
import com.example.BookStore_app.fragment.admin.UserFragment;

public class ViewPagerAdapterAdmin extends FragmentStatePagerAdapter {


    public ViewPagerAdapterAdmin(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new BookFragment();
            case 1: return new UserFragment();
            case 2: return new StatisticalFragment();
            case 3: return new LogoutFragment();

        }
        return new BookFragment();
    }

    @Override
    public int getCount() {
        return 4;
    }

}
