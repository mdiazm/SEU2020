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

    public MQTT(String ip) {
        this.ip = ip;
        send_topic = "sensors/send/";
        try {
            this.client = new MqttClient("tcp://" + ip + ":1883", "", new MemoryPersistence());
            client.setCallback( this);

        } catch (MqttException e) {
            e.printStackTrace();

        }
    }

    public boolean init(){
        if(!connected) {
            try {
                Log.d("MQTT", "intentando");
                client.connect();
                send_topic = "sensors/send/";
                client.subscribe(send_topic);
                connected = true;
                return true;
            } catch (MqttException e) {
                Log.d("MQTT", e.toString());
                e.printStackTrace();
                connected = false;
                return false;
            }
        }
        return true;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d("MQTT", "connection lost");
        connected = false;
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
                try {
                    client.disconnect();
                } catch (MqttException er) {
                    er.printStackTrace();
                }
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
                try {
                    client.disconnect();
                } catch (MqttException err) {
                    err.printStackTrace();
                }
            }

        }
    }

    public Boolean getConnected() {
        return connected;
    }
}
