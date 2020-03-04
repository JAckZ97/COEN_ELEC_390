package com.example.coen_elec_390_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    EditText name, gender, height, weight;
    Button save, edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpBottomNavigationView();

        name = findViewById(R.id.profileName);
        gender = findViewById(R.id.profileGender);
        height = findViewById(R.id.profileHeight);
        weight = findViewById(R.id.profileWeight);
        save = findViewById(R.id.save);
        edit = findViewById(R.id.edit);

        name.setEnabled(false);
        gender.setEnabled(false);
        height.setEnabled(false);
        weight.setEnabled(false);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(false);
                gender.setEnabled(false);
                height.setEnabled(false);
                weight.setEnabled(false);

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                gender.setEnabled(true);
                height.setEnabled(true);
                weight.setEnabled(true);
            }
        });
    }

    private void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        break;

                    case R.id.profile:
                        break;

                    case R.id.statistics:
                        startActivity(new Intent(ProfileActivity.this, StatisticsActivity.class));
                        break;


                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }

                return false;
            }
        });
    }
}
