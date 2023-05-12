package com.example.BookStore_app.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.BookStore_app.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapterBookCustomer extends RecyclerView.Adapter<RecycleViewAdapterBookCustomer.HomeViewHolder> {
    private List<Book> list;
    private ItemListener itemListener;
    private SQLiteHelper sqLiteHelper;

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public RecycleViewAdapterBookCustomer() {
        list = new ArrayList<>();
    }

    public void setList(List<Book> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public Book getBook(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_customer, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        sqLiteHelper = new SQLiteHelper(holder.itemView.getContext());
        Book book = list.get(position);
        holder.tvName.setText(book.getName());
        holder.tvPrice.setText(book.getPrice());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvCategory.setText(book.getCategory());
        String urlImage = book.getUrlImage();
        Picasso.get().load(urlImage).into(holder.imgBook);
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    String username = user.getEmail();

                    User user1 = sqLiteHelper.getUserByUsername(username);
                    Cart cart = sqLiteHelper.getCartByUserId(user1.getId());
//                    Log.e("cart", cart.toString() + "");
                    Boolean checkBookInCart = sqLiteHelper.checkBookInCart(book.getId(), cart.getId());
                    if (checkBookInCart) {
                        Toast.makeText(v.getContext(), "Book " + book.getName() + " already in cart", Toast.LENGTH_SHORT).show();
                    } else {
                        // Khởi tạo AlertDialog
                        LayoutInflater inflater = LayoutInflater.from(v.getContext());
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        View dialogView = inflater.inflate(R.layout.dialog_number, null);
                        builder.setView(dialogView);


                        final EditText input = dialogView.findViewById(R.id.edtNumberBook);

                        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Xử lý khi người dùng chọn "Submit"
                                int quantity = Integer.parseInt(input.getText().toString());
                                if (quantity > 0) {
                                    sqLiteHelper.addCartBook(book.getId(), cart.getId(), quantity);
                                    Toast.makeText(v.getContext(), "Add to cart successfully", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(v.getContext(), "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                    // Sử dụng email ở đây
                } else
                    Toast.makeText(v.getContext(), "You are not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName, tvPrice, tvAuthor, tvCategory;
        private Button btnAddToCart;
        private ImageView imgBook;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
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
