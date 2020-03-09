package com.example.coen_elec_390_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    TextView bpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBottomNavigationView();

        bpm = findViewById(R.id.bpm);

        bpm.setText("180 bpm");
        bpm.setBackgroundResource(R.drawable.ic_bpm);
        bpm.setTextColor(getResources().getColor(R.color.colorPrimary));
    }


    private void setUpBottomNavigationView() {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.home:
                            break;

                        case R.id.profile:
                            if (user == null) {
                                // User is signed in
                                startActivity(new Intent(MainActivity.this, StartActivity.class));
                                break;
                            } else {
                                // No user is signed in
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                break;
                            }

                        case R.id.statistics:
                            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
                            break;

                        case R.id.logout:
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            break;
                    }

                    return false;
                }
            });
    }
}
