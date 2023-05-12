package com.example.BookStore_app.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.model.Bill;
import com.example.BookStore_app.model.BillDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapterBillDetailsCustomer extends RecyclerView.Adapter<RecycleViewAdapterBillDetailsCustomer.HomeViewHolder> {
    private List<BillDetails> list;
    private ItemListener itemListener;

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public RecycleViewAdapterBillDetailsCustomer() {
        list = new ArrayList<>();
    }

    public void setList(List<BillDetails> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public BillDetails getBillDetails(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_details, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        BillDetails billDetails = list.get(position);
        holder.tvName.setText(billDetails.getBook().getName());
        holder.tvPrice.setText(billDetails.getBook().getPrice());
        holder.tvAuthor.setText(billDetails.getBook().getAuthor());
        holder.tvCategory.setText(billDetails.getBook().getCategory());
        holder.tvNumber.setText(String.valueOf(billDetails.getQuantity()));
        String urlImage = billDetails.getBook().getUrlImage();
        Picasso.get().load(urlImage).into(holder.imgBook);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName, tvPrice, tvAuthor, tvCategory, tvNumber;
        private ImageView imgBook;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            imgBook = itemView.findViewById(R.id.imgBook);
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
