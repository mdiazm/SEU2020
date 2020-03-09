package com.seu.sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    ArrayList<Sensor> arrayList = new ArrayList<>();

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

        GuardarDatos();

    }

    public void AddSensorsItems(){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean save = myPreferences.getBoolean("save_sensor", false);
        RecyclerView recyclerView = findViewById(R.id.list_view);
        //Ponemos un tamaño fijo a la lista
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        if(!save) {
            System.out.println("de cero");
            arrayList.add(new Sensor("Giroscopio", true, R.drawable.ic_action_gyroscope, "giroscopio", this));
            ///> Giroscopio
            Aware.setSetting(this, Aware_Preferences.FREQUENCY_GYROSCOPE, 200000);
            Aware.setSetting(this, Aware_Preferences.THRESHOLD_GYROSCOPE, 0.02f);
            Aware.startGyroscope(this);

            arrayList.add(new Sensor("Acelerómetro", true, R.drawable.ic_action_accelerometer, "acelerometro" , this));
            ///> Acelerómetro
            Aware.setSetting(this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
            Aware.setSetting(this, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f);
            Aware.startAccelerometer(this);
            arrayList.add(new Sensor("GPS", false, R.drawable.ic_action_locations, "gps", this));
            arrayList.add(new Sensor("Micrófono", false, R.drawable.ic_action_ambient_noise, "microfono", this));
            arrayList.add(new Sensor("Luminosidad", true, R.drawable.ic_action_light, "luminosidad", this));
            ///> Luminosidad
            Aware.setSetting(this, Aware_Preferences.FREQUENCY_LIGHT, 200000);
            Aware.setSetting(this, Aware_Preferences.THRESHOLD_LIGHT, 0.02f);
            Aware.startLight(this);
            arrayList.add(new Sensor("Proximidad", false, R.drawable.ic_action_proximity, "proximidad", this));
            ///> Temperatura
            Aware.setSetting(this, Aware_Preferences.FREQUENCY_TEMPERATURE, 200000);
            Aware.setSetting(this, Aware_Preferences.THRESHOLD_TEMPERATURE, 0.02f);
            Aware.startTemperature(this);
            arrayList.add(new Sensor("Temperatura", true, R.drawable.ic_action_temperature, "temperatura", this));
            arrayList.add(new Sensor("Uso de apps", false, R.drawable.ic_action_applications, "uso_apps", this));
            arrayList.add(new Sensor("Batería", false, R.drawable.ic_action_battery, "bateria", this));
            arrayList.add(new Sensor("Barómetro", false, R.drawable.ic_action_barometer, "barometro", this));
            arrayList.add(new Sensor("MQTT", false, R.drawable.ic_action_communication, "mqtt" ,this));
        }else{
            System.out.println("restore");

            boolean item = myPreferences.getBoolean("giroscopio", false);

            arrayList.add(new Sensor("Giroscopio", item, R.drawable.ic_action_gyroscope, "giroscopio", this));
            if(item) {
                ///> Giroscopio
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_GYROSCOPE, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_GYROSCOPE, 0.02f);
                Aware.startGyroscope(this);
            }

            item = myPreferences.getBoolean("acelerometro", false);

            arrayList.add(new Sensor("Acelerómetro", item, R.drawable.ic_action_accelerometer, "acelerometro", this));
            if(item) {
                ///> Acelerómetro
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f);
                Aware.startAccelerometer(this);
            }

            item = myPreferences.getBoolean("gps", false);
            arrayList.add(new Sensor("GPS", item, R.drawable.ic_action_locations, "gps", this));
            if(item){
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_LOCATION_GPS, 200000);
               // Aware.setSetting(this, Aware_Preferences.LOCA, 0.02f);
                Aware.startLocations(this);
            }

            item = myPreferences.getBoolean("microfono", false);
            arrayList.add(new Sensor("Micrófono", item, R.drawable.ic_action_ambient_noise, "microfono", this));
            if(item) {
            }

            item = myPreferences.getBoolean("luminosidad", false);
            arrayList.add(new Sensor("Luminosidad", item, R.drawable.ic_action_light, "luminosidad", this));
            if(item) {
                ///> Luminosidad
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_LIGHT, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_LIGHT, 0.02f);
                Aware.startLight(this);
            }

            item = myPreferences.getBoolean("proximidad", false);
            arrayList.add(new Sensor("Proximidad", item, R.drawable.ic_action_proximity, "proximidad", this));
            if(item){
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_PROXIMITY, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_PROXIMITY, 0.02f);
                Aware.startLight(this);
            }

            item = myPreferences.getBoolean("temperatura", false);
            arrayList.add(new Sensor("temperatura", item, R.drawable.ic_action_temperature, "temperatura", this));
            if(item) {
                ///> Temperatura
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_TEMPERATURE, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_TEMPERATURE, 0.02f);
                Aware.startTemperature(this);
            }

            item = myPreferences.getBoolean("uso_apps", false);
            arrayList.add(new Sensor("Uso de apps", item, R.drawable.ic_action_applications, "uso_apps", this));
            if(item){
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_APPLICATIONS, 200000);
             //   Aware.startApplications(this);
            }

            item = myPreferences.getBoolean("bateria", false);
            arrayList.add(new Sensor("Batería", item, R.drawable.ic_action_battery, "bateria" ,this));
            if(item){
               //Aware.setSetting(this, Aware_Preferences.FREQ, 200000);
               // Aware.setSetting(this, Aware_Preferences.THRESHOLD_TEMPERATURE, 0.02f);
                Aware.startBattery(this);
            }

            item = myPreferences.getBoolean("bateria", false);
            if(item){
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_BAROMETER, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_BAROMETER, 0.02f);
                Aware.startBarometer(this);
            }
            arrayList.add(new Sensor("Barómetro", item, R.drawable.ic_action_barometer, "barometro", this));

            item = myPreferences.getBoolean("mqtt", false);
            arrayList.add(new Sensor("MQTT", item, R.drawable.ic_action_communication, "mqtt", this));

        }
        mAdapter = new MyAdapter(arrayList, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    public void GuardarDatos(){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putBoolean( arrayList.get(0).getKey(), arrayList.get(0).getState());
        editor.putBoolean(arrayList.get(1).getKey(), arrayList.get(1).getState());
        editor.putBoolean(arrayList.get(2).getKey(), arrayList.get(2).getState());
        editor.putBoolean(arrayList.get(3).getKey(), arrayList.get(3).getState());
        editor.putBoolean(arrayList.get(4).getKey(), arrayList.get(4).getState());
        editor.putBoolean(arrayList.get(5).getKey(), arrayList.get(5).getState());
        editor.putBoolean(arrayList.get(6).getKey(), arrayList.get(6).getState());
        editor.putBoolean(arrayList.get(7).getKey(), arrayList.get(7).getState());
        editor.putBoolean(arrayList.get(8).getKey(), arrayList.get(8).getState());
        editor.putBoolean(arrayList.get(9).getKey(), arrayList.get(9).getState());
        editor.putBoolean(arrayList.get(10).getKey(), arrayList.get(10).getState());

        editor.putBoolean("save_sensor", true);

        editor.commit();
    }

    @Override
    public void onPause(){
        super.onPause();
        GuardarDatos();
    }

}
