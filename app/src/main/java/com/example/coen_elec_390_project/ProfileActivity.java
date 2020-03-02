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

public class ProfileActivity extends AppCompatActivity {
    EditText name, lastname, gender, height, weight;
    Button save, edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpBottomNavigatioNView();

        name = findViewById(R.id.name);
        lastname = findViewById(R.id.lastname);
        gender = findViewById(R.id.gender);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        save = findViewById(R.id.save);
        edit = findViewById(R.id.edit);

        name.setEnabled(false);
        lastname.setEnabled(false);
        gender.setEnabled(false);
        height.setEnabled(false);
        weight.setEnabled(false);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(false);
                lastname.setEnabled(false);
                gender.setEnabled(false);
                height.setEnabled(false);
                weight.setEnabled(false);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                lastname.setEnabled(true);
                gender.setEnabled(true);
                height.setEnabled(true);
                weight.setEnabled(true);
            }
        });
    }

    private void setUpBottomNavigatioNView() {
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
                }

                return false;
            }
        });
    }
}
