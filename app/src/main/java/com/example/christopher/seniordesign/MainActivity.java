package com.example.christopher.seniordesign;

import android.bluetooth.*;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private List<Address> addresses;
    private Geocoder geocoder;
    EditText address;
    ListView disp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geocoder = new Geocoder(this);
        disp = (ListView) findViewById(R.id.disp);
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
        if(longitude != null && latitude != null) {
            display.add(latString + " " + longString);
            ArrayAdapter<String> that = new ArrayAdapter<String>(this, R.layout.list_item, R.id.list_view_item, display);
            disp.setAdapter(that);
            address.setText("");
        }
        else{
            display.add("Something went wrong! " + addressString);
            ArrayAdapter<String> that = new ArrayAdapter<String>(this, R.layout.list_item, R.id.list_view_item, display);
            disp.setAdapter(that);
            address.setText("");
        }
        sendLoc(longString, latString);
    }

    public void sendLoc(String longitude, String latitude){
        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bDevice;
        ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
        ArrayAdapter<String> pairedDeviceArray = new ArrayAdapter<String>(this, R.layout.device_selection);
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
        if(list.size() > 0){ bDevice = list.get(0);}

    }
}