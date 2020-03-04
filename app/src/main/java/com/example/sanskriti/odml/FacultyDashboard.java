package com.example.sanskriti.odml;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class FacultyDashboard extends Faculty_BaseActivity {

    private TextView mail;
    private Intent intent;
    private String email = "";
    private String TAG = "FacultyDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_faculty_dashboard, null, false);
        mDrawerLayout.addView(contentView, 0);

        mail = findViewById(R.id.email_faculty_display);


        mail.setText(email);

    }
}
