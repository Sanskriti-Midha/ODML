package com.example.sanskriti.odml.Faculty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.sanskriti.odml.R;
import com.example.sanskriti.odml.Student.ShowMyOds;
import com.example.sanskriti.odml.Stuff.Constants;
import com.example.sanskriti.odml.Stuff.MySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowApprovedOds extends AppCompatActivity {

    private Intent getEmail;
    private String facultyEmail;
    private TextView goHomeTextView;
    private ListView showOdsListView;
    private EditText dateEntryEditText;
    private Button searchOdsBtn;
    private String TAG = "ShowApprovedOds";
    private String info;
    private String[] ODsplit;
    private ArrayList<String> roll, fromdate, todate,type;
    private ArrayList<String> DisplayOds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_approved_ods);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getEmail = getIntent();
        roll = new ArrayList<>();
        fromdate = new ArrayList<>();
        todate = new ArrayList<>();
        type = new ArrayList<>();
        DisplayOds = new ArrayList<>();

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
        DisplayOds.clear();
        Log.d(TAG, "getApprovedOds called");
        StringRequest request = new StringRequest(Request.Method.POST, Constants.FACULTY_GET_OD_TRIAL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Response from GetApprovedOdsDetails : " + response);

                if(!response.equals("No data retrieved"))
                {
                    info = response;
                    ODsplit = info.split("\n");
                    for(int i=0; i<ODsplit.length; i++)
                    {
                        String[] temp = ODsplit[i].split(",");
                        roll.add(temp[1]);
                        fromdate.add(temp[2]);
                        todate.add(temp[3]);
                        type.add(temp[6]);
                        DisplayOds.add("Roll: "+temp[1]+", Type: "+temp[6]+", From: "+temp[2]+", To: "+temp[3]);

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, DisplayOds);

                    showOdsListView.setAdapter(adapter);
                }
                else
                {
                    Toast.makeText(ShowApprovedOds.this, "No approved ODs for the given date", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                System.out.println("Error is " + error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams()    throws AuthFailureError {
                Map <String,String> params  = new HashMap<String,String>();

                params.put("date",dateEntryEditText.getText().toString());

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }
}
