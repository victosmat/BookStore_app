package com.example.BookStore_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.model.Bill;
import com.example.BookStore_app.model.User;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapterTransactionCustomer extends RecyclerView.Adapter<RecycleViewAdapterTransactionCustomer.HomeViewHolder> {
    private List<Bill> list;
    private ItemListener itemListener;

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public RecycleViewAdapterTransactionCustomer() {
        list = new ArrayList<>();
    }

    public void setList(List<Bill> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public Bill getBill(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Bill bill = list.get(position);
        holder.tvDate.setText(bill.getDate());
        holder.tvTotalPrice.setText(bill.getTotal());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvDate, tvTotalPrice;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemListener != null) {
                itemListener.OnItemClick(v, getAdapterPosition());
            }

        }
    }


    public interface ItemListener {
        void OnItemClick(View view, int p);
    }
}
