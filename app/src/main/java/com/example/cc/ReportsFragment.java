package com.example.cc;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ReportsFragment extends Fragment {

    private LinearLayout reportsContainer;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        reportsContainer = view.findViewById(R.id.reportsContainer);
        dbHelper = new DatabaseHelper(getContext());
        displayCoachingReports();
        return view;
    }

    private void displayCoachingReports() {
        Cursor classCursor = dbHelper.getAllClasses();
        if (classCursor != null && classCursor.moveToFirst()) {
            do {
                String className = classCursor.getString(classCursor.getColumnIndexOrThrow("name"));
                String category = classCursor.getString(classCursor.getColumnIndexOrThrow("category"));
                String time = classCursor.getString(classCursor.getColumnIndexOrThrow("time"));
                String coachEmail = classCursor.getString(classCursor.getColumnIndexOrThrow("created_by"));

                View card = LayoutInflater.from(getContext()).inflate(R.layout.item_report_card, reportsContainer, false);

                TextView txtClassInfo = card.findViewById(R.id.txtClassInfo);
                TextView txtCoach = card.findViewById(R.id.txtCoach);
                TextView txtLearners = card.findViewById(R.id.txtLearners);

                txtClassInfo.setText("Class: " + className + "\nCategory: " + category + "\nTime: " + time);
                txtCoach.setText("Coach: " + coachEmail);

                StringBuilder enrolledList = new StringBuilder();
                Cursor bookings = dbHelper.getBookingsByClassName(className);
                if (bookings.moveToFirst()) {
                    do {
                        int learnerId = bookings.getInt(bookings.getColumnIndexOrThrow("user_id"));
                        Cursor learnerCursor = dbHelper.getLearnerById(learnerId);
                        if (learnerCursor != null && learnerCursor.moveToFirst()) {
                            String learnerName = learnerCursor.getString(learnerCursor.getColumnIndexOrThrow("name"));
                            enrolledList.append("- ").append(learnerName).append("\n");
                            learnerCursor.close();
                        }
                    } while (bookings.moveToNext());
                } else {
                    enrolledList.append("No learners enrolled.");
                }
                bookings.close();

                txtLearners.setText(enrolledList.toString());
                reportsContainer.addView(card);
            } while (classCursor.moveToNext());
        }
        if (classCursor != null) classCursor.close();
    }
}