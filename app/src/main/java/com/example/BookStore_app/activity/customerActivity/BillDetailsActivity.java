package com.example.BookStore_app.activity.customerActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.adapter.RecycleViewAdapterBillDetailsCustomer;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.Bill;
import com.example.BookStore_app.model.BillDetails;

import java.util.List;

public class BillDetailsActivity extends AppCompatActivity implements RecycleViewAdapterBillDetailsCustomer.ItemListener {
    private RecyclerView recyclerView;
    private SQLiteHelper sqLiteHelper;
    private ImageView backArrow;

    private RecycleViewAdapterBillDetailsCustomer adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bill_details);
        recyclerView = findViewById(R.id.recycleView);
        backArrow = findViewById(R.id.backArrow);
        sqLiteHelper = new SQLiteHelper(this);

        Intent intent = getIntent();
        Bill bill = (Bill) intent.getSerializableExtra("bill");
        adapter = new RecycleViewAdapterBillDetailsCustomer();
        List<BillDetails> billDetails = sqLiteHelper.getAllBillDetailsByBillId(bill.getId());
        for (BillDetails billDetail : billDetails) {
            Log.e("billDetail", billDetail.toString());
        }
//        Log.e("billDetails", billDetails.toString());
        adapter.setList(billDetails);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void OnItemClick(View view, int p) {

    }
}
