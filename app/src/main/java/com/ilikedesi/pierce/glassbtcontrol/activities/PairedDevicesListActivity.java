package com.ilikedesi.pierce.glassbtcontrol.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardScrollView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ilikedesi.pierce.glassbtcontrol.adapters.BTDeviceCardScrollAdapter;
import com.ilikedesi.pierce.glassbtcontrol.R;
/**
 * Created by pierce on 2/27/15.
 */
public class PairedDevicesListActivity  extends Activity implements AdapterView.OnItemClickListener {

    BluetoothAdapter mBluetoothAdapter;
    List<BluetoothDevice> mBondedDevices;
    CardScrollView mCardScrollView;
    BluetoothDevice mSelectedDevice;
    BTDeviceCardScrollAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("onCreate", "Got default BT adapter.");

        mCardScrollView = new CardScrollView(this);
        mCardScrollView.activate();
        mCardScrollView.setOnItemClickListener(this);
        setContentView(mCardScrollView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        mBondedDevices = new ArrayList<>(devices);
        adapter = new BTDeviceCardScrollAdapter(this, mBondedDevices);
        mCardScrollView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSelectedDevice = (BluetoothDevice) mCardScrollView.getItemAtPosition(position);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.playSoundEffect(Sounds.TAP);
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.device, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.unpair:
                removeBond(mSelectedDevice);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeBond(BluetoothDevice device) {
        try {
            Log.d("removeBond", "Unpairing BT device " + device.getName() + "...");
            Class<?> btDeviceInstance =  Class.forName(BluetoothDevice.class.getCanonicalName());
            Method removeBondMethod = btDeviceInstance.getMethod("removeBond");
            removeBondMethod.invoke(device);
            mBondedDevices.remove(device);
            adapter.notifyDataSetChanged();
        } catch (Throwable th) {
            Log.e("removeBond", "Exception thrown", th);
            return;
        }
        Log.d("removeBond", "Device " + device.getName() + " unpaired.");
    }
}

