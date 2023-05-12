package com.example.BookStore_app.fragment.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.adapter.RecycleViewAdapterBook;
import com.example.BookStore_app.adapter.RecycleViewAdapterBookCustomer;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeFragment extends Fragment implements RecycleViewAdapterBook.ItemListener {
    private RecycleViewAdapterBookCustomer adapter;
    private RecyclerView recyclerView;
    private SQLiteHelper sqLiteHelper;
    private SearchView search;

    private Spinner spinerCategory;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycleView);
        sqLiteHelper = new SQLiteHelper(getContext());

        adapter = new RecycleViewAdapterBookCustomer();
        sqLiteHelper = new SQLiteHelper(getContext());
        search = view.findViewById(R.id.search);

        spinerCategory = view.findViewById(R.id.spinerCategory);
        String[] arr = getResources().getStringArray(R.array.category);
        String[] arr1 = new String[arr.length + 1];
        arr1[0] = "All";
        for (int i = 0; i < arr.length; i++) {
            arr1[i + 1] = arr[i];
        }
        spinerCategory.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.item_spiner, arr1));

        List<Book> list = sqLiteHelper.getAllBook();
        adapter.setList(list);
//        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<Book> list = sqLiteHelper.searchByNameBook(s);
                adapter.setList(list);
                return true;
            }
        });

        spinerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = spinerCategory.getItemAtPosition(position).toString();
                List<Book> list;
                if (!category.equalsIgnoreCase("All")) {
                    list = sqLiteHelper.searchByCategoryBook(category);
                } else {
                    list = sqLiteHelper.getAllBook();
                }
                adapter.setList(list);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Book> list = sqLiteHelper.getAllBook();
        adapter.setList(list);
    }

    @Override
    public void OnItemClick(View view, int p) {
    }
}
