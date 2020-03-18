package com.seu.sensors;


import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTT implements MqttCallback {

    private String ip;
    private MqttClient client ;
    private String send_topic;
    private Boolean connected = false;

    public MQTT(String ip){
        this.ip = ip;
    }

    public void init(){
        try {
            Log.d("MQTT", "intentando");
            this.client = new MqttClient("tcp://" + ip + ":1883", "", new MemoryPersistence());
            client.setCallback( this);
            client.connect();
            send_topic = "sensors/send/";
            client.subscribe(send_topic);
            Log.d("MQTT", send_topic);
            connected = true;
        } catch (MqttException e) {
            Log.d("MQTT", e.toString());
            e.printStackTrace();
            connected = false;
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d("MQTT", "connection lost");
        connected = false;
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

    public void sendMessage(String sensor, MqttMessage message){
        if(connected) {
            try {
                Log.d("MQTT", sensor);
                client.publish(send_topic + sensor, message);

            } catch (MqttException e) {
                e.printStackTrace();
                connected = false;
            }
        }else {
            try {
                this.client = new MqttClient("tcp://" + ip + ":1883", "", new MemoryPersistence());
                client.setCallback( this);
                client.connect();
                sendMessage(sensor,message);
            } catch (MqttException e) {
                e.printStackTrace();
                connected = false;
            }

        }
    }

    public Boolean getConnected() {
        return connected;
    }
}
