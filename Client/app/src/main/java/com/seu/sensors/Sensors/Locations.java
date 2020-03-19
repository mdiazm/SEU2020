package com.seu.sensors.Sensors;

import android.content.Context;

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
    public Locations(String name, boolean state, int image, String key, Context c){
        super(name, state, image, key, c);

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
        super.setState(state);
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
