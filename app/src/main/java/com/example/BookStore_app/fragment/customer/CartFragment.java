package com.example.BookStore_app.fragment.customer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.adapter.RecycleViewAdapterCartCustomer;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.Bill;
import com.example.BookStore_app.model.BillDetails;
import com.example.BookStore_app.model.CartBook;
import com.example.BookStore_app.model.User;
import com.example.BookStore_app.notification.MyNotification;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CartFragment extends Fragment implements RecycleViewAdapterCartCustomer.OnCheckBoxClickListener {
    private RecycleViewAdapterCartCustomer adapter;
    private RecyclerView recyclerView;
    private SQLiteHelper sqLiteHelper;
    private TextView tvNumberOfPicks, tvTotalPrice;
    private Button btnPay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycleView);
        sqLiteHelper = new SQLiteHelper(getContext());

        adapter = new RecycleViewAdapterCartCustomer();
        sqLiteHelper = new SQLiteHelper(getContext());
        tvNumberOfPicks = view.findViewById(R.id.tvNumberOfPicks);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnPay = view.findViewById(R.id.btnPay);

        tvNumberOfPicks.setText("0");
        tvTotalPrice.setText("0");


        FirebaseAuth auth = FirebaseAuth.getInstance();
        String username = auth.getCurrentUser().getEmail();
        List<CartBook> list = sqLiteHelper.getAllCartBookByUserId(sqLiteHelper.getUserByUsername(username).getId());
        adapter.setList(list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setOnCheckBoxClickListener(this);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                List<CartBook> list = sqLiteHelper.getAllCartBookByUserId(sqLiteHelper.getUserByUsername(username).getId());
                int countChecked = 0;
                for (CartBook cartBook : list)
                    if (cartBook.getChecked() == 1) countChecked++;
                if (countChecked == 0) {
                    Toast.makeText(getContext(), "No products selected !", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Payment notice");
                    builder.setMessage("Are you sure you want to pay?");
                    builder.setIcon(R.drawable.pay);
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        List<BillDetails> billDetailsList = new ArrayList<>();
                        int numberOfPicks = 0;
                        int totalPrice = 0;
                        for (CartBook cartBook : list) {
                            if (cartBook.getChecked() == 1) {
                                numberOfPicks += cartBook.getQuantity();
                                totalPrice += cartBook.getQuantity() * Integer.parseInt(cartBook.getBook().getPrice());
                                billDetailsList.add(new BillDetails(null, cartBook.getBook(), cartBook.getQuantity()));
                                sqLiteHelper.deleteCartBook(cartBook.getId());
                            }
                        }

                        // lấy ngày hiện tại
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        String currentDate = dateFormat.format(calendar.getTime());
//                        String currentDate = "2023/05/11";
                        Long id = sqLiteHelper.addBill(currentDate, String.valueOf(totalPrice), sqLiteHelper.getUserByUsername(username).getId());
                        int billId = Integer.parseInt(String.valueOf(id));
                        for (BillDetails billDetails : billDetailsList) {
                            sqLiteHelper.addBillDetails(billId, billDetails.getBook().getId(), billDetails.getQuantity());
                        }

                        User user = sqLiteHelper.getUserByUsername(username);
                        // notification
                        NotificationCompat.Builder builderNotification = new NotificationCompat.Builder(getActivity(), MyNotification.CHANNEL_ID)
                                .setSmallIcon(R.drawable.notifications)
                                .setContentTitle(user.getName() + " paid successfully !")
                                .setContentText("Date of payment: " + currentDate + "\nTotal price: " + totalPrice + "$")
                                .setColor(Color.RED)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
                        notificationManagerCompat.notify(getID(), builderNotification.build());

                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                        View dialogView = inflater.inflate(R.layout.dialog_pay, null);
                        builder1.setView(dialogView);


                        final TextView tvDate = dialogView.findViewById(R.id.tvDate);
                        final TextView tvTotalPrice = dialogView.findViewById(R.id.tvTotalPrice);
                        tvTotalPrice.setText(String.valueOf(totalPrice));
                        tvDate.setText(currentDate);
                        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                // reload lại trang
                                onResume();
                            }
                        });

                        AlertDialog dialog1 = builder1.create();
                        dialog1.show();
                    });
                    builder.setNegativeButton("No", (dialog, which) -> {
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        tvNumberOfPicks.setText("0");
        tvTotalPrice.setText("0");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String username = auth.getCurrentUser().getEmail();
        List<CartBook> list = sqLiteHelper.getAllCartBookByUserId(sqLiteHelper.getUserByUsername(username).getId());
        adapter.setList(list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCheckBoxClick() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String username = auth.getCurrentUser().getEmail();
        List<CartBook> list = sqLiteHelper.getAllCartBookByUserId(sqLiteHelper.getUserByUsername(username).getId());
        int numberOfPicks = 0;
        int totalPrice = 0;
        for (CartBook cartBook : list) {
            if (cartBook.getChecked() == 1) {
                numberOfPicks += cartBook.getQuantity();
                totalPrice += cartBook.getQuantity() * Integer.parseInt(cartBook.getBook().getPrice());
            }
        }
        tvNumberOfPicks.setText(String.valueOf(numberOfPicks));
        tvTotalPrice.setText(String.valueOf(totalPrice));
    }

    public int getID() {
        return (int) System.currentTimeMillis();
    }
}
