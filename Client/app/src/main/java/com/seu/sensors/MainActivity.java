package com.seu.sensors;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.seu.sensors.Sensors.Locations;
import com.seu.sensors.Sensors.Sensor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aware.Accelerometer;
import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Barometer;
import com.aware.Battery;
import com.aware.Gyroscope;
import com.aware.Light;
import com.aware.Proximity;
import com.aware.Temperature;
import com.aware.providers.Accelerometer_Provider;
import com.aware.providers.Barometer_Provider;
import com.aware.providers.Battery_Provider;
import com.aware.providers.Gyroscope_Provider;
import com.aware.providers.Light_Provider;
import com.aware.providers.Proximity_Provider;
import com.aware.providers.Temperature_Provider;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Date;
import java.util.UUID;



/**
 * Clase principal para el control de la aplicación
 */
public class MainActivity extends AppCompatActivity implements LocationListener {

    private MyAdapter mAdapter; ///> Adaptador para mostrar los distintos sensores
    ArrayList<Object> arrayList = new ArrayList<>(); ///> Array de sensores

    private MQTT mqtt; ///> Mqtt: se usa para la comunicación con el servidor que almacenará los datos en la base de datos
    private String mac; ///> Dirección MAC del teléfono

    private Menu menu;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest _locationRequest; ///< Configuration of how the location will be requested.
    private LocationCallback _locationCallback; ///< What to do when location is received
    private static final int LOCATION_REFRESH_TIME = 5000; ///< Minimum time interval between changes
    private static final int LOCATION_REFRESH_DISTANCE = 5; ///< Minimum distance interval between changes
    private static final int UPDATES_MILLISECONDS = 10000;
    private static final int FASTEST_INTERVAL = 8000;
    private LocationManager _locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme); ///> Volvemos al tema por defecto de la app para ocultar el splash
        setContentView(R.layout.activity_main);

        checkPermissionLocation();
        checkPermissionFinesLocation();
        // Aware framework
        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        Aware.startAWARE(this);
        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, true);
        Applications.isAccessibilityServiceActive(getApplicationContext());


        _locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ///> Añadir los datos de los sensores
        AddSensorsItems();

        ///> Inicializar los observadores de los sensores
        InitListener();

        ///> Guardar los datos de los sensores
        GuardarDatos();

        ///> Construir la comunicación MQTT
        mqtt = new MQTT("192.168.0.16");
       // mqtt = new MQTT("178.62.241.158");
        ///> Obtener la MAC del dispositivo
        getMacAddress();

    }

    /**
     * Método para añadir el botón en la Toolbar
     *
     * @param menu Botón para conectar con mqtt
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        this.menu = menu;

        return true;
    }

    /**
     * Método para añadir funcionalidad al botón de la Toolbar
     *
     * @param item botón que se ha presionado
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mqtt.getConnected()) {
            mqtt.disconnect();
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_import_export_black_24dp));
        } else {
            boolean connected = mqtt.init();
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.green_icon));

            // Once Mqtt service has been started, send registration request to this server.
            String device = getDevice();
            JSONObject json = new JSONObject();
            try {
                json.put("device", device);

                // Cast JSON object as Mqtt Message
                MqttMessage message = new MqttMessage(json.toString().getBytes());

                // Send message via MQTT and register device.
                mqtt.registerDevice(message);
            } catch (JSONException e) {
                Log.e("MainActivity", "Exception on creating JSON object to register device via MQTT.");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Método para obtener la MAC del teléfono
     */
    public void getMacAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String mac = wInfo.getMacAddress();
        String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        this.mac = new UUID(androidId.hashCode(), mac.hashCode()).toString();

    }

    /**
     * Método para obtener la MAC del teléfono
     *
     * @return String MAC
     */
    public String getDevice() {
        return mac;
    }


    /**
     * Método para almacenar los datos de un sensor determinado en un fichero
     *
     * @param filename nombre del fichero
     * @param data     datos a almacenar
     */
    public void saveData(String filename, JSONObject data) {

        File temp;
        try {
            temp = File.createTempFile(filename, ".json");

            boolean exists = temp.exists();

            if (!exists) {
                new File(getApplicationContext().getFilesDir(), filename + ".json");
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput(filename + ".json", Context.MODE_APPEND));
            outputStreamWriter.write(data.toString());
            outputStreamWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para leer los datos de un fichero
     *
     * @param filename fichero a leer
     */
    public void readData(String filename) {
        try {
            InputStream inputStream = getApplicationContext().openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    /**
     * Método para enviar los datos de un fichero por mqtt
     *
     * @param filename fichero a enviar
     */
    public void sendSaveData(String filename) {
        try {
            InputStream inputStream = getApplicationContext().openFileInput(filename + ".json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();

                mqtt.sendMessageCollection(filename, new MqttMessage(stringBuilder.toString().getBytes()));

                ///> Eliminación del fichero para que no se vuelva a mandar más lo mismo
                File file = new File(getFilesDir(), filename + ".json");
                file.delete();

                File temp = File.createTempFile(filename, ".json");
                temp.exists();
                temp.delete();

            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    /**
     * Método para inicializar los listener de los sensores
     */
    public void InitListener() {

        ///> Giroscopio
        Gyroscope.setSensorObserver(new Gyroscope.AWARESensorObserver() {

            /**
             * Método para detectar un cambio en el giroscopio
             * */
            @Override
            public void onGyroscopeChanged(ContentValues data) {
                String x = data.get(Gyroscope_Provider.Gyroscope_Data.VALUES_0).toString();
                String y = data.get(Gyroscope_Provider.Gyroscope_Data.VALUES_1).toString();
                String z = data.get(Gyroscope_Provider.Gyroscope_Data.VALUES_2).toString();
                String device = getDevice();
                String timestamp = data.get(Gyroscope_Provider.Gyroscope_Data.TIMESTAMP).toString();


                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof com.seu.sensors.Sensors.Gyroscope) {
                        com.seu.sensors.Sensors.Gyroscope g = (com.seu.sensors.Sensors.Gyroscope) arrayList.get(i);
                        float _x = g.getX();
                        float _y = g.getY();
                        float _z = g.getZ();

                        Date actual = new Date(Long.parseLong(timestamp)) ;
                        Date anterior = null;
                        float differencia = 0;
                        if(g.getTimestamp() != null && g.getTimestamp() != "") {
                            anterior = new Date(Long.parseLong(g.getTimestamp()));
                            differencia = Math.abs(actual.getTime() - new Date(Long.parseLong(g.getTimestamp())).getTime());

                        }else {
                            differencia = 1001;
                        }


                            if (Math.abs(_x - Float.parseFloat(x)) > g.getOffset() ||
                                Math.abs(_y - Float.parseFloat(y)) > g.getOffset() ||
                                Math.abs(_z - Float.parseFloat(z)) > g.getOffset() &&
                                differencia > 1000) {
                            g.setX(Float.parseFloat(x));
                            g.setY(Float.parseFloat(y));
                            g.setZ(Float.parseFloat(z));
                            g.setTimestamp((timestamp));

                            arrayList.set(0, g);
                            try {
                                JSONObject json = new JSONObject();
                                json.put("device", device);
                                json.put("timestamp", timestamp);
                                json.put("x", x);
                                json.put("y", y);
                                json.put("z", z);

                                if (mqtt.getConnected()) { ///> Hay conexión
                                    sendSaveData("gyroscope");
                                    mqtt.sendMessage("gyroscope", new MqttMessage(json.toString().getBytes()));
                                } else { ///> No hay conexión
                                    saveData("gyroscope", json);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        ///> Acelerómetro
        Accelerometer.setSensorObserver(new Accelerometer.AWARESensorObserver() {
            /**
             * Método para detectar un cambio en el acelerómetro
             * */
            @Override
            public void onAccelerometerChanged(ContentValues data) {
                String x = data.get(Accelerometer_Provider.Accelerometer_Data.VALUES_0).toString();
                String y = data.get(Accelerometer_Provider.Accelerometer_Data.VALUES_1).toString();
                String z = data.get(Accelerometer_Provider.Accelerometer_Data.VALUES_2).toString();
                String device = getDevice();
                String timestamp = data.get(Accelerometer_Provider.Accelerometer_Data.TIMESTAMP).toString();

                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof com.seu.sensors.Sensors.Accelerometer) {
                        com.seu.sensors.Sensors.Accelerometer g = (com.seu.sensors.Sensors.Accelerometer) arrayList.get(i);
                        float _x = g.getX();
                        float _y = g.getY();
                        float _z = g.getZ();

                        Date actual = new Date(Long.parseLong(timestamp)) ;
                        Date anterior = null;
                        float differencia = 0;
                        if(g.getTimestamp() != null && g.getTimestamp() != "") {
                            anterior = new Date(Long.parseLong(g.getTimestamp()));
                            differencia = Math.abs(actual.getTime() - new Date(Long.parseLong(g.getTimestamp())).getTime());

                        }else {
                            differencia = 1001;
                        }

                        if ((Math.abs(_x - Float.parseFloat(x)) > g.getOffset()) ||
                                (Math.abs(_y - Float.parseFloat(y)) > g.getOffset()) ||
                                (Math.abs(_z - Float.parseFloat(z)) > g.getOffset()) &&
                                differencia > 1000) {
                            g.setY(Float.parseFloat(y));
                            g.setZ(Float.parseFloat(z));
                            g.setX(Float.parseFloat(x));
                            g.setTimestamp(timestamp);

                            arrayList.set(1, g);
                            try {
                                JSONObject json = new JSONObject();
                                json.put("device", device);
                                json.put("timestamp", timestamp);
                                json.put("x", x);
                                json.put("y", y);
                                json.put("z", z);

                                if (mqtt.getConnected()) { ///> Hay conexión
                                    sendSaveData("accelerometer");
                                    mqtt.sendMessage("accelerometer", new MqttMessage(json.toString().getBytes()));
                                } else { ///> No hay conexión
                                    saveData("accelerometer", json);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        ///> Localización
        /**
         * Método para detectar un cambio en la localización
         * */
        _locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);
                if(locationResult == null){
                    return;
                }

                for(Location location: locationResult.getLocations()){
                    String device = getDevice();
                    String timestamp = new Date(location.getTime()).toString();
                    String latitude = location.getLatitude() + "";
                    String longitude = location.getLongitude() + "";
                    String bearing = location.getBearing() + "";
                    String speed = location.getSpeed() + "";
                    String altitude = location.getAltitude() + "";
                    String provider = location.getProvider() + "";
                    String accuracy = location.getAccuracy() + "";

                    try {
                        JSONObject json = new JSONObject();
                        json.put("device", device);
                        json.put("timestamp", timestamp);
                        json.put("latitude", latitude);
                        json.put("longitude", longitude);
                        json.put("bearing", bearing);
                        json.put("speed", speed);
                        json.put("provider", provider);
                        json.put("altitude", altitude);
                        json.put("accuracy", accuracy);

                        if (mqtt.getConnected()) { ///> Hay conexión
                            sendSaveData("gps");
                            mqtt.sendMessage("gps", new MqttMessage(json.toString().getBytes()));
                        } else { ///> No hay conexión
                            saveData("gps", json);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    // Get last known location
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String device = getDevice();
                            String timestamp = "";//new Date(location.getTime()).toString();
                            String latitude = location.getLatitude() + "";
                            String longitude = location.getLongitude() + "";
                            String bearing = location.getBearing() + "";
                            String speed = location.getSpeed() + "";
                            String altitude = location.getAltitude() + "";
                            String provider = location.getProvider() + "";
                            String accuracy = location.getAccuracy() + "";

                            try {
                                JSONObject json = new JSONObject();
                                json.put("device", device);
                                json.put("timestamp", timestamp);
                                json.put("latitude", latitude);
                                json.put("longitude", longitude);
                                json.put("bearing", bearing);
                                json.put("speed", speed);
                                json.put("provider", provider);
                                json.put("altitude", altitude);
                                json.put("accuracy", accuracy);

                                if (mqtt.getConnected()) { ///> Hay conexión
                                    sendSaveData("gps");
                                    mqtt.sendMessage("gps", new MqttMessage(json.toString().getBytes()));
                                } else { ///> No hay conexión
                                    saveData("gps", json);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        fusedLocationClient.requestLocationUpdates(_locationRequest, _locationCallback,null);



        ///> Luminosidad
        Light.setSensorObserver(new Light.AWARESensorObserver() {
            /**
             * Método para detectar un cambio en la luminosidad
             * */
            @Override
            public void onLightChanged(ContentValues data) {
                String lux = data.get(Light_Provider.Light_Data.LIGHT_LUX).toString();
                String device = getDevice();
                String timestamp = data.get(Gyroscope_Provider.Gyroscope_Data.TIMESTAMP).toString();

                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof com.seu.sensors.Sensors.Light) {
                        com.seu.sensors.Sensors.Light g = (com.seu.sensors.Sensors.Light) arrayList.get(i);

                        Date actual = new Date(Long.parseLong(timestamp)) ;
                        Date anterior = null;
                        float differencia = 0;
                        if(g.getTimestamp() != null && g.getTimestamp() != "") {
                            anterior = new Date(Long.parseLong(g.getTimestamp()));
                            differencia = Math.abs(actual.getTime() - new Date(Long.parseLong(g.getTimestamp())).getTime());

                        }else {
                            differencia = 1001;
                        }

                        if ((Math.abs(g.getX() - Float.parseFloat(lux)) > g.getOffset()) ||
                                (Math.abs(g.getX() - Float.parseFloat(lux)) > g.getOffset()) ||
                                (Math.abs(g.getX() - Float.parseFloat(lux)) > g.getOffset()) &&
                        differencia > 1000) {
                            g.setX(Float.parseFloat(lux));
                            g.setTimestamp((timestamp));



                            try {
                                JSONObject json = new JSONObject();
                                json.put("device", device);
                                json.put("timestamp", timestamp);
                                json.put("lux", lux);

                                if (mqtt.getConnected()) { ///> Hay conexión
                                    sendSaveData("light");

                                    mqtt.sendMessage("light", new MqttMessage(json.toString().getBytes()));
                                } else { ///> No hay conexión
                                    saveData("light", json);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        });

        ///> Proximidad
        Proximity.setSensorObserver(new Proximity.AWARESensorObserver() {
            /**
             * Método para detectar un cambio en la proximidad
             * */
            @Override
            public void onProximityChanged(ContentValues data) {
                String device = getDevice();
                String timestamp = data.getAsString(Proximity_Provider.Proximity_Data.TIMESTAMP);
                String proximity = data.getAsString(Proximity_Provider.Proximity_Data.PROXIMITY);

                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof com.seu.sensors.Sensors.Proximity) {
                        com.seu.sensors.Sensors.Proximity g = (com.seu.sensors.Sensors.Proximity) arrayList.get(i);

                        Date actual = new Date(Long.parseLong(timestamp)) ;
                        Date anterior = null;
                        float differencia = 0;
                        if(g.getTimestamp() != null && g.getTimestamp() != "") {
                            anterior = new Date(Long.parseLong(g.getTimestamp()));
                            differencia = Math.abs(actual.getTime() - new Date(Long.parseLong(g.getTimestamp())).getTime());

                        }else {
                            differencia = 1001;
                        }

                        if ((Math.abs(g.getProximity() - Float.parseFloat(proximity)) > g.getOffset()) ||
                                (Math.abs(g.getProximity() - Float.parseFloat(proximity)) > g.getOffset()) ||
                                (Math.abs(g.getProximity() - Float.parseFloat(proximity)) > g.getOffset()) &&
                                differencia > 1000) {
                            g.setProximity(Float.parseFloat(proximity));
                            g.setTimestamp((timestamp));

                            try {
                                JSONObject json = new JSONObject();
                                json.put("device", device);
                                json.put("timestamp", timestamp);
                                json.put("proximity", proximity);

                                if (mqtt.getConnected()) { ///> Hay conexión
                                    sendSaveData("proximity");
                                    mqtt.sendMessage("proximity", new MqttMessage(json.toString().getBytes()));
                                } else { ///> No hay conexión
                                    saveData("proximity", json);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        ///> Nivel de batería
        Battery.setSensorObserver(new Battery.AWARESensorObserver() {
            /**
             * Método para detectar un cambio en el nivel de batería
             * */
            @Override
            public void onBatteryChanged(ContentValues data) {
                String device = getDevice();
                String timestamp = data.getAsString(Battery_Provider.Battery_Data.TIMESTAMP);
                String level = data.getAsString(Battery_Provider.Battery_Data.LEVEL);
                String scale = data.getAsString(Battery_Provider.Battery_Data.SCALE);
                String voltage = data.getAsString(Battery_Provider.Battery_Data.VOLTAGE);
                String temperature = data.getAsString(Battery_Provider.Battery_Data.TEMPERATURE);

                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof com.seu.sensors.Sensors.Battery) {
                        com.seu.sensors.Sensors.Battery g = (com.seu.sensors.Sensors.Battery) arrayList.get(i);

                        Date actual = new Date(Long.parseLong(timestamp)) ;
                        Date anterior = null;
                        float differencia = 0;
                        if(g.getTimestamp() != null && g.getTimestamp() != "") {
                            anterior = new Date(Long.parseLong(g.getTimestamp()));
                            differencia = Math.abs(actual.getTime() - new Date(Long.parseLong(g.getTimestamp())).getTime());

                        }else {
                            differencia = 1001;
                        }

                        if ((Math.abs(g.getLevel() - Float.parseFloat(level)) > g.getOffset()) ||
                                (Math.abs(g.getLevel() - Float.parseFloat(level)) > g.getOffset()) ||
                                (Math.abs(g.getLevel() - Float.parseFloat(level)) > g.getOffset()) &&
                                differencia > 1000) {
                            g.setLevel(Float.parseFloat(level));
                            g.setVoltage(Float.parseFloat(voltage));
                            g.setScale(Float.parseFloat(scale));
                            g.setTemperature(Float.parseFloat(temperature));
                            g.setTimestamp((timestamp));

                            try {
                                JSONObject json = new JSONObject();
                                json.put("device", device);
                                json.put("timestamp", timestamp);
                                json.put("level", level);
                                json.put("scale", scale);
                                json.put("voltage", voltage);
                                json.put("temperature", temperature);

                                if (mqtt.getConnected()) { ///> Hay conexión
                                    sendSaveData("battery");
                                    mqtt.sendMessage("battery", new MqttMessage(json.toString().getBytes()));
                                } else { ///> No hay conexión
                                    saveData("battery", json);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            /**
             * Método para detectar el reboot del teléfono
             * */
            @Override
            public void onPhoneReboot() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("device", getDevice());
                    json.put("value", "reboot");

                    if (mqtt.getConnected()) { ///> Hay conexión
                        sendSaveData("status");
                        mqtt.sendMessage("status", new MqttMessage(json.toString().getBytes()));
                    } else { ///> No hay conexión
                        saveData("status", json);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * Método para detectar el apagado del teléfono
             * */
            @Override
            public void onPhoneShutdown() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("device", getDevice());
                    json.put("value", "shutdown");

                    if (mqtt.getConnected()) { ///> Hay conexión
                        sendSaveData("status");
                        mqtt.sendMessage("status", new MqttMessage(json.toString().getBytes()));
                    } else { ///> No hay conexión
                        saveData("status", json);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * Método para detectar batería baja
             * */
            @Override
            public void onBatteryLow() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("device", getDevice());
                    json.put("value", "battery_low");

                    if (mqtt.getConnected()) { ///> Hay conexión
                        sendSaveData("status");
                        mqtt.sendMessage("status", new MqttMessage(json.toString().getBytes()));
                    } else { ///> No hay conexión
                        saveData("status", json);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * Método para detectar que se está cargando el teléfono
             * */
            @Override
            public void onBatteryCharging() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("device", getDevice());
                    json.put("value", "charging");

                    if (mqtt.getConnected()) { ///> Hay conexión
                        sendSaveData("status");
                        mqtt.sendMessage("status", new MqttMessage(json.toString().getBytes()));
                    } else { ///> No hay conexión
                        saveData("status", json);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * Método para detectar que se está descargando el teléfono
             * */
            @Override
            public void onBatteryDischarging() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("device", getDevice());
                    json.put("value", "discharging");

                    if (mqtt.getConnected()) { //> Hay conexión
                        sendSaveData("status");
                        mqtt.sendMessage("status", new MqttMessage(json.toString().getBytes()));
                    } else { ///> Noo hay conexión
                        saveData("status", json);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ///> Barómetro
        Barometer.setSensorObserver(new Barometer.AWARESensorObserver() {
            /**
             * Método para detectar un cambio en el barómetro
             * */
            @Override
            public void onBarometerChanged(ContentValues data) {
                String device = getDevice();
                String timestamp = data.getAsString(Barometer_Provider.Barometer_Data.TIMESTAMP);
                String value = data.getAsString(Barometer_Provider.Barometer_Data.AMBIENT_PRESSURE);

                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof com.seu.sensors.Sensors.Barometer) {
                        com.seu.sensors.Sensors.Barometer g = (com.seu.sensors.Sensors.Barometer) arrayList.get(i);
                        if ((Math.abs(g.getValue() - Float.parseFloat(value)) > g.getOffset()) ||
                                (Math.abs(g.getValue() - Float.parseFloat(value)) > g.getOffset()) ||
                                (Math.abs(g.getValue() - Float.parseFloat(value)) > g.getOffset())) {
                            g.setValue(Float.parseFloat(value));
                            g.setTimestamp((timestamp));

                            try {
                                JSONObject json = new JSONObject();
                                json.put("device", device);
                                json.put("timestamp", timestamp);
                                json.put("value", value);

                                if (mqtt.getConnected()) { ///> Hay conexión
                                    sendSaveData("barometer");
                                    mqtt.sendMessage("barometer", new MqttMessage(json.toString().getBytes()));
                                } else { ///> No hay conexión
                                    saveData("barometer", json);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        ///> Temperatura
        Temperature.setSensorObserver(new Temperature.AWARESensorObserver() {
            /**
             * Método para detectar un cambio en la temperatura
             * */
            @Override
            public void onTemperatureChanged(ContentValues data) {
                String timestamp = data.getAsString(Temperature_Provider.Temperature_Data.TIMESTAMP);
                String value = data.getAsString(Temperature_Provider.Temperature_Data.TEMPERATURE_CELSIUS);

                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof com.seu.sensors.Sensors.Temperature) {
                        com.seu.sensors.Sensors.Temperature g = (com.seu.sensors.Sensors.Temperature) arrayList.get(i);
                        if ((Math.abs(g.getValue() - Float.parseFloat(value)) > g.getOffset()) ||
                                (Math.abs(g.getValue() - Float.parseFloat(value)) > g.getOffset()) ||
                                (Math.abs(g.getValue() - Float.parseFloat(value)) > g.getOffset())) {
                            g.setValue(Float.parseFloat(value));
                            g.setTimestamp((timestamp));

                            try {
                                JSONObject json = new JSONObject();
                                json.put("device", getDevice());
                                json.put("timestamp", timestamp);
                                json.put("value", value);

                                if (mqtt.getConnected()) { ///> Hay conexión
                                    sendSaveData("temperature");
                                    mqtt.sendMessage("temperature", new MqttMessage(json.toString().getBytes()));
                                } else { ///> No hay conexión
                                    saveData("temperature", json);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Método para añadir los sensores que vamos a monitorizar
     */
    public void AddSensorsItems() {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean save = myPreferences.getBoolean("save_sensor", false);
        RecyclerView recyclerView = findViewById(R.id.list_view);
        //Ponemos un tamaño fijo a la lista
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        if (!save) { ///> Inicializar los datos con los por defecto (primera ejecución)

            arrayList.add(new com.seu.sensors.Sensors.Gyroscope("Giroscopio", true, R.drawable.ic_action_gyroscope, "giroscopio", this));
            ///> Giroscopio
            Aware.setSetting(this, Aware_Preferences.FREQUENCY_GYROSCOPE, 200000);
            Aware.setSetting(this, Aware_Preferences.THRESHOLD_GYROSCOPE, 0.02f);
            Aware.startGyroscope(this);

            arrayList.add(new com.seu.sensors.Sensors.Accelerometer("Acelerómetro", true, R.drawable.ic_action_accelerometer, "acelerometro", this));
            ///> Acelerómetro
            Aware.setSetting(this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
            Aware.setSetting(this, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f);
            Aware.startAccelerometer(this);

            arrayList.add(new com.seu.sensors.Sensors.Locations("GPS", false, R.drawable.ic_action_locations, "gps", this, _locationManager));

            arrayList.add(new com.seu.sensors.Sensors.Light("Luminosidad", true, R.drawable.ic_action_light, "luminosidad", this));
            ///> Luminosidad
            Aware.setSetting(this, Aware_Preferences.FREQUENCY_LIGHT, 200000);
            Aware.setSetting(this, Aware_Preferences.THRESHOLD_LIGHT, 0.02f);
            Aware.startLight(this);

            arrayList.add(new com.seu.sensors.Sensors.Proximity("Proximidad", false, R.drawable.ic_action_proximity, "proximidad", this));

            ///> Temperatura
            Aware.setSetting(this, Aware_Preferences.FREQUENCY_TEMPERATURE, 200000);
            Aware.setSetting(this, Aware_Preferences.THRESHOLD_TEMPERATURE, 0.02f);
            Aware.startTemperature(this);
            arrayList.add(new com.seu.sensors.Sensors.Temperature("Temperatura", true, R.drawable.ic_action_temperature, "temperatura", this));

            ///> Batería
            arrayList.add(new com.seu.sensors.Sensors.Battery("Batería", false, R.drawable.ic_action_battery, "bateria", this));
            ///> Barómetro
            arrayList.add(new com.seu.sensors.Sensors.Barometer("Barómetro", false, R.drawable.ic_action_barometer, "barometro", this));

        } else { ///> Inicializar los datos con los valores almacenados

            boolean item = myPreferences.getBoolean("giroscopio", false);

            arrayList.add(new com.seu.sensors.Sensors.Gyroscope("Giroscopio", item, R.drawable.ic_action_gyroscope, "giroscopio", this));
            if (item) {
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_GYROSCOPE, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_GYROSCOPE, 0.02f);
                Aware.startGyroscope(this);
            }

            item = myPreferences.getBoolean("acelerometro", false);

            arrayList.add(new com.seu.sensors.Sensors.Accelerometer("Acelerómetro", item, R.drawable.ic_action_accelerometer, "acelerometro", this));
            if (item) {
                ///> Acelerómetro
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_ACCELEROMETER, 0.02f);
                Aware.startAccelerometer(this);
            }

            item = myPreferences.getBoolean("gps", false);

            arrayList.add(new com.seu.sensors.Sensors.Locations("GPS", item, R.drawable.ic_action_locations, "gps", this, _locationManager));
            if (item) {
                // To get location updates in background
                _locationRequest = new LocationRequest();
                _locationRequest.setInterval(UPDATES_MILLISECONDS);
                _locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                _locationRequest.setFastestInterval(FASTEST_INTERVAL);
                _locationRequest.setSmallestDisplacement(LOCATION_REFRESH_DISTANCE);
            }

            item = myPreferences.getBoolean("luminosidad", false);
            arrayList.add(new com.seu.sensors.Sensors.Light("Luminosidad", item, R.drawable.ic_action_light, "luminosidad", this));
            if (item) {
                ///> Luminosidad
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_LIGHT, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_LIGHT, 0.02f);
                Aware.startLight(this);
            }

            item = myPreferences.getBoolean("proximidad", false);
            arrayList.add(new com.seu.sensors.Sensors.Proximity("Proximidad", item, R.drawable.ic_action_proximity, "proximidad", this));
            if (item) {
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_PROXIMITY, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_PROXIMITY, 0.02f);
                Aware.startLight(this);
            }

            item = myPreferences.getBoolean("temperatura", false);
            arrayList.add(new com.seu.sensors.Sensors.Temperature("Temperatura", item, R.drawable.ic_action_temperature, "temperatura", this));
            if (item) {
                ///> Temperatura
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_TEMPERATURE, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_TEMPERATURE, 0.02f);
                Aware.startTemperature(this);
            }

            item = myPreferences.getBoolean("bateria", false);
            arrayList.add(new com.seu.sensors.Sensors.Battery("Batería", item, R.drawable.ic_action_battery, "bateria", this));
            if (item) {
                Aware.startBattery(this);
            }

            item = myPreferences.getBoolean("bateria", false);
            if (item) {
                Aware.setSetting(this, Aware_Preferences.FREQUENCY_BAROMETER, 200000);
                Aware.setSetting(this, Aware_Preferences.THRESHOLD_BAROMETER, 0.02f);
                Aware.startBarometer(this);
            }
            arrayList.add(new com.seu.sensors.Sensors.Barometer("Barómetro", item, R.drawable.ic_action_barometer, "barometro", this));

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
     * Método para guardar los datos relacionados con los sensores activos e inicativos del teléfono
     **/
    public void GuardarDatos() {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putBoolean(((Sensor) arrayList.get(0)).getKey(), ((Sensor) arrayList.get(0)).getState());
        editor.putBoolean(((Sensor) arrayList.get(1)).getKey(), ((Sensor) arrayList.get(1)).getState());
        editor.putBoolean(((Sensor) arrayList.get(2)).getKey(), ((Sensor) arrayList.get(2)).getState());
        editor.putBoolean(((Sensor) arrayList.get(3)).getKey(), ((Sensor) arrayList.get(3)).getState());
        editor.putBoolean(((Sensor) arrayList.get(4)).getKey(), ((Sensor) arrayList.get(4)).getState());
        editor.putBoolean(((Sensor) arrayList.get(5)).getKey(), ((Sensor) arrayList.get(5)).getState());
        editor.putBoolean(((Sensor) arrayList.get(6)).getKey(), ((Sensor) arrayList.get(6)).getState());
        editor.putBoolean(((Sensor) arrayList.get(7)).getKey(), ((Sensor) arrayList.get(7)).getState());
        editor.putBoolean("save_sensor", true);

        editor.commit();
    }

    @Override
    public void onPause() {
        super.onPause();

        ///> Guardar los datos de los sensores antes de pasar a segundo plano
        GuardarDatos();

    }

    public boolean checkPermissionLocation(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                fusedLocationClient.getLastLocation();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        99);
            }
            return  false;
        } else {
            return true;
        }
    }
    public boolean checkPermissionFinesLocation(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                fusedLocationClient.getLastLocation();

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        101);
            }
            return  false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                    fusedLocationClient.getLastLocation();
                } else {
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i) instanceof com.seu.sensors.Sensors.Locations) {
                            ((Locations) arrayList.get(i)).setState(false);
                        }
                        return;
                    }
                }
            }
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                    fusedLocationClient.getLastLocation();
                } else {
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i) instanceof com.seu.sensors.Sensors.Locations) {
                            ((Locations) arrayList.get(i)).setState(false);
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            String device = getDevice();
            String timestamp = new Date(location.getTime()).toString();
            String latitude = location.getLatitude() + "";
            String longitude = location.getLongitude() + "";
            String bearing = location.getBearing() + "";
            String speed = location.getSpeed() + "";
            String altitude = location.getAltitude() + "";
            String provider = location.getProvider() + "";
            String accuracy = location.getAccuracy() + "";

            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) instanceof com.seu.sensors.Sensors.Locations) {
                    com.seu.sensors.Sensors.Locations g = (com.seu.sensors.Sensors.Locations) arrayList.get(i);

                    g.setTimestamp((timestamp));
                    g.setAccuracy(Float.parseFloat(accuracy));
                    g.setAltitude(Float.parseFloat(altitude));
                    g.setBearing(Float.parseFloat(bearing));
                    g.setLatitude(Float.parseFloat(latitude));
                    g.setLongitude(Float.parseFloat(longitude));

                    try {
                        JSONObject json = new JSONObject();
                        json.put("device", device);
                        json.put("timestamp", timestamp);
                        json.put("latitude", latitude);
                        json.put("longitude", longitude);
                        json.put("bearing", bearing);
                        json.put("speed", speed);
                        json.put("provider", provider);
                        json.put("altitude", altitude);
                        json.put("accuracy", accuracy);

                        if (mqtt.getConnected()) { ///> Hay conexión
                            sendSaveData("gps");
                            mqtt.sendMessage("gps", new MqttMessage(json.toString().getBytes()));
                        } else { ///> No hay conexión
                            saveData("gps", json);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onDestroy(){
        super.onDestroy();


    }
}
