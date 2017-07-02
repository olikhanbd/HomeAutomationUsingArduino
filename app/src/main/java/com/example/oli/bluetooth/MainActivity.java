package com.example.oli.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btn1, btn2, btn3, btn4;
    BluetoothAdapter adapter;
    ArrayList<BluetoothDevice> deviceList;
    ProgressDialog dialog;
    private final BroadcastReceiver dBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Log.e("TAG", "Device Found");
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.e("TAG", "discovery started");
                dialog.show();
            }
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Log.e("TAG", "discovery finished");
                dialog.dismiss();
                Intent intent1 = new Intent(MainActivity.this, SdActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Arraylist", deviceList);
                intent1.putExtra("Bundle", bundle);
                if (deviceList.size() > 0) {
                    startActivity(intent1);
                } else {
                    Toast.makeText(MainActivity.this, "No nearby device found!", Toast.LENGTH_SHORT).show();
                }
            }

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                Log.e("TAG", "Device Found");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("Name: ", device.getName() + "");
                deviceList.add(device);
            }
        }
    };
    private int ENABLE_RQST_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = BluetoothAdapter.getDefaultAdapter();

        btn1 = (Button) findViewById(R.id.btn_enable);
        btn2 = (Button) findViewById(R.id.btn_pd);
        btn3 = (Button) findViewById(R.id.btn_sd);
        btn4 = (Button) findViewById(R.id.btn_sm);

        if (adapter.isEnabled()) {
            btn1.setText("Bluetooth: ON");
        } else {
            btn1.setText("Bluetooth: OFF");
        }


        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(dBroadcastReceiver, filter);


        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }

        deviceList = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Scanning...");
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                adapter.cancelDiscovery();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!adapter.isEnabled()) {
                    Toast.makeText(MainActivity.this, "Turn on Bluetooth first", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(MainActivity.this, SMActivity.class));
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, SdActivity.class));
                if (!adapter.isEnabled()) {
                    Toast.makeText(MainActivity.this, "Turn on Bluetooth first", Toast.LENGTH_SHORT).show();
                } else
                    discover();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!adapter.isEnabled()) {
                    Toast.makeText(MainActivity.this, "Turn on Bluetooth first", Toast.LENGTH_SHORT).show();
                } else
                    startActivity(new Intent(MainActivity.this, PdActivity.class));
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = BluetoothAdapter.getDefaultAdapter();
                if (!adapter.isEnabled()) {

                    builder.setTitle("Bluetooth")
                            .setMessage("Do you want to turn on Bluetooth?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    enableBluetooth();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();


                }
                if (adapter.isEnabled()) {
                    builder.setTitle("Bluetooth")
                            .setMessage("Do you want to turn off Bluetooth?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    disableBluetooth();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }
            }
        });
    }

    private void discover() {
        deviceList.clear();
        adapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        if (dBroadcastReceiver != null) {
            unregisterReceiver(dBroadcastReceiver);
        }
        super.onDestroy();
    }


    public void enableBluetooth() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, ENABLE_RQST_BT);
        btn1.setText("Bluetooth: ON");
    }

    public void disableBluetooth() {
        adapter.disable();
        btn1.setText("Bluetooth: OFF");
    }

}
