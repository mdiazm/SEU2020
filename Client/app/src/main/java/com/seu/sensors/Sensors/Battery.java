package com.seu.sensors.Sensors;

import android.content.Context;

import java.util.Date;

public class Battery extends Sensor {

    private float device;
    private String timestamp;
    private float level;
    private float scale;
    private float voltage;
    private float temperature;
    private float offset;

    public Battery(String name, boolean state, int image, String key, Context c){
        super(name, state, image, key, c);
        offset = 0.1f;
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


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setAttributes( float level, float scale,float voltage, float temperature, String timestamp){
        this.level = level;
        this.scale = scale;
        this.voltage = voltage;
        this.temperature = temperature;
        this.timestamp = timestamp;

    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getOffset() {
        return offset;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getLevel() {
        return level;
    }

    public float getScale() {
        return scale;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getVoltage() {
        return voltage;
    }

}
