package com.example.sanskriti.odml.Student;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.sanskriti.odml.Stuff.Constants;
import com.example.sanskriti.odml.Stuff.MySingleton;
import com.example.sanskriti.odml.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.Boolean.FALSE;

public class AppForm extends AppCompatActivity {

    private final Calendar myCalendar = Calendar.getInstance();
    private ToggleButton ml,od ;
    private int ml_flag=0, od_flag=0;
    private RadioGroup leave_type;
    private EditText fromDate, toDate;
    private TextView name_EditText, regnum_EditText;
    private int check = -1;
    private DatePickerDialog.OnDateSetListener date;
    private Button attachCert;
    private Button apply_Btn;
    private Intent intent;
    private String link,email;
    private TextView goHomeTextView;
    private String TAG = "AppForm";
    private Button select_certificate;
    private Button upload_certificate;
    private ProgressBar progress_bar;
    private EditText file_name;
    private String image_download_uri;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Uri filePath;
    private String type;
    private int from_date_flag=0, to_date_flag=0, filename_flag=0, upload_flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_form);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        intent = getIntent();
        email = intent.getStringExtra("email");

        ml = findViewById(R.id.ML);
        od = findViewById(R.id.OD);
        file_name=findViewById(R.id.file_name_EditText);
        name_EditText = findViewById(R.id.name_edit_text);
        regnum_EditText = findViewById(R.id.regnum_edit_text);
        goHomeTextView = findViewById(R.id.goHomeTextView_AppForm);

        fromDate = findViewById(R.id.from_date);
        toDate = findViewById(R.id.to_date);
        apply_Btn = findViewById(R.id.apply_Btn);
        leave_type = findViewById(R.id.leave_type);

        select_certificate = findViewById(R.id.select_certificate_btn);
        upload_certificate = findViewById(R.id.upload_certificate_btn);

        progress_bar = findViewById(R.id.progress_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Uploads");

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

        getValue();


        select_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        ml.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Log.d(TAG, "ML Checked");
                    ml_flag=1;
                    od_flag=0;
                }
            }
        });
        od.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Log.d(TAG, "OD Checked");
                    ml_flag=0;
                    od_flag=1;
                }
            }
        });
        upload_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(AppForm.this);
                builder.setMessage("Are you sure you want to upload this image?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(mUploadTask!=null && mUploadTask.isInProgress())
                        {
                            Toast.makeText(AppForm.this, "Cant upload now, upload pending", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            uploadImage();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        goHomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StudentDashboard.class);
                i.putExtra("email", email);
                startActivity(i);
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
                if(fromDate.getText()!=null)
                {
                    from_date_flag=1;
                    Log.d(TAG, "from_date_flag = 1 ,"+fromDate.getText().toString() );
                }
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
                //updateLabel(fromDate);
                if(toDate.getText()!=null)
                {
                    to_date_flag=1;
                    Log.d(TAG, "to_date_flag = 1 ,"+toDate.getText().toString() );
                }
            }
        });


        apply_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(file_name.getText()!=null)
                    {
                        filename_flag=1;
                        Log.d(TAG, "file_name_flag = 1 ,"+file_name.getText().toString() );
                    }
                    if(filename_flag==1 && upload_flag==1 && from_date_flag==1 && to_date_flag==1 && (ml_flag==1||od_flag==1))
                    {
                        Log.d("AppForm", "Apply button clicked");
                        if(ml_flag==1 && od_flag==0)
                            sendValue("ml");
                        else if(ml_flag==0 && od_flag==1)
                            sendValue("od");
                        Toast.makeText(AppForm.this, "Values saved", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(AppForm.this, "Provide all details needed for the od form", Toast.LENGTH_SHORT).show();
                    }
            }
        });



    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage() {
        if(filePath != null)
        {
            /*final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.show();*/
            final StorageReference reference = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(filePath));

            mUploadTask = reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //progressDialog.dismiss();
                    Toast.makeText(AppForm.this, "Image Uploaded", Toast.LENGTH_SHORT);

                    //saving img name and download url to database
                    final HashMap<String,String> imgDetails = new HashMap<>();
                    imgDetails.put("Filename",file_name.getText().toString().trim());

                    //image download url
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "onSuccess: uri= "+ uri.toString());
                            image_download_uri = uri.toString();
                            imgDetails.put("Download",image_download_uri);

                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(imgDetails);
                        }
                    });
                    upload_flag=1;
                    Log.d(TAG, "File uploaded successfuly, upload_flag = 1");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AppForm.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    //progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    progress_bar.setProgress((int) progress);
                }
            });
        }
        else
        {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void getImage()
    {
        Intent getPic = new Intent();
        getPic.setType("image/*"); //format to get all types of images for choosing
        getPic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(getPic,"Choose Image"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            filePath = data.getData();
        }
    }

    private void getValue()
    {
        Log.d("AppForm", "Reached getValue()");
        StringRequest request = new StringRequest(Request.Method.POST, Constants.FETCH_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("Response is : " + response.toString());

                String[] temp = response.split(",");
                name_EditText.setText(temp[0]);
                Log.d(TAG, "Setting name with - "+temp[0]);
                regnum_EditText.setText(temp[1]);
                Log.d(TAG, "Setting register number with - "+temp[1]);

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

    private void sendValue(final String type)
    {
        Log.d("AppForm", "Reached sendValue(), type - "+type);
        StringRequest request = new StringRequest(Request.Method.POST,Constants.WRITE_DETAILS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("Response is : " + response.toString());

                if(response.equals("Record added successfully"))
                {
                    Intent i = new Intent(getApplicationContext(), StudentDashboard.class);
                    i.putExtra("email", email);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(AppForm.this, "Record insertion failed.", Toast.LENGTH_SHORT).show();
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

                params.put("name",name_EditText.getText().toString().trim());
                params.put("rollnumber",regnum_EditText.getText().toString().trim());
                params.put("from_date",fromDate.getText().toString().trim());
                params.put("to_date",toDate.getText().toString().trim());
                params.put("certificate_link",image_download_uri);
                params.put("type",type);


                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (check == 0)
            fromDate.setText(sdf.format(myCalendar.getTime()));
        else if (check == 1)
            toDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}

