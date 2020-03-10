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
import com.aware.Barometer;
import com.aware.Battery;
import com.aware.Gyroscope;
import com.aware.Light;
import com.aware.Locations;
import com.aware.Mqtt;
import com.aware.Proximity;
import com.aware.providers.Accelerometer_Provider;
import com.aware.providers.Barometer_Provider;
import com.aware.providers.Battery_Provider;
import com.aware.providers.Gyroscope_Provider;
import com.aware.providers.Light_Provider;
import com.aware.providers.Locations_Provider;
import com.aware.providers.Proximity_Provider;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.security.acl.AclEntry;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    ArrayList<Sensor> arrayList = new ArrayList<>();

    private MQTT mqtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Volvemos al tema por defecto de la app
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);


        // Aware framework
        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        Aware.startAWARE(this);
        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, true);
        Applications.isAccessibilityServiceActive(getApplicationContext());

        ///> Añadir los datos de los sensores
        AddSensorsItems();


        ///> Inicializar los observadores de los sensores
        InitListener();

        ///> Guardar los datos de los sensores
        GuardarDatos();

        mqtt = new MQTT("192.168.2.1");
          mqtt.init();
    }

    public void InitListener(){
        Accelerometer.setSensorObserver(new Accelerometer.AWARESensorObserver() {
            @Override
            public void onAccelerometerChanged(ContentValues data) {
                String x = data.get(Accelerometer_Provider.Accelerometer_Data.VALUES_0).toString();
                String y = data.get(Accelerometer_Provider.Accelerometer_Data.VALUES_1).toString();
                String z = data.get(Accelerometer_Provider.Accelerometer_Data.VALUES_2).toString();
                String device = data.get(Accelerometer_Provider.Accelerometer_Data.DEVICE_ID).toString();
                String timestamp = data.get( Accelerometer_Provider.Accelerometer_Data.TIMESTAMP).toString();

                try {
                    JSONObject json = new JSONObject();
                    json.put("device", device);
                    json.put("timestamp", timestamp);
                    json.put("x", x);
                    json.put("y", y);
                    json.put("z", z);

                    mqtt.sendMessage("accelerometer", new MqttMessage(json.toString().getBytes()));

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        Gyroscope.setSensorObserver(new Gyroscope.AWARESensorObserver() {
            @Override
            public void onGyroscopeChanged(ContentValues data) {
                String x = data.get(Gyroscope_Provider.Gyroscope_Data.VALUES_0).toString();
                String y = data.get(Gyroscope_Provider.Gyroscope_Data.VALUES_1).toString();
                String z = data.get(Gyroscope_Provider.Gyroscope_Data.VALUES_2).toString();
                String device = data.get(Gyroscope_Provider.Gyroscope_Data.DEVICE_ID).toString();
                String timestamp = data.get(Gyroscope_Provider.Gyroscope_Data.TIMESTAMP).toString();

                try {
                    JSONObject json = new JSONObject();
                    json.put("device", device);
                    json.put("timestamp", timestamp);
                    json.put("x", x);
                    json.put("y", y);
                    json.put("z", z);

                    mqtt.sendMessage("gyroscope", new MqttMessage(json.toString().getBytes()));

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        Locations.setSensorObserver(new Locations.AWARESensorObserver() {
            @Override
            public void onLocationChanged(ContentValues data) {

                try {
                    JSONObject json = new JSONObject();
                    String device = data.getAsString(Locations_Provider.Locations_Data.DEVICE_ID);
                    String timestamp = data.getAsString(Locations_Provider.Locations_Data.TIMESTAMP);
                    String latitude = data.getAsString(Locations_Provider.Locations_Data.LATITUDE);
                    String longitude = data.getAsString(Locations_Provider.Locations_Data.LONGITUDE);
                    String bearing = data.getAsString(Locations_Provider.Locations_Data.BEARING);
                    String speed = data.getAsString(Locations_Provider.Locations_Data.SPEED);
                    String altitude = data.getAsString(Locations_Provider.Locations_Data.ALTITUDE);
                    String provider = data.getAsString(Locations_Provider.Locations_Data.PROVIDER);
                    String accuracy = data.getAsString(Locations_Provider.Locations_Data.ACCURACY);

                    json.put("device", device);
                    json.put("timestamp", timestamp);
                    json.put("latitude", latitude);
                    json.put("longitude", longitude);
                    json.put("bearing", bearing);
                    json.put("speed", speed);
                    json.put("provider", provider);
                    json.put("altitude", altitude);
                    json.put("accuracy", accuracy);

                    mqtt.sendMessage("gps", new MqttMessage(json.toString().getBytes()));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Light.setSensorObserver(new Light.AWARESensorObserver() {
            @Override
            public void onLightChanged(ContentValues data) {
                String lux = data.get(Light_Provider.Light_Data.LIGHT_LUX).toString();
                String device = data.get(Gyroscope_Provider.Gyroscope_Data.DEVICE_ID).toString();
                String timestamp = data.get(Gyroscope_Provider.Gyroscope_Data.TIMESTAMP).toString();

                try {
                    JSONObject json = new JSONObject();
                    json.put("device", device);
                    json.put("timestamp", timestamp);
                    json.put("lux", lux);

                    mqtt.sendMessage("light", new MqttMessage(json.toString().getBytes()));

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        Proximity.setSensorObserver(new Proximity.AWARESensorObserver() {
            @Override
            public void onProximityChanged(ContentValues data) {
                String device = data.getAsString(Proximity_Provider.Proximity_Data.DEVICE_ID);
                String timestamp = data.getAsString(Proximity_Provider.Proximity_Data.TIMESTAMP);
                String proximity = data.getAsString(Proximity_Provider.Proximity_Data.PROXIMITY);
                try {
                    JSONObject json = new JSONObject();
                    json.put("device", device);
                    json.put("timestamp", timestamp);
                    json.put("proximity", proximity);

                    mqtt.sendMessage("proximity", new MqttMessage(json.toString().getBytes()));

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        Battery.setSensorObserver(new Battery.AWARESensorObserver() {
            @Override
            public void onBatteryChanged(ContentValues data) {
                String device = data.getAsString(Battery_Provider.Battery_Data.DEVICE_ID);
                String timestamp = data.getAsString(Battery_Provider.Battery_Data.TIMESTAMP);
                String level = data.getAsString(Battery_Provider.Battery_Data.LEVEL);
                String scale = data.getAsString(Battery_Provider.Battery_Data.SCALE);
                String voltage = data.getAsString(Battery_Provider.Battery_Data.VOLTAGE);
                String temperature = data.getAsString(Battery_Provider.Battery_Data.TEMPERATURE);

                try {
                    JSONObject json = new JSONObject();
                    json.put("device", device);
                    json.put("timestamp", timestamp);
                    json.put("level", level);
                    json.put("scale", scale);
                    json.put("voltage", voltage);
                    json.put("temperature", temperature);

                    mqtt.sendMessage("battery", new MqttMessage(json.toString().getBytes()));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPhoneReboot() {

            }

            @Override
            public void onPhoneShutdown() {

            }

            @Override
            public void onBatteryLow() {

            }

            @Override
            public void onBatteryCharging() {

            }

            @Override
            public void onBatteryDischarging() {

            }
        });

        Barometer.setSensorObserver(new Barometer.AWARESensorObserver() {
            @Override
            public void onBarometerChanged(ContentValues data) {
                String device = data.getAsString(Barometer_Provider.Barometer_Data.DEVICE_ID);
                String timestamp = data.getAsString(Barometer_Provider.Barometer_Data.TIMESTAMP);
                String value = data.getAsString(Barometer_Provider.Barometer_Data.AMBIENT_PRESSURE);

                try {
                    JSONObject json = new JSONObject();
                    json.put("device", device);
                    json.put("timestamp", timestamp);
                    json.put("value", value);

                    mqtt.sendMessage("barometer", new MqttMessage(json.toString().getBytes()));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    public void AddSensorsItems(){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean save = myPreferences.getBoolean("save_sensor", false);
        RecyclerView recyclerView = findViewById(R.id.list_view);
        //Ponemos un tamaño fijo a la lista
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        if(!save) { ///> Inicializar los datos con los por defecto (primera ejecución)

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

        }else{ ///> Inicializar los datos con los valores almacenados

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

        ///> Agregar el adapter al recycler view
        mAdapter = new MyAdapter(arrayList, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    /**
     *
     *
     **/
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

        ///> Guardar los datos de los sensores antes de pasar a segundo plano
        GuardarDatos();
    }

}
