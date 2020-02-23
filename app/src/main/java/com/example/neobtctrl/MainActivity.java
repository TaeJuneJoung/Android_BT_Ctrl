package com.example.neobtctrl;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button mBtnPaired;
    ListView deviceList;

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnPaired = (Button) findViewById(R.id.pairBtn);
        deviceList = (ListView) findViewById(R.id.btListView);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if ( mBluetoothAdapter == null ) {
            Toast.makeText(getApplicationContext(), "Bluetooth device not available", Toast.LENGTH_LONG).show();
            finish();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent turnOnBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnBT, 1);
        }

        mBtnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDeviceList();
            }
        });
    }

    private void pairedDeviceList() {
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                list.add(device.getName().toString() + "\n" + device.getAddress().toString());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found", Toast.LENGTH_SHORT).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(mListClickListener);
    }

    private AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length()-17);

            Intent intent = new Intent(MainActivity.this, ControlActivity.class);
            intent.putExtra(EXTRA_ADDRESS, address);
            startActivity(intent);
        }
    };
}
