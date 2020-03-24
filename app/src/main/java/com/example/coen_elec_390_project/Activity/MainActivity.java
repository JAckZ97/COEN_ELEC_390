package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
//-------
import android.widget.EditText;
//-------
import android.widget.Toast;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.Statistic;
import com.example.coen_elec_390_project.Model.Temp;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.Model.readbpm;
import com.example.coen_elec_390_project.MyBluetoothService;
import com.example.coen_elec_390_project.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationListener, Runnable {
    static TextView bpm;
    String email;
    Double weightinkg;
    Double actduration;
    DatabaseHelper databaseHelper;

    //private Switch aSwitch;
    public static Button button1;
    private double performanceIndex;
    private int counter = 0;
    private int speed_counter = 0;
    private TextView speed_txt;
    // static int[] array1[] = new int[][];
    float continuous_average_speed = 0;
    double average_speed = 0;
    float speed_sum = 0;
    boolean check = false;
    public static boolean lock = false;
    LocationManager lm;
    private long start;
    private User user;
    private boolean understood =false;
    /*
    EditText weight, met, duration;
    TextView resulttext;
    String calculation;
    */

    public void run() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBottomNavigationView();
        email = getIntent().getStringExtra("email");
        databaseHelper = new DatabaseHelper(MainActivity.this);
        user = databaseHelper.getUser(email);
        if(user != null){
            List<Statistic> stats = databaseHelper.getStatisticsByUser(user.getId());
            Statistic.counter = stats.size();
        }
        speed_txt = this.findViewById(R.id.speed);
        if(Temp.session_counter>0){
            if(user!=null)
                store_temp_dialog();
        }

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        button1 = findViewById(R.id.recordingbutton);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (counter) {
                    case 0:
                        //while(recordings.size()<10 && MyBluetoothService.success);
                        Toast.makeText(getApplicationContext(),"Put your finger on the sensor!",Toast.LENGTH_SHORT);
                        if (MyBluetoothService.success) {
                            synchronized (readbpm.getprebpm) {
                                readbpm.getprebpm = true;
                            }
                            start = System.currentTimeMillis();
                            lock = true;
                            button1.setText("Getting your bpm");
                        } else {
                            button1.setText("Get your average speed");
                            if(!understood) {
                                Toast.makeText(MainActivity.this, "The sensor is disconnected", Toast.LENGTH_SHORT).show();
                                understood=true;
                            }
                        }
                        counter++;
                        check = true;
                        speed_counter = 0;
                        continuous_average_speed = 0;
                        //timestamp seconds since epoch
                        break;

                    case 1:
                        if (MyBluetoothService.success) {
                            long duration = System.currentTimeMillis() - start;
                            if (lock) {
                                readbpm.getpostbpm = true;
                                button1.setText("Getting your bpm");
                            }

                            if (!lock) {
                                button1.setText("Show Performance Index");
                                double user_weight, calories;
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                Date date = new Date();
                                String str_date = dateFormat.format(date);
                                if (user != null) {
                                    if (user.getWeightUnit() == 1) {
                                        user_weight = Double.parseDouble(user.getWeight());
                                        calories = Statistic.getCaloriesBurned(user_weight, (duration) / 1000 / 60,continuous_average_speed);
                                    } else {
                                        user_weight = Double.parseDouble(user.getWeight()) * 0.45359237;
                                        calories = Statistic.getCaloriesBurned(user_weight, (duration) / 1000 / 60,continuous_average_speed);
                                    }
                                    databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, Statistic.getperformanceindex(readbpm.preBPM, readbpm.postBPM), (double) continuous_average_speed, calories));
                                }else if ( user != null && Temp.isNumeric(user.getWeight())){
                                    Toast.makeText(getApplicationContext(),"Failed to store temp to statistic database! Please enter your profile first!",Toast.LENGTH_LONG).show();
                                    Intent intent;
                                    intent = new Intent(new Intent(MainActivity.this, ProfileActivity.class));
                                    intent.putExtra("email", email);
                                    intent.putExtra("temp",true);
                                    startActivity(intent);
                                }else {
                                    //Temp is required
                                    if (Temp.insertTemp(readbpm.preBPM, readbpm.postBPM, str_date, continuous_average_speed, duration)) {
                                        Toast.makeText(getApplicationContext(), "Your running session is stored temporarily. Please login to save your data in database! Your temporary data will be lost if you exit the application.", Toast.LENGTH_LONG).show();
                                        Temp.session_counter++;
                                    } else
                                        Toast.makeText(getApplicationContext(), "Out of temp limit, storing temp failed!", Toast.LENGTH_LONG).show();
                                }
                                counter++;
                            }
                        } else {
                            button1.setText("Start Recording");
                            counter = 0;
                        }

                        check = false;
                        average_speed = continuous_average_speed;
                        break;

                    case 2:
                        if (MyBluetoothService.success)
                            displayPerformanceindex(readbpm.preBPM, readbpm.postBPM);
                        button1.setText("Start Recording");
                        counter = 0;
                        Log.e("Tag", "case 2");
                        break;


                }

            }
        });

        bpm = findViewById(R.id.bpm);
        bpm.setBackgroundResource(R.drawable.ic_bpm);
        bpm.setTextColor(getResources().getColor(R.color.colorBlack));

        if (email == null) {
            Log.e("Tag", "<DEBUG> email is null");
        }
        Log.e("Tag", "<MAIN> email-> " + email);
        if (!MyBluetoothService.success) {
            bpm.setText("Sensor Disconnected");
            bpm.setTextSize(20);
            /**if(!MyBluetoothService.understood)
             showBTDialog();*/
        } else {
            bpm.setText("Your BPM value");
        }


    }


    public void store_temp_dialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_bluetooth_list, (ViewGroup) findViewById(R.id.bt_list));
        popDialog.setTitle("Store Temp Sessions");
        popDialog.setMessage("Do you want to store your temporary statistic data in this account?");
        popDialog.setView(Viewlayout);
        popDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (user.getEmail() != null && Temp.isNumeric(user.getWeight())) {
                            //Insert temp to stats
                            double user_weight, calories, prebpm, postbpm;
                            long duration;
                            float speed;
                            String str_date;
                            for (int i = 0; i < Temp.session_counter; i++) {
                                speed = Temp.Speeds.get(i);
                                str_date = Temp.Dates.get(i);
                                duration = Temp.Durations.get(i);
                                prebpm = Temp.PreBPMs.get(i);
                                postbpm = Temp.PostBPMs.get(i);
                                if (user.getWeightUnit() == 1) {
                                    user_weight = Double.parseDouble(user.getWeight());
                                    calories = Statistic.getCaloriesBurned(user_weight, (duration) / 1000 / 60,speed);
                                } else {
                                    user_weight = Double.parseDouble(user.getWeight()) * 0.45359237;
                                    calories = Statistic.getCaloriesBurned(user_weight, (duration) / 1000 / 60,speed);
                                }
                                databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, Statistic.getperformanceindex(prebpm, postbpm), (double) speed, calories));
                            }
                            Temp.clear();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Failed to store temp to statistic database! Please enter your profile first!",Toast.LENGTH_LONG).show();
                            Intent intent;
                            intent = new Intent(new Intent(MainActivity.this, ProfileActivity.class));
                            intent.putExtra("email", email);
                            intent.putExtra("temp",true);
                            startActivity(intent);
                        }
                        dialog.dismiss();
                    }

                });
        popDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int which){
                dialog.dismiss();
            }
        });

        // Create popup and show
        popDialog.create();
        popDialog.show();
    }

    private void setUpBottomNavigationView() {
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;


                switch (menuItem.getItemId()) {
                    case R.id.map:
                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "<Message> Please Login to use this feature", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, StartActivity.class));
                            break;
                        } else {
                            Log.e("Tag", "<MAIN> entering statistic");
                            intent = new Intent(new Intent(MainActivity.this, MapsActivity.class));
                            intent.putExtra("email", email);
                            startActivity(intent);
                            break;
                        }
                    /*
                    case R.id.home:
                        startActivity(new Intent(MainActivity.this, DatabaseViewerActivity.class));
                        break;
                    */

                    case R.id.statistics:
                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "<Message> Please Login to use this feature", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, StartActivity.class));
                            break;
                        } else {
                            Log.e("Tag", "<MAIN> entering statistic");
                            intent = new Intent(new Intent(MainActivity.this, StatisticsActivity.class));
                            intent.putExtra("email", email);
                            startActivity(intent);
                            break;
                        }

                    case R.id.profile:

                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "<Message> Please Login to use this feature", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, StartActivity.class));
                            break;
                        } else {
                            intent = new Intent(new Intent(MainActivity.this, ProfileActivity.class));
                            intent.putExtra("email", email);
                            startActivity(intent);
                            break;
                        }

                    case R.id.logout:
                        startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }

                return true;
            }
        });
    }

    protected void displayPerformanceindex(double pre, double post) {
        if (pre != 0 && post != 0) {
            performanceIndex = (15.3 * (post / pre));
            Toast.makeText(getApplicationContext(), "Your Performance Index for this Workout is: " + performanceIndex, Toast.LENGTH_LONG).show();
            //TODO Send Performance Index to DB.
        } else {
            Toast.makeText(getApplicationContext(), "Some Measurements were Missing, Try again next time", Toast.LENGTH_LONG).show();
        }

    }

    public static void Update_bpm(String a) {
        if (bpm != null) {
            bpm.setText(a);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            speed_txt.setText("-- km/h");
        } else {

            float Currentspeed = location.getSpeed();
            speed_sum += Currentspeed;
            speed_counter++;
            continuous_average_speed = speed_sum / speed_counter;


            speed_txt.setText("Your current speed is " + (int) (+Currentspeed * 3.6f) + " km/hr");
            if (!check) {
                speed_txt.setText("Your average speed is: " + (int) (average_speed) * 3.6f);
                speed_sum = 0;
            }

            //    speed_txt.setText( "Your current speed is "+(double)(+Currentspeed*3.6f) + " km/h");
            //if(!check){speed_txt.setText("Your average speed is: " + average_speed + " km/h");}


        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    //-----------

    //MET is set to a default value for now (MET = 5)

    //-----------
}
