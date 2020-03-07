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
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.aware.Accelerometer;
import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Gyroscope;
import com.aware.providers.Accelerometer_Provider;

import java.security.acl.AclEntry;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Volvemos al tema por defecto de la app
        setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        AddSensorsItems();

        // Aware framework
        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        Aware.startAWARE(this);
        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, true);
        Applications.isAccessibilityServiceActive(getApplicationContext());

        // Configure parameters of the accelerometer
        Aware.setSetting(context, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
        Aware.setSetting(context, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f);

        // Start accelerometer service
        Aware.startAccelerometer(this);

        // Set a observer to accelerometer (listener) that sees changes
        Accelerometer.setSensorObserver(new Accelerometer.AWARESensorObserver() {
            @Override
            public void onAccelerometerChanged(ContentValues data) {
                Log.d("TAG", data.toString());
            }
        });

    }

    public void AddSensorsItems(){
        RecyclerView recyclerView = findViewById(R.id.list_view);
        //Ponemos un tamaño fijo a la lista
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        ArrayList<Sensor> arrayList = new ArrayList<>();
        arrayList.add( new Sensor("Giroscopio" , true, R.drawable.ic_action_gyroscope, this));
        ///> Giroscopio
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_GYROSCOPE, 200000);
        Aware.setSetting(this, Aware_Preferences.THRESHOLD_GYROSCOPE, 0.02f);
        Aware.startGyroscope(this);

        arrayList.add(new Sensor("Acelerómetro" , true, R.drawable.ic_action_accelerometer, this));
        ///> Acelerómetro
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
        Aware.setSetting(this, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f);
        Aware.startAccelerometer(this);
        arrayList.add(new Sensor("GPS" , false, R.drawable.ic_action_locations, this));
        arrayList.add(new Sensor("Micrófono" , false, R.drawable.ic_action_ambient_noise, this));
        arrayList.add(new Sensor("Luminosidad" , true, R.drawable.ic_action_light, this));
        ///> Luminosidad
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_LIGHT, 200000);
        Aware.setSetting(this, Aware_Preferences.THRESHOLD_LIGHT, 0.02f);
        Aware.startLight(this);
        arrayList.add(new Sensor("Proximidad" , false, R.drawable.ic_action_proximity, this));
        ///> Temperatura
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_TEMPERATURE, 200000);
        Aware.setSetting(this, Aware_Preferences.THRESHOLD_TEMPERATURE, 0.02f);
        Aware.startTemperature(this);
        arrayList.add(new Sensor("Uso de apps" , false, R.drawable.ic_action_applications, this));
        arrayList.add(new Sensor("Batería" , false, R.drawable.ic_action_battery, this));
        arrayList.add(new Sensor("Barómetro" , false, R.drawable.ic_action_barometer, this));
        arrayList.add(new Sensor("MQTT" , false, R.drawable.ic_action_communication, this));

        mAdapter = new MyAdapter(arrayList, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                System.out.println("clicao " + item);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }


}
