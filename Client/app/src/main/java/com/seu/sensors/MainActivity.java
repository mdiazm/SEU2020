package com.seu.sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.aware.Accelerometer;
import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;

import java.security.acl.AclEntry;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        Aware.startAWARE(this);
        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, true);
        Applications.isAccessibilityServiceActive(getApplicationContext());

        // Aware framework
         //   Aware.startAWARE(context);
            Aware.setSetting(context, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
            Aware.setSetting(context, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f);

            Aware.startAccelerometer(this);

    }
}
