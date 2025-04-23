package com.example.cc;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class CoachAttendanceFragment extends Fragment {

    DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coach_attendance, container, false);
        ListView listAttendance = view.findViewById(R.id.listAttendance);

        dbHelper = new DatabaseHelper(getContext());
        String coachEmail = SessionManager.getUserEmail(getContext());

        Cursor cursor = dbHelper.getAttendanceByCoach(coachEmail);
        List<String> records = new ArrayList<>();

        while (cursor.moveToNext()) {
            int learnerId = cursor.getInt(cursor.getColumnIndexOrThrow("learner_id"));
            String className = cursor.getString(cursor.getColumnIndexOrThrow("class_name"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

            records.add("👤 Learner ID: " + learnerId + "\n🏷 Class: " + className + "\n🕒 " + time);
        }
        cursor.close();

        listAttendance.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, records));
        return view;
    }
}
