package com.example.sanskriti.odml;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.Boolean.FALSE;

public class AppForm extends BaseActivity {

    private final Calendar myCalendar = Calendar.getInstance();
    private ToggleButton ml,od ;
    private RadioGroup leave_type;
    private EditText fromDate, toDate, name_EditText, regnum_EditText;
    private int check = -1;
    private DatePickerDialog.OnDateSetListener date;
    private Button attachCert;
    private Button apply_Btn;
    private Intent intent;
    private String link,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_app_form, null, false);
        mDrawerLayout.addView(contentView, 0);

        intent = getIntent();
        link = intent.getStringExtra("link");
        email = intent.getStringExtra("email");

        ml = findViewById(R.id.ML);
        od = findViewById(R.id.OD);
        name_EditText = findViewById(R.id.name_edit_text);
        regnum_EditText = findViewById(R.id.regnum_edit_text);

        fromDate = findViewById(R.id.from_date);
        toDate = findViewById(R.id.to_date);
        apply_Btn = findViewById(R.id.apply_Btn);
        leave_type = findViewById(R.id.leave_type);

        ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                od.setChecked(FALSE);
            }
        });
        od.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ml.setChecked(FALSE);
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        fromDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                check = 0;
                new DatePickerDialog(AppForm.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                //updateLabel(fromDate);
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                check = 1;
                new DatePickerDialog(AppForm.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                //updateLabel(toDate);
            }
        });

        attachCert = findViewById(R.id.attachCertificate);
        attachCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AppForm.this, GetCertificate.class);
                startActivity(i);
            }
        });

        apply_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Log.d("AppForm", "Apply button clicked");
                    sendValue();
                Toast.makeText(AppForm.this, "Values saved", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendValue()
    {
        Log.d("AppForm", "Reached sendValue()");
        StringRequest request = new StringRequest(Request.Method.POST,Constants.WRITE_DETAILS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("Response is : " + response.toString());

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

                params.put("name",name_EditText.getText().toString().trim());
                params.put("rollnumber",regnum_EditText.getText().toString().trim());
                params.put("from_date",fromDate.getText().toString().trim());
                params.put("to_date",toDate.getText().toString().trim());
                params.put("certificate_link",link);

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (check == 0)
            fromDate.setText(sdf.format(myCalendar.getTime()));
        else if (check == 1)
            toDate.setText(sdf.format(myCalendar.getTime()));
    }
}

