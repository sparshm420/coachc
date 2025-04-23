package com.example.cc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView imgAvatar;
    EditText edtName, edtEmail, edtPhone;
    TextView tvEmail, tvPreferences, tvBookingCount, tvWelcome;
    Button btnChangeAvatar, btnSave, btnLogout, btnShowQRCode;
    LinearLayout layoutMyQueries;
    FloatingActionButton fabQuery;

    DatabaseHelper dbHelper;
    byte[] avatarBytes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getContext() == null) {
            return inflater.inflate(R.layout.fragment_profile, container, false);
        }

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgAvatar = view.findViewById(R.id.imgAvatar);
        edtName = view.findViewById(R.id.edtName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPreferences = view.findViewById(R.id.tvPreferences);
        tvBookingCount = view.findViewById(R.id.tvBookingCount);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        layoutMyQueries = view.findViewById(R.id.layoutMyQueries);
        fabQuery = view.findViewById(R.id.fabAskQuery);

        btnChangeAvatar = view.findViewById(R.id.btnChangeAvatar);
        btnSave = view.findViewById(R.id.btnSave);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnShowQRCode = view.findViewById(R.id.btnShowQRCode);

        dbHelper = new DatabaseHelper(getContext());

        loadProfileData();

        btnChangeAvatar.setOnClickListener(v -> openGallery());

        btnSave.setOnClickListener(v -> saveChanges());

        btnLogout.setOnClickListener(v -> {
            SessionManager.logout(getContext());
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        });

        String userType = SessionManager.getUserType(getContext());
        if (!"learner".equals(userType)) {
            btnShowQRCode.setVisibility(View.GONE);
        }

        fabQuery.setOnClickListener(v -> showQueryDialog());

        if ("learner".equals(userType) || "coach".equals(userType)) {
            fabQuery.setVisibility(View.VISIBLE);
        }

        layoutMyQueries = view.findViewById(R.id.layoutMyQueries);

        userType = SessionManager.getUserType(getContext());
        if ("learner".equals(userType) || "coach".equals(userType)) {
            layoutMyQueries.setVisibility(View.VISIBLE);
            loadMyQueries(); // only load queries if applicable
        } else {
            layoutMyQueries.setVisibility(View.GONE);
        }
        btnShowQRCode.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), QRCodeActivity.class);
            startActivity(intent);
        });


       // loadMyQueries();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SessionManager.getUserType(getContext()).equals("learner") || SessionManager.getUserType(getContext()).equals("coach")) {
            boolean hasUnread = dbHelper.hasUnreadResponses(SessionManager.getUserId(getContext()));
            if (hasUnread) {
                fabQuery.setImageResource(R.drawable.red_dot);
            } else {
                fabQuery.setImageResource(R.drawable.ic_question);
            }
            loadMyQueries();
        }
    }

    private void loadMyQueries() {
        layoutMyQueries.removeAllViews();

        int userId = SessionManager.getUserId(getContext());
        Cursor cursor = dbHelper.getUserQueries(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String response = cursor.getString(cursor.getColumnIndexOrThrow("response"));

                TextView queryCard = new TextView(getContext());
                String display = "• " + message;
                if (response != null && !response.isEmpty()) {
                    display += "\n📝 Response: " + response;
                } else {
                    display += "\n⏳ Awaiting response...";
                }

                queryCard.setText(display);
                queryCard.setTextSize(14);
                queryCard.setPadding(24, 24, 24, 24);
                queryCard.setBackgroundResource(R.drawable.query_card_background);
                queryCard.setTextColor(getResources().getColor(android.R.color.black));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 24);
                queryCard.setLayoutParams(params);

                layoutMyQueries.addView(queryCard);
            } while (cursor.moveToNext());

            cursor.close();
            dbHelper.markQueriesAsRead(userId);
        }
    }

    private void loadProfileData() {
        int userId = SessionManager.getUserId(getContext());
        String userType = SessionManager.getUserType(getContext());
        String email = SessionManager.getUserEmail(getContext());

        tvEmail.setText("Email: " + email);
        Cursor cursor = dbHelper.getUserByEmail(email);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            byte[] avatar = cursor.getBlob(cursor.getColumnIndexOrThrow("avatar"));

            edtName.setText(name);
            edtEmail.setText(email);
            edtPhone.setText(phone);
            tvWelcome.setText("Welcome, " + name);

            if (avatar != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                imgAvatar.setImageBitmap(bitmap);
                avatarBytes = avatar;
            }
        }
        cursor.close();

        Cursor bookingsCursor = dbHelper.getBookingsByUserId(userId);
        int bookingCount = bookingsCursor.getCount();
        tvBookingCount.setText("Bookings Made: " + bookingCount);
        bookingsCursor.close();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imgAvatar.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                avatarBytes = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveChanges() {
        int userId = SessionManager.getUserId(getContext());
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        boolean updated = dbHelper.updateUserProfile(userId, name, email, phone, Arrays.toString(avatarBytes));
        if (updated) {
            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
            SessionManager.saveUserEmail(getContext(), email);
            loadProfileData();
        } else {
            Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void showQueryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ask Admin");

        final EditText input = new EditText(getContext());
        input.setHint("Enter your query...");
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String message = input.getText().toString().trim();
            int userId = SessionManager.getUserId(getContext());

            if (!message.isEmpty()) {
                boolean submitted = dbHelper.insertQuery(userId, message);
                Toast.makeText(getContext(), submitted ? "Query sent!" : "Failed to send", Toast.LENGTH_SHORT).show();
                loadMyQueries();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
