package com.example.BookStore_app.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.BookStore_app.fragment.LogoutFragment;
import com.example.BookStore_app.fragment.admin.BookFragment;
import com.example.BookStore_app.fragment.admin.StatisticalFragment;
import com.example.BookStore_app.fragment.admin.UserFragment;
import com.example.BookStore_app.fragment.customer.CartFragment;
import com.example.BookStore_app.fragment.customer.HomeFragment;
import com.example.BookStore_app.fragment.customer.ProfileFragment;
import com.example.BookStore_app.fragment.customer.TransactionFragment;

public class ViewPagerAdapterCustomer extends FragmentStatePagerAdapter {


    public ViewPagerAdapterCustomer(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new CartFragment();
            case 2:
                return new TransactionFragment();
            case 3:
                return new ProfileFragment();
            case 4:
                return new LogoutFragment();

        }
        return new BookFragment();
    }

    @Override
    public int getCount() {
        return 5;
    }

}
