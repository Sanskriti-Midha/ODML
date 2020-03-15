package com.example.sanskriti.odml.Student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
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
    private TextView goHomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_status);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        intent = getIntent();
        email = intent.getStringExtra("email");
        details = new HashMap<>();
        mylistview = findViewById(R.id.od_list);
        goHomeTextView = findViewById(R.id.goHomeTextView_CheckStatus);

        goHomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StudentDashboard.class);
                i.putExtra("email", email);
                startActivity(i);
            }
        });


        System.out.println("This is the email from intent - "+email);

        getDetails();

        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                checkApproval(position);
            }
        });

    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
    private void checkApproval(final int listviewPos)
    {
        Log.d(TAG, "checkApproval() called");
        final int[] val = new int[1];
        StringRequest request = new StringRequest(Request.Method.POST, Constants.CHECK_APPROVE_STATUS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d("CheckStatus", "Response from APPROVE STATUS : " + response);
                res = response;

                if(!res.equals("ERROR OCCURED"))
                {
                    ODapproved = Integer.parseInt(res);

                    if(ODapproved == 1) //OD approved case.
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CheckStatus.this);
                        dialog.setCancelable(false);
                        dialog.setMessage("OD Approved, close OD.");
                        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userAcknowledge(1); //User has been notified of approval
                                Log.d(TAG, "Removed OD details from the database");
                                names.remove(listviewPos);  //Removing the data from the listview data array
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, names);

                                mylistview.setAdapter(adapter); //Update listview

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
                                userAcknowledge(0); //User has been notified of decline.
                                Log.d(TAG, "Removed OD details from the database");
                                //Removing the data from the listview data array
                                names.remove(listviewPos);
                                //Update listview
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, names);

                                mylistview.setAdapter(adapter);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
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

                params.put("rollnumber",email);
                //params.put("password",password);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

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

    private void userAcknowledge(final int approved)
    {
        StringRequest request = new StringRequest(Request.Method.POST,Constants.STUDENT_ACKNOWLEDGEMENT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                Log.d("CheckStatus", "Response from DELETE OD : " + response);
                res = response;
                if(res.equals("Deleted"))
                {
                    Log.d(TAG, "OD deleted.");
                }
                else
                {
                    Log.d(TAG, "OD delete failed.");
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
                params.put("flag",""+approved);
                //params.put("password",password);

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
                Log.d("CheckStatus", "Response from GET DETAILS : " + response);

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
