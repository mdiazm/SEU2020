package com.seu.sensors.Sensors;

import android.content.Context;

public class Apps extends Sensor {

    private String timestamp;

    public Apps(String name, boolean state, int image, String key, Context c){
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

}
