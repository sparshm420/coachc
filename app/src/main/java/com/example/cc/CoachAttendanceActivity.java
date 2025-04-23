package com.example.cc;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CoachAttendanceActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    LinearLayout attendanceLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_attendance);

        dbHelper = new DatabaseHelper(this);
        attendanceLayout = findViewById(R.id.attendanceLayout);

        String className = getIntent().getStringExtra("class_name");
        setTitle("Attendance for " + className);

        if (className != null) {
            loadAttendance(className);
        }
    }

    private void loadAttendance(String className) {
        Cursor cursor = dbHelper.getAttendanceByCoach(SessionManager.getUserEmail(this));
        if (cursor != null && cursor.moveToFirst()) {
            boolean found = false;
            do {
                String attendedClass = cursor.getString(cursor.getColumnIndexOrThrow("class_name"));
                if (attendedClass.equals(className)) {
                    int learnerId = cursor.getInt(cursor.getColumnIndexOrThrow("learner_id"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                    Cursor learnerCursor = dbHelper.getLearnerById(learnerId);
                    String learnerName = learnerCursor.moveToFirst() ? learnerCursor.getString(learnerCursor.getColumnIndexOrThrow("name")) : "Learner ID " + learnerId;
                    learnerCursor.close();

                    TextView tv = new TextView(this);
                    tv.setText(learnerName + " - " + date);
                    tv.setTextSize(16);
                    attendanceLayout.addView(tv);
                    found = true;
                }
            } while (cursor.moveToNext());

            if (!found) {
                TextView tv = new TextView(this);
                tv.setText("No attendance records found.");
                tv.setTextSize(16);
                attendanceLayout.addView(tv);
            }

            cursor.close();
        } else {
            TextView tv = new TextView(this);
            tv.setText("No attendance records found.");
            tv.setTextSize(16);
            attendanceLayout.addView(tv);
        }
    }
}
