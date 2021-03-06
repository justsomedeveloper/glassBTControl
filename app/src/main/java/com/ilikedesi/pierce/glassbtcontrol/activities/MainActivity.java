package com.ilikedesi.pierce.glassbtcontrol.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import com.ilikedesi.pierce.glassbtcontrol.R;

import java.util.Set;

/**
 * Created by pierce on 2/27/15.
 */
public class MainActivity extends Activity {

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mBondedDevices;
    TextView pairedDeviceCount;
    ImageView imageView;

    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGestureDetector = createGestureDetector(this);

        setContentView(R.layout.activity_main);
        pairedDeviceCount = (TextView) findViewById(R.id.pairedDeviceCount);
        imageView = (ImageView) findViewById(R.id.imageView);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        imageView.setImageResource(mBluetoothAdapter.isEnabled() ? R.drawable.ic_bluetooth_on_big :
                R.drawable.ic_bluetooth_off_big);

        mBondedDevices = mBluetoothAdapter.getBondedDevices();
        int count = mBondedDevices.size();
        Resources res = getResources();
        String songsFound = res.getQuantityString(R.plurals.numberOfPairedDevices, count, count);
        pairedDeviceCount.setText(songsFound);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        switch (item.getItemId()) {
            case R.id.action_paired:
                startActivity(new Intent(this, PairedDevicesListActivity.class));
                return true;
            case R.id.action_pair:
                startActivity(new Intent(this, PairDevicesActivity.class));
                return true;
            case R.id.action_scan:
                startActivity(new Intent(this, BleDevicesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    openOptionsMenu();
                    return true;
                }
                return false;
            }
        });
        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
}
