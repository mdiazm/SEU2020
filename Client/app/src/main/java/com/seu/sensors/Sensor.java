package com.seu.sensors;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.aware.Accelerometer;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Gyroscope;
import com.aware.Light;
import com.aware.Temperature;
import com.aware.providers.Accelerometer_Provider;

public class Sensor {

    private String name;
    private boolean state;
    private int image;

    Context context;

    public Sensor(String name, boolean state, int image, Context c){
        this.name = name;
        this.state = state;
        this.image = image;
        this.context = c;
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

}
