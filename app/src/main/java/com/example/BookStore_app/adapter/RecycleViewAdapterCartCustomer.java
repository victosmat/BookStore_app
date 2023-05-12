package com.example.BookStore_app.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BookStore_app.R;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.Book;
import com.example.BookStore_app.model.Cart;
import com.example.BookStore_app.model.CartBook;
import com.example.BookStore_app.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapterCartCustomer extends RecyclerView.Adapter<RecycleViewAdapterCartCustomer.HomeViewHolder> {
    private List<CartBook> list;
    private SQLiteHelper sqLiteHelper;
    private OnCheckBoxClickListener mListener;


    public void setOnCheckBoxClickListener(OnCheckBoxClickListener listener) {
        mListener = listener;
    }

    public RecycleViewAdapterCartCustomer() {
        list = new ArrayList<>();
    }

    public void setList(List<CartBook> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public CartBook getCartBook(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_customer, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        sqLiteHelper = new SQLiteHelper(holder.itemView.getContext());
        CartBook cartBook = list.get(position);
        Book book = cartBook.getBook();
        holder.tvName.setText(book.getName());
        holder.tvPrice.setText(book.getPrice());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvCategory.setText(book.getCategory());
        holder.tvNumber.setText(String.valueOf(cartBook.getQuantity()));
        String urlImage = book.getUrlImage();
        Picasso.get().load(urlImage).into(holder.imgBook);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                TextView title = new TextView(holder.itemView.getContext());
                title.setText("Delete notice");
                title.setTextColor(Color.RED);
                title.setTextSize(20);
                title.setPadding(20, 20, 20, 20);
                title.setGravity(Gravity.CENTER);
                builder.setCustomTitle(title);
                builder.setMessage("Do you want to remove " + book.getName() + " books from your cart?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sqLiteHelper.deleteCartBook(cartBook.getId());
                        list.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(holder.itemView.getContext(), "Successfully removed the book from the cart", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();


            }
        });
        holder.cbBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.cbBook.isChecked()) {
                    holder.cbBook.setChecked(true);
                    sqLiteHelper.updateIsCheckedCartBook(cartBook.getId(), 1);
                } else {
                    holder.cbBook.setChecked(false);
                    sqLiteHelper.updateIsCheckedCartBook(cartBook.getId(), 0);
                }
                mListener.onCheckBoxClick();
            }
        });

        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.parseInt(holder.tvNumber.getText().toString());
                number++;
                holder.tvNumber.setText(String.valueOf(number));
                sqLiteHelper.updateQuantityCartBook(cartBook.getId(), number);
            }
        });

        holder.ivSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.parseInt(holder.tvNumber.getText().toString());
                if (number > 1) {
                    number--;
                    holder.tvNumber.setText(String.valueOf(number));
                    sqLiteHelper.updateQuantityCartBook(cartBook.getId(), number);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName, tvPrice, tvAuthor, tvCategory, tvNumber;
        private ImageView ivAdd, ivSub, ivDelete, imgBook;
        private CheckBox cbBook;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            ivAdd = itemView.findViewById(R.id.ivAdd);
            ivSub = itemView.findViewById(R.id.ivSub);
            cbBook = itemView.findViewById(R.id.cbBook);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            imgBook = itemView.findViewById(R.id.imgBook);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }


    public interface OnCheckBoxClickListener {
        void onCheckBoxClick();
    }
}
