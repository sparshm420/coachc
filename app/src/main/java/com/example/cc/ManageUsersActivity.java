package com.example.cc;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ManageUsersActivity extends AppCompatActivity {

    LinearLayout usersContainer;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        usersContainer = findViewById(R.id.usersContainer);
        dbHelper = new DatabaseHelper(this);

        loadUsers();
    }

    private void loadUsers() {
        Cursor cursor = dbHelper.getAllUsers();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_EMAIL));

                TextView userView = new TextView(this);
                userView.setText("• " + name + " (" + email + ")");
                userView.setPadding(16, 8, 16, 8);
                usersContainer.addView(userView);

            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}