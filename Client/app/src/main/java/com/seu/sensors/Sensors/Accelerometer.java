package com.seu.sensors.Sensors;

import android.content.Context;

import java.util.Date;

public class Accelerometer extends Sensor {

    private float x;
    private  float y;
    private  float z;
    private String timestamp;
    private float offset;

    public Accelerometer(String name, boolean state, int image, String key, Context c){
        super(name, state, image, key, c);
        offset = 2.0f;
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

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setAttributes(float x, float y, float z, String timestamp){
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;

    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getOffset() {
        return offset;
    }
}
