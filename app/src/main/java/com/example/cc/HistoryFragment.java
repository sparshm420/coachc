package com.example.cc;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment {

    private LinearLayout layoutHistory;
    private DatabaseHelper dbHelper;

    public HistoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        layoutHistory = view.findViewById(R.id.layoutHistory);
        dbHelper = new DatabaseHelper(getContext());

        loadAttendanceHistory();
        return view;
    }

    private void loadAttendanceHistory() {
        layoutHistory.removeAllViews();

        int userId = SessionManager.getUserId(getContext());
        Cursor cursor = dbHelper.getAttendanceByLearner(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String className = cursor.getString(cursor.getColumnIndexOrThrow("class_name"));
                String coachEmail = cursor.getString(cursor.getColumnIndexOrThrow("coach_email"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

                TextView historyItem = new TextView(getContext());
                historyItem.setText("• " + className + "\n   Coach: " + coachEmail + "\n   Attended on: " + date);
                historyItem.setTextSize(15);
                historyItem.setPadding(16, 12, 16, 12);
                historyItem.setBackgroundResource(R.drawable.booking_card_background);
                historyItem.setTextColor(getResources().getColor(android.R.color.black));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 16);
                historyItem.setLayoutParams(params);

                layoutHistory.addView(historyItem);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            TextView empty = new TextView(getContext());
            empty.setText("No attendance history yet.");
            empty.setPadding(16, 32, 16, 32);
            layoutHistory.addView(empty);
        }
    }
}
