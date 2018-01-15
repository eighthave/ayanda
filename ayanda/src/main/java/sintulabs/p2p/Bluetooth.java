package sintulabs.p2p;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED;
import static android.bluetooth.BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
import static android.bluetooth.BluetoothAdapter.STATE_ON;


/**
 * Created by sabzo on 1/14/18.
 */

public class Bluetooth extends P2P {
    private Context context;
    BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    public static Integer REQUEST_ENABLE_BT = 1;
    public static Integer BT_ENABLED = 3000;


    public Bluetooth(Context context) {
        this.context = context;
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        createIntentFilter();
        createReceiver();
        registerReceivers();
    }

    @Override
    protected Boolean isSupported() {
       return  (mBluetoothAdapter == null)? false : true;
    }

    @Override
    protected Boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    private void enable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity)context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    /* Enable Bluetooth if it's supported but not yet enabled */
    @Override
    public void announce() {
        if ( isSupported() && !isEnabled()) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            context.startActivity(discoverableIntent);
        }
    }

    private void createIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STATE_CHANGED);
        intentFilter.addAction(ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
    }

    /* Bluetooth event handler */
    private void createReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                switch (action) {
                    case ACTION_DISCOVERY_STARTED:
                        actionDiscoveryStarted(intent);
                        break;
                    case ACTION_DISCOVERY_FINISHED:
                        actionDiscoveryFinished(intent);
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        deviceFound(intent);
                        break;
                    case ACTION_SCAN_MODE_CHANGED:
                        scanModeChange(intent);
                        break;
                    case ACTION_STATE_CHANGED:
                        stateChanged(intent);
                        break;
                }
            }

            private void scanModeChange(Intent intent) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    // discoverable and can be connected to
                    case SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Toast.makeText(context, "Device is connectable and discoverable", Toast.LENGTH_SHORT).show();
                        Log.d(TAG_DEBUG, "Device is connectable and discoverable");
                        break;
                    // not discoverable but connectable from previously paired devices
                    case SCAN_MODE_CONNECTABLE:
                        break;
                }
            }

            private void actionDiscoveryStarted(Intent intent) {
                Log.d(TAG_DEBUG, "Discovery started");
            }
            private void actionDiscoveryFinished(Intent intent) {
                Log.d(TAG_DEBUG, "Discovery finished");
            }

            /* Bluetooth enabled/disabled */
            private void stateChanged(Intent intent) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                   switch (state) {
                    // Bluetooth state changed: Is it On?
                    case STATE_ON:
                        if (!mBluetoothAdapter.startDiscovery()) {
                            Log.d(TAG_DEBUG, "unable to start bluetooth discovery");
                        };
                        break;
                }
            }


            private void deviceFound(Intent intent) {
                String s = "test";
            }

        };
    }

    /* Register/unregister Receiver */

    public void registerReceivers() {
        context.registerReceiver(receiver, intentFilter);
    }

    public void unregisterReceiver() {
        context.unregisterReceiver(receiver);
    }


    @Override
    public void discover() {
        if ( isSupported()) {
            if (!isEnabled()) {
                enable();
            }
        }
    }

    @Override
    protected void disconnect() {

    }

    @Override
    protected void send() {

    }

    @Override
    protected void cancel() {

    }
}
