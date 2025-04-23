package com.example.cc;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScanActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this);

        // Start scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan Learner's QR Code");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                try {
                    int learnerId = Integer.parseInt(result.getContents().trim());
                    String coachEmail = SessionManager.getUserEmail(this);

                    // Fetch classes created by this coach
                    Cursor coachClassesCursor = dbHelper.getClassesByCoach(coachEmail);
                    boolean attendanceMarked = false;

                    if (coachClassesCursor != null && coachClassesCursor.moveToFirst()) {
                        do {
                            String className = coachClassesCursor.getString(coachClassesCursor.getColumnIndexOrThrow("name"));

                            // Check if learner has booked this class
                            Cursor bookingCursor = dbHelper.getBookingsByUserAndClass(learnerId, className);
                            if (bookingCursor != null && bookingCursor.moveToFirst()) {
                                // Found match — mark attendance
                                boolean marked = dbHelper.markAttendance(learnerId, coachEmail, className);
                                if (marked) {
                                    Toast.makeText(this, "Attendance marked for " + className, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to mark attendance", Toast.LENGTH_SHORT).show();
                                }

                                // Optionally remove from bookings if needed
                                int bookingId = bookingCursor.getInt(bookingCursor.getColumnIndexOrThrow("id"));
                                dbHelper.deleteBooking(bookingId);
                                attendanceMarked = true;
                                bookingCursor.close();
                                break;
                            }

                            if (bookingCursor != null) bookingCursor.close();
                        } while (coachClassesCursor.moveToNext());

                        coachClassesCursor.close();
                    }

                    if (!attendanceMarked) {
                        Toast.makeText(this, "This learner has no bookings for your classes.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Invalid QR Code.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Scan cancelled.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        finish();
    }
}
