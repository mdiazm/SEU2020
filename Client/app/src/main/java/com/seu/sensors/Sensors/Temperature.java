package com.seu.sensors.Sensors;

import android.content.Context;

import java.util.Date;

public class Temperature  extends Sensor {

    private float value;
    private String timestamp;
    private float offset;

    public Temperature(String name, boolean state, int image, String key, Context c){
        super(name, state, image, key, c);
        offset = 0.5f;
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
        this.value = x;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setAttributes(float x, String timestamp){
        this.value = x;
        this.timestamp = timestamp;

    }

    public float getValue() {
        return value;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getOffset() {
        return offset;
    }
}
