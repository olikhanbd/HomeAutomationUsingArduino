package com.example.oli.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appname = "BLUETOOTH";
    private static final UUID my_uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    static boolean connected = false;
    private static BluetoothSocket cSocket;
    private final BluetoothAdapter mAdapter;
    private Context context;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private UUID deviceUUID;
    private BluetoothDevice mmDevice;
    private ProgressDialog dialog;
    private ConnectedThread connectedThread;

    public BluetoothConnectionService(Context context) {
        this.context = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    public synchronized void start() {
        Log.e(TAG, "start");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device, UUID uuid) {
        Log.e(TAG, "ClientStart");

        dialog = ProgressDialog.show(context, "Connecting to bluetooth", "Please wait...", true);

        connectThread = new ConnectThread(device, uuid);
        connectThread.start();
    }

    public void connected(BluetoothSocket socket) {
        Log.e(TAG, "Connected Starting");
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    public void write(byte[] out) {
        Log.e(TAG, "write called");
        if (cSocket != null) {
            ConnectedThread mConnectedThread = new ConnectedThread(cSocket);
            mConnectedThread.write(out);
        } else {
            Toast.makeText(context, "Bluetooth not connected", Toast.LENGTH_SHORT).show();
        }

    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            Log.e(TAG, "Accept thread start");
            BluetoothServerSocket tmp = null;

            try {
                tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(appname, my_uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;

            while (true) {
                try {
                    Log.e(TAG, "RUN: rfcomm server socket start");
                    socket = mmServerSocket.accept();
                    Log.e(TAG, "RUN: rfcomm server socket connection accepted");
                } catch (IOException e) {
                    Log.e(TAG, "RUN: rfcomm server socket connection not accepted");
                    e.printStackTrace();
                    break;
                }

                if (socket != null) {
                    //do something
                    connected(socket);
                    try {
                        mmServerSocket.close();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void cancel() {
            Log.e(TAG, "Canceling server socket");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class ConnectThread extends Thread {
        BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.e(TAG, "ConnectThread: started");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run() {
            Log.e(TAG, "Run: ConnectThread");
            BluetoothSocket tmp = null;

            try {
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            cSocket = tmp;
            mmSocket = tmp;

            mAdapter.cancelDiscovery();

            Handler handler = new Handler(Looper.getMainLooper());

            try {
                mmSocket.connect();
                Log.e(TAG, "Run: ConnectThread connected");
                dialog.dismiss();
                connected = true;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Connected to: " +mmDevice.getName(), Toast.LENGTH_SHORT).show();
                    }
                });

                if (context instanceof PdActivity) {
                    ((PdActivity) context).finish();
                } else if (context instanceof SdActivity) {
                    ((SdActivity) context).finish();
                }


            } catch (IOException e) {
                try {
                    mmSocket.close();
                    dialog.dismiss();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e(TAG, "Run: ConnectThread closed");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

            //connected(mmSocket);
        }

        public void cancel() {
            Log.e(TAG, "Canceling client socket");
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.e(TAG, "ConnectedThread starting");
            mmSocket = socket;
            //dialog.dismiss();

            InputStream itmp = null;
            OutputStream otmp = null;

            try {
                itmp = mmSocket.getInputStream();
                otmp = mmSocket.getOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = itmp;
            outputStream = otmp;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read();
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.e(TAG, "Message: " + incomingMessage);
                } catch (IOException e) {
                    Log.e(TAG, "Error reading input stream: " + e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.e(TAG, "Writting to output stream: " + text);

            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error writting output stream: " + text);
            }
        }

        public void cancel() {
            Log.e(TAG, "Canceling client socket");
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
