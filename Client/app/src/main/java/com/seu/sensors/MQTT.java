package com.seu.sensors;


import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Clase para enviar datos mediante el protocolo MQTT
 * */
public class MQTT implements MqttCallback {

    private String ip; ///> IP a la que conectar el cliente
    private MqttClient client ; ///> Cliente MQTT
    private String send_topic; ///> Topic al que enviar
    private Boolean connected = false; ///> Estado de la conexión
    private String register_topic; ///> Topic para registrarse

    /**
     * Constructor parametrizado
     * @param ip ip a la que enviar los datos
     * */
    public MQTT(String ip) {
        this.ip = ip;
        send_topic = "sensors/send/";
        this.register_topic = "sensors/register";
        try {
            this.client = new MqttClient("tcp://" + ip + ":1883", "", new MemoryPersistence());
            client.setCallback( this);

        } catch (MqttException e) {
            e.printStackTrace();

        }
    }

    /**
     * Método para inicializar la conexión
     * */
    public boolean init(){
        if(!connected) {
            try {
                Log.d("MQTT", "conectando");
                client.connect();
                send_topic = "sensors/send/";
                client.subscribe(send_topic);
                connected = true;
                return true;
            } catch (MqttException e) { ///> No ha sido posible conectar
                Log.d("MQTT", e.toString());
                e.printStackTrace();
                connected = false;
                return false;
            }
        }
        return true;
    }

    /**
     * Método que indica conexión perdidda
     * */
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

    /**
     * Método que indica que ha llegado un mensaje
     * */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d("MQTT", payload);
    }

    /**
     * Método que indica que se ha enviado un mensaje
     * */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d("MQTT", "deliveryComplete");
    }

    /**
     * Método para enviar un mensaje a un topic concreto
     * @param message  mensaje a enviar
     * @param sensor sensor del cual son los datos
     * */
    public void sendMessage(String sensor, MqttMessage message){
        if(connected) { ///> Está conectado --> Envío
            try {
                Log.d("MQTT", sensor);
                client.publish(send_topic + sensor, message);

            } catch (MqttException e) {
                ///> Se pierde la conexión
                e.printStackTrace();
                connected = false;
                try {
                    client.disconnect();
                } catch (MqttException er) {
                    er.printStackTrace();
                }
            }
        }else { ///> No hay conexión --> Conecto y envío
            try {
                this.client = new MqttClient("tcp://" + ip + ":1883", "", new MemoryPersistence());
                client.setCallback( this);
                client.connect();
                sendMessage(sensor,message);
            } catch (MqttException e) {
                ///> Se pierde la conexión
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

    public void sendMessageCollection(String sensor, MqttMessage message){
        if(connected) { ///> Está conectado --> Envío
            try {
                Log.d("MQTT", sensor);
                client.publish("send/collection/" + sensor, message);

            } catch (MqttException e) {
                ///> Se pierde la conexión
                e.printStackTrace();
                connected = false;
                try {
                    client.disconnect();
                } catch (MqttException er) {
                    er.printStackTrace();
                }
            }
        }else { ///> No hay conexión --> Conecto y envío
            try {
                this.client = new MqttClient("tcp://" + ip + ":1883", "", new MemoryPersistence());
                client.setCallback( this);
                client.connect();
                sendMessageCollection(sensor,message);
            } catch (MqttException e) {
                ///> Se pierde la conexión
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

    /**
     * Método para obtener el estado de conexión de MQTT
     * @return true si está conectado, false en otro caso
     * */
    public Boolean getConnected() {
        return connected;
    }

    /**
     * Método para registrar un dispositivo en MongoDB mediante MQTT.
     * @param message identificador del dispositivo en formato mensaje de MQTT.
     */
    public void registerDevice(MqttMessage message){
        if(connected) { ///> Está conectado --> Envío
            try {
                client.publish(register_topic, message);
            } catch (MqttException e) {
                ///> Se pierde la conexión
                e.printStackTrace();
                connected = false;
                try {
                    client.disconnect();
                } catch (MqttException er) {
                    er.printStackTrace();
                }
            }
        }else { ///> No hay conexión --> Conecto y envío
            try {
                this.client = new MqttClient("tcp://" + ip + ":1883", "", new MemoryPersistence());
                client.setCallback( this);
                client.connect();
                registerDevice(message);
            } catch (MqttException e) {
                ///> Se pierde la conexión
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
}
