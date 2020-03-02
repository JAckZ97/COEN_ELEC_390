package com.example.coen_elec_390_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpBottomNavigatioNView();
    }

    private void setUpBottomNavigatioNView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        break;

                    case R.id.profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;

                    case R.id.statistics:
                        startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
                        break;
                }

                return false;
            }
        });
    }
}
