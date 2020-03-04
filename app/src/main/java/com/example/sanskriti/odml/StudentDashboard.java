package com.example.sanskriti.odml;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StudentDashboard extends Student_BaseActivity {

    private String info;
    private String[] infoArray;
    private String TAG = "Student Dashboard";
    private Intent intent;
    public String email;
    private TextView name;
    private TextView regno;
    private TextView mail;
    private TextView number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_student_dashboard, null, false);
        mDrawerLayout.addView(contentView, 0);

        name = findViewById(R.id.student_name);
        regno = findViewById(R.id.student_regnum);
        mail = findViewById(R.id.student_email);
        number = findViewById(R.id.student_phNo);

        intent = getIntent();
        email = intent.getStringExtra("email");
        Log.d(TAG, "This is the mail recevied from login - "+email);
        StringRequest request = new StringRequest(Request.Method.POST,Constants.FETCH_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                System.out.println("Response is : " + response);

                info = response;
                infoArray = info.split(",");
                Log.d(TAG, "This is the info "+response);

                name.setText(infoArray[0]);
                regno.setText(infoArray[1]);
                mail.setText(infoArray[2]);
                number.setText(infoArray[3]);

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params  = new HashMap<String,String>();

                params.put("email",email);
                //params.put("password",password);

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
