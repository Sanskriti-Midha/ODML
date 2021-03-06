package com.example.sanskriti.odml.Faculty;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sanskriti.odml.Authentication.Login;
import com.example.sanskriti.odml.R;

public class FacultyDashboard extends AppCompatActivity {

    private TextView mail;
    private Intent intent;
    private String email = "";
    private String TAG = "FacultyDashboard";
    private androidx.appcompat.widget.Toolbar facultyToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View contentView = inflater.inflate(R.layout.activity_faculty_dashboard, null, false);
//        mDrawerLayout.addView(contentView, 0);
        facultyToolBar = findViewById(R.id.facultyToolBar);
        facultyToolBar.inflateMenu(R.menu.menu_faculty);

        intent = getIntent();
        email = intent.getStringExtra("email");

        facultyToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.approveOdOption:
                    {
                        Intent i = new Intent(getApplicationContext(), ApproveOD.class);
                        i.putExtra("email",email);
                        startActivity(i);
                        return true;
                    }
                    case R.id.showApprovedOdsOption:
                    {
                        Intent i = new Intent(getApplicationContext(), ShowApprovedOds.class);
                        i.putExtra("email",email);
                        startActivity(i);
                        return true;
                    }
                    case R.id.logoutFacultyOption:
                    {
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        startActivity(i);
                        return true;
                    }
                    default :
                    {
                        return false;
                    }
                }
            }
        });
        mail = findViewById(R.id.email_faculty_display);

        mail.setText(email);

    }
    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}
