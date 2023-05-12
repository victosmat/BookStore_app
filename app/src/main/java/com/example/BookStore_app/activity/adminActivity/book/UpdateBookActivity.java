package com.example.BookStore_app.activity.adminActivity.book;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.BookStore_app.R;
import com.example.BookStore_app.activity.LoginActivity.RegistryActivity;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class UpdateBookActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 71;
    private EditText edtName, edtAuthor, edtPrice;
    private Spinner spinerCategory;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filePath;
    private Button btnUpdate, btnBack, btnRemove, btnUpload;
    private ImageView imgBook;
    private Book book;
    private SQLiteHelper sqLiteHelper;
    private Boolean isChooseImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_book);
        sqLiteHelper = new SQLiteHelper(this);

        edtName = findViewById(R.id.edtName);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtPrice = findViewById(R.id.edtPrice);
        spinerCategory = findViewById(R.id.spinerCategory);
        spinerCategory.setAdapter(new ArrayAdapter<String>(this, R.layout.item_spiner, getResources().getStringArray(R.array.category)));
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnBack);
        btnRemove = findViewById(R.id.btnRemove);
        btnUpload = findViewById(R.id.btnUpload);
        imgBook = findViewById(R.id.imgBook);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
                isChooseImage = true;
            }
        });

        Intent intent = getIntent();
        book = (Book) intent.getSerializableExtra("book");
        String urlImage = book.getUrlImage();
        Picasso.get().load(urlImage).into(imgBook);
        edtName.setText(book.getName());
        edtAuthor.setText(book.getAuthor());
        edtPrice.setText(book.getPrice());
        spinerCategory.setSelection(((ArrayAdapter<String>) spinerCategory.getAdapter()).getPosition(book.getCategory()));

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(UpdateBookActivity.this);
                progressDialog.setMessage("Loading..."); // thiết lập tin nhắn
                progressDialog.show(); // hiển thị ProgressDialog
                String name = edtName.getText().toString();
                String author = edtAuthor.getText().toString();
                String price = edtPrice.getText().toString();
                String category = spinerCategory.getSelectedItem().toString();
                book.setName(name);
                book.setAuthor(author);
                book.setPrice(price);
                book.setCategory(category);
                sqLiteHelper.updateBook(book);
                String urlImage = book.getUrlImage();
                if (isChooseImage) {
                    // thực hiện xoá ảnh cũ trên firebase
                    if (filePath != null) {
                        StorageReference ref = storage.getReferenceFromUrl(urlImage);
                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "onSuccess: delete file old");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", "onFailure: old file deletion failed");
                            }
                        });
                    }
                    // thực hiện upload ảnh mới lên firebase
                    uploadImage(book.getId());
                } else finish();
                progressDialog.dismiss();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                builder.setTitle("Notice of deletion of book information");
                TextView title = new TextView(v.getContext());
                title.setText("Notice of deletion of book information");
                title.setTextColor(Color.RED);
                title.setTextSize(20);
                title.setPadding(20, 20, 20, 20);
                title.setGravity(Gravity.CENTER);
                builder.setCustomTitle(title);
                builder.setMessage("You want to delete " + book.getName() + " book?");
                builder.setIcon(R.drawable.remove);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    ProgressDialog progressDialog = new ProgressDialog(UpdateBookActivity.this);
                    progressDialog.setMessage("Loading..."); // thiết lập tin nhắn
                    progressDialog.show(); // hiển thị ProgressDialog
                    sqLiteHelper.deleteBook(book.getId());
                    // xoá ảnh trên firebase
                    String urlImage = book.getUrlImage();
                    StorageReference ref = storage.getReferenceFromUrl(urlImage);
                    ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "onSuccess: delete file old");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "onFailure: old file deletion failed");
                        }
                    });

                    // xoá book trong cartBook
                    sqLiteHelper.deleteCartBook(book.getId());

                    // xoá book trong Statistic
                    sqLiteHelper.deleteStatistical(book.getId());

                    // xoá book trong BillDetail
                    sqLiteHelper.deleteBookInBillDetails(book.getId());

                    sqLiteHelper.deleteBookInCartBook(book.getId());

                    progressDialog.dismiss();
                    finish();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgBook.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(int bookId) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(UpdateBookActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/Book" + String.valueOf(bookId));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
                            // lấy url ảnh
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = String.valueOf(uri);
                                    sqLiteHelper.updateBookImage(bookId, url);
                                }
                            });
                            Toast.makeText(UpdateBookActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
                            Toast.makeText(UpdateBookActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
            finish();
        }
    }
}
