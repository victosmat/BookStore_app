package com.example.BookStore_app.fragment.customer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.activity.adminActivity.user.UpdateUserActivity;
import com.example.BookStore_app.activity.customerActivity.BillDetailsActivity;
import com.example.BookStore_app.adapter.RecycleViewAdapterCartCustomer;
import com.example.BookStore_app.adapter.RecycleViewAdapterTransactionCustomer;
import com.example.BookStore_app.adapter.RecycleViewAdapterUser;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.Bill;
import com.example.BookStore_app.model.BillDetails;
import com.example.BookStore_app.model.CartBook;
import com.example.BookStore_app.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.List;

public class TransactionFragment extends Fragment implements RecycleViewAdapterTransactionCustomer.ItemListener {
    private RecycleViewAdapterTransactionCustomer adapter;

    private EditText From, To;
    private Button btnSearch;
    private RecyclerView recyclerView;
    private SQLiteHelper sqLiteHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_transaction, container, false);
    }

    @Override
    public void OnItemClick(View view, int p) {
        Bill bill = adapter.getBill(p);
        Intent intent = new Intent(getActivity(), BillDetailsActivity.class);
        intent.putExtra("bill", bill);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycleView);
        From = view.findViewById(R.id.From);
        To = view.findViewById(R.id.To);
        btnSearch = view.findViewById(R.id.btnSearch);
        adapter = new RecycleViewAdapterTransactionCustomer();
        sqLiteHelper = new SQLiteHelper(getContext());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String username = auth.getCurrentUser().getEmail();

        List<Bill> list = sqLiteHelper.getAllBillByUserId(sqLiteHelper.getUserByUsername(username).getId());
        for (Bill bill : list) {
            List<BillDetails> billDetails  = sqLiteHelper.getAllBillDetailsByBillId(bill.getId());
            if (billDetails.isEmpty())
                sqLiteHelper.deleteBill(bill.getId());
        }
        list = sqLiteHelper.getAllBillByUserId(sqLiteHelper.getUserByUsername(username).getId());
        adapter.setList(list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setItemListener(this);

        From.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                    String date = "";
                    if (month > 8) {
                        if (dayOfMonth > 9)
                            date = y + "/" + (month + 1) + "/" + dayOfMonth;
                        else
                            date = y + "/" + (month + 1) + "/0" + dayOfMonth;
                    } else {
                        if (dayOfMonth > 9)
                            date = y + "/0" + (month + 1) + "/" + dayOfMonth;
                        else
                            date = y + "/0" + (month + 1) + "/0" + dayOfMonth;
                    }
                    From.setText(date);
                }, y, m, d);
                dialog.show();
            }
        });

        To.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                    String date = "";
                    if (month > 8) {
                        if (dayOfMonth > 9)
                            date = y + "/" + (month + 1) + "/" + dayOfMonth;
                        else
                            date = y + "/" + (month + 1) + "/0" + dayOfMonth;
                    } else {
                        if (dayOfMonth > 9)
                            date = y + "/0" + (month + 1) + "/" + dayOfMonth;
                        else
                            date = y + "/0" + (month + 1) + "/0" + dayOfMonth;
                    }
                    To.setText(date);
                }, y, m, d);
                dialog.show();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = From.getText().toString();
                String to = To.getText().toString();
                if (from.isEmpty() || to.isEmpty()) {
                    List<Bill> list = sqLiteHelper.getAllBillByUserId(sqLiteHelper.getUserByUsername(username).getId());
                    adapter.setList(list);
                } else {
                    List<Bill> list = sqLiteHelper.getAllBillByUserIdAndDate(sqLiteHelper.getUserByUsername(username).getId(), from, to);
                    if (list.isEmpty())
                        Toast.makeText(getContext(), "No bill for this period", Toast.LENGTH_SHORT).show();
                    adapter.setList(list);
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String username = auth.getCurrentUser().getEmail();
        List<Bill> list = sqLiteHelper.getAllBillByUserId(sqLiteHelper.getUserByUsername(username).getId());
        adapter.setList(list);
    }
}
