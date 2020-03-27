package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.coen_elec_390_project.Database.DatabaseHelper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;
import com.example.coen_elec_390_project.DialogFragment.DateSelectionFragment;
import com.example.coen_elec_390_project.Model.Statistic;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG = "StatisticsActivity";
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    String email;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    DatabaseHelper databaseHelper;
    ListView statisticsListView;
    User user;
    DataPoint[] data;
    FloatingActionButton dateSelection;
    String startDate = "", endDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setUpBottomNavigationView();

        databaseHelper = new DatabaseHelper(this);
        statisticsListView = findViewById(R.id.listview);
        graph = findViewById(R.id.graph);
        email = getIntent().getStringExtra("email");
        user = databaseHelper.getUser(email);
        dateSelection = findViewById(R.id.dateSelection);

        loadListView();
        setUpGraph();

        dateSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelectionFragment dateSelectionFragment = new DateSelectionFragment();
                dateSelectionFragment.show(getSupportFragmentManager(), "DateSelectionFragment");
            }
        });

        //TO TEST THE GRAPH WHEN THERE IS A LOT OF POINTS
        /**Calendar calendar = Calendar.getInstance();
        Random randomobj = new Random();
        for(int i = 1; i < 26; i++) {
            calendar.set(2020, 03, i);
            String str_date = calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
            databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, randomobj.nextDouble()*100, randomobj.nextDouble()*100, randomobj.nextDouble()*100));
            Log.e("Tag","<STAT> "+randomobj.nextDouble());
        }*/
    }

    /**Function to set up the GraphView*/
    public void setUpGraph() {
        if(data.length >= 2) {
            series = new LineGraphSeries<>(data);
            series.setDrawDataPoints(true);
            graph.addSeries(series);

            graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
            graph.getGridLabelRenderer().setVerticalAxisTitle("Performance index");
            graph.getGridLabelRenderer().setPadding(50);
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScalable(true);
            graph.getViewport().setScrollableY(true);
            graph.getViewport().setScalableY(true);
            graph.getGridLabelRenderer().setHorizontalLabelsAngle(45);
            graph.getViewport().setXAxisBoundsManual(false);
            graph.getGridLabelRenderer().setHumanRounding(false);

            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if(isValueX) {
                        Date date = new Date((long) value);
                        date.setYear(date.getYear()-1900);
                        return simpleDateFormat.format(date);
                    }

                    else {
                        return super.formatLabel(value, isValueX);
                    }
                }
            });
        }
    }

    public void receiveStartEndDate(String start, String end) {
        startDate = start;
        endDate = end;
    }

    public void loadListView() {
        /**Both start and end dates are not selected*/
        if(startDate.equals("") && endDate.equals("")) {
            executeQuery(0);
        }

        /**Only start date is selected*/
        else if(!startDate.equals("") && endDate.equals("")) {
            executeQuery(1);
        }

        /**Only end date is selected*/
        else if(startDate.equals("") && !endDate.equals("")) {
            executeQuery(2);
        }

        /**Both start and end dates are selected*/
        else {
            executeQuery(3);
        }
    }

    private void executeQuery(int type) {
        ArrayList<String> statisticsListText = new ArrayList<>();
        List<Statistic> statistics;

        switch (type) {
            case 0:
                statistics = databaseHelper.getStatisticsByUser(user.getId());
                break;

            case 1:
                statistics = databaseHelper.getStatisticsAfterStartDate(user.getId(), /**"2020/3/9"*/startDate);
                break;

            case 2:
                statistics = databaseHelper.getStatisticsBeforeEndDate(user.getId(), /**"2020/3/9"*/endDate);
                break;

            case 3:
                statistics = databaseHelper.getStatisticsBetweenStartAndEndDates(user.getId(), /**"2020/3/9", "2020/3/12"*/startDate, endDate);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        data = new DataPoint[statistics.size()];

        for(int i = 0; i < statistics.size(); i++) {
            String temp = "";
            temp += "Statistic's id: " + statistics.get(i).getCounter_id() + "\n";
            temp += "Date: " + statistics.get(i).getDate() + "\n";
            temp += "Performance index: " + Math.round((statistics.get(i).getPerformance_index())*100.0)/100.0 + "\n";
            temp += "Calories burned: " + Math.round(statistics.get(i).getCalories()) + "\n";
            temp += "Speed: " + Math.round(statistics.get(i).getSpeed()*100.0)/100.0 + " km/h";

            String str_date = statistics.get(i).getDate();
            String date_component[] = str_date.split("/");

            Date date = new Date(Integer.parseInt(date_component[0]), Integer.parseInt(date_component[1]) - 1, Integer.parseInt(date_component[2]));
            Long date_long = date.getTime();

            DataPoint dataPoint = new DataPoint(date_long, statistics.get(i).getPerformance_index());

            data[i] = dataPoint;
            statisticsListText.add(temp);
        }

        Collections.reverse(statisticsListText);

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statisticsListText);
        statisticsListView.setAdapter(arrayAdapter);
    }

    private void setUpBottomNavigationView() {
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;

                switch (menuItem.getItemId()){
                    case R.id.map:
                        intent = new Intent(new Intent(StatisticsActivity.this, MapsActivity.class));
                        intent.putExtra("email", email);
                        startActivity(intent);
                        break;

                    case R.id.home:
                        intent = new Intent(new Intent(StatisticsActivity.this, MainActivity.class));
                        intent.putExtra("email", email);
                        startActivity(intent);
                        break;

                    case R.id.statistics:
                        break;

                    case R.id.profile:
                        if (user.getEmail()== null) {
                            // User is signed in
                            startActivity(new Intent(StatisticsActivity.this, StartActivity.class));
                            break;
                        } else {
                            // No user is signed in
                            intent = new Intent(new Intent(StatisticsActivity.this, ProfileActivity.class));
                            intent.putExtra("email", email);
                            startActivity(intent);
                            break;
                        }

                    case R.id.logout:
                        startActivity(new Intent(StatisticsActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }

                return true;
            }
        });
    }
}

