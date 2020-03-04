package com.seu.sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();


        RecyclerView recyclerView = findViewById(R.id.list_view);
        //Ponemos un tamaño fijo a la lista
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        ArrayList<Sensor> arrayList = new ArrayList<>();
        arrayList.add( new Sensor("Giroscopio" , false, R.drawable.ic_action_gyroscope));
        arrayList.add(new Sensor("Acelerómetro" , false, R.drawable.ic_action_accelerometer));
        arrayList.add(new Sensor("GPS" , false, R.drawable.ic_action_locations));
        arrayList.add(new Sensor("Micrófono" , false, R.drawable.ic_action_ambient_noise));
        arrayList.add(new Sensor("Luminosidad" , false, R.drawable.ic_action_light));
        arrayList.add(new Sensor("Proximidad" , false, R.drawable.ic_action_proximity));
        arrayList.add(new Sensor("Uso de apps" , false, R.drawable.ic_action_applications));
        arrayList.add(new Sensor("Batería" , false, R.drawable.ic_action_battery));
        arrayList.add(new Sensor("Barómetro" , false, R.drawable.ic_action_barometer));
        arrayList.add(new Sensor("MQTT" , false, R.drawable.ic_action_communication));

        mAdapter = new MyAdapter(arrayList, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                System.out.println("clicao " + item);
            }
        });
        recyclerView.setAdapter(mAdapter);

        /*
        // Aware framework
        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        Aware.startAWARE(this);
        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, true);
        Applications.isAccessibilityServiceActive(getApplicationContext());

        Aware.setSetting(context, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
        Aware.setSetting(context, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f);

        Aware.startAccelerometer(this);

        Applications.setSensorObserver(new Applications.AWARESensorObserver() {
            @Override public void onForeground(ContentValues data) {
                Log.d("aware debuggg", data.toString());
            }

            @Override public void onNotification(ContentValues data) {

            }

            @Override public void onCrash(ContentValues data) {

            }

            @Override public void onKeyboard(ContentValues data) {

            }

            @Override public void onBackground(ContentValues data) {

            }

            @Override public void onTouch(ContentValues data) {

            }
        });
        */

    }
}
