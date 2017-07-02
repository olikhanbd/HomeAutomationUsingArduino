package com.example.oli.bluetooth.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.oli.bluetooth.BluetoothConnectionService;
import com.example.oli.bluetooth.R;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by oli on 6/15/17.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private Context context;
    private ArrayList<BluetoothDevice> deviceList;
    private BluetoothConnectionService bluetoothConnectionService;

    public DataAdapter(Context context, ArrayList<BluetoothDevice> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
        Log.e("DEVICE FOUND", deviceList.size() + "");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pdlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BluetoothDevice device = deviceList.get(position);

        holder.name_tv.setText(device.getName());
        holder.address_tv.setText(device.getAddress());
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {

                    bluetoothConnectionService = new BluetoothConnectionService(context);
                    bluetoothConnectionService.startClient(device, uuid);

                } else {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        device.createBond();
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name_tv, address_tv;
        LinearLayout ll;

        public ViewHolder(View itemView) {
            super(itemView);
            name_tv = (TextView) itemView.findViewById(R.id.name_tv);
            address_tv = (TextView) itemView.findViewById(R.id.address_tv);
            ll = (LinearLayout) itemView.findViewById(R.id.ll);
        }
    }

}
