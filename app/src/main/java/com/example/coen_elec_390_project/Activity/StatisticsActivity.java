package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.coen_elec_390_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity {
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setUpBottomNavigationView();

        graph = findViewById(R.id.graph);
        series = new LineGraphSeries<>(getDataPoint());
        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX) {
                    return simpleDateFormat.format(new Date((long) value));
                }

                else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    private DataPoint[] getDataPoint() {
        DataPoint[] dataPoints = new DataPoint[] {
            new DataPoint(new Date(1577914796), 180),
            new DataPoint(new Date(1578001196), 185),
            new DataPoint(new Date().getTime(), 178),
            new DataPoint(new Date().getTime(), 190),
            new DataPoint(new Date().getTime(), 172),
            new DataPoint(new Date().getTime(), 170)
        };

        return dataPoints;
    }

    private void setUpBottomNavigationView() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(StatisticsActivity.this, MainActivity.class));
                        break;

                    case R.id.profile:
                        if (user == null) {
                            // User is signed in
                            startActivity(new Intent(StatisticsActivity.this, StartActivity.class));
                            break;
                        } else {
                            // No user is signed in
                            startActivity(new Intent(StatisticsActivity.this, ProfileActivity.class));
                            break;
                        }

                    case R.id.statistics:
                        break;


                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(StatisticsActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }

                return false;
            }
        });
    }
}

