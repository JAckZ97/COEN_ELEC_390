package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
//-------
import android.widget.EditText;
//-------
import android.widget.Toast;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.MyBluetoothService;
import com.example.coen_elec_390_project.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LocationListener {
    static TextView bpm;
    String email;
    DatabaseHelper databaseHelper;
  
    //private Switch aSwitch;
    protected Button button1;
    private int preBPM;
    private int postBPM;
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
    float average_speed = 0;
    float speed_sum = 0;
    boolean check = false;
    LocationManager lm;

    //-----------
    EditText weight, met, duration;
    TextView resulttext;
    String calculation;
    //-----------
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBottomNavigationView();
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

        /*
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check=true;
                start.setEnabled(false);
                stop.setEnabled(true);
                counter = 0;
                continuous_average_speed = 0;
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                start.setEnabled(true);
                stop.setEnabled(false);
                check=false;
                average_speed=continuous_average_speed;

            }
        });
        */

        button1 = (Button) findViewById(R.id.recordingbutton);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (counter){
                    case 0:
                        listen_pre_bpm=true;
                        while(recordings.size()<10 && MyBluetoothService.success);

                        if(MyBluetoothService.success) {
                            button1.setText("Getting your BPM");
                            getPreBPM();
                            button1.setText("Post-Workout Measurement Start");
                            counter++;
                        }

                        else
                            Toast.makeText(MainActivity.this, "The sensor is disconnected", Toast.LENGTH_SHORT).show();

                        check=true;
                        speed_counter = 0;
                        continuous_average_speed = 0;
                        //timestamp seconds since epoch
                        break;

                    case 1:
                        //new timestamp - oldtimestamp (duration of a session in seconds)
                        //call calories function()
                        //write calories burned to database with current user

                        listen_post_bpm=true;
                        while(recordings.size()<10 && MyBluetoothService.success);
                        if(MyBluetoothService.success)
                            getPostBPM();
                        double index = getperformanceindex(preBPM,postBPM);
                        button1.setText("Show Performance Index");
                        counter++;
                        //getperformance index
                        //write performance index to database with current user
                        check=false;
                        average_speed=continuous_average_speed;
                        break;

                    case 2:
                        if(MyBluetoothService.success)
                            displayPerformanceindex(preBPM, postBPM);
                        button1.setText("Start Recording");
                        counter=0;
                        break;


                }

            }
        });

        bpm = findViewById(R.id.bpm);
        bpm.setBackgroundResource(R.drawable.ic_bpm);
        bpm.setTextColor(getResources().getColor(R.color.colorPrimary));
        //if(global_email.equals(""))
        email = getIntent().getStringExtra("email");
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
                    databaseHelper = new DatabaseHelper(MainActivity.this);
                    User user = databaseHelper.getUser(email);
                    switch (menuItem.getItemId()){
                        case R.id.map:
                            startActivity(new Intent(MainActivity.this, MapsActivity.class));
                            break;
                        /*
                        case R.id.home:
                            startActivity(new Intent(MainActivity.this, DatabaseViewerActivity.class));
                            break;

                         */

                        case R.id.statistics:
                            if (user.getEmail()== null) {
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

    protected void getPreBPM(){
        preBPM=0;

        int div=0;
        //int[] array1 = new int[]{85, 90, 78, 82, 84, 87, 88, 91, 92, 90, 87, 84, 85, 86, 89, 76, 89, 79, 87, 84};

        for (int i = 0; i <= recordings.size() - 1; i++) {
            bpmrecording = recordings.get(i);
            if (bpmrecording != 0 && bpmrecording < 190 && bpmrecording > 55) ;
            sumbpm += bpmrecording;
            div++;
        }

        //preBPM = sumbpm / recordings.size();
        preBPM = sumbpm / div;
        Toast.makeText(getApplicationContext(), preBPM + " BPM <pre>", Toast.LENGTH_SHORT).show();

        recordings.clear();
        sumbpm=0;
    }

    protected void getPostBPM(){
        postBPM=0;

        int div =0;
        //int[] array2 = new int[]{111, 119, 128, 132, 111, 112, 118, 1117, 115, 111, 114, 118, 110, 109, 132, 122, 121, 125, 113, 109};

        for (int i = 0; i <= recordings.size() - 1; i++) {
            bpmrecording = recordings.get(i);
            if (bpmrecording != 0 && bpmrecording < 190 && bpmrecording > 55) ;
            sumbpm += bpmrecording;
            div++;
        }

        //postBPM = sumbpm / recordings.size();
        postBPM = sumbpm / div;
        Toast.makeText(getApplicationContext(), postBPM + " BPM <post>", Toast.LENGTH_SHORT).show();

        recordings.clear();
        sumbpm=0;
    }

    protected void displayPerformanceindex(int pre, int post){
        if(pre!=0 && post !=0){
            performanceIndex =  (15.3 * (post/pre));
            Toast.makeText(getApplicationContext(), "Your Performance Index for this Workout is: "+ performanceIndex, Toast.LENGTH_LONG).show();
            //TODO Send Performance Index to DB.
        }
        else{
            Toast.makeText(getApplicationContext(), "Some Measurements were Missing, Try again next time", Toast.LENGTH_LONG).show();
        }

    }

    protected double getperformanceindex(int pre, int post){
        if(pre!=0 && post !=0) {
            return (15.3 * (post / pre));
        }
        else
            return 0;
    }

    public static void  Update_bpm(String a){
        if(bpm!=null) {
            bpm.setText(a);
            //each time the display is updated, we store the value as an int in realtime, overwriting the previous one
            if(listen_pre_bpm || listen_post_bpm) {
                recording = Integer.parseInt(a);
                recordings.add(recording);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {


        if(location == null){
            speed_txt.setText("-- km/hr");
        }
        else{

            float Currentspeed=location.getSpeed();
            speed_sum = speed_sum+Currentspeed;
            speed_counter =speed_counter+1;
            continuous_average_speed = speed_sum/speed_counter;

            speed_txt.setText( "Your current speed is "+(double)(+Currentspeed*3.6f) + " km/hr");
            if(!check){speed_txt.setText("Your average speed is: " + average_speed);}

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

    public void calculateTotalCaloriesBurned(View view) {

        String S1 = weight.getText().toString();
        String S2 = met.getText().toString();
        String S3 = duration.getText().toString();

        double weightValue = Float.parseFloat(S1);
        double metValue = Float.parseFloat(S2);
        double durationValue = Float.parseFloat(S3);

        double cb = ((weightValue * metValue * 3.5) / (200)) * (durationValue);

        calculation = "Total Calories Burned:nn" + cb + "nCal";

        resulttext.setText(calculation);
    }

    //-----------
}
