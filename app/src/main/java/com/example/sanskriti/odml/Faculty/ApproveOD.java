package com.example.sanskriti.odml.Faculty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.sanskriti.odml.Stuff.Constants;
import com.example.sanskriti.odml.Stuff.MySingleton;
import com.example.sanskriti.odml.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApproveOD extends AppCompatActivity {

    private ListView od_list;
    private String info;
    private String[] ODsplit;
    private String[] infoSplit;
    private String res = "";
    private ArrayList<String> names_od;
    private String TAG = "ApproveOD";
    private TextView goHomeTextView;
    private String email;
    private Intent getEmail;
    private String facultyEmail;
    private ArrayList<String> odDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_od);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        od_list = findViewById(R.id.od_list);
        names_od = new ArrayList<>();
        getEmail = getIntent();
        odDetails = new ArrayList<>();
        facultyEmail = getEmail.getStringExtra("email");

        goHomeTextView = findViewById(R.id.goHomeTextView_ApproveOD);

        goHomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FacultyDashboard.class);
                i.putExtra("email",facultyEmail);
                startActivity(i);
            }
        });
        getDetails();

        od_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String n = parent.getItemAtPosition(position).toString().trim();
                final AlertDialog.Builder dialog = new AlertDialog.Builder(ApproveOD.this);
                dialog.setMessage("Approve OD?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Approve button clicked");
                        Approve(n);
                        names_od.remove(position); //Removing this od request from array
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_list_item_1, names_od);

                        od_list.setAdapter(adapter); //Updating listview
                        Log.d(TAG, "Listview updated after approval");
                        Toast.makeText(ApproveOD.this, "OD approved.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "OD approved successful.");
                        dialog.dismiss();
                    }
                });
                dialog.setNeutralButton("Show details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Show details button clicked");
                        getDetailsOfOD(n);
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Decline button clicked");
                        declineOD(n);
                        names_od.remove(position); //Removing this od request from array
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_list_item_1, names_od);

                        od_list.setAdapter(adapter); //Updating listview
                        Log.d(TAG, "OD declined successful, updated listview");
                        dialog.dismiss();
                    }
                });
                dialog.show();
//                if(DetailsReceived == 1)
//                {
//                    AlertDialog.Builder detailsReceived = new AlertDialog.Builder(ApproveOD.this);
//                    detailsReceived.setCancelable(false);
//                    detailsReceived.setTitle("OD Details");
//                    detailsReceived.setMessage("From: "+odDetails.get(0)+"\nTo: "+odDetails.get(0));
//                    detailsReceived.setPositiveButton("Visual Proof", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(odDetails.get(3)));
//                            startActivity(browserIntent);
//                        }
//                    });
//                    detailsReceived.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
//                    detailsReceived.show();
//                }
            }
        });
    }
    private void getDetailsOfOD(final String email)
    {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.APPROVE_CHECK_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d("ApproveOD - ", "Response from OD DETAILS : " + response);
                info = response;
                ODsplit = info.split(",");

                    odDetails.add(ODsplit[2]);
                    odDetails.add(ODsplit[3]);
                    odDetails.add(ODsplit[4]);

                AlertDialog.Builder detailsReceived = new AlertDialog.Builder(ApproveOD.this);
                detailsReceived.setCancelable(false);
                detailsReceived.setTitle("OD Details");
                detailsReceived.setMessage("From: "+odDetails.get(0)+"\nTo: "+odDetails.get(1));
                detailsReceived.setPositiveButton("Visual Proof", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(odDetails.get(2)));
                        startActivity(browserIntent);
                    }
                });
                detailsReceived.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                detailsReceived.show();
                    Log.d(TAG, "Od details retrieved - "+ODsplit[2]+","+ODsplit[3]+","+ODsplit[4]);

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
                params.put("email", email);
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
    private void declineOD(final String n)
    {
        StringRequest request = new StringRequest(Request.Method.POST,Constants.DECLINE_OD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Response from DECLINE OD : " + response);

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params  = new HashMap<String,String>();
                params.put("rollnumber", n);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
    private void getDetails(){
        StringRequest request = new StringRequest(Request.Method.POST,Constants.APPROVE_CHECK_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d("ApproveOD - getDetails", "Response from GET DETAILS : " + response);
                info = response;
                ODsplit = info.split("\n");

                for(int i=0; i<ODsplit.length; i++)
                {
                    String[] temp = ODsplit[i].split(",");
                    names_od.add(temp[1]);
                    Log.d(TAG, "Getting ods - roll number : "+temp[1]);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, names_od);

                od_list.setAdapter(adapter);
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
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
    private void Approve(final String n){
        StringRequest request = new StringRequest(Request.Method.POST,Constants.UPDATE_OD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Response from APPROVE OD : " + response);
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params  = new HashMap<String,String>();
                params.put("rollnumber", n);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }
    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}
