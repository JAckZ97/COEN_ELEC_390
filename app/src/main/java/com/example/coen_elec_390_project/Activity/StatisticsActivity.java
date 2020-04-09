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

import android.view.MotionEvent;
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

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

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
        boolean tutorial = getIntent().getBooleanExtra("tutorial",false);

        databaseHelper = new DatabaseHelper(this);
        statisticsListView = findViewById(R.id.listview);
        graph = findViewById(R.id.graph);
        dateSelection = findViewById(R.id.dateSelection);
        email = getIntent().getStringExtra("email");
        Log.e("Tag","<STAT> "+email);
        user = databaseHelper.getUser(email);
        if(!tutorial) {

            startDate = "";
            endDate = "";

            loadListView();

            final String maxDate = databaseHelper.getMaxDate(user.getId());
            final String minDate = databaseHelper.getMinDate(user.getId());

            dateSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateSelectionFragment dateSelectionFragment = new DateSelectionFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("maxDate", maxDate);
                    bundle.putString("minDate", minDate);
                    dateSelectionFragment.setArguments(bundle);
                    dateSelectionFragment.show(getSupportFragmentManager(), "DateSelectionFragment");
                }
            });
        }
        else{

            Log.e("TAG", "<STAT> tutorial session");
            loadListView_tutorial();
            tutorialSequence();
        }



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
        graph.removeSeries(series);
        if(data.length >= 2) {
            series = new LineGraphSeries<>(data);
            series.setDrawDataPoints(true);
            series.setColor(R.color.colorBlack);
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
                statistics = databaseHelper.getStatisticsAfterStartDate(user.getId(), startDate);
                break;

            case 2:
                statistics = databaseHelper.getStatisticsBeforeEndDate(user.getId(), endDate);
                break;

            case 3:
                statistics = databaseHelper.getStatisticsBetweenStartAndEndDates(user.getId(), startDate, endDate);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        data = new DataPoint[statistics.size()];

        for(int i = 0; i < statistics.size(); i++) {
            String temp = "";
            temp += "Statistic's id: " + statistics.get(i).getId() + "\n";
            temp += "Date: " + statistics.get(i).getDate() + "\n";
            temp += "Performance index: " + Math.round((statistics.get(i).getPerformance_index())*100.0)/100.0 + "\n";
            temp += "Calories burned: " + Math.round(statistics.get(i).getCalories()) + "\n";
            temp += "Speed: " + Math.round(statistics.get(i).getSpeed()*100.0)/100.0 + " km/h\n";
            temp += "Step Counter: " +statistics.get(i).getStep_counter() + " steps";
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

        setUpGraph();
    }

    private void loadListView_tutorial(){
        ArrayList<String> statisticsListText = new ArrayList<>();
        data = new DataPoint[3];
        Calendar calendar = Calendar.getInstance();


        for(int i = 1; i < 4; i++) {
            calendar.set(2020, 03, i);
            String temp = "";
            temp += "Statistic's id: " + i + "\n";
            temp += "Date: " + calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "\n";
            temp += "Performance index: " + i*2 + "\n";
            temp += "Calories burned: " + 200 + "\n";
            temp += "Speed: " + 9.5 + " km/h\n";
            temp += "Step Counter: 5000 steps";

            DataPoint dataPoint = new DataPoint(i, i*2);
            data[i-1] = dataPoint;
            Collections.reverse(statisticsListText);
            statisticsListText.add(temp);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statisticsListText);
        statisticsListView.setAdapter(arrayAdapter);

        setUpGraph();
    }

    private void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.statistics);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Intent intent;

                switch (menuItem.getItemId()){
                    case R.id.map:
                        Intent intent = new Intent(StatisticsActivity.this, MapsActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        break;

                    case R.id.home:
                        intent = new Intent(new Intent(StatisticsActivity.this, MainActivity.class));
                        intent.putExtra("email", email);
                        startActivity(intent);
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

    public void tutorialSequence(){

        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);

        sequence.setConfig(config);

        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(graph)
                .setDismissOnTouch(true)
                .setContentText("This is shows your performance index over date. As mentioned before, if you see a increasing line it means you're improving! "
                        + "Don't worry if you see a small decreasing!")
                .build()
        );

        //sequence.addSequenceItem(new View(getApplicationContext()),"Make sure sensor is connected!","GOT IT");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(statisticsListView)
                        .setDismissOnTouch(true)
                        .setContentText("This is the table of your running session stats. You can find the calories burn in the table and other measurement if you're looking for specific numbers.")
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(dateSelection)
                        .setDismissOnTouch(true)
                        .setContentText("This button allows you to filter the date interval of for the graph and the table.")
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView showcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView showcaseView) {
                                Intent intent;
                                Log.e("Tag", "<MAIN> entering statistic");
                                intent = new Intent(new Intent(StatisticsActivity.this, ProfileActivity.class));
                                intent.putExtra("tutorial",true);
                                if(user!=null)
                                    intent.putExtra("email",email);
                                startActivity(intent);
                            }
                        })
                        .build()
        );


        sequence.start();

    }
}

