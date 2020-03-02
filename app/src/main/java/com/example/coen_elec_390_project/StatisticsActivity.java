package com.example.coen_elec_390_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
        setUpBottomNavigatioNView();

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

    private void setUpBottomNavigatioNView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(StatisticsActivity.this, MainActivity.class));
                        break;

                    case R.id.profile:
                        startActivity(new Intent(StatisticsActivity.this, ProfileActivity.class));
                        break;

                    case R.id.statistics:
                        break;
                }

                return false;
            }
        });
    }
}

