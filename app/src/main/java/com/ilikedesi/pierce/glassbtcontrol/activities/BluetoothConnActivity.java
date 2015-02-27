package com.ilikedesi.pierce.glassbtcontrol.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.ilikedesi.pierce.glassbtcontrol.R;

/**
 * Created by pierce on 2/27/15.
 */
public class BluetoothConnActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothconn);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
