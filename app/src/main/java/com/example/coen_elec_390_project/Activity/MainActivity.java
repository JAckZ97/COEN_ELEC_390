package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.MyBluetoothService;
import com.example.coen_elec_390_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static TextView bpm;
    static TextView zone;
    private BluetoothAdapter bluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean scanning;
    public static String global_email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBottomNavigationView();
        bpm = findViewById(R.id.bpm);
        bpm.setBackgroundResource(R.drawable.ic_bpm);
        bpm.setTextColor(getResources().getColor(R.color.colorPrimary));
        //if(global_email.equals(""))
            global_email = getIntent().getStringExtra("email");
    }

    private void setUpBottomNavigationView() {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Intent intent;

                    switch (menuItem.getItemId()){
                        case R.id.home:
                            startActivity(new Intent(MainActivity.this, DatabaseViewerActivity.class));
                            break;

                        case R.id.statistics:
                            intent = new Intent(new Intent(MainActivity.this, StatisticsActivity.class));
                            startActivity(intent);
                            break;

                        case R.id.profile:
                            intent = new Intent(new Intent(MainActivity.this, ProfileActivity.class));
                            startActivity(intent);
                            break;

                        case R.id.logout:
                            startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            break;
                    }

                    return true;
                }
            });
    }

    public static void  Update_bpm(String a){
        if(bpm!=null) {
            bpm.setText(a);
        }
    }
}
