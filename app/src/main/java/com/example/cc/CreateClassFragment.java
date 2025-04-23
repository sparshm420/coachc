package com.example.cc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.*;
import java.util.*;

public class CreateClassFragment extends Fragment {

    EditText edtClassName, edtCategory;
    Button btnCreate, btnAddTimeSlot;
    LinearLayout timeSlotContainer;
    DatabaseHelper dbHelper;
    List<EditText> timeSlotFields = new ArrayList<>();
    FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_class, container, false);

        edtClassName = view.findViewById(R.id.edtClassName);
        edtCategory = view.findViewById(R.id.edtCategory);
        btnCreate = view.findViewById(R.id.btnCreate);
        btnAddTimeSlot = view.findViewById(R.id.btnAddTimeSlot);
        timeSlotContainer = view.findViewById(R.id.timeSlotContainer);
        dbHelper = new DatabaseHelper(getContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        addTimeSlotField();

        btnAddTimeSlot.setOnClickListener(v -> addTimeSlotField());

        btnCreate.setOnClickListener(v -> {
            String name = edtClassName.getText().toString().trim();
            String category = edtCategory.getText().toString().trim();
            String time = getCombinedTimeSlots();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(category) || TextUtils.isEmpty(time)) {
                Toast.makeText(getContext(), "Fill all fields and time slots", Toast.LENGTH_SHORT).show();
                return;
            }

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                return;
            }

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String createdBy = SessionManager.getUserEmail(getContext());

                    boolean inserted = dbHelper.insertClass(name, category, time, lat, lon, createdBy);
                    if (inserted) {
                        Toast.makeText(getContext(), "Class created!", Toast.LENGTH_SHORT).show();
                        clearForm();
                    } else {
                        Toast.makeText(getContext(), "Failed to create class.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Could not fetch location.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private void addTimeSlotField() {
        EditText newSlot = new EditText(getContext());
        newSlot.setHint("Time Slot (e.g. 5:00 PM)");
        newSlot.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        newSlot.setPadding(0, 16, 0, 16);
        timeSlotContainer.addView(newSlot);
        timeSlotFields.add(newSlot);
    }

    private String getCombinedTimeSlots() {
        List<String> slotStrings = new ArrayList<>();
        for (EditText slot : timeSlotFields) {
            String text = slot.getText().toString().trim();
            if (!text.isEmpty()) {
                slotStrings.add(text);
            }
        }
        return TextUtils.join(",", slotStrings);
    }

    private void clearForm() {
        edtClassName.setText("");
        edtCategory.setText("");
        timeSlotContainer.removeAllViews();
        timeSlotFields.clear();
        addTimeSlotField();
    }
}