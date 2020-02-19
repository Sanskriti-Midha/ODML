package com.example.sanskriti.odml;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    Button login_b;
    private String email,password;
    private EditText emailEdit,passwordEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_b = findViewById(R.id.login_button);
        emailEdit = findViewById(R.id.EmailEditText);
        passwordEdit = findViewById(R.id.PasswordEditText);
        login_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEdit.getText().toString().trim();
                password = passwordEdit.getText().toString().trim();
                login();

            }
        });

    }

    private void login() {

        StringRequest request = new StringRequest(Request.Method.POST,Constants.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response.toString(), Toast.LENGTH_LONG).show();
                System.out.println("Response is : " + response.toString());
                if(response.contains("Login Successful"))
                {
                    Intent i = new Intent(login.this, StudentDashboard.class);
                    i.putExtra("email",email);
                    startActivity(i);

                }
                else if(response.contains("Faculty"))
                {

                    Intent i = new Intent(login.this, FacultyDashboard.class);
                    i.putExtra("email",email);
                    startActivity(i);
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params  = new HashMap<String,String>();

                params.put("email",email);
                params.put("password",password);

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }


}
