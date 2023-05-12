package com.example.BookStore_app.fragment.customer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.BookStore_app.R;
import com.example.BookStore_app.database.SQLiteHelper;
import com.example.BookStore_app.model.Book;
import com.example.BookStore_app.model.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    private Button btnUpdate;

    private EditText edtName, edtDateOfBirth, edtUserName, edtPassword;
    private RadioGroup radio_group_gender;

    private SQLiteHelper sqLiteHelper;
    private TextInputLayout passwordTextInputLayout;
    private ImageView passwordToggleImageView;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        edtName = view.findViewById(R.id.edtName);
        edtDateOfBirth = view.findViewById(R.id.edtDateOfBirth);
        edtUserName = view.findViewById(R.id.edtUserName);
        radio_group_gender = view.findViewById(R.id.radio_group_gender);
        passwordTextInputLayout = view.findViewById(R.id.password_text_input_layout);
        edtPassword = view.findViewById(R.id.edtPassword);
        passwordToggleImageView = view.findViewById(R.id.password_toggle_image_view);
        sqLiteHelper = new SQLiteHelper(getActivity());

        user = sqLiteHelper.getUserByUsername(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        edtName.setText(user.getName());
        edtDateOfBirth.setText(user.getDateOfBirth());
        edtUserName.setText(user.getUsername());
        edtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "you cannot change your email", Toast.LENGTH_SHORT).show();
            }
        });

        edtPassword.setText(user.getPassword());

        // Thiết lập sự kiện click cho ImageView
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

        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getContext(), (v, year, month, dayOfMonth) -> {
                    String date = "";
                    if (month > 8) {
                        if (dayOfMonth > 9)
                            date = dayOfMonth + "/" + (month + 1) + "/" + y;
                        else
                            date = "0" + dayOfMonth + "/" + (month + 1) + "/" + y;
                    } else {
                        if (dayOfMonth > 9)
                            date = dayOfMonth + "/0" + (month + 1) + "/" + y;
                        else
                            date = "0" + dayOfMonth + "/0" + (month + 1) + "/" + y;
                    }
                    edtDateOfBirth.setText(date);
                }, y, m, d);
                dialog.show();
            }
        });

        String gender = user.getGender();
        if (gender.equals("male")) radio_group_gender.check(R.id.radio_button_male);
        else if (gender.equals("female")) radio_group_gender.check(R.id.radio_button_female);
        else radio_group_gender.check(R.id.radio_button_other);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setName(edtName.getText().toString());
                user.setDateOfBirth(edtDateOfBirth.getText().toString());
                user.setUsername(edtUserName.getText().toString());
                user.setPassword(edtPassword.getText().toString());

                radio_group_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        RadioButton radioButton = radioGroup.findViewById(i);
                        user.setGender(radioButton.getText().toString());
                    }
                });
                sqLiteHelper.updateUser(user);

                // update password in firebase
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseUser.updatePassword(user.getPassword());

                Toast.makeText(getActivity(), "User information update successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
