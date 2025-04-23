package com.example.cc;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminQueryFragment extends Fragment {

    private LinearLayout layoutContainer;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_queries, container, false);

        layoutContainer = view.findViewById(R.id.queryContainer);
        dbHelper = new DatabaseHelper(getContext());

        loadQueries();

        return view;
    }

    private void loadQueries() {
        layoutContainer.removeAllViews();

        Cursor cursor = dbHelper.getAllPendingQueries();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String response = cursor.getString(cursor.getColumnIndexOrThrow("response"));

                LinearLayout card = new LinearLayout(getContext());
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(32, 32, 32, 32);
                card.setBackgroundResource(R.drawable.card_background);
                card.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                card.setElevation(4);

                TextView tvUser = new TextView(getContext());
                tvUser.setText("From User ID: " + userId);
                tvUser.setTextSize(16f);
                tvUser.setTypeface(null, android.graphics.Typeface.BOLD);

                TextView tvQuery = new TextView(getContext());
                tvQuery.setText("Query: " + message);

                TextView tvResponse = new TextView(getContext());
                tvResponse.setText("Response: " + (response == null ? "(No response yet)" : response));
                tvResponse.setPadding(0, 8, 0, 8);

                Button btnReply = new Button(getContext());
                btnReply.setText(response == null ? "Reply" : "Edit Reply");
                btnReply.setBackgroundColor(getResources().getColor(R.color.teal_700));
                btnReply.setTextColor(getResources().getColor(android.R.color.white));

                btnReply.setOnClickListener(v -> showReplyDialog(userId, id, message, response));

                card.addView(tvUser);
                card.addView(tvQuery);
                card.addView(tvResponse);
                card.addView(btnReply);

                layoutContainer.addView(card);
            } while (cursor.moveToNext());
        } else {
            TextView empty = new TextView(getContext());
            empty.setText("No queries available.");
            empty.setTextSize(16f);
            layoutContainer.addView(empty);
        }

        cursor.close();
    }

    private void showReplyDialog(int userId, int queryId, String question, String oldResponse) {
        EditText edtResponse = new EditText(getContext());
        edtResponse.setHint("Enter your response");
        edtResponse.setText(oldResponse != null ? oldResponse : "");

        new AlertDialog.Builder(getContext())
                .setTitle("Respond to Query")
                .setMessage("Query: " + question)
                .setView(edtResponse)
                .setPositiveButton("Send", (dialog, which) -> {
                    String newReply = edtResponse.getText().toString().trim();
                    if (!newReply.isEmpty()) {
                        boolean updated = dbHelper.respondToQuery(queryId, newReply);
                        Toast.makeText(getContext(), updated ? "Reply sent" : "Error sending reply", Toast.LENGTH_SHORT).show();
                        loadQueries();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
