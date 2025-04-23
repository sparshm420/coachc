package com.example.cc;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeActivity extends AppCompatActivity {

    ImageView imgQRCode;
    TextView tvLearnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        imgQRCode = findViewById(R.id.imgQRCode);
        tvLearnerId = findViewById(R.id.tvLearnerId);

        String learnerId = String.valueOf(SessionManager.getUserId(this)); // or use email
        tvLearnerId.setText("Learner ID: " + learnerId);

        generateQRCode(learnerId);
    }

    private void generateQRCode(String data) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
            imgQRCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
