package com.example.BookStore_app.activity.LoginActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.BookStore_app.R;
import com.example.BookStore_app.activity.adminActivity.MainActivity;
import com.example.BookStore_app.activity.customerActivity.MainCustomerActivity;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.User;
import com.example.BookStore_app.notification.MyNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button btnLogin;
    private TextView link_signup;

    private SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        link_signup = findViewById(R.id.link_signup);
        sqLiteHelper = new SQLiteHelper(this);

        btnLogin.setOnClickListener(view -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading..."); // thiết lập tin nhắn
            progressDialog.show(); // hiển thị ProgressDialog
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
//                                sqLiteHelper.deleteCartBook(1);
//                                sqLiteHelper.deleteCartBook(2);
                                // Đăng nhập thành công
                                Toast.makeText(LoginActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                User user1 = sqLiteHelper.getUserByUsername(user.getEmail());
                                if (user1.getPosition().equals("admin")) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    progressDialog.dismiss();
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainCustomerActivity.class);
                                    progressDialog.dismiss();
                                    startActivity(intent);
                                }
                                // Tiến hành xử lý dữ liệu
                            } else {
                                // Đăng nhập thất bại
                                Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        });

        link_signup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistryActivity.class);
            startActivity(intent);
        });
    }

    public int getID() {
        return (int) new Date().getTime();
    }
}
