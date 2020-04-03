package com.seu.sensors.Sensors;

import android.content.Context;
import com.aware.Aware;

/**
 * Clase que define un sensor por defecto
 * */
public class Sensor {

    private String name; ///> Nombre del sensor
    public boolean state; ///> Estado del sensor
    private int image; ///> Imagen del sensor
    private String key; ///> Clave del sensor

    Context context; ///> Contexto
    /**
     * Constructor parametrizado
     * @param c
     * @param image
     * @param key
     * @param name
     * @param state
     * */
    public Sensor(String name, boolean state, int image, String key, Context c){
        this.name = name;
        this.state = state;
        this.image = image;
        this.context = c;
        this.key = key;

    }

    /**
     * Método para obteneer el nombre del sensor
     * */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del sensor
     * @param name nombre del sensor
     * */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para establecer el estado del sensor
     * @param state estado del sensor
     * */
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
            }else if(name == "Batería"){
                Aware.startBattery(context);
            }else if(name == "GPS"){
              //  loc.getLastLocation();
               // Aware.startLocations(context);
            }else if(name == "Proximidad"){
                Aware.startProximity(context);
            }else if(name == "Barómetro"){
                Aware.startBarometer(context);
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
            }else if(name == "Batería"){
                Aware.stopBattery(context);
            }else if(name == "GPS"){
                //loc.removeLocationUpdates();
               // Aware.stopLocations(context);
            }else if(name =="Proximidad"){
                Aware.stopProximity(context);
            }else if(name == "Barómetro"){
                Aware.stopBarometer(context);
            }
        }

        this.state = state;
    }

    /**
     * Método para obtener el estado del sensor
     * */
    public boolean getState() {
        return state;
    }

    /**
     * Método para establecer la imagen del sensor
     * @param image imagen para el sensor
     * */
    public void setImage(int image) {
        this.image = image;
    }

    /**
     * Método para obtener la imagen del sensor
     * */
    public int getImage() {
        return image;
    }

    /**
     * Método para obtener la clave del sensor
     * */
    public String getKey() {
        return key;
    }

    /**
     * Método para establecer la clave del sensor
     * */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Método para obtener el contexto
     * */
    public Context getContext() {
        return context;
    }

    /**
     * Método para establecer el contexto
     * */
    public void setContext(Context context) {
        this.context = context;
    }

}
