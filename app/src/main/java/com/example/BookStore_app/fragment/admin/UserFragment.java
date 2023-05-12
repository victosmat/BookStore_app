package com.example.BookStore_app.fragment.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.activity.LoginActivity.RegistryActivity;
import com.example.BookStore_app.activity.adminActivity.user.UpdateUserActivity;
import com.example.BookStore_app.adapter.RecycleViewAdapterUser;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.Book;
import com.example.BookStore_app.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class UserFragment extends Fragment implements RecycleViewAdapterUser.ItemListener {
    private RecycleViewAdapterUser adapter;
    private RecyclerView recyclerView;
    private SearchView search;
    private SQLiteHelper sqLiteHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_user, container, false);
    }

    @Override
    public void OnItemClick(View view, int p) {
        User user = adapter.getUser(p);
        Intent intent = new Intent(getActivity(), UpdateUserActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycleView);

        adapter = new RecycleViewAdapterUser();
        sqLiteHelper = new SQLiteHelper(getContext());
        List<User> list = sqLiteHelper.getAllUser();
        search = view.findViewById(R.id.search);
        adapter.setList(list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setItemListener(this);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<User> list = sqLiteHelper.searchByName(s);
                adapter.setList(list);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        List<User> list = sqLiteHelper.getAllUser();
        adapter.setList(list);
    }
}