package com.example.BookStore_app.activity.adminActivity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BookStore_app.R;
import com.example.BookStore_app.activity.adminActivity.book.AddBookActivity;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UpdateUserActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnUpdate, btnBack, btnRemove;

    private EditText edtName, edtDateOfBirth, edtUserName;
    private RadioGroup radio_group_gender, radio_group_position;
    private User user;

    private SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_user);
        sqLiteHelper = new SQLiteHelper(this);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnBack);
        btnRemove = findViewById(R.id.btnRemove);
        radio_group_position = findViewById(R.id.radio_group_position);
        edtName = findViewById(R.id.edtName);
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth);
        edtUserName = findViewById(R.id.edtUserName);
        radio_group_gender = findViewById(R.id.radio_group_gender);

        btnUpdate.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnRemove.setOnClickListener(this);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        edtName.setText(user.getName());
        edtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateUserActivity.this, "You can't change name", Toast.LENGTH_SHORT).show();
            }
        });
        edtDateOfBirth.setText(user.getDateOfBirth());
        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateUserActivity.this, "You can't change date of birth", Toast.LENGTH_SHORT).show();
            }
        });
        edtUserName.setText(user.getUsername());
        edtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateUserActivity.this, "You can't change email", Toast.LENGTH_SHORT).show();
            }
        });
        String gender = user.getGender();
        if (gender.equals("male")) radio_group_gender.check(R.id.radio_button_male);
        else if (gender.equals("female")) radio_group_gender.check(R.id.radio_button_female);
        else radio_group_gender.check(R.id.radio_button_other);
        radio_group_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Toast.makeText(UpdateUserActivity.this, "You can't change gender", Toast.LENGTH_SHORT).show();
            }
        });

        String position = user.getPosition();
        if (position.equals("admin")) radio_group_position.check(R.id.radio_button_admin);
        else radio_group_position.check(R.id.radio_button_customer);

        radio_group_position.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                user.setPosition(radioButton.getText().toString());
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteHelper.updateUser(user);
                Toast.makeText(UpdateUserActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                builder.setTitle("Notice of deletion of user information");
                TextView title = new TextView(v.getContext());
                title.setText("Notice of deletion of user information");
                title.setTextColor(Color.RED);
                title.setTextSize(20);
                title.setPadding(20, 20, 20, 20);
                title.setGravity(Gravity.CENTER);
                builder.setCustomTitle(title);
                builder.setMessage("are you sure you want to delete " + user.getName() + " ?");
                builder.setIcon(R.drawable.remove);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    ProgressDialog progressDialog = new ProgressDialog(UpdateUserActivity.this);
                    progressDialog.setMessage("Loading..."); // thiết lập tin nhắn
                    progressDialog.show(); // hiển thị ProgressDialog
                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(user.getUsername())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        SignInMethodQueryResult result = task.getResult();
                                        if (result != null && result.getSignInMethods() != null
                                                && result.getSignInMethods().size() > 0) {
                                            // Email đã được đăng ký trong hệ thống, lấy uid của người dùng
                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            // Tiếp tục xóa thông tin người dùng từ Firebase Realtime Database hoặc Cloud Firestore
                                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                            Query query = usersRef.orderByChild("uid").equalTo(uid);
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                        userSnapshot.getRef().removeValue();
                                                        // Thông tin người dùng đã được xóa thành công
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    // Xảy ra lỗi trong quá trình truy vấn, xử lý ở đây
                                                    Log.d("TAG", "Error getting documents: ", databaseError.toException());
                                                }
                                            });


                                        } else {
                                            // Email không tồn tại trong hệ thống, xử lý ở đây
                                            Log.d("TAG", "Email not found");
                                        }
                                    } else {
                                        // Xảy ra lỗi trong quá trình truy vấn, xử lý ở đây
                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                    Toast.makeText(UpdateUserActivity.this, "Delete user successfully", Toast.LENGTH_SHORT).show();
                    sqLiteHelper.deleteUser(user.getId());
                    progressDialog.dismiss();
                    finish();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
    }
}