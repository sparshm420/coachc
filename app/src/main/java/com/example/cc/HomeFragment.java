// === Updated HomeFragment.java (Dynamic Role-based Home) ===
package com.example.cc;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class HomeFragment extends Fragment {

    DatabaseHelper dbHelper;
    LinearLayout layoutUpcoming, layoutPopular;
    TextView txtWelcome, txtPopular;

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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        txtWelcome = view.findViewById(R.id.txtWelcome);
        txtPopular = view.findViewById(R.id.txtPopular);
        layoutPopular = view.findViewById(R.id.layoutPopular);
        layoutUpcoming = view.findViewById(R.id.layoutUpcoming);

        Button btnSearch = view.findViewById(R.id.btnSearch);
        Button btnBookings = view.findViewById(R.id.btnBookings);
        Button btnProfile = view.findViewById(R.id.btnProfile);

        dbHelper = new DatabaseHelper(getContext());

        String userType = SessionManager.getUserType(getContext());

        loadUserName();

        switch (userType) {
            case "learner":
                loadUpcomingBookings();
                loadPopularClasses();
                break;

            case "coach":
                loadCoachClasses();
                break;

            case "admin":
                loadAdminInsights();
                break;
        }

        btnSearch.setOnClickListener(v -> navigateTo(new SearchFragment()));
        btnBookings.setOnClickListener(v -> navigateTo(new BookingsFragment()));
        btnProfile.setOnClickListener(v -> navigateTo(new ProfileFragment()));

        return view;
    }

    private void loadUserName() {
        int userId = SessionManager.getUserId(getContext());
        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_NAME));
            txtWelcome.setText("Welcome, " + name + "!");
            cursor.close();
        }
    }

    private void loadUpcomingBookings() {
        layoutUpcoming.removeAllViews();
        layoutUpcoming.setVisibility(View.VISIBLE);

        int userId = SessionManager.getUserId(getContext());
        Cursor cursor = dbHelper.getBookingsByUserId(userId);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String className = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOKING_CLASS_NAME));
                    String classTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOKING_CLASS_TIME));

                    TextView bookingText = new TextView(getContext());
                    bookingText.setText("• " + className + " at " + classTime);
                    bookingText.setTextSize(14);
                    bookingText.setPadding(8, 4, 8, 4);

                    layoutUpcoming.addView(bookingText);
                } while (cursor.moveToNext());
            } else {
                TextView noBooking = new TextView(getContext());
                noBooking.setText("You have no upcoming bookings.");
                noBooking.setTextSize(14);
                noBooking.setPadding(8, 8, 8, 8);
                layoutUpcoming.addView(noBooking);
            }
            cursor.close();
        }
    }

    private void loadPopularClasses() {
        layoutPopular.removeAllViews();
        layoutPopular.setVisibility(View.VISIBLE);

        List<String> topRated = dbHelper.getTopRatedClasses(3);

        if (topRated.isEmpty()) {
            TextView none = new TextView(getContext());
            none.setText("No reviews yet.");
            none.setPadding(8, 8, 8, 8);
            layoutPopular.addView(none);
        } else {
            for (String item : topRated) {
                TextView classText = new TextView(getContext());
                classText.setText("• " + item);
                classText.setTextSize(15);
                classText.setPadding(16, 12, 16, 12);
                classText.setBackgroundResource(R.drawable.booking_card_background);
                classText.setTextColor(getResources().getColor(android.R.color.black));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 16);
                classText.setLayoutParams(params);

                layoutPopular.addView(classText);
            }
        }
    }

    private void loadCoachClasses() {
        layoutUpcoming.setVisibility(View.GONE);
        layoutPopular.setVisibility(View.VISIBLE);

        layoutPopular.removeAllViews();
        TextView coachInfo = new TextView(getContext());
        coachInfo.setText("Coach Dashboard: Manage your classes here.");
        coachInfo.setPadding(16, 16, 16, 16);
        layoutPopular.addView(coachInfo);

        // You could load the coach's classes here (dummy for now)
        TextView dummy = new TextView(getContext());
        dummy.setText("• FitKick Football Arena - 5:00 PM\n• Cricket Pro Academy - 3:00 PM");
        dummy.setPadding(16, 12, 16, 12);
        layoutPopular.addView(dummy);
    }

    private void loadAdminInsights() {
        layoutUpcoming.setVisibility(View.GONE);
        layoutPopular.setVisibility(View.VISIBLE);

        layoutPopular.removeAllViews();
        TextView adminInfo = new TextView(getContext());
        adminInfo.setText("Admin Panel: System stats and user controls.");
        adminInfo.setPadding(16, 16, 16, 16);
        layoutPopular.addView(adminInfo);

        // Simulate admin features shown on home
        TextView dummy = new TextView(getContext());
        dummy.setText("• 230 users registered\n• 18 classes pending approval\n• 2 reports flagged");
        dummy.setPadding(16, 12, 16, 12);
        layoutPopular.addView(dummy);
    }

    private void navigateTo(Fragment fragment) {
        if (fragment instanceof SearchFragment) {
            MainDashboardActivity.bottomNav.setSelectedItemId(R.id.nav_search);
        } else if (fragment instanceof BookingsFragment) {
            MainDashboardActivity.bottomNav.setSelectedItemId(R.id.nav_bookings);
        } else if (fragment instanceof ProfileFragment) {
            MainDashboardActivity.bottomNav.setSelectedItemId(R.id.nav_profile);
        }

        // This will be handled automatically by the BottomNavigationView listener in MainDashboardActivity
    }

}
