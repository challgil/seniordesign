package com.example.christopher.seniordesign;

import android.bluetooth.*;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private List<Address> addresses;
    private Geocoder geocoder;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    EditText address;
    ListView disp;
    ListView disp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geocoder = new Geocoder(this);
        disp = (ListView) findViewById(R.id.disp);
      //  disp2 = (ListView) findViewById(R.id.disp2);
    }

    public void getLoc(View view){
        ArrayList<String> display = new ArrayList<String>();
        address = (EditText) findViewById(R.id.addressInput);
        String addressString = address.getText().toString();
        Double longitude = null;
        Double latitude = null;
        String longString = new String();
        String latString = new String();

        try {
            addresses = geocoder.getFromLocationName(addressString, 1);
        }
        catch(Throwable e){}
        if(addresses.size() > 0) {
            longitude = addresses.get(0).getLongitude();
            latitude = addresses.get(0).getLatitude();
            if(longitude > 0){ longString = "E" +  Double.toString(longitude);}
            else if(longitude <= 0 ){ longString = "W" + Double.toString(-1*longitude);}
            if(latitude > 0){ latString = "N" + Double.toString(latitude);}
            if(latitude <= 0){ latString = "S" + Double.toString(-1*latitude);}

        }
        sendLoc(longString, latString);
    }

    public void sendLoc(final String longitude, final String latitude){
        final BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        final BluetoothDevice bDevice;
        final ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
        ArrayAdapter<String> pairedDeviceArray = new ArrayAdapter<String>(this, R.layout.list_item, R.id.list_view_item);
        if (bAdapter == null) {
            return;
        }
        if (!bAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                pairedDeviceArray.add(device.getName() + "\n" + device.getAddress());
                list.add(device);
            }
        }
      //  if(list.size() > 0){ bDevice = list.get(0);}
        disp.setAdapter(pairedDeviceArray);
        disp.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mmDevice = list.get(position);
                bAdapter.cancelDiscovery();
                try {
                    // MY_UUID is the app's UUID string, also used by the server code
                    mmSocket = mmDevice.createRfcommSocketToServiceRecord(mmDevice.getUuids()[0].getUuid());
                } catch (IOException e) {
                }
                try {
                    // Connect the device through the socket. This will block
                    // until it succeeds or throws an exception
                    mmSocket.connect();
                } catch (IOException connectException) {
                    // Unable to connect; close the socket and get out
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) { }
                    return;
                }
                String destination = longitude + " " + latitude;
                manageConnectedSocket(destination);
            }
        });
        /*
        bAdapter.cancelDiscovery();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        String destination = longitude + " " + latitude;
        manageConnectedSocket(destination);
        */
    }

    void manageConnectedSocket(String msg){
        InputStream inStream = null;
        OutputStream outStream = null;

        // Get the input and output streams
        try {
            inStream = mmSocket.getInputStream();
            outStream = mmSocket.getOutputStream();
        } catch (IOException e) { }
        byte[] bytes = msg.getBytes();
        try {
            outStream.write(bytes);
        } catch(IOException e){}
    }
}