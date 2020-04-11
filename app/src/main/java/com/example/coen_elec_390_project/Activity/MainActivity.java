package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.Random;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LocationListener, Runnable, SensorEventListener {
    static TextView bpm;
    String email;
    DatabaseHelper databaseHelper;
    private FloatingActionButton tutorial_btn;
    public static boolean active=false;

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
    public static boolean developer_mode = false;
    public static int dev_count=0;
    private Integer dev_count2=0;
  
    SensorManager sensorManager;
    boolean running = false;
    private TextView count;
    int steps;
    int stepsCounter;
    /*
    EditText weight, met, duration;
    TextView resulttext;
    String calculation;
    */

    public void run() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        active=true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBottomNavigationView();
        count = (TextView) findViewById(R.id.count);
        email = getIntent().getStringExtra("email");
        //get firebase user
        // Check if we need to display our OnboardingSupportFragment

        databaseHelper = new DatabaseHelper(MainActivity.this);
        user = databaseHelper.getUser(email);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Log.e("Tag","<DEV> "+dev_count2);
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
                        Toast.makeText(getApplicationContext(),"Put your finger on the sensor!",Toast.LENGTH_SHORT).show();
                        if (MyBluetoothService.success) {
                            synchronized (readbpm.getprebpm) {
                                readbpm.getprebpm = true;
                            }
                            start = System.currentTimeMillis();
                            lock = true;
                            button1.setText("Getting your bpm");
                            running = true;
                        } else {
                            button1.setText("Get your average speed");
                            if(!understood) {
                                Toast.makeText(MainActivity.this, "The sensor is disconnected", Toast.LENGTH_SHORT).show();
                                understood=true;
                            }
                        }



                        if(developer_mode){
                            start = System.currentTimeMillis();
                            lock = true;
                            button1.setText("Getting your bpm");
                        }
                        counter++;
                        check = true;
                        speed_counter = 0;
                        continuous_average_speed = 0;
                        stepsCounter=0;
                        count.setText("Steps Count:\n--");
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
                                if (user != null && Temp.isNumeric(user.getWeight())) {
                                    if (user.getWeightUnit() == 1) {
                                        user_weight = Double.parseDouble(user.getWeight());
                                        calories = Statistic.getCaloriesBurned(user_weight, (duration) / 1000 / 60,continuous_average_speed);
                                    } else {
                                        user_weight = Double.parseDouble(user.getWeight()) * 0.45359237;
                                        calories = Statistic.getCaloriesBurned(user_weight, (duration) / 1000 / 60,continuous_average_speed);
                                    }
                                    databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, Statistic.getperformanceindex(readbpm.preBPM, readbpm.postBPM), (double) continuous_average_speed, calories,stepsCounter),user);

                                }else if ( user!=null ){
                                    Toast.makeText(getApplicationContext(),"Failed to store temp to statistic database! Please enter your profile first!",Toast.LENGTH_LONG).show();
                                    Intent intent;
                                    intent = new Intent(MainActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("email", email);
                                    intent.putExtra("temp",true);
                                    startActivity(intent);
                                }else {
                                    //Temp is required
                                    if (Temp.insertTemp(readbpm.preBPM, readbpm.postBPM, str_date, continuous_average_speed, duration,stepsCounter)) {
                                        store_temp_alert_dialog();
                                        //Toast.makeText(getApplicationContext(), "Your running session is stored temporarily. Please login to save your data in database! Your temporary data will be lost if you exit the application.", Toast.LENGTH_LONG).show();
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

                        if(developer_mode){
                            long duration = System.currentTimeMillis() - start;
                            button1.setText("Show Performance Index");
                            double user_weight, calories;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            Date date = new Date();
                            String str_date = dateFormat.format(date);
                            if (user != null && Temp.isNumeric(user.getWeight())) {
                                if (user.getWeightUnit() == 1) {
                                    user_weight = Double.parseDouble(user.getWeight());
                                    calories = Statistic.getCaloriesBurned(user_weight, (duration) / 1000 / 60,Temp.speed);
                                } else {
                                    user_weight = Double.parseDouble(user.getWeight()) * 0.45359237;
                                    calories = Statistic.getCaloriesBurned(user_weight, (duration) / 1000 / 60,Temp.speed);
                                }
                                Random randobj = new Random();
                                databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, Statistic.getperformanceindex(Temp.dev_prebpm, Temp.dev_postbpm), (double) Temp.speed, calories,(randobj.nextInt(3000)+1000)),user);

                            }else if ( user!=null ){
                                Toast.makeText(getApplicationContext(),"Failed to store temp to statistic database! Please enter your profile first!",Toast.LENGTH_LONG).show();
                                Intent intent;
                                intent = new Intent(MainActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("email", email);
                                intent.putExtra("temp",true);
                                startActivity(intent);
                            }else {
                                //Temp is required
                                if (Temp.insertTemp(Temp.dev_prebpm, Temp.dev_postbpm, str_date, Temp.speed, duration,stepsCounter)) {
                                    //Toast.makeText(getApplicationContext(), "Your running session is stored temporarily. Please login to save your data in database! Your temporary data will be lost if you exit the application.", Toast.LENGTH_LONG).show();
                                    store_temp_alert_dialog();
                                    Temp.session_counter++;
                                } else
                                    Toast.makeText(getApplicationContext(), "Out of temp limit, storing temp failed!", Toast.LENGTH_LONG).show();
                            }
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

        tutorial_btn = findViewById(R.id.tutorial_btn);
        tutorial_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tutorialSequence2();
            }
        });

        boolean tutorial = getIntent().getBooleanExtra("tutorial",false);

        if(tutorial)
            new MaterialShowcaseView.Builder(this)
                    .setTarget(tutorial_btn)
                    .setDismissOnTouch(true)
                    .setContentText("If you still have some questions, click this button to see the tutorial again or send email to coen390@HRperformance.com\n\nHave FUN!")
                    .show();

        bpm = findViewById(R.id.bpm);
        tutorialSequence();
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
        popDialog.setTitle("Store Temperarily Sessions");
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
                                databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, Statistic.getperformanceindex(prebpm, postbpm), (double) speed, calories,stepsCounter),user);
                            }
                            Temp.clear();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Failed to store temp to statistic database! Please enter your profile first!",Toast.LENGTH_LONG).show();
                            Intent intent;
                            intent = new Intent(MainActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    private void store_temp_alert_dialog(){
            final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
            final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View Viewlayout = inflater.inflate(R.layout.dialog_bluetooth_list, (ViewGroup) findViewById(R.id.bt_list));
            popDialog.setTitle("You might lose your data!");
            popDialog.setMessage("Your running session is stored temporarily. Please login to save your data in database! Your temporary data will be lost if you exit the application.");
            popDialog.setView(Viewlayout);
            popDialog.setPositiveButton("Understood",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }

                    });
    }

    private void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;

                switch (menuItem.getItemId()) {
                    case R.id.map:
                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "<Message> Please Login to use this feature", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            break;
                        } else {
                            Log.e("Tag", "<MAIN> entering statistic");
                            intent = new Intent(MainActivity.this, MapsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            break;
                        }


                    case R.id.home:
                        break;


                    case R.id.statistics:
                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "<Message> Please Login to use this feature", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, StartActivity.class));
                            break;
                        } else {
                            Log.e("Tag", "<MAIN> entering statistic");
                            intent = new Intent(MainActivity.this, StatisticsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            break;
                        }

                    case R.id.profile:

                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "<Message> Please Login to use this feature", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            break;
                        } else {
                            intent = new Intent(MainActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            break;
                        }

                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("main",true);
                        startActivity(intent);
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
            speed_txt.setText("Speed:\n-- km/h");
        } else {

            float Currentspeed = location.getSpeed();
            speed_sum += Currentspeed;
            speed_counter++;
            continuous_average_speed = speed_sum / speed_counter;


            speed_txt.setText("Current Speed:\n" + (int) (+Currentspeed * 3.6f) + " km/hr");
            if (!check) {
                speed_txt.setText("Average Speed:\n" + (int) (average_speed) * 3.6f);
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(running){
            steps = (int) Double.parseDouble(String.valueOf(sensorEvent.values[0]));
            stepsCounter++;
            count.setText("Steps Count:\n" + String.valueOf(stepsCounter));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Countsensor unavailable", Toast.LENGTH_LONG).show();
        }
    }
      
    public void tutorialSequence(){

        if(!developer_mode) {

            // sequence example
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500); // half second between each showcase view
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "1");

            sequence.setConfig(config);

            sequence.addSequenceItem(new View(getApplicationContext()), "WELCOME on board, RUNNER! Let's give you a brief introduction to let you get familiar with this app!", "Ok");

            sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                    .setTarget(new View(getApplicationContext()))
                    .setDismissOnTouch(true)
                    .setContentText("This application measures your calories burned, heart rate performance,"
                            + "and your average speed of every running session.\n\n"
                            + "Heart rate performance use an indicator, heart rate performance index,"
                            + "which is calculated by using your bpm at rest and bpm after a run session.\n\n"
                            + "Heart rate performance index is a relative measurement: performance index number"
                            + " is larger than previous run means you improved your heart rate performance.")
                    .build()
            );

            //sequence.addSequenceItem(new View(getApplicationContext()),"Make sure sensor is connected!","GOT IT");

            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(this)
                            .setTarget(bpm)
                            .setDismissOnTouch(true)
                            .setContentText("Please make sure that the sensor is connected to your device via bluetooth!")
                            .build()
            );

            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(this)
                            .setTarget(button1)
                            .setDismissOnTouch(true)
                            .setContentText("Press this button to start a running session, you need to first use the heart rate sensor to measure your bpm at rest.")
                            .setListener(new IShowcaseListener() {
                                @Override
                                public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                                }

                                @Override
                                public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                    button1.setText("Getting your bpm");
                                }


                            })
                            .build()
            );


            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(this)
                            .setTarget(button1)
                            .setDismissOnTouch(true)
                            .setContentText("After press the button for the first time, we are getting your bpm at rest. "
                                    + " Put your finger on the sensor until you see a different message")
                            .setListener(new IShowcaseListener() {
                                @Override
                                public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                                }

                                @Override
                                public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                    button1.setText("Show Performance Index");
                                }


                            })
                            .build()
            );


            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(this)
                            .setTarget(button1)
                            .setDismissOnTouch(true)
                            .setContentText("Now you can run, while you're running we are also recording "
                                    + "your speed and location. We will display your path of this run session")
                            .setListener(new IShowcaseListener() {
                                @Override
                                public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                                }

                                @Override
                                public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                    button1.setText(getResources().getString(R.string.recording_btn_text));
                                }


                            })
                            .build()
            );
            sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                    .setTarget(speed_txt)
                    .setDismissOnTouch(true)
                    .setContentText("This is your current speed. Finish the recording to see your average speed of this running session.")
                    .setListener(new IShowcaseListener() {
                        @Override
                        public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                        }

                        @Override
                        public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                            Intent intent;
                            button1.setText(getResources().getString(R.string.recording_btn_text));
                            Log.e("Tag", "<MAIN> entering statistic");
                            intent = new Intent(MainActivity.this, StatisticsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("tutorial", true);
                            if (user != null)
                                intent.putExtra("email", email);
                            startActivity(intent);
                        }


                    })
                    .build());
            sequence.start();
        }

    }

    public void tutorialSequence2(){

        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);

        sequence.setConfig(config);

        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(new View(getApplicationContext()))
                .setDismissOnTouch(true)
                .setContentText("This application measures your calories burned, heart rate performance,"
                        + "and your average speed of every running session.\n\n"
                        + "Heart rate performance use an indicator, heart rate performance index,"
                        + "which is calculated by using your bpm at rest and bpm after an physical activity.\n\n"
                        + "Heart rate performance index is a relative measurement: performance index number"
                        + " is larger than previous measurement means you improved your heart rate performance.")
                .build()
        );

        //sequence.addSequenceItem(new View(getApplicationContext()),"Make sure sensor is connected!","GOT IT");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(bpm)
                        .setDismissOnTouch(true)
                        .setContentText("Please make sure that the sensor is connected!")
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(button1)
                        .setDismissOnTouch(true)
                        .setContentText("Press this button to start a running session, you need to first use the heart rate sensor to measure your bpm at rest.\n\nWithout the heart rate sensor, we can only record your speed!")
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                button1.setText("Getting your bpm");
                            }


                        })
                        .build()
        );


        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(button1)
                        .setDismissOnTouch(true)
                        .setContentText("After press the button for the first time, we are getting your bpm at rest. "
                                +" Put your finger on the sensor until you see a different message")
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                button1.setText("Show Performance Index");
                            }


                        })
                        .build()
        );


        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(button1)
                        .setDismissOnTouch(true)
                        .setContentText("Now you can run, while you're running we are also recording "
                                + "your speed and location. We will display your path of this run session")
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                button1.setText(getResources().getString(R.string.recording_btn_text));
                            }


                        })
                        .build()
        );
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(speed_txt)
                .setDismissOnTouch(true)
                .setContentText("This is your current speed. Finish the recording to see your average speed of this running session.")
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        Intent intent;
                        button1.setText(getResources().getString(R.string.recording_btn_text));
                        Log.e("Tag", "<MAIN> entering statistic");
                        intent = new Intent(MainActivity.this, StatisticsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("tutorial",true);
                        if(user!=null)
                            intent.putExtra("email",email);
                        startActivity(intent);
                    }


                })
                .build());
        sequence.start();

    }
    //-----------

    //MET is set to a default value for now (MET = 5)

    //-----------
}
