package com.example.oli.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SMActivity extends AppCompatActivity {

    ToggleButton tb1, tb2, tb3;
    SeekBar seekBar;
    TextView tv;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {

                Toast.makeText(SMActivity.this, "Bluetooth Disconnecting", Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Toast.makeText(SMActivity.this, "Bluetooth Disconnected", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm);

        tb1 = (ToggleButton) findViewById(R.id.tb1);
        tb2 = (ToggleButton) findViewById(R.id.tb2);
        tb3 = (ToggleButton) findViewById(R.id.tb3);



        mySeekBar();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(broadcastReceiver, filter);

        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        tb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(SMActivity.this, "Light 1: " + tb1.getText(), Toast.LENGTH_SHORT).show();
                if (tb1.getText().equals("ON")) {
                    BluetoothConnectionService service = new BluetoothConnectionService(SMActivity.this);
                    String string = "A";
                    byte[] bytes = string.getBytes();
                    service.write(bytes);
                }

                if (tb1.getText().equals("OFF")) {
                    BluetoothConnectionService service = new BluetoothConnectionService(SMActivity.this);
                    String string = "a";
                    byte[] bytes = string.getBytes();
                    service.write(bytes);
                }

            }
        });

        tb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SMActivity.this, "Light 2: " + tb2.getText(), Toast.LENGTH_SHORT).show();

                if (tb2.getText().equals("ON")) {
                    BluetoothConnectionService service = new BluetoothConnectionService(SMActivity.this);
                    String string = "B";
                    byte[] bytes = string.getBytes();
                    service.write(bytes);
                }

                if (tb2.getText().equals("OFF")) {
                    BluetoothConnectionService service = new BluetoothConnectionService(SMActivity.this);
                    String string = "b";
                    byte[] bytes = string.getBytes();
                    service.write(bytes);
                }

            }
        });

        tb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(SMActivity.this, "Light 3: " + tb3.getText(), Toast.LENGTH_SHORT).show();

                if (tb3.getText().equals("ON")) {
                    BluetoothConnectionService service = new BluetoothConnectionService(SMActivity.this);
                    String string = "C";
                    byte[] bytes = string.getBytes();
                    service.write(bytes);
                }
                if (tb3.getText().equals("OFF")) {
                    BluetoothConnectionService service = new BluetoothConnectionService(SMActivity.this);
                    String string = "c";
                    byte[] bytes = string.getBytes();
                    service.write(bytes);
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    public void mySeekBar(){

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv = (TextView) findViewById(R.id.tvs);
        seekBar.setMax(255);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
                tv.setVisibility(View.VISIBLE);
                tv.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv.setVisibility(View.INVISIBLE);
                BluetoothConnectionService service = new BluetoothConnectionService(SMActivity.this);
                String string = progressValue+"";
                byte[] bytes = string.getBytes();
                service.write(bytes);
            }
        });
    }
}
