package com.ilikedesi.pierce.glassbtcontrol.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardScrollView;

import com.ilikedesi.pierce.glassbtcontrol.adapters.BleDeviceCardScrollAdapter;
import com.ilikedesi.pierce.glassbtcontrol.BleDevice;
import com.ilikedesi.pierce.glassbtcontrol.R;

import java.util.ArrayList;
import java.util.List;


public class BleDevicesActivity extends Activity implements AdapterView.OnItemClickListener, BluetoothAdapter.LeScanCallback {

    BluetoothAdapter mBluetoothAdapter;
    CardScrollView mCardScrollView;
    BleDeviceCardScrollAdapter adapter;
    BleDevice mSelectedDevice;
    List<BleDevice> mDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("onCreate", "Got default BT adapter.");

        mBluetoothAdapter.startLeScan(this);
        Log.d("onCreate", "Started BTLE discovery...");

        mCardScrollView = new CardScrollView(this);
        mCardScrollView.activate();
        mCardScrollView.setOnItemClickListener(this);
        mCardScrollView.setHorizontalScrollBarEnabled(true);
        setContentView(mCardScrollView);

        mDevices = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBluetoothAdapter.stopLeScan(this);
        Log.d("onDestroy", "Canceled BTLE discovery.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ble, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        switch (item.getItemId()) {
            case R.id.connect:
                Intent intent = new Intent(this, BleServicesActivity.class);
                intent.putExtra(BleServicesActivity.EXTRA_DEVICE_ADDRESS, mSelectedDevice.getAddress());
                startActivity(intent);
                Log.d("onOptionsItemSelected", "Connecting to device " + mSelectedDevice.getName() + "...");
                mBluetoothAdapter.stopLeScan(this);
                Log.d("onOptionsItemSelected", "Canceled BTLE discovery.");
                return true;
            case R.id.action_scan:
                startActivity(new Intent(this, BleDevicesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.playSoundEffect(Sounds.TAP);
        mSelectedDevice = adapter.getItem(position);
        openOptionsMenu();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] bytes) {

        if (device.getName() != null) {
            BleDevice bleDevice = new BleDevice(device, rssi);

            if (!mDevices.contains(bleDevice)) {
                mDevices.add(bleDevice);
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audio.playSoundEffect(Sounds.SUCCESS);
            } else {
                int index = mDevices.indexOf(bleDevice);
                mDevices.get(index).setRssi(rssi);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new BleDeviceCardScrollAdapter(BleDevicesActivity.this, mDevices);
                    mCardScrollView.setAdapter(adapter);
                }
            });
        }
    }
}
