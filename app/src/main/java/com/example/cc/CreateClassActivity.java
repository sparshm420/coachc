package com.example.cc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class CreateClassActivity extends AppCompatActivity {

    EditText edtClassName, edtCategory, edtTime;
    Button btnCreate;
    DatabaseHelper dbHelper;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        edtClassName = findViewById(R.id.edtClassName);
        edtCategory = findViewById(R.id.edtCategory);
        edtTime = findViewById(R.id.edtTime);
        btnCreate = findViewById(R.id.btnCreate);
        dbHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnCreate.setOnClickListener(v -> {
            String name = edtClassName.getText().toString().trim();
            String category = edtCategory.getText().toString().trim();
            String time = edtTime.getText().toString().trim();
            String createdBy = SessionManager.getUserEmail(this); // Get coach email

            if (name.isEmpty() || category.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check location permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                return;
            }

            // Get current location
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    boolean success = dbHelper.insertClass(name, category, time, lat, lon, createdBy);
                    if (success) {
                        Toast.makeText(this, "Class created successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to create class.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Could not retrieve location", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
