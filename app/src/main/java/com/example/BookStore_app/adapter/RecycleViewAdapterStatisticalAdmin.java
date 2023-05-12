package com.example.BookStore_app.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.model.BillDetails;
import com.example.BookStore_app.model.Statistical;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapterStatisticalAdmin extends RecyclerView.Adapter<RecycleViewAdapterStatisticalAdmin.HomeViewHolder> {
    private List<Statistical> list;
    private ItemListener itemListener;

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public RecycleViewAdapterStatisticalAdmin() {
        list = new ArrayList<>();
    }

    public void setList(List<Statistical> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public Statistical getStatistical(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistical, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Statistical statistical = list.get(position);
        Log.e("billDetails", statistical.getBook().getName());
        holder.tvName.setText(statistical.getBook().getName());
        holder.tvPrice.setText(statistical.getBook().getPrice());
        holder.tvAuthor.setText(statistical.getBook().getAuthor());
        holder.tvCategory.setText(statistical.getBook().getCategory());
        holder.tvNumber.setText(String.valueOf(statistical.getTotal()));
        String urlImage = statistical.getBook().getUrlImage();
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
