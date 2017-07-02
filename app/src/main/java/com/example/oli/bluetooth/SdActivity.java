package com.example.oli.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.oli.bluetooth.adapters.DataAdapter;

import java.util.ArrayList;
import java.util.UUID;

public class SdActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothConnectionServ";
    private static final UUID my_uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    RecyclerView rv;
    ArrayList<BluetoothDevice> deviceList;
    BluetoothAdapter bluetoothAdapter;
    DataAdapter adapter;
    BluetoothConnectionService bluetoothConnectionService;
    BluetoothDevice mBTDevice;
    private final BroadcastReceiver createBondReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.e("BOND", "BOND_BONDED");
                    //Toast.makeText(SdActivity.this, "Bonded with: " +device.getName(), Toast.LENGTH_SHORT).show();
                    //finish();
                    mBTDevice = device;
                    startBTConnection(mBTDevice, my_uuid);

                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.e("BOND", "BOND_BONDING");

                }
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.e("BOND", "BOND_NONE");
                    Toast.makeText(SdActivity.this, "Failed to bond, try again.", Toast.LENGTH_SHORT).show();

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sd);

        deviceList = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Bundle");
        deviceList = (ArrayList<BluetoothDevice>) bundle.getSerializable("Arraylist");
        Log.e("DEVICE FOUND", deviceList.size() + "");

        rv = (RecyclerView) findViewById(R.id.rv2);
        rv.setLayoutManager(new LinearLayoutManager(SdActivity.this));
        if (deviceList.size() > 0) {
            adapter = new DataAdapter(SdActivity.this, deviceList);
            rv.setAdapter(adapter);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(createBondReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(createBondReceiver);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.e(TAG, "Initializing bluetooth connection");
        bluetoothConnectionService = new BluetoothConnectionService(this);
        bluetoothConnectionService.startClient(device, uuid);


    }
}
