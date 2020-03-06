package com.example.coen_elec_390_project;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StartActivity extends AppCompatActivity {
    Button login, register, guest;

    FirebaseUser firebaseUser;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean scanning;
    private android.bluetooth.le.ScanCallback lescanCallback;
    private Handler mHandler;
    ArrayList<BluetoothDevice> listBluetoothDevice;

    private BluetoothGattCallback gattCallback;
    private BluetoothGatt bluetoothGatt;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 100000;

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

        listBluetoothDevice = new ArrayList<>();
        mHandler = new Handler();

        getBluetoothAdapterAndLeScanner();

        scanLeDevice(true);




        // Checks if Bluetooth is supported on the device.


        //bluetoothGatt = listBluetoothDevice.get(0).connectGatt(this, false, gattCallback);
//        if(bluetoothGatt.discoverServices()){
//            Log.e("bluetooth","coen390 message discovered service");
//        }
//        else
//            Log.e("bluetooth","coen390 message no service");
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            listBluetoothDevice.clear();
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.stopScan(lescanCallback);
                    Toast.makeText(StartActivity.this,
                            "Scan timeout",
                            Toast.LENGTH_LONG).show();
                    scanning = false;
                }
            }, SCAN_PERIOD);

            //mBluetoothLeScanner.startScan(scanCallback);

            //scan specified devices only with ScanFilter
            ScanFilter scanFilter =
                    new ScanFilter.Builder()
                            .setServiceUuid(new ParcelUuid(UUID.fromString("1382f944-9af1-4e0e-b7ee-5cd77f585f40")))
                            .build();
            List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
            scanFilters.add(scanFilter);
            ScanSettings scanSettings =
                    new ScanSettings.Builder().build();
            //mBluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback);
            mBluetoothLeScanner.startScan(lescanCallback);
            Log.e("list size","size -> "+listBluetoothDevice.size());

            scanning = true;
        } else {
            mBluetoothLeScanner.stopScan(lescanCallback);
            scanning = false;
        }
    }

    private void getBluetoothAdapterAndLeScanner(){
        // Get BluetoothAdapter and BluetoothLeScanner.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        scanning = false;

        lescanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                addBluetoothDevice(result.getDevice());
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                for (ScanResult result : results) {
                    addBluetoothDevice(result.getDevice());
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Toast.makeText(StartActivity.this,
                        "onScanFailed: " + String.valueOf(errorCode),
                        Toast.LENGTH_LONG).show();
            }

            private void addBluetoothDevice(BluetoothDevice device) {
                if (!listBluetoothDevice.contains(device)) {
                    listBluetoothDevice.add(device);
                }
            }
        };

        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    "bluetoothManager.getAdapter()==null",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    /*

     */

}
