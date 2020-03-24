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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
//-------
import android.widget.EditText;
//-------
import android.widget.Toast;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.Statistic;
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


public class MainActivity extends AppCompatActivity implements LocationListener,Runnable {
    static TextView bpm;
    String email;
    Double weightinkg;
    Double actduration;
    DatabaseHelper databaseHelper;
  
    //private Switch aSwitch;
    public static Button button1;
    static int recording;
    static double bpmrecording;
    private int sumbpm=0;
    private double performanceIndex;
    private int counter=0;
    private int speed_counter=0;
    private static boolean listen_pre_bpm = false;
    private static boolean listen_post_bpm = false;
    private TextView speed_txt;
    static ArrayList<Integer> recordings = new ArrayList<Integer>();
   // static int[] array1[] = new int[][];
    float continuous_average_speed = 0;
    double average_speed = 0;
    float speed_sum = 0;
    boolean check = false;
    public static boolean lock = false;
    LocationManager lm;
    private long start,end;
    private User user;
    /*
    EditText weight, met, duration;
    TextView resulttext;
    String calculation;
    */

    public void run(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBottomNavigationView();
        databaseHelper = new DatabaseHelper(MainActivity.this);
        user = databaseHelper.getUser(email);
        speed_txt = (TextView) this.findViewById(R.id.speed);

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

        button1 = (Button) findViewById(R.id.recordingbutton);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (counter){
                    case 0:
                        //while(recordings.size()<10 && MyBluetoothService.success);

                        if(MyBluetoothService.success) {
                            synchronized (readbpm.getprebpm) {
                                readbpm.getprebpm = true;
                            }
                            counter++;
                            start = System.currentTimeMillis();
                            lock=true;
                            button1.setText("Getting your bpm");
                        }
                        else
                        {
                            button1.setText("Get your average speed");
                            Toast.makeText(MainActivity.this, "The sensor is disconnected", Toast.LENGTH_SHORT).show();
                        }

                        check=true;
                        speed_counter = 0;
                        continuous_average_speed = 0;
                        //timestamp seconds since epoch
                        break;

                    case 1:

                        if(MyBluetoothService.success){
                            long duration = System.currentTimeMillis()-start;
                            synchronized (readbpm.getpostbpm) {
                                readbpm.getpostbpm = true;
                            }
                            button1.setText("Getting your bpm");
                            if(!lock){
                                button1.setText("Show Performance Index");
                                double user_weight,calories;
                                if(user.getEmail()!=null){
                                    if(user.getWeightUnit()==1){
                                        user_weight = Double.parseDouble(user.getWeight());
                                        calories =getCaloriesBurned(user_weight,(end)/1000/60);
                                    }
                                    else{
                                        user_weight = Double.parseDouble(user.getWeight())*0.45359237;
                                        calories =getCaloriesBurned(user_weight,(end)/1000/60);
                                    }
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                    Date date = new Date();
                                    String str_date = dateFormat.format(date);
                                    databaseHelper.insertStatistic(new Statistic(user.getId(), str_date, getperformanceindex(readbpm.preBPM,readbpm.postBPM) ,(double)continuous_average_speed, calories));
                                }
                                counter++;
                            }
                        }
                        else{
                            button1.setText("Start Recording");
                            counter=0;
                        }

                        check=false;
                        average_speed=continuous_average_speed;
                        break;

                    case 2:
                        if(MyBluetoothService.success)
                            displayPerformanceindex(readbpm.preBPM, readbpm.postBPM);
                        button1.setText("Start Recording");
                        counter=0;
                        Log.e("Tag","case 2");
                        break;


                }

            }
        });

        bpm = findViewById(R.id.bpm);
        bpm.setBackgroundResource(R.drawable.ic_bpm);
        bpm.setTextColor(getResources().getColor(R.color.colorBlack));

        email = getIntent().getStringExtra("email");
        if(email==null){
            Log.e("Tag","<DEBUG> email is null");
        }
        Log.e("Tag","<MAIN> email-> "+email);
        if(!MyBluetoothService.success){
            bpm.setText("Sensor Disconnected");
            bpm.setTextSize(20);
            /**if(!MyBluetoothService.understood)
                showBTDialog();*/
        }else{
            bpm.setText("Your BPM value");
        }


    }

    /**public void showBTDialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_bluetooth_list, (ViewGroup) findViewById(R.id.bt_list));

        popDialog.setTitle("Paired Bluetooth Devices");
        popDialog.setMessage("Please go to Settings -> Bluetooth -> Pair new device and pair your device.");
        popDialog.setView(Viewlayout);
        popDialog.setPositiveButton("Understood",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            dialog.dismiss();
                        }catch (Exception e){
                            Log.e("Tag","<Message> Failed creating bond with chosen bt device");
                        }

                    }
                });

        // Create popup and show
        popDialog.create();
        popDialog.show();
    }*/

    private void setUpBottomNavigationView() {
            final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Intent intent;


                    switch (menuItem.getItemId()){
                        case R.id.map:
                            if (user.getEmail()== null) {
                                Toast.makeText(getApplicationContext(), "<Message> Please Login to use this feature", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, StartActivity.class));
                                break;
                            } else {
                                Log.e("Tag","<MAIN> entering statistic");
                                intent = new Intent(new Intent(MainActivity.this, MapsActivity.class));
                                intent.putExtra("email", email);
                                startActivity(intent);
                                break;
                            }

//                        case R.id.home:
//                            startActivity(new Intent(MainActivity.this, DatabaseViewerActivity.class));
//                            break;
//

                        case R.id.statistics:
                            if (user.getEmail()== null) {
                                Toast.makeText(getApplicationContext(), "<Message> Please Login to use this feature", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, StartActivity.class));
                                break;
                            } else {
                                Log.e("Tag","<MAIN> entering statistic");
                                intent = new Intent(new Intent(MainActivity.this, StatisticsActivity.class));
                                intent.putExtra("email", email);
                                startActivity(intent);
                                break;
                            }

                        case R.id.profile:

                            if (user.getEmail()== null) {
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


    protected void displayPerformanceindex(double pre, double post){
        if(pre!=0 && post !=0){
            performanceIndex =  (15.3 * (post/pre));
            Toast.makeText(getApplicationContext(), "Your Performance Index for this Workout is: "+ performanceIndex, Toast.LENGTH_LONG).show();
            //TODO Send Performance Index to DB.
        }
        else{
            Toast.makeText(getApplicationContext(), "Some Measurements were Missing, Try again next time", Toast.LENGTH_LONG).show();
        }

    }

    protected double getperformanceindex(double pre, double post){
        if(pre!=0 && post !=0) {
            return (15.3 * (post / pre));
        }
        else
            return 0;
    }

    public static void  Update_bpm(String a){
        if(bpm!=null) {
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

    protected double getCaloriesBurned(double weight, long duration) {


        double cb = ((weight * 5 * 3.5) / (200)) * (duration);


        Double result = 0.0;
        String a = "Total Calories Burned:nn" + cb + "nCal";

        return result;

        //resulttext.setText(calculation);
    }

    //-----------
}
