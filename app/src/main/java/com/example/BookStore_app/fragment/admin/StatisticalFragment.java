package com.example.BookStore_app.fragment.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.adapter.RecycleViewAdapterStatisticalAdmin;
import com.example.BookStore_app.adapter.RecycleViewAdapterTransactionCustomer;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.Statistical;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticalFragment extends Fragment implements RecycleViewAdapterStatisticalAdmin.ItemListener {
    private EditText edtMonth, edtYear, edtDate;
    private TextView tvPrice;
    private RecycleViewAdapterStatisticalAdmin adapter;
    private RadioGroup radio_group;
    private Button btnSearch;
    private RecyclerView recyclerView;
    private SQLiteHelper sqLiteHelper;
    private String ans = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_statistical, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtMonth = view.findViewById(R.id.edtMonth);
        edtYear = view.findViewById(R.id.edtYear);
        recyclerView = view.findViewById(R.id.recycleView);
        btnSearch = view.findViewById(R.id.btnSearch);
        adapter = new RecycleViewAdapterStatisticalAdmin();
        sqLiteHelper = new SQLiteHelper(getContext());
        tvPrice = view.findViewById(R.id.tvPrice);
        edtDate = view.findViewById(R.id.edtDate);
        radio_group = view.findViewById(R.id.radio_group);

        List<Statistical> statistics = sqLiteHelper.getAllStatistical();

        adapter.setList(statistics);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setItemListener(this);

        int totalRevenue = 0;
        if (!statistics.isEmpty()) {
            for (Statistical statistical : statistics) {
                Log.e("TAG", "onViewCreated: " + statistical.getBook().getUrlImage());
                totalRevenue += (statistical.getTotal() * Integer.parseInt(statistical.getBook().getPrice()));
            }
        }

        tvPrice.setText(String.valueOf(totalRevenue));

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                ans = radioButton.getText().toString();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = edtDate.getText().toString();
                String month = edtMonth.getText().toString();
                String year = edtYear.getText().toString();

                List<Statistical> statistics = new ArrayList<>();
                if (ans.equals("")) {
                    statistics = sqLiteHelper.getAllStatistical();
                } else if (Integer.parseInt(month) > 12 || Integer.parseInt(month) < 1) {
                    Toast.makeText(getContext(), "Month must be between 1 and 12", Toast.LENGTH_SHORT).show();
                    return;
                } else if (ans.equals("Statistics by month")) {
                    if (month.isEmpty() && year.isEmpty()) {
                        statistics = sqLiteHelper.getAllStatistical();
                    } else if (month.isEmpty() || year.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter full information", Toast.LENGTH_SHORT).show();
                    } else {
                        statistics = sqLiteHelper.getAllStatisticalByMonth(month, year);
                    }
                    if (statistics.size() == 0) {
                        Toast.makeText(getContext(), "No figures for this month", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (date.isEmpty() && month.isEmpty() && year.isEmpty()) {
                        statistics = sqLiteHelper.getAllStatistical();
                    } else if (date.isEmpty() || month.isEmpty() || year.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter full information", Toast.LENGTH_SHORT).show();
                    } else {
                        // lấy số ngày trong tháng
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        if (Integer.parseInt(date) > maxDay || Integer.parseInt(date) < 1) {
                            Toast.makeText(getContext(), "Date must be between 1 and " + maxDay, Toast.LENGTH_SHORT).show();
                            return;
                        } else statistics = sqLiteHelper.getAllStatisticalByDate(date, month, year);
                    }
                    if (statistics.size() == 0) {
                        Toast.makeText(getContext(), "No figures for this date", Toast.LENGTH_SHORT).show();
                    }
                }
                adapter.setList(statistics);
                LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(adapter);
                int totalRevenue = 0;
                for (Statistical statistical : statistics) {
                    totalRevenue += (statistical.getTotal() * Integer.parseInt(statistical.getBook().getPrice()));
                }
                tvPrice.setText(String.valueOf(totalRevenue));
            }
        });
    }

    @Override
    public void OnItemClick(View view, int p) {

    }
}
