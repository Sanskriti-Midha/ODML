package com.example.sanskriti.odml.Student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sanskriti.odml.R;

public class ShowMyOds extends AppCompatActivity {

    private Intent intent;
    private String email;
    private TextView goHomeTextView;
    private Button searchOdsBtn_Student;
    private ListView showOdsListView_Student;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_ods);

        intent = getIntent();
        email = intent.getStringExtra("email");
        goHomeTextView = findViewById(R.id.goHomeTextView_ShowMyOds);
        searchOdsBtn_Student = findViewById(R.id.searchOdsBtn_Student);
        showOdsListView_Student = findViewById(R.id.showOdsListView_Student);
        goHomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StudentDashboard.class);
                i.putExtra("email", email);
                startActivity(i);
            }
        });

        searchOdsBtn_Student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetApprovedOdsDetails();
            }
        });
    }

    public void GetApprovedOdsDetails(){


    }
}
