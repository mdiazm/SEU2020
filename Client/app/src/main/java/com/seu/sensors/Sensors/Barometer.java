package com.seu.sensors.Sensors;

import android.content.Context;

import java.util.Date;

public class Barometer extends Sensor {

    private float x;
    private  float y;
    private  float z;
    private String timestamp;
    private float offset;

    public Barometer(String name, boolean state, int image, String key, Context c){
        super(name, state, image, key, c);
        offset = 5.0f;
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

    public void setValue(float x) {
        this.x = x;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public float getValue() {
        return x;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getOffset() {
        return offset;
    }
}
