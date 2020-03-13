package com.example.sanskriti.odml;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.concurrent.TimeUnit;

public class ApproveOD extends AppCompatActivity {

    private ListView od_list;
    private String info;
    private String[] ODsplit;
    private String[] infoSplit;
    private String res = "";
    private ArrayList<String> names_od;
    private String TAG = "ApproveOD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_od);

        od_list = findViewById(R.id.od_list);
        names_od = new ArrayList<>();
        getDetails();

        od_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String n = parent.getItemAtPosition(position).toString().trim();
                AlertDialog.Builder dialog = new AlertDialog.Builder(ApproveOD.this);
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
                        Toast.makeText(ApproveOD.this, "We will show details here", Toast.LENGTH_SHORT).show();
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
            }
        });
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
                params.put("email", n);
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
