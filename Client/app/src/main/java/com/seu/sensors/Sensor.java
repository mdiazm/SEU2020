package com.seu.sensors;

import android.content.Context;
import com.aware.Aware;

public class Sensor {

    private String name;
    private boolean state;
    private int image;
    private String key;

    Context context;

    public Sensor(String name, boolean state, int image, String key, Context c){
        this.name = name;
        this.state = state;
        this.image = image;
        this.context = c;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(boolean state) {
        if(state){
            if(name == "Acelerómetro") {
                ///> Acelerómetro
                Aware.startAccelerometer(context);
            }else if(name == "Giroscopio") {
                ///> Giroscopio
                Aware.startGyroscope(context);

            }else if(name == "Luminosidad") {
                ///> Luminosidad
                 Aware.startLight(context);

            }else if(name == "Temperatura") {
                ///> Temperatura
                Aware.startTemperature(context);
            }
        }else{
            if(name == "Acelerómetro") {
                Aware.stopAccelerometer(context);
            }else if(name == "Giroscopio") {
                Aware.stopGyroscope(context);
            }else if(name == "Luminosidad") {
                Aware.stopLight(context);
            }else if(name == "Temperatura") {
                Aware.stopTemperature(context);
            }
        }

        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
