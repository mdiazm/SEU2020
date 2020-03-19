package com.seu.sensors.Sensors;

import android.content.Context;

import java.util.Date;

public class Proximity extends Sensor {

    private float proximity;
    private String timestamp;

    private float offset;

    public Proximity(String name, boolean state, int image, String key, Context c){
        super(name, state, image, key, c);
        this.offset = 0.2f;
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

    public void setAttributes(float proximity, String timestamp){
      this.proximity = proximity;
        this.timestamp = timestamp;

    }

    public float getProximity() {
        return proximity;
    }

    public void setProximity(float proximity) {
        this.proximity = proximity;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getOffset() {
        return offset;
    }
}
