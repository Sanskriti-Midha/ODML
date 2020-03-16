package com.example.sanskriti.odml.Authentication;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.sanskriti.odml.Faculty.FacultyDashboard;
import com.example.sanskriti.odml.R;
import com.example.sanskriti.odml.Student.StudentDashboard;
import com.example.sanskriti.odml.Stuff.Constants;
import com.example.sanskriti.odml.Stuff.MySingleton;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button login_b;
    private String email,password;
    private EditText emailEdit,passwordEdit;
    private CheckBox rememberCheckBox;
    private int saveLogin = 0;
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        rememberCheckBox = findViewById(R.id.rememberCheckBox);
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

        //Getting the login details from shared preference
        sp = getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        edit = sp.edit();
        String e = sp.getString("email","NA");
        String p = sp.getString("password","NA");
        String check = sp.getString("remember", "NA");

        //Checking is the user has saved his details
        if(check.equals("true"))
        {
            emailEdit.setText(e);
            passwordEdit.setText(p);
            rememberCheckBox.setChecked(true);
            saveLogin=1;
        }
        else
        {
            emailEdit.setText("");
            passwordEdit.setText("");
            rememberCheckBox.setChecked(false);
        }

        //Choosing between saving/not saving login details
        rememberCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked())
                {
                    edit.putString("remember","true");
                    saveLogin = 1;

                }
                else if(!buttonView.isChecked())
                {
                    edit.putString("remember","false");
                    saveLogin = 0;
                }
            }
        });

    }

    public void login() {

        StringRequest request = new StringRequest(Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response.toString(), Toast.LENGTH_LONG).show();
                System.out.println("Response is : " + response.toString());

                if(response.contains("Login Successful"))
                {
                    if(saveLogin == 1)
                    {
                        edit.putString("email",email);
                        edit.putString("password",password);
                        edit.apply();

                    }
                    else {
                        edit.putString("email","");
                        edit.putString("password","");
                        edit.apply();
                    }
                    Intent i = new Intent(Login.this, StudentDashboard.class);
                    i.putExtra("email",email);
                    startActivity(i);

                }
                else if(response.contains("Faculty"))
                {

                    if(saveLogin == 1)
                    {
                        edit.putString("email",email);
                        edit.putString("password",password);
                        edit.apply();

                    }
                    else {
                        edit.putString("email","");
                        edit.putString("password","");
                        edit.apply();
                    }
                    Intent i = new Intent(Login.this, FacultyDashboard.class);
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
