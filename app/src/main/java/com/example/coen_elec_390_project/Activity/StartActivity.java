package com.example.coen_elec_390_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.Statistic;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.MyBluetoothService;
import com.example.coen_elec_390_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private boolean found = false;
    private ConnectThread mythread;
    private MyBluetoothService mbs;
    private boolean btpass = false;
    private Switch wifiSwitch;
    private WifiManager wifiManager;
    ProgressDialog pd;
    DatabaseReference reff;
    DatabaseHelper databaseHelper;
    FirebaseAuth auth;
    boolean lock = true;

    @Override
    protected void onStart() {
        super.onStart();
        if (!MyBluetoothService.initialized) {
            bluetoothsetup();
        } else
            btpass = true;

        if (!btpass && !MyBluetoothService.understood) {
            MyBluetoothService.understood = true;
            showBTDialog();
        }

        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifistateReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifistateReceiver);
    }

    private void bluetoothsetup() {
        Log.e("Tag", "<Message> Bluetooth setup");

        if (bluetoothAdapter != null) {
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
                    Log.e("Tag", "<Message> device name " + deviceName);
                    if (deviceName.equalsIgnoreCase("COEN390") &&
                            deviceHardwareAddress.equalsIgnoreCase("30:AE:A4:58:3E:DA")) {
                        MyBluetoothService.initialized = true;
                        Log.e("Tag", "<Message> Connecting,should cancel discovery");
                        mythread = new ConnectThread(device);
                        mbs = new MyBluetoothService(mythread.tryconnect());
                        bluetoothAdapter.cancelDiscovery();
                        btpass = true;
                        break;
                    }
                }
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

        Boolean frommain = getIntent().getBooleanExtra("main",false);
        Log.e("Tag","<START> " + frommain);
        //wifiSwitch = findViewById(R.id.wifiCheckSwitch);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        auth = FirebaseAuth.getInstance();

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
                if (checkNetworkConnection()) {
                    signInAnonimously();
                }
            }
        });


        /*
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    wifiManager.setWifiEnabled(true);
                    wifiSwitch.setText("You have access the wifi. ");
                } else {
                    wifiManager.setWifiEnabled(false);
                    wifiSwitch.setText("You turn off the wifi. ");
                }
            }
        });

         */



//      UPLOAD all updated stats
        if (checkNetworkConnection() && !frommain) {
            databaseHelper = new DatabaseHelper(this);
            final List<User> users = databaseHelper.getAllUsers();
            Log.e("Tag", "<START> try login here");
            for (int i = 0; i < users.size(); i++) {
                final User temp_user = users.get(i);
                final int index = i;
                auth.signInWithEmailAndPassword(users.get(i).getEmail(), users.get(i).getPassword()).addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("Tag", "<START> login here");
                            update_user_stats(users.get(index));
                        } else {
                            Log.e("Tag", "<START> Try register here");
                            registerOnline(temp_user);
                            FirebaseAuth.getInstance().signOut();
                            pd.dismiss();
                        }
                    }
                });
                Log.e("Tag", "<START> finish here");
                //while(lock);
                lock = true;
            }

        }


    }

    private BroadcastReceiver wifistateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(wifiManager.EXTRA_WIFI_STATE,
                    wifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    //wifiSwitch.setChecked(true);
                    //wifiSwitch.setText("You have access the wifi. ");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    //wifiSwitch.setChecked(false);
                    //wifiSwitch.setText("You turn off the wifi. ");
                    break;
            }
        }
    };

    private boolean checkNetworkConnection() {
        pd = new ProgressDialog(StartActivity.this);
        boolean wifiConnected;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();

        if (activeInfo != null && activeInfo.isConnected()) { // wifi connected
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;

            if (wifiConnected) {
                Toast.makeText(StartActivity.this, "Wifi is connected", Toast.LENGTH_SHORT).show();
                Log.e("Tag", "wifi is connected");
                pd.dismiss();

                return true;
            }
        } else { // no internet connected
            Toast.makeText(StartActivity.this, "No internet connect", Toast.LENGTH_SHORT).show();
            Log.e("Tag", "no internet connect ");
            pd.dismiss();

            return false;
        }

        return false;
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
                } else {
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


        } catch (IllegalArgumentException e) {

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
                Log.e("", "<Message> " + e.getMessage());
                try {
                    Class<?> clazz = mmsocket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};

                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[]{Integer.valueOf(1)};

                    fallbackSocket = (BluetoothSocket) m.invoke(mmsocket.getRemoteDevice(), params);
                    fallbackSocket.connect();
                } catch (Exception e2) {
                    Log.e("", "<Message> Couldn't establish Bluetooth connection!");
                }
            }
            if (fallbackSocket == null) {
                return mmsocket;
            } else {
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
        if (EasyPermissions.hasPermissions(this, perms)) {
            //Toast.makeText(this, "<Message> Permission already granted", Toast.LENGTH_SHORT).show();
        } else {
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
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                            Log.e("Tag", "<Message> Failed creating bond with chosen bt device");
                        }

                    }
                });

        popDialog.create();
        popDialog.show();
    }

    public void update_user_stats(User user) {
        final User temp_user = user;
        final String uid = auth.getCurrentUser().getUid();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        final List<Statistic> stats = databaseHelper.getStatisticsByUser(temp_user.getId());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("Tag", "<START> onDataChange triggered");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Id", uid);
                hashMap.put("Email", temp_user.getEmail());
                hashMap.put("Fullname", temp_user.getFullname());
                hashMap.put("Gender", temp_user.getGender());
                hashMap.put("Age", temp_user.getAge());
                hashMap.put("Weight", temp_user.getWeight());
                hashMap.put("Height", temp_user.getHeight());
                hashMap.put("height unit", Integer.toString(temp_user.getHeightUnit()));
                hashMap.put("weight unit", Integer.toString(temp_user.getWeightUnit()));
                hashMap.put("Password", temp_user.getPassword());
                Log.e("Tag","<START> user stat counter "+temp_user.getStat_counter());
                hashMap.put("Stat_Counter", Integer.toString(temp_user.getStat_counter()));
                reference.child(uid).setValue(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.dismiss();
            }
        });

        final DatabaseReference reff_stats = FirebaseDatabase.getInstance().getReference().child("Stats");
        reff_stats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> updateresult;
                for (int i = 0; i < stats.size(); i++) {
                    Log.e("Tag", "<START> " + stats.get(i).getDate());
                    updateresult = new HashMap<>();
                    if (!dataSnapshot.child(uid).child(Integer.toString(i)).exists()) {
                        updateresult.put("stat_id", Integer.toString(i));
                        updateresult.put("stat_date", stats.get(i).getDate());
                        updateresult.put("stat_speed", Double.toString(stats.get(i).getSpeed()));
                        updateresult.put("stat_calory", Double.toString(stats.get(i).getCalories()));
                        updateresult.put("stat_perf_index", Double.toString(stats.get(i).getPerformance_index()));
                        updateresult.put("stat_step_counter", Integer.toString(stats.get(i).getStep_counter()));
                        reff_stats.child(auth.getCurrentUser().getUid()).child(String.valueOf(i)).setValue(updateresult).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.e("Tag", "Update stats complete");
                                    pd.dismiss();
                                }
                            }
                        });
                    }

                }
                databaseHelper.clearDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void registerOnline(final User user) {

        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            update_user_stats(user);
                        } else {
                            pd.dismiss();
                        }
                    }
                });

    }
}
