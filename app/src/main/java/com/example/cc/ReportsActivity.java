package com.example.cc;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReportsActivity extends AppCompatActivity {

    LinearLayout reportsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        reportsContainer = findViewById(R.id.reportsContainer);

        // Example dummy report items
        TextView report1 = new TextView(this);
        report1.setText("• Spam complaint on Yoga Class");
        report1.setPadding(16, 8, 16, 8);

        TextView report2 = new TextView(this);
        report2.setText("• Inappropriate comment flagged on Football Arena");
        report2.setPadding(16, 8, 16, 8);

        reportsContainer.addView(report1);
        reportsContainer.addView(report2);
    }
}