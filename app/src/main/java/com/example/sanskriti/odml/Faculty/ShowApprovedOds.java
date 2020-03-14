package com.example.sanskriti.odml;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowApprovedOds extends AppCompatActivity {

    private Intent getEmail;
    private String facultyEmail;
    private TextView goHomeTextView;
    private ListView showOdsListView;
    private EditText dateEntryEditText;
    private Button searchOdsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_approved_ods);

        getEmail = getIntent();
        facultyEmail = getEmail.getStringExtra("email"); //Faculty email received
        showOdsListView = findViewById(R.id.showOdsListView_Faculty);
        dateEntryEditText = findViewById(R.id.dateEntryEditText);
        searchOdsBtn = findViewById(R.id.searchOdsBtn_Faculty);
        goHomeTextView = findViewById(R.id.goHomeTextView_ShowApprovedOds);  //Sending back faculty email for displaying in dashboard
        goHomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FacultyDashboard.class);
                i.putExtra("email",facultyEmail);
                startActivity(i);
            }
        });

        searchOdsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateEntryEditText.getText()!=null)
                {
                    if(dateEntryEditText.getText().toString().trim().matches("\\d{2}\\/\\d{2}\\/\\d{4}"))
                    {
                        getApprovedOds();
                    }
                    else
                    {
                        Toast.makeText(ShowApprovedOds.this, "Wrong format, try again.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(ShowApprovedOds.this, "Please entre date to search.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getApprovedOds(){

    }
}
