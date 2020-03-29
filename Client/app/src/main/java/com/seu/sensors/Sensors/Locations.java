package com.seu.sensors.Sensors;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import java.util.Date;

public class Locations extends Sensor {

    private String timestamp;
    private float latitude;
    private float longitude;
    private float bearing;
    private float speed;
    private float provider;
    private float altitude;
    private float accuracy;
    private float offset;
    private LocationManager locationManager;
    private static final int LOCATION_REFRESH_TIME = 5000; ///< Minimum time interval between changes
    private static final int LOCATION_REFRESH_DISTANCE = 5; ///< Minimum distance interval between changes
    private static final int UPDATES_MILLISECONDS = 10000;
    private static final int FASTEST_INTERVAL = 8000;

    public Locations(String name, boolean state, int image, String key, Context c, LocationManager locationManager){
        super(name, state, image, key, c);
        this.locationManager = locationManager;
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE,
                    (LocationListener) context);
        } catch(SecurityException e){
            Log.e("gps", "No hay permisos para obtener la localización");
        } catch (IllegalArgumentException ie){
            Log.e("gps", "No hay un proveedor para el GPS");
        }
    }



    @Override
    public void setContext(Context context) {
        super.setContext(context);
    }

    @Override
    public void setKey(String key) {
        super.setKey(key);
    }

    @Override
    public void setImage(int image) {
        super.setImage(image);
    }

    @Override
    public void setState(boolean state) {

        if(state == false) {
            if (locationManager != null) {
                locationManager.removeUpdates((LocationListener) context);
            }
        }else{
            // Register location manager
            //locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

            // Listen for location changes
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_REFRESH_TIME,
                        LOCATION_REFRESH_DISTANCE,
                        (LocationListener) context);
            } catch(SecurityException e){
                Log.e("gps", "No hay permisos para obtener la localización");
            } catch (IllegalArgumentException ie){
                Log.e("gps", "No hay un proveedor para el GPS");
            }
        }
        super.state = state;

    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setAttributes(float x, float y, float z, String timestamp){
        this.timestamp = timestamp;

    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getOffset() {
        return offset;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setProvider(float provider) {
        this.provider = provider;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
