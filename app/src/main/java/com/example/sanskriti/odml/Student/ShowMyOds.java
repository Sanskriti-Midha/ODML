package com.example.sanskriti.odml.Student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.sanskriti.odml.R;
import com.example.sanskriti.odml.Stuff.Constants;
import com.example.sanskriti.odml.Stuff.MySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowMyOds extends AppCompatActivity {

    private Intent intent;
    private String email;
    private TextView goHomeTextView;
    private Button searchOdsBtn_Student;
    private ListView showOdsListView_Student;
    private String info;
    private String[] ODsplit;
    private ArrayList<String> names;
    private String TAG = "ShowMyOds";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_ods);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        StringRequest request = new StringRequest(Request.Method.POST, Constants.SHOW_APPROVED_ODS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Response from GetApprovedOdsDetails : " + response);

                if(!response.equals("ERROR OCCURED"))
                {
                    info = response;
                    ODsplit = info.split("\n");
                    names = new ArrayList<>();
                    for(int i=0; i<ODsplit.length; i++)
                    {
                        String[] temp = ODsplit[i].split(",");
                        names.add("From :"+temp[2]+" , To: "+temp[3]);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, names);

                    showOdsListView_Student.setAdapter(adapter);
                }
                else
                {
                    Toast.makeText(ShowMyOds.this, "No approved ODs", Toast.LENGTH_SHORT).show();
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

                params.put("rollnumber",email);
                //params.put("password",password);

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }
}
