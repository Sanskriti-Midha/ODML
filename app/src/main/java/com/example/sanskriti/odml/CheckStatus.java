package com.example.sanskriti.odml;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckStatus extends AppCompatActivity{

    private String TAG = "CheckStatus";
    private Intent intent;
    private String email;
    private String info;
    private String[] ODsplit;
    private ListView mylistview;
    private HashMap<String,String[]> details;
    private ArrayList<String> names;
    private String[] infoSplit;
    private String res ="0";
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
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                int x = checkApproval();

                if(x == 666)
                {
                    Log.d(TAG, "Approval status check failed.");
                }
                else
                {
                    ODapproved = Integer.parseInt(res);
                }


                if(ODapproved == 1) //OD approved case.
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CheckStatus.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("OD Approved, close OD.");
                    dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int x = closeOD(); //Removing OD details from database
                            Log.d(TAG, "Removed OD details from the database");
                            names.remove(position);  //Removing the data from the listview data array
                            mylistview.deferNotifyDataSetChanged(); //Update listview

                            if(x==1)
                            {
                                Toast.makeText(CheckStatus.this, "OD closed successfully.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "OD closed successfully.");
                            }
                            else
                            {
                                Toast.makeText(CheckStatus.this, "OD closed failed.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "OD closed failed.");
                            }

                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else if(ODapproved == 0) //OD pending case.
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CheckStatus.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("Pending..");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else if(ODapproved == -1) //OD rejected case.
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CheckStatus.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("Your OD application was rejected");
                    dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Removing OD details from database
                            int x = closeOD();
                            Log.d(TAG, "Removed OD details from the database");
                            //Removing the data from the listview data array
                            names.remove(position);
                            //Update listview
                            mylistview.deferNotifyDataSetChanged();

                            if(x==1)
                            {
                                Log.d(TAG, "OD closed successfully.");
                            }
                            else
                            {
                                Log.d(TAG, "OD closed failed.");
                            }

                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

            }
        });

    }


    private int checkApproval()
    {
        Log.d(TAG, "checkApproval() called");
        final int[] val = new int[1];
        StringRequest request = new StringRequest(Request.Method.POST,Constants.CHECK_APPROVE_STATUS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d("CheckStatus", "Response is : " + response);
                res = response;
                System.out.println(res);
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

                params.put("rollnumber",email);
                //params.put("password",password);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        if(res.equals("ERROR OCCURED")){
            return 666;
        }
        else
        {
            int temp = Integer.parseInt(res);
            if(temp == 1)
            {
                return 1;
            }
            else if(temp == 0)
            {
                Log.d(TAG, "Returning - "+res);
                return 0;
            }
            else if(temp == -1)
            {
                return -1;
            }
            return 666;
        }
    }

//    private int check(String res) {
//        if(res.equals("ERROR OCCURED")){
//            return 666;
//        }
//        else
//        {
//            int temp = Integer.parseInt(res);
//            if(temp == 1)
//            {
//                return 1;
//            }
//            else if(temp == 0)
//            {
//                return 0;
//            }
//            else if(temp == -1)
//            {
//                return -1;
//            }
//            return 666;
//        }
//    }

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
