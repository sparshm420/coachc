package com.example.cc;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ResolveQueriesFragment extends Fragment {

    private LinearLayout layoutQueries;
    private DatabaseHelper dbHelper;

    public ResolveQueriesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resolve_queries, container, false);
        layoutQueries = view.findViewById(R.id.layoutQueries);
        dbHelper = new DatabaseHelper(getContext());

        loadAllQueries();
        return view;
    }

    private void loadAllQueries() {
        layoutQueries.removeAllViews();
        Cursor cursor = dbHelper.getAllPendingQueries();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int queryId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String response = cursor.getString(cursor.getColumnIndexOrThrow("response"));

                TextView queryView = new TextView(getContext());
                queryView.setText("User #" + userId + ":\n" + message +
                        (response != null ? ("\n\n🟩 Response:\n" + response) : "\n\n❗ No Response Yet"));
                queryView.setPadding(24, 16, 24, 16);
                queryView.setBackgroundResource(R.drawable.booking_card_background);
                queryView.setTextColor(getResources().getColor(android.R.color.black));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 32);
                queryView.setLayoutParams(params);

                if (response == null) {
                    queryView.setOnClickListener(v -> showResponseDialog(queryId, message));
                }

                layoutQueries.addView(queryView);

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void showResponseDialog(int queryId, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Respond to Query");

        final EditText input = new EditText(getContext());
        input.setHint("Enter your response...");
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String response = input.getText().toString().trim();
            if (!response.isEmpty()) {
                boolean sent = dbHelper.respondToQuery(queryId, response);
                if (sent) {
                    Toast.makeText(getContext(), "Response sent", Toast.LENGTH_SHORT).show();
                    loadAllQueries();
                } else {
                    Toast.makeText(getContext(), "Failed to send", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
}
