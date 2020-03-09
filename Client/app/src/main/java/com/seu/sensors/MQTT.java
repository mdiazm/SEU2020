package com.seu.sensors;


import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTT implements MqttCallback {

    private String ip;
    public MQTT(String ip){
        this.ip = ip;
    }

    public void init(){
        try {
            Log.d("MQTT", "intentando");
            MqttClient client = new MqttClient("tcp://" + ip + ":1883", "", new MemoryPersistence());
            client.setCallback( this);
            client.connect();
            String topic = "sensors/send/accelerometer";
            client.subscribe(topic);
            Log.d("MQTT", topic);
        } catch (MqttException e) {
            Log.d("MQTT", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d("MQTT", "connection lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d("MQTT", payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d("MQTT", "deliveryComplete");
    }
}
