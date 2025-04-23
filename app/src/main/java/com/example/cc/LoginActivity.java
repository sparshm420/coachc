package com.example.cc;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        Button btnGoBack = findViewById(R.id.btnGoBack);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> loginUser());
        btnGoBack.setOnClickListener(v -> finish());
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check Admin
        Cursor cursor = dbHelper.validateFromTable(email, password, DatabaseHelper.TABLE_ADMINS);
        if (cursor != null && cursor.moveToFirst()) {
            loginSuccess(cursor, "admin", email);
            return;
        }

        // Check Coach
        cursor = dbHelper.validateFromTable(email, password, DatabaseHelper.TABLE_COACHES);
        if (cursor != null && cursor.moveToFirst()) {
            loginSuccess(cursor, "coach", email);
            return;
        }

        // Check Learner
        cursor = dbHelper.validateFromTable(email, password, DatabaseHelper.TABLE_LEARNERS);
        if (cursor != null && cursor.moveToFirst()) {
            loginSuccess(cursor, "learner", email);
            return;
        }

        // If none matched
        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        if (cursor != null) cursor.close();
    }

    private void loginSuccess(Cursor cursor, String userType, String email) {
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        SessionManager.saveUserId(this, userId);
        SessionManager.saveUserType(this, userType);
        SessionManager.saveUserEmail(this, email);

        Toast.makeText(this, userType.substring(0, 1).toUpperCase() + userType.substring(1) + " login successful", Toast.LENGTH_SHORT).show();

        cursor.close();
        startActivity(new Intent(this, MainDashboardActivity.class));
        finish();
    }
}
