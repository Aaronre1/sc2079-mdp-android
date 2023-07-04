package edu.ntu.scse.test.ui.bluetooth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothClient extends Thread {

    private static final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private ProgressDialog progressDialog;

    private final BluetoothAdapter bluetoothAdapter;
    Context context;
    public BluetoothClient(String pairdeviceAddress,Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(pairdeviceAddress);
        this.context = context;
        BluetoothSocket tmpSocket = null;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Connecting");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        try {
            tmpSocket = device.createRfcommSocketToServiceRecord(APP_UUID);
            tmpIn = tmpSocket.getInputStream();
            tmpOut = tmpSocket.getOutputStream();
        } catch (IOException e) {
            Log.e("BluetoothClient", "Socket's create() method failed", e);
        }catch (SecurityException e) {
            Log.e("BluetoothClient", "Permission missing for createRfcommSocketToServiceRecord", e);
        }
        Log.d("BluetoothClient", "tmpSocket>>> "+ tmpSocket);
        socket = tmpSocket;
        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    public void run() {
        int maxRetryCount = 2; // Maximum number of retries
        int retryCount = 0; // Current retry count

        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.show();
            }
        });

        while (retryCount < maxRetryCount) {
            try {
                // Adding a delay of 5 seconds before each connection attempt
                Thread.sleep(50000);
                socket.connect();
                // If the connection is successful, break from the loop
                break;
            } catch (IOException connectException) {
                retryCount++; // Increment the retry count
                Log.e("BluetoothClient", "Could not connect the client socket. Attempt " + retryCount, connectException);
                // If the maximum retry count is reached, clean up the resources and stop the thread
                if (retryCount == maxRetryCount) {
                    cleanup();
                    return;
                }
            } catch (InterruptedException e) {
                Log.e("BluetoothClient", "Sleep interrupted", e);
                cleanup();
                return;
            } catch (SecurityException e) {
                Log.e("BluetoothClient", "Permission missing for socket connect", e);
                cleanup();
                return;
            }
        }

        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss(); // Dismiss the ProgressDialog
                Toast.makeText(context, "Connection established", Toast.LENGTH_SHORT).show();
            }
        });

        if (socket.isConnected()) {
            for(int i =0;i<10;i++){
                sendData("Test String: "  + i);
            }

            receiveData();

        } else {
            cleanup();
        }
    }

    public void sendData(String data) {
        Log.i("BluetoothClient", "calling sendData()...");
        if (outputStream != null) {
            try {
                outputStream.write(data.getBytes());
                Log.i("BluetoothClient", "Data sent: " + data);
            } catch (IOException e) {
                Log.e("BluetoothClient", "Error occurred when sending data", e);
            }
        }
    }
    public void receiveData() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = inputStream.read(buffer);
                String received = new String(buffer, 0, bytes);
                Log.i("BluetoothClient", "Received data: " + received);
            } catch (IOException e) {
                Log.e("BluetoothClient", "Error occurred when receiving data", e);
                break;
            }
        }
    }
    public void stopClient() {
        cleanup();
    }

    private void cleanup() {

        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss(); // Dismiss the ProgressDialog
            }
        });

        if (socket != null) {
            try {
                socket.close();
                Log.i("BluetoothClient", "Client socket closed");
            } catch (IOException e) {
                Log.e("BluetoothClient", "Could not close the client socket", e);
            }
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e("BluetoothClient", "Could not close the input stream", e);
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e("BluetoothClient", "Could not close the output stream", e);
            }
        }
    }
}

