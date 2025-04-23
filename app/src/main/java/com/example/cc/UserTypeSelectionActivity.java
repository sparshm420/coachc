// UserTypeSelectionActivity.java
package com.example.cc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class UserTypeSelectionActivity extends AppCompatActivity {

    Button btnAdmin, btnCoach, btnLearner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_selection);

        btnAdmin = findViewById(R.id.btnAdmin);
        btnCoach = findViewById(R.id.btnCoach);
        btnLearner = findViewById(R.id.btnLearner);

        btnAdmin.setOnClickListener(v -> startActivity(new Intent(this, AdminRegisterActivity.class)));
        btnCoach.setOnClickListener(v -> startActivity(new Intent(this, CoachRegisterActivity.class)));
        btnLearner.setOnClickListener(v -> startActivity(new Intent(this, LearnerRegisterActivity.class)));
    }
}
