package com.example.cc;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyClassesFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private LinearLayout layoutContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_classes, container, false);
        layoutContainer = view.findViewById(R.id.myClassesContainer);
        dbHelper = new DatabaseHelper(getContext());

        Button btnScanQR = view.findViewById(R.id.btnScanQR);
        if (btnScanQR != null) {
            btnScanQR.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), QRScanActivity.class);
                startActivity(intent);
            });
        }

        loadMyClasses();
        return view;
    }

    private void loadMyClasses() {
        layoutContainer.removeAllViews();
        String coachEmail = SessionManager.getUserEmail(getContext());

        if (coachEmail == null || coachEmail.isEmpty()) {
            Toast.makeText(getContext(), "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.getClassesByCoach(coachEmail);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int classId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));

                // Card Layout
                LinearLayout classCard = new LinearLayout(getContext());
                classCard.setOrientation(LinearLayout.VERTICAL);
                classCard.setPadding(32, 32, 32, 32);
                classCard.setBackgroundResource(R.drawable.booking_card_background);
                classCard.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                classCard.setElevation(8f);
                classCard.setPadding(24, 24, 24, 24);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 24); // left, top, right, bottom
                classCard.setLayoutParams(params);


                // Class Info
                TextView txtDetails = new TextView(getContext());
                txtDetails.setText("Class: " + name + "\nCategory: " + category + "\nTime(s): " + time);
                txtDetails.setTextSize(16);
                txtDetails.setPadding(0, 0, 0, 16);

                // Delete Button
                Button btnDelete = new Button(getContext());
                btnDelete.setText("Delete Class");
                btnDelete.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                btnDelete.setTextColor(getResources().getColor(android.R.color.white));
                btnDelete.setOnClickListener(v -> confirmDelete(classId));

                // View Attendance Button
                Button btnViewAttendance = new Button(getContext());
                btnViewAttendance.setText("View Attendance");
                btnViewAttendance.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                btnViewAttendance.setTextColor(getResources().getColor(android.R.color.white));
                btnViewAttendance.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), CoachAttendanceActivity.class);
                    intent.putExtra("class_id", classId);
                    intent.putExtra("class_name", name);
                    startActivity(intent);
                });

                // Add to card
                classCard.addView(txtDetails);
                classCard.addView(btnDelete);
                classCard.addView(btnViewAttendance);

                layoutContainer.addView(classCard);

            } while (cursor.moveToNext());
            cursor.close();
        } else {
            TextView empty = new TextView(getContext());
            empty.setText("No active classes.");
            empty.setTextSize(16);
            empty.setPadding(16, 32, 16, 32);
            layoutContainer.addView(empty);
        }
    }

    private void confirmDelete(int classId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean success = dbHelper.deleteClass(classId);
                    if (success) {
                        Toast.makeText(getContext(), "Class deleted successfully", Toast.LENGTH_SHORT).show();
                        loadMyClasses();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete class", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
