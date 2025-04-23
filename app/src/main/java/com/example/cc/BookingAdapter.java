package com.example.cc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class BookingAdapter extends ArrayAdapter<Booking> {

    private final Context context;
    private final List<Booking> bookingList;
    private final DatabaseHelper dbHelper;

    public BookingAdapter(Context context, List<Booking> bookings, DatabaseHelper dbHelper) {
        super(context, 0, bookings);
        this.context = context;
        this.bookingList = bookings;
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Booking booking = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvClassName);
        TextView tvTime = convertView.findViewById(R.id.tvTime);
        TextView tvCategory = convertView.findViewById(R.id.tvCategory);
        Button btnCancel = convertView.findViewById(R.id.btnCancel);

        tvName.setText(booking.className);
        tvTime.setText("Time: " + booking.time);
        tvCategory.setText("Category: " + booking.category);

        btnCancel.setOnClickListener(v -> {
            boolean success = dbHelper.deleteBooking(booking.id);
            if (success) {
                bookingList.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Booking cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
