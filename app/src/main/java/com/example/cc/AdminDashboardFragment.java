package com.example.cc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminDashboardFragment extends Fragment {

    DatabaseHelper dbHelper;
    TextView tvTotalLearners, tvTotalCoaches, tvTotalBookings, tvTotalReviews;
    TextView tvTopClass, tvTopCoach, tvTopLearner, tvPeakTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        dbHelper = new DatabaseHelper(getContext());

        tvTotalLearners = view.findViewById(R.id.tvTotalLearners);
        tvTotalCoaches = view.findViewById(R.id.tvTotalCoaches);
        tvTotalBookings = view.findViewById(R.id.tvTotalBookings);
        tvTotalReviews = view.findViewById(R.id.tvTotalReviews);
        tvTopClass = view.findViewById(R.id.tvTopClass);
        tvTopCoach = view.findViewById(R.id.tvTopCoach);
        tvTopLearner = view.findViewById(R.id.tvTopLearner);
        tvPeakTime = view.findViewById(R.id.tvPeakTime);

        loadAnalytics();

        return view;
    }

    private void loadAnalytics() {
        int totalLearners = dbHelper.getTableCount(DatabaseHelper.TABLE_LEARNERS);
        int totalCoaches = dbHelper.getTableCount(DatabaseHelper.TABLE_COACHES);
        int totalBookings = dbHelper.getTableCount(DatabaseHelper.TABLE_BOOKING);
        int totalReviews = dbHelper.getTableCount("reviews");

        String topClass = dbHelper.getTopRatedClassName();
        String topCoach = dbHelper.getMostActiveCoach();
        String topLearner = dbHelper.getTopLearner();
        String peakTime = dbHelper.getMostPopularTimeSlot();

        tvTotalLearners.setText("Learners: " + totalLearners);
        tvTotalCoaches.setText("Coaches: " + totalCoaches);
        tvTotalBookings.setText("Bookings: " + totalBookings);
        tvTotalReviews.setText("Reviews: " + totalReviews);

        tvTopClass.setText("Top Class: " + (topClass != null ? topClass : "-"));
        tvTopCoach.setText("Top Coach: " + (topCoach != null ? topCoach : "-"));
        tvTopLearner.setText("Top Learner: " + (topLearner != null ? topLearner : "-"));
        tvPeakTime.setText("Peak Time: " + (peakTime != null ? peakTime : "-"));
    }
}
