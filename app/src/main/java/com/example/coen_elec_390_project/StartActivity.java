package com.example.coen_elec_390_project;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class StartActivity extends AppCompatActivity {
    Button login, register, guest;

    FirebaseUser firebaseUser;
    private final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // redirect if user is not null
        if(firebaseUser != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }

    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                if(deviceName == "COEN390"){
                    Log.e("Tag","FOUND ESP32 DEVICE USING BT");
                    Log.e("Tag", "device address "+deviceHardwareAddress);
                }



            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        guest = findViewById(R.id.guest);

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
                startActivity(new Intent(StartActivity.this, MainActivity.class));
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                Log.e("Tag", "device name " + deviceName);
                Log.e("Tag", "device address " + deviceHardwareAddress);
                if (deviceName.equalsIgnoreCase("COEN390") &&
                        deviceHardwareAddress.equalsIgnoreCase("30:AE:A4:58:3E:DA")) {
                    ConnectThread mythread = new ConnectThread(device);
                    MyBluetoothService mbs = new MyBluetoothService(mythread.tryconnect());
                    break;
                }
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        registerReceiver(receiver, filter);

//        Intent discoverableIntent =
//                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 500);
//        startActivity(discoverableIntent);

        bluetoothAdapter.startDiscovery();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
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
                tmp = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
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
                Log.e("","Connected");
            } catch (IOException e) {
                Log.e("",e.getMessage());
                try {
                    Class<?> clazz = mmsocket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};

                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[] {Integer.valueOf(1)};

                    fallbackSocket = (BluetoothSocket) m.invoke(mmsocket.getRemoteDevice(), params);
                    fallbackSocket.connect();
                }
                catch (Exception e2) {
                    Log.e("", "Couldn't establish Bluetooth connection!");
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
}
