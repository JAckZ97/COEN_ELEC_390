package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coen_elec_390_project.MyBluetoothService;
import com.example.coen_elec_390_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    static TextView bpm;
    static TextView zone;
    private BluetoothAdapter bluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean scanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBottomNavigationView();
        bpm = findViewById(R.id.bpm);
        if(!MyBluetoothService.success){
            bpm.setText("Bluetooth Disconnected");
            bpm.setBackgroundResource(R.drawable.ic_bpm);
            bpm.setTextColor(getResources().getColor(R.color.colorPrimary));
            showBTDialog();
        }else{
            bpm.setText("Your BPM value");
            bpm.setBackgroundResource(R.drawable.ic_bpm);
            bpm.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

    }

    public void showBTDialog() {

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_bluetooth_list, (ViewGroup) findViewById(R.id.bt_list));

        popDialog.setTitle("Paired Bluetooth Devices");
        popDialog.setMessage("Please go to Settings -> Bluetooth -> Pair new device and pair your device.");
        popDialog.setView(Viewlayout);

        // create the arrayAdapter that contains the BTDevices, and set it to a ListView

//        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
//        myListView = (ListView) Viewlayout.findViewById(R.id.BTList);
//        // get paired devices
//
//        // put it's one to the adapter
//        for (BluetoothDevice device : mybtlist)
//            BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//        myListView.setAdapter(BTArrayAdapter);
//
//        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//                chosenbtdevice=mybtlist.get(position);
//
//
//            }
//        });
        // Button OK
        popDialog.setPositiveButton("Understood",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try{
//                            chosenbtdevice.createBond();
//                            Log.e("Tag","<Message> Creating bound");
//                            MyBluetoothService.initialized=true;
//                            Log.e("Tag","<Message> Connecting,should cancel discovery");
//                            mythread = new ConnectThread(chosenbtdevice);
//                            mbs = new MyBluetoothService(mythread.tryconnect());
                            dialog.dismiss();
                        }catch (Exception e){
                            Log.e("Tag","<Message> Failed creating bond with chosen bt device");
                        }

                    }
                });

        // Create popup and show
        popDialog.create();
        popDialog.show();
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

    public static void  Update_bpm(String a){
        if(bpm!=null) {
            bpm.setText(a);
        }
    }
}
