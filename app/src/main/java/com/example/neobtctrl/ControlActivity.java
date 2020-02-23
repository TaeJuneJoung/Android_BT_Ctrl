package com.example.neobtctrl;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {
    Button mOnBtn, mOffBtn;
    String address;
    private ProgressDialog progressDialog;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mBluetoothSocket;
    private boolean isBTConnected = false;
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);

        setContentView(R.layout.activity_ctrl);

        mOnBtn = (Button) findViewById(R.id.onBtn);
        mOffBtn = (Button) findViewById(R.id.offBtn);

        new ConnectBT().execute();

        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendString("on");
            }
        });

        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendString("off");
            }
        });
    }

    private void sendString(String value) {
        if ( mBluetoothSocket != null ) {
            try {
                mBluetoothSocket.getOutputStream().write(value.toString().getBytes());
            } catch ( IOException e ) {
                msg("Error");
            }
        }
    }

    private void Disconnect() {
        if ( mBluetoothSocket != null ) {
            try {
                mBluetoothSocket.close();
            } catch ( IOException e ) {
                msg("Error");
            }
        }
        finish();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ControlActivity.this, "Connecting...", "Please Wait!");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if ( mBluetoothSocket == null || !isBTConnected ) {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = mBluetoothAdapter.getRemoteDevice(address);
                    mBluetoothSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(mUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBluetoothSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if ( !ConnectSuccess ) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected");
                isBTConnected = true;
            }
            progressDialog.dismiss();
        }
    }
}
