package com.example.coen_elec_390_project;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.os.Handler;

import com.example.coen_elec_390_project.Activity.MainActivity;
import com.example.coen_elec_390_project.Model.readbpm;

public class MyBluetoothService {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler handler; // handler that gets info from Bluetooth service
    private BluetoothSocket socket;
    private int size = 512;
    //public static ConnectedThread mythread;
    public static boolean success = false;
    public static boolean initialized = false;
    public static boolean understood = false;
  
    private int convert(ArrayList<Double> complex_list){

        Complex real_list[] = new Complex[size];

        for(int i = 0;i<size;i++){
            real_list[i] = new Complex(complex_list.get(i),0);
        }

        Complex ylist[] = FFT.fft(real_list);
        //FFT.show(ylist, "result");

        double mag[] = new double[size];
        for(int i = 0;i<size;i++){
            mag[i] = ylist[i].abs();
        }
        int Fs = 100;
        double freq[] = new double[size];
        for(int i = 0;i<size;i++){
            freq[i] = (double)i*Fs/size;
        }

        int  local_min = 3;
        double max = 0;
        int index = 0;

        boolean first_encounter=false;

        for(int i = local_min;i<size/2;i++){
            if(max < mag[i]){
                max = mag[i];
                index=i;
            }
        }

        Log.e("Tag","<FFT> The index with the right frequency is "+index);
        Log.e("Tag","<FFT> The HR of index 7 is " + (int)Math.round(freq[index]*60));
        return (int)Math.round(freq[index]*60);
    }

    public MyBluetoothService(BluetoothSocket socket){
        this.socket=socket;
        handler = new Handler();
        ConnectedThread mythread = new ConnectedThread();
        mythread.start();
    }

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
        // ... (Add other message types here as needed.)
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread() {

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "<Message> Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "<Message> Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[2048];
            ArrayList<Double> voltage_readings = new ArrayList<>();

            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            int i = 0;
            while (true) {
                try {
                    MyBluetoothService.success = true;
                    // Read from the InputStream.
                    if((readbpm.getprebpm || readbpm.getpostbpm)) {
                        numBytes = mmInStream.read(mmBuffer);
                        String s = new String(mmBuffer, 0, numBytes);
                        String[] list_of_str = s.split(",");

                        for (String s1 : list_of_str) {
                            //Log.e("Tag","<Message> "+s1);
                            if (voltage_readings.size() < size) {
                                voltage_readings.add(Double.valueOf(s1) - 1.49);
                            }
                        }
                    }
                    /*
                    else if(MainActivity.active){
                       MainActivity.Update_bpm("BPM: --");
                    }

                     */

                    if(voltage_readings.size()==size){

                        Log.e("Tag","<DATACOM> Got 1024 Data");
                        int BPM = (convert(voltage_readings));

                        if((readbpm.getprebpm || readbpm.getpostbpm)){
                            int counter = 0;
                            boolean finger_detected=true;
                            for(int j=0;j<voltage_readings.size();j++){if(voltage_readings.get(j)<=0.1){counter++;}}

                            if(counter==size)
                            {
                                MainActivity.Update_bpm("No finger detected");
                                finger_detected=false;
                            }

                            if(readbpm.recordings.size()<4) {
                                if(finger_detected) {
                                    readbpm.recordings.add(BPM);
                                    MainActivity.Update_bpm(Integer.toString(BPM) + "\nBPM");
                                }
                            }
                            else{
                                if(readbpm.getprebpm){
                                    readbpm.getPreBPM();
                                    readbpm.getprebpm=false;
                                    MainActivity.button1.setText("Record Post Activity BPM");
                                }
                                else if(readbpm.getpostbpm){
                                    readbpm.getPostBPM();
                                    readbpm.getpostbpm=false;
                                    MainActivity.lock=false;
                                    MainActivity.button1.setText("Finish a session");
                                }
                            }
                        }

                        voltage_readings.clear();

                    }


                    // Send the obtained bytes to the UI activity.
//                    Message readMsg = handler.obtainMessage(
//                            MessageConstants.MESSAGE_READ, numBytes, -1,
//                            mmBuffer);
//                    readMsg.sendToTarget();
                    /*
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    }
                    catch (Exception e){

                    }
                    */
                } catch (IOException e) {
                    Log.d(TAG, "<Message> Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "<Message> Could not close the connect socket", e);
            }
        }
    }


}