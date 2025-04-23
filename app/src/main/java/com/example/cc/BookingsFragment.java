package com.example.cc;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BookingsFragment extends Fragment {

    DatabaseHelper dbHelper;
    LinearLayout layoutBookings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int userId = SessionManager.getUserId(getContext());
        if (userId == -1) {
            Toast.makeText(getContext(), "Session expired. Please log in.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        layoutBookings = view.findViewById(R.id.layoutBookings);
        dbHelper = new DatabaseHelper(getContext());

        loadBookings();

        return view;
    }

    private void loadBookings() {
        layoutBookings.removeAllViews();

        int userId = SessionManager.getUserId(getContext());
        Cursor cursor = dbHelper.getBookingsByUserId(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOKING_ID));
                String className = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOKING_CLASS_NAME));
                String classTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOKING_CLASS_TIME));
                String classCategory = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOKING_CLASS_CATEGORY));

                LinearLayout cardLayout = new LinearLayout(getContext());
                cardLayout.setOrientation(LinearLayout.VERTICAL);
                cardLayout.setPadding(32, 32, 32, 32);
                cardLayout.setBackgroundResource(R.drawable.booking_card_background);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 0, 0, 32);
                cardLayout.setLayoutParams(layoutParams);

                TextView txtClassName = new TextView(getContext());
                txtClassName.setText(className);
                txtClassName.setTextSize(18);
                txtClassName.setTypeface(null, android.graphics.Typeface.BOLD);

                TextView txtClassTime = new TextView(getContext());
                txtClassTime.setText("Timing: " + classTime);

                TextView txtClassCategory = new TextView(getContext());
                txtClassCategory.setText("Category: " + classCategory);

                Button btnCancel = new Button(getContext());
                btnCancel.setText("Cancel Booking");
                btnCancel.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                btnCancel.setTextColor(getResources().getColor(android.R.color.white));
                btnCancel.setOnClickListener(v -> showCancelConfirmationDialog(bookingId));

                Button btnRate = new Button(getContext());
                btnRate.setText("Rate Class");
                btnRate.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                btnRate.setTextColor(getResources().getColor(android.R.color.white));

                boolean hasReviewed = dbHelper.hasUserReviewedClass(userId, className);
                if (hasReviewed) {
                    btnRate.setText("Already Rated");
                    btnRate.setEnabled(false);
                    btnRate.setAlpha(0.6f);
                } else {
                    btnRate.setOnClickListener(v -> showRatingDialog(bookingId, className));
                }

                Button btnAttendance = new Button(getContext());
                btnAttendance.setText("View Attendance");
                btnAttendance.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                btnAttendance.setTextColor(getResources().getColor(android.R.color.white));
                btnAttendance.setOnClickListener(v -> showAttendanceDialog(userId, className));

                cardLayout.addView(txtClassName);
                cardLayout.addView(txtClassTime);
                cardLayout.addView(txtClassCategory);
                cardLayout.addView(btnCancel);
                cardLayout.addView(btnRate);
                cardLayout.addView(btnAttendance);

                layoutBookings.addView(cardLayout);

            } while (cursor.moveToNext());
            cursor.close();
        } else {
            TextView noBooking = new TextView(getContext());
            noBooking.setText("No bookings yet.");
            noBooking.setTextSize(16);
            layoutBookings.addView(noBooking);
        }
    }

    private void showAttendanceDialog(int learnerId, String className) {
        Cursor cursor = dbHelper.getAttendanceByLearnerAndClass(learnerId, className);

        StringBuilder records = new StringBuilder();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                records.append("• ").append(date).append("\n");
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            records.append("No attendance marked yet.");
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Attendance for " + className)
                .setMessage(records.toString())
                .setPositiveButton("Close", null)
                .show();
    }

    private void showCancelConfirmationDialog(int bookingId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteBooking(bookingId);
                    if (deleted) {
                        Toast.makeText(getContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                        loadBookings();
                    } else {
                        Toast.makeText(getContext(), "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showRatingDialog(int bookingId, String className) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rate: " + className);

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_rating, null);
        builder.setView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText edtComment = dialogView.findViewById(R.id.edtReview);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            int userId = SessionManager.getUserId(getContext());
            int rating = (int) ratingBar.getRating();
            String comment = edtComment.getText().toString();

            boolean success = dbHelper.insertReview(userId, className, rating, comment);
            if (success) {
                Toast.makeText(getContext(), "Review submitted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to submit review.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

}
