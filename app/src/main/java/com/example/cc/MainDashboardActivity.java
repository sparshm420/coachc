package com.example.cc;

import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.elevation.SurfaceColors;

public class MainDashboardActivity extends AppCompatActivity {

    String userType;
    public static BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        // Set status bar and nav bar color to match the surface tone
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this));
        bottomNav.setItemIconSizeRes(R.dimen.bottom_nav_icon_size);
        bottomNav.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_LABELED);

        userType = SessionManager.getUserType(this);
        customizeMenuForUser(bottomNav.getMenu(), userType);

        if (savedInstanceState == null) {
            Fragment defaultFragment = getFragmentForMenuItem(R.id.nav_home);
            loadFragment(defaultFragment);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentForMenuItem(item.getItemId());
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private Fragment getFragmentForMenuItem(int itemId) {
        switch (itemId) {
            case R.id.nav_home:
                return "admin".equals(userType) ? new AdminDashboardFragment() : new HomeFragment();
            case R.id.nav_search:
                if ("learner".equals(userType)) return new SearchFragment();
                if ("coach".equals(userType)) return new MyClassesFragment();
                return new UserManagementFragment();
            case R.id.nav_bookings:
                if ("learner".equals(userType)) return new BookingsFragment();
                if ("coach".equals(userType)) return new CreateClassFragment();
                return new ReportsFragment();
            case R.id.nav_history:
                if ("learner".equals(userType)) return new HistoryFragment();
                return new ResolveQueriesFragment();
            case R.id.nav_profile:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void customizeMenuForUser(Menu menu, String userType) {
        if ("learner".equals(userType)) {
            menu.findItem(R.id.nav_search).setTitle("Search").setIcon(R.drawable.ic_search).setVisible(true);
            menu.findItem(R.id.nav_bookings).setTitle("Bookings").setIcon(R.drawable.ic_bookings).setVisible(true);
            menu.findItem(R.id.nav_history).setVisible(true);
        } else if ("coach".equals(userType)) {
            menu.findItem(R.id.nav_search).setTitle("My Classes").setIcon(R.drawable.ic_class).setVisible(true);
            menu.findItem(R.id.nav_bookings).setTitle("Add Class").setIcon(R.drawable.ic_add).setVisible(true);
            menu.findItem(R.id.nav_history).setVisible(false);
        } else if ("admin".equals(userType)) {
            menu.findItem(R.id.nav_search).setTitle("Users").setIcon(R.drawable.ic_users).setVisible(true);
            menu.findItem(R.id.nav_bookings).setTitle("Reports").setIcon(R.drawable.ic_report).setVisible(true);
            menu.findItem(R.id.nav_history).setTitle("Resolve").setIcon(R.drawable.ic_help).setVisible(true);
        }
    }
}
