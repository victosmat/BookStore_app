package com.example.BookStore_app.activity.LoginActivity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.BookStore_app.R;
import com.example.BookStore_app.activity.customerActivity.MainCustomerActivity;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.Cart;
import com.example.BookStore_app.model.User;
import com.example.BookStore_app.model.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Calendar;
import java.util.Date;

public class RegistryActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtName, edtDateOfBirth, edtUserName, edtPassword;
    private Button btnSubmit, btnCancel;
    private RadioGroup radio_group_gender;
    private ImageView passwordToggleImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry_user);

        edtName = findViewById(R.id.edtName);
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth);
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        passwordToggleImageView = findViewById(R.id.password_toggle_image_view);

        passwordToggleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kiểm tra trạng thái của EditText
                if (edtPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Nếu đang hiện mật khẩu thì ẩn đi
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    // Ngược lại, hiển thị mật khẩu
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                // Di chuyển con trỏ về cuối EditText
                edtPassword.setSelection(edtPassword.length());
            }
        });

        radio_group_gender = findViewById(R.id.radio_group_gender);

        User user = new User();
        radio_group_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                user.setGender(radioButton.getText().toString());
            }
        });

        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(RegistryActivity.this, (picker, year, month, dayOfMonth) -> {
                    String date = "";
                    if (month > 8) {
                        if (dayOfMonth > 9)
                            date = y + "/" + (month + 1) + "/" + dayOfMonth;
                        else
                            date = y + "/" + (month + 1) + "/0" + dayOfMonth;
                    } else {
                        if (dayOfMonth > 9)
                            date = y + "/0" + (month + 1) + "/" + dayOfMonth;
                        else
                            date = y + "/0" + (month + 1) + "/0" + dayOfMonth;
                    }
                    edtDateOfBirth.setText(date);
                }, y, m, d);
                dialog.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String dateOfBirth = edtDateOfBirth.getText().toString();
                String username = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();
                String position = "customer";
                User user1 = new User(name, dateOfBirth, user.getGender(), position, username, password);

                // kiểm tra password có đủ 7 ký tự không
                if (user1.getPassword().length() < 7) {
                    Toast.makeText(getApplicationContext(), "Password must be at least 7 characters", Toast.LENGTH_SHORT).show();
                } else if (user1.getName().isEmpty() || user1.getGender().isEmpty() || user1.getPosition().isEmpty() || user1.getDateOfBirth().isEmpty() || user1.getUsername().isEmpty() || user1.getPassword().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter full information", Toast.LENGTH_SHORT).show();
                } else {
                    ProgressDialog progressDialog = new ProgressDialog(RegistryActivity.this);
                    progressDialog.setMessage("Loading..."); // thiết lập tin nhắn
                    progressDialog.show(); // hiển thị ProgressDialog
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                    // Lấy tham chiếu tới collection "users" trong Firestore
                    CollectionReference usersRef = firebaseFirestore.collection("users");

                    firebaseAuth.createUserWithEmailAndPassword(username, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        // Lấy ID của user mới tạo
                                        String userId = firebaseAuth.getCurrentUser().getUid();

                                        // Tạo một đối tượng User với thông tin đăng ký
                                        UserDetails newUser = new UserDetails(userId, name, username);

                                        // Lưu thông tin user vào Firestore
                                        usersRef.document(userId).set(newUser)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(RegistryActivity.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
                                                        // Thành công: đăng nhập và chuyển đến trang chủ
                                                        Intent intent = new Intent(RegistryActivity.this, MainCustomerActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Lỗi: in ra thông báo và không chuyển đến trang chủ
                                                        Log.w(TAG, "Error adding document", e);
                                                        Toast.makeText(RegistryActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        SQLiteHelper sqLiteHelper = new SQLiteHelper(RegistryActivity.this);
                                        sqLiteHelper.addUser(user1);

                                        // tạo giỏ hàng cho user
                                        int id = sqLiteHelper.getUserByUsername(user1.getUsername()).getId();
                                        sqLiteHelper.addCart(id);

                                        Intent intent = new Intent(RegistryActivity.this, LoginActivity.class);
                                        Toast.makeText(RegistryActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                    } else {
                                        // Lỗi: in ra thông báo và không chuyển đến trang chủ
                                        Toast.makeText(RegistryActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
    }
}