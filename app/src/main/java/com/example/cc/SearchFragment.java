package com.example.cc;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private ListView classListView;
    private ArrayAdapter<String> classAdapter;
    private List<String> classList;
    private DatabaseHelper dbHelper;
    private LinearLayout layoutFilters;

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int userId = SessionManager.getUserId(getContext());
        if (userId == -1) {
            Toast.makeText(getContext(), "Session expired. Please log in.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        classListView = view.findViewById(R.id.classListView);
        layoutFilters = view.findViewById(R.id.layoutFilters);
        dbHelper = new DatabaseHelper(getContext());
        classList = new ArrayList<>();

        Button btnNearby = view.findViewById(R.id.btnNearby);
        btnNearby.setOnClickListener(v -> {
            Log.d("NEARBY_CLICK", "Button was clicked!");
            Toast.makeText(getContext(), "Fetching nearby classes...", Toast.LENGTH_SHORT).show();
            loadAllClassesNearMe();
        });

        classAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, classList);
        classListView.setAdapter(classAdapter);

        loadAllClasses();
        setupFilterButtons();

        classListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = classList.get(position);
            new AlertDialog.Builder(getContext())
                    .setTitle("Book Class")
                    .setMessage("Do you want to book this class?\n\n" + selectedItem)
                    .setPositiveButton("Yes", (dialog, which) -> bookClass(selectedItem))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        classListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            String selectedItem = classList.get(position);
            String className = selectedItem.split(" \\(")[0].split("★")[0].trim();
            showReviewsDialog(className);
            return true;
        });

        return view;
    }

    private void loadAllClasses() {
        classList.clear();
        List<String> allClasses = dbHelper.getAllClasses("");
        for (String classEntry : allClasses) {
            String className = classEntry.split(" \\(")[0].trim();
            float rating = dbHelper.getAverageRating(className);
            String stars = (rating > 0) ? " ★" + String.format("%.1f", rating) : " ★New";
            classList.add(classEntry + stars);
        }
        classAdapter.notifyDataSetChanged();
    }

    private void loadAllClassesNearMe() {
        classList.clear();

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 102);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double userLat = location.getLatitude();
                double userLon = location.getLongitude();
                Log.d("USER_LOCATION", "Lat: " + userLat + ", Lon: " + userLon);

                Cursor cursor = dbHelper.getAllClasses();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                        String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                        double classLat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                        double classLon = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

                        Log.d("CLASS_LOCATION", name + ": Lat = " + classLat + ", Lon = " + classLon);

                        double distance = getDistance(userLat, userLon, classLat, classLon);
                        Log.d("DISTANCE", name + " is " + distance + " km away");

                        if (distance <= 5.0) {
                            float rating = dbHelper.getAverageRating(name);
                            String stars = (rating > 0) ? " ★" + String.format("%.1f", rating) : " ★New";
                            classList.add(name + " (" + category + ") • " + String.format("%.2f", distance) + " km away" + stars);
                        }

                    } while (cursor.moveToNext());

                    cursor.close();
                }

                if (classList.isEmpty()) {
                    Toast.makeText(getContext(), "No nearby classes found.", Toast.LENGTH_SHORT).show();
                }

                classAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Couldn't get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterClasses(String category) {
        classList.clear();
        List<String> filtered = dbHelper.getAllClasses(category);
        for (String classEntry : filtered) {
            String className = classEntry.split(" \\(")[0].trim();
            float rating = dbHelper.getAverageRating(className);
            String stars = (rating > 0) ? " ★" + String.format("%.1f", rating) : " ★New";
            classList.add(classEntry + stars);
        }
        classAdapter.notifyDataSetChanged();
    }

    private void setupFilterButtons() {
        String[] filters = {"All", "Football", "Swimming", "Yoga", "Cricket"};

        for (String filter : filters) {
            Button btn = new Button(getContext());
            btn.setText(filter);
            btn.setAllCaps(false);
            btn.setTextColor(Color.WHITE);
            btn.setBackgroundResource(R.drawable.filter_button_background);
            btn.setPadding(24, 8, 24, 8);

            btn.setOnClickListener(v -> filterClasses(filter.equals("All") ? "" : filter));

            layoutFilters.addView(btn);
        }
    }

    // In bookClass(String selectedItem)
    private void bookClass(String selectedItem) {
        int userId = SessionManager.getUserId(getContext());
        String[] mainParts = selectedItem.split("★")[0].split(" \\(");
        String className = mainParts[0].trim();

        Cursor classCursor = dbHelper.getClassByName(className);
        if (classCursor != null && classCursor.moveToFirst()) {
            String classCategory = classCursor.getString(classCursor.getColumnIndexOrThrow(DatabaseHelper.CLASS_CATEGORY));
            String timeSlotString = classCursor.getString(classCursor.getColumnIndexOrThrow(DatabaseHelper.CLASS_TIME));
            String[] timeSlots = timeSlotString.split(","); // Coach-defined times

            new AlertDialog.Builder(getContext())
                    .setTitle("Select Time Slot")
                    .setItems(timeSlots, (dialog, which) -> {
                        String selectedTime = timeSlots[which].trim();
                        boolean booked = dbHelper.addBooking(userId, className, selectedTime, classCategory);
                        if (booked) {
                            Toast.makeText(getContext(), "Booked at " + selectedTime, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Booking failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            classCursor.close();
        } else {
            Toast.makeText(getContext(), "Class not found.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showReviewsDialog(String className) {
        List<String> reviews = dbHelper.getReviewsForClass(className);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Reviews for " + className);

        if (reviews.isEmpty()) {
            builder.setMessage("No reviews yet for this class.");
        } else {
            StringBuilder reviewList = new StringBuilder();
            for (String r : reviews) {
                reviewList.append("• ").append(r).append("\n\n");
            }
            builder.setMessage(reviewList.toString());
        }

        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] result = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, result);
        return result[0] / 1000.0;
    }
}