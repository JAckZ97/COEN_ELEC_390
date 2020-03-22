package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen_elec_390_project.MyBluetoothService;
import com.example.coen_elec_390_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    static TextView bpm;
    static TextView zone;
    private BluetoothAdapter bluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean scanning;
    //private Switch aSwitch;
    protected Button button1;
    protected Button button2;
    private int preBPM;
    private int postBPM;
    static int recording;
    static double bpmrecording;
    private int sumbpm=0;
    private double performanceIndex;
    private int counter=0;
    static ArrayList<Integer> recordings = new ArrayList<Integer>();
   // static int[] array1[] = new int[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBottomNavigationView();
        bpm = findViewById(R.id.bpm);
        bpm.setText("180 bpm");
        bpm.setBackgroundResource(R.drawable.ic_bpm);
        bpm.setTextColor(getResources().getColor(R.color.colorPrimary));

        button1 = (Button) findViewById(R.id.recordingbutton);
        //button2 = (Button) findViewById(R.id.performancebutton);
        //aSwitch = (Switch) findViewById(R.id.switch1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (counter){
                    case 0:
                        getPreBPM();
                        button1.setText("Post-Workout Measurement Start");
                        break;

                    case 1:
                        getPostBPM();
                        button1.setText("Show Performance Index");
                        break;

                    case 2:
                        getPerformanceIndex(preBPM, postBPM);

                }



                /*if(counter%2==0){
                    getPreBPM();
                    counter++;
                    button1.setText("Post-Workout Measurement Start");
                }
                else{
                    getPostBPM();
                    counter=0;
                }*/
            }
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        });



        // Performance Index Calculation and Toast
        /*button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double restHR = preBPM;
                double maxHR= postBPM;
                if(restHR!=0 && maxHR !=0){
                    performanceIndex =  (15.3 * (maxHR/restHR));
                    Toast.makeText(getApplicationContext(), "Your Performance Index for this Workout is: "+ String.valueOf(performanceIndex), Toast.LENGTH_LONG).show();
                }
            }
        });*/

    }


    private void setUpBottomNavigationView() {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.home:
                            break;

                        case R.id.profile:
                            if (user == null) {
                                // User is signed in
                                startActivity(new Intent(MainActivity.this, StartActivity.class));
                                break;
                            } else {
                                // No user is signed in
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                break;
                            }

                        case R.id.statistics:
                            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
                            break;

                        case R.id.logout:
                            FirebaseAuth.getInstance().signOut();
                            //MyBluetoothService.cancel_service();
                            startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            break;
                    }

                    return false;
                }
            });
    }

    protected void getPreBPM(){
        preBPM=0;
        recordings.add(recording);
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
        counter++;
    }

    protected void getPostBPM(){
        postBPM=0;
        recordings.add(recording);
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
        counter++;
    }

    protected void getPerformanceIndex(int pre, int post){
        if(pre!=0 && post !=0){
            performanceIndex =  (15.3 * (post/pre));
            Toast.makeText(getApplicationContext(), "Your Performance Index for this Workout is: "+ performanceIndex, Toast.LENGTH_LONG).show();
            //TODO Send Performance Index to DB.
        }
        else{
            Toast.makeText(getApplicationContext(), "Some Measurements were Missing, Try again next time", Toast.LENGTH_LONG).show();
        }
        counter=0;

    }

    public static void  Update_bpm(String a){
        if(bpm!=null) {
            bpm.setText(a);
            recording = Integer.parseInt(a);
        }
    }

}
