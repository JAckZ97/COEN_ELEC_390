package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.Statistic;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG = "StatisticsActivity";
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    DatabaseHelper databaseHelper;
    ListView statisticsListView;
    User user;
    DataPoint[] data;
    int min;
    int max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setUpBottomNavigationView();

        databaseHelper = new DatabaseHelper(this);
        statisticsListView = findViewById(R.id.listview);
        graph = findViewById(R.id.graph);
        user = databaseHelper.getUser(MainActivity.global_email);

        /**Insert statistic into the database (TEST, NEED TO REMOVE LATER)*/
        /*Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 03 ,15);
        String str_date = calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
        databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, 14, 150));
        calendar.set(2020, 03 ,16);
        str_date = calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
        databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, 15, 151));
        calendar.set(2020, 03 ,17);
        str_date = calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
        databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, 16, 152));*/

        loadListView();

        series = new LineGraphSeries<>(data);
        series.setDrawDataPoints(true);
        graph.addSeries(series);

        graph.getViewport().setMinX(min);
        graph.getViewport().setMaxX(max);
        graph.getGridLabelRenderer().setNumHorizontalLabels(max - min + 1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Statistic's id");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Performance index");
        graph.getGridLabelRenderer().setPadding(50);

        /**graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX) {
                    return simpleDateFormat.format(new Date((long) value));
                }

                else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });*/
    }

    protected void loadListView() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Statistic> statistics = databaseHelper.getStatisticsByUser(user.getId());
        ArrayList<String> statisticsListText = new ArrayList<>();
        data = new DataPoint[statistics.size()];

        for(int i = 0; i < statistics.size(); i++) {
            String temp = "";
            temp += "Statistic's id: " + statistics.get(i).getId() + "\n";
            temp += "Date: " + statistics.get(i).getDate() + "\n";
            temp += "Performance index: " + statistics.get(i).getPerformance_index() + "\n";
            temp += "BPM: " + statistics.get(i).getBpm();

            /**String str_date = statistics.get(i).getDate();
            String[] date_array = str_date.split("/");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(date_array[0]));
            calendar.set(Calendar.MONTH, Integer.parseInt(date_array[1])-1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_array[2]));
            Date date = calendar.getTime();*/

            if(i == 0)
                min = statistics.get(i).getId();

            if(i == statistics.size()-1)
                max = statistics.get(i).getId();

            DataPoint dataPoint = new DataPoint(statistics.get(i).getId(), statistics.get(i).getPerformance_index());
            data[i] = dataPoint;

            statisticsListText.add(temp);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statisticsListText);
        statisticsListView.setAdapter(arrayAdapter);
    }

    private void setUpBottomNavigationView() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(StatisticsActivity.this, MainActivity.class));
                        break;

                    case R.id.statistics:
                        break;

                    case R.id.profile:
                        startActivity(new Intent(StatisticsActivity.this, ProfileActivity.class));
                        break;


                    case R.id.logout:
                        startActivity(new Intent(StatisticsActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }

                return true;
            }
        });
    }
}

