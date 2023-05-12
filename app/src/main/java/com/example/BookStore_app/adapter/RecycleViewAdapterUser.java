package com.example.BookStore_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.model.User;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapterUser extends RecyclerView.Adapter<RecycleViewAdapterUser.HomeViewHolder> {
    private List<User> list;
    private ItemListener itemListener;

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public RecycleViewAdapterUser() {
        list = new ArrayList<>();
    }

    public void setList(List<User> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public User getUser(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        User user = list.get(position);
        holder.tvName.setText(user.getName());
        holder.tvDateOfBirth.setText(user.getDateOfBirth());
        holder.tvGender.setText(user.getGender());
        holder.tvPosition.setText(user.getPosition());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName, tvDateOfBirth, tvGender, tvPosition;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDateOfBirth = itemView.findViewById(R.id.tvDateOfBirth);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvPosition = itemView.findViewById(R.id.tvPosition);
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
