package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.coen_elec_390_project.MyBluetoothService;
import com.example.coen_elec_390_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import android.Manifest;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class StartActivity extends AppCompatActivity {
    Button login, register, guest;

    FirebaseUser firebaseUser;
    private final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private ListView myListView;
    private ArrayAdapter BTArrayAdapter;
    private ArrayList<BluetoothDevice> mybtlist;
    private BluetoothDevice chosenbtdevice;
    private boolean found =false;
    private ConnectThread mythread;
    private MyBluetoothService mbs;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // redirect if user is not null
        if(firebaseUser != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }

        if(!MyBluetoothService.initialized)
            bluetoothsetup();
        if(!MyBluetoothService.success){
            showBTDialog();
        }

    }

    private void bluetoothsetup(){
        Log.e("Tag","<Message> Bluetooth setup");

        if (bluetoothAdapter!=null){
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            //Look for old devices
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.e("Tag","<Message> device name "+deviceName);
                    if (deviceName.equalsIgnoreCase("COEN390") &&
                            deviceHardwareAddress.equalsIgnoreCase("30:AE:A4:58:3E:DA")) {
                        MyBluetoothService.initialized=true;
                        Log.e("Tag","<Message> Connecting,should cancel discovery");
                        mythread = new ConnectThread(device);
                        mbs = new MyBluetoothService(mythread.tryconnect());
                        bluetoothAdapter.cancelDiscovery();
                        MyBluetoothService.success=true;
                        break;
                    }
                }
            }
            if(!MyBluetoothService.success && !found) {
                requestLocationPermission();
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
                registerReceiver(receiver, filter);

            }


        }



    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                mybtlist.add(device);
                if(deviceName!=null && !found) {
                    if (deviceName.equalsIgnoreCase("COEN390") && deviceHardwareAddress.equalsIgnoreCase("30:AE:A4:58:3E:DA")) {
                        Log.e("Tag", "<Message> Found esp32");
                        found=true;
                        showBTDialog();
                    }
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mybtlist = new ArrayList<>();
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        guest = findViewById(R.id.guest);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAnonimously();
            }
        });




    }

    public void signInAnonimously() {
        final FirebaseAuth auth;

        auth = FirebaseAuth.getInstance();

        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInAnonymously:success");
                    FirebaseUser user = auth.getCurrentUser();


                }

                else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(StartActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        try {
            unregisterReceiver(receiver);
            //Register or UnRegister your broadcast receiver here

        } catch(IllegalArgumentException e) {

            e.printStackTrace();
        }

    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        private BluetoothSocket mmsocket;
        private BluetoothSocket fallbackSocket;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                UUID SERIAL_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
                tmp = device.createInsecureRfcommSocketToServiceRecord(SERIAL_UUID);
            } catch (IOException e) {
                Log.e("Socket tag", "Socket's create() method failed", e);
            }
            mmsocket = tmp;
        }

        public BluetoothSocket tryconnect() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();
            try {
                mmsocket.connect();
            } catch (IOException e) {
                Log.e("","<Message> "+e.getMessage());
                try {
                    Class<?> clazz = mmsocket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};

                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[] {Integer.valueOf(1)};

                    fallbackSocket = (BluetoothSocket) m.invoke(mmsocket.getRemoteDevice(), params);
                    fallbackSocket.connect();
                }
                catch (Exception e2) {
                    Log.e("", "<Message> Couldn't establish Bluetooth connection!");
                }
            }
            if(fallbackSocket==null){
                return mmsocket;
            }
            else{
                return fallbackSocket;
            }

        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmsocket.close();
            } catch (IOException e) {
                Log.e("Failed closing", "Could not close the client socket", e);
            }
        }


    }

    /*The following two functions can ask permission for access fine location.
    * It can be used to ask other permission for example google's api*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "<Message> Permission already granted", Toast.LENGTH_SHORT).show();
        }
        else {
            EasyPermissions.requestPermissions(this, "<Message> Please grant the location permission", MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION, perms);
        }
    }

    public void showBTDialog() {

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_bluetooth_list, (ViewGroup) findViewById(R.id.bt_list));

        popDialog.setTitle("Pair your Bluetooth Devices");
        popDialog.setMessage("Please go to Settings -> Bluetooth -> Pair new device, to pair your sensor device.");
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

        popDialog.create();
        popDialog.show();
    }

}
