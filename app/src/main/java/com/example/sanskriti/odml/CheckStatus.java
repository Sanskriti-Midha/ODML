package com.example.sanskriti.odml;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckStatus extends AppCompatActivity {

    private String TAG = "CheckStatus";
    private Intent intent;
    private String email;
    private String info;
    private String[] ODsplit;
    private ListView mylistview;
    private HashMap<String,String[]> details;
    private ArrayList<String> names;
    private String[] infoSplit;
    private String res;
    private int ODapproved = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_status);

        intent = getIntent();
        email = intent.getStringExtra("email");
        details = new HashMap<>();
        mylistview = findViewById(R.id.od_list);

        System.out.println("This is the email from intent - "+email);

        getDetails();

        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int x = checkApproval();
                if(x == -1)
                {
                    Log.d(TAG, "Approval status check failed.");
                }
                else
                {
                    ODapproved = Integer.parseInt(res);
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
                dialog.setCancelable(false);
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                if(ODapproved == 1)
                {
                    dialog.setMessage("OD Approved, close OD.");
                    dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int x = closeOD();
                            if(x==1)
                            {
                                Toast.makeText(CheckStatus.this, "OD closed successfully.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(CheckStatus.this, "OD closed failed.", Toast.LENGTH_SHORT).show();
                            }

                            dialog.dismiss();
                        }
                    });
                }

            }
        });

    }


    private int checkApproval()
    {
        StringRequest request = new StringRequest(Request.Method.POST,Constants.CHECK_APPROVE_STATUS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d("CheckStatus", "Response is : " + response);
                res = response;
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

                params.put("email",email);
                //params.put("password",password);

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        if(res.equals("ERROR OCCURED")){
            return -1;
        }
        else
        {
            int temp = Integer.parseInt(res);
            if(temp == 1)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }
    private int closeOD()
    {

        StringRequest request = new StringRequest(Request.Method.POST,Constants.DELETE_OD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d("CheckStatus", "Response is : " + response);
                res = response;
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

                params.put("email",email);
                //params.put("password",password);

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        if(res.equals("Record updated successfully")){
            return 1;
        }
        else
        {
            return 0;
        }
    }
    private void getDetails(){
        StringRequest request = new StringRequest(Request.Method.POST,Constants.APPROVE_CHECK_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d("CheckStatus", "Response is : " + response);

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

                mylistview.setAdapter(adapter);
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

                params.put("email",email);
                //params.put("password",password);

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
