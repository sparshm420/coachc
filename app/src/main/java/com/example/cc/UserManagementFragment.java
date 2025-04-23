package com.example.cc;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class UserManagementFragment extends Fragment {

    private LinearLayout userManageContainer;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);
        userManageContainer = view.findViewById(R.id.userManageContainer);
        dbHelper = new DatabaseHelper(getContext());
        loadAllUsers();
        return view;
    }

    private void loadAllUsers() {
        userManageContainer.removeAllViews();

        loadUserList(DatabaseHelper.TABLE_LEARNERS, "Learner");
        loadUserList(DatabaseHelper.TABLE_COACHES, "Coach");
    }

    private void loadUserList(String tableName, String userTypeLabel) {
        Cursor cursor = dbHelper.getAllFromTable(tableName);
        while (cursor.moveToNext()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            View card = LayoutInflater.from(getContext()).inflate(R.layout.item_user_manage, userManageContainer, false);

            TextView txtDetails = card.findViewById(R.id.txtUserDetails);
            TextView txtUserType = card.findViewById(R.id.txtUserType);
            ImageView avatar = card.findViewById(R.id.userAvatar);
            Button btnKick = card.findViewById(R.id.btnKick);
            Button btnEndorse = card.findViewById(R.id.btnEndorse);

            txtDetails.setText("Name: " + name + "\nEmail: " + email);
            txtUserType.setText("User Type: " + userTypeLabel);

            avatar.setImageResource(R.drawable.ic_users); // or load dynamically later

            btnKick.setOnClickListener(v -> confirmKick(userId, tableName));
            btnEndorse.setOnClickListener(v -> Toast.makeText(getContext(), name + " endorsed!", Toast.LENGTH_SHORT).show());

            userManageContainer.addView(card);
        }
        cursor.close();
    }

    private void confirmKick(int userId, String tableName) {
        new AlertDialog.Builder(getContext())
                .setTitle("Kick User")
                .setMessage("Are you sure you want to remove this user?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean removed = dbHelper.deleteUserFromTable(userId, tableName);
                    if (removed) {
                        Toast.makeText(getContext(), "User removed.", Toast.LENGTH_SHORT).show();
                        userManageContainer.removeAllViews();
                        loadAllUsers();
                    } else {
                        Toast.makeText(getContext(), "Failed to remove user.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
