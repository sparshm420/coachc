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

public class LearnerAttendanceFragment extends Fragment {

    DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coach_attendance, container, false);
        ListView listAttendance = view.findViewById(R.id.listAttendance);

        dbHelper = new DatabaseHelper(getContext());
        int learnerId = SessionManager.getUserId(getContext());

        Cursor cursor = dbHelper.getAttendanceByLearner(learnerId);
        List<String> records = new ArrayList<>();

        while (cursor.moveToNext()) {
            String className = cursor.getString(cursor.getColumnIndexOrThrow("class_name"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

            records.add("🏷 Class: " + className + "\n🕒 " + time);
        }
        cursor.close();

        listAttendance.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, records));
        return view;
    }
}
