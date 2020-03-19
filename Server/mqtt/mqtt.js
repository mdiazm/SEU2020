/**
 * Script to handle communication with the Android client using MQTT protocol.
 */

const mqtt = require('mqtt');
const database = require('../database/database')
var client = mqtt.connect('tcp://localhost'); // MQTT Broker url (via TCP)

// Possible actions
const Actions = {
    SEND: 'send',
    REQUEST: 'request'
};

// Sensors
const Sensors = database.Sensors;

// Data models
const Models = database.Models;

// Functions from database module
const InsertData = database.insertData;

// Connect to MQTT server and subscribe
client.on('connect', () => {
    client.subscribe("sensors/#");
})

client.on('message', (topic, message) => {
    var elements = topic.split("/");
    var action = elements[1];
    var sensor = elements[2];

    if(elements.length == 3){
        if(action === Actions.SEND){
            // Code to store user data
            parseReceivedMessage(sensor, message.toString());
        } else if(action === Actions.REQUEST){
            // Code to send requested information to Android Client
        }
    }
});

/**
 * Functions to parse messages received by MQTT protocol
 * 
 * @param sensor which sent the information
 * @param data string formatted as json with the information from each sensor
 */

 function parseReceivedMessage(sensor, data){

    var json = JSON.parse(data);

    switch(sensor){
        case Sensors.ACCELEROMETER: 
            var values = new Models.Accelerometer({
                device: json.device,
                timestamp: new Date(parseInt(json.timestamp)),
                x: json.x,
                y: json.y,
                z: json.z
            });

            // Insert data in the database
            InsertData(values);

            break;

        case Sensors.GYROSCOPE:
            var values = new Models.Gyroscope({
                device: json.device,
                timestamp: new Date(parseInt(json.timestamp)),
                x: json.x,
                y: json.y,
                z: json.z
            });
            // Insert data in the database
            InsertData(values);
            console.log(values);
            break;

        case Sensors.GPS:
            var values = new Models.GPS({
                device: json.device,
                timestamp: new Date(parseInt(json.timestamp)),
                latitude: json.latitude,
                longitude: json.longitude,
                bearing: json.bearing,
                speed: json.speed,
                altitude: json.altitude,
                provider: json.provider,
                accuracy: json.accuracy
            });
            // Insert data in the database
            InsertData(values);
            break;

        case Sensors.LIGHT:
            var values = new Models.Light({
                device: json.device,
                timestamp: new Date(parseInt(json.timestamp)),
                lux: json.lux
            });
            // Insert data in the database
            InsertData(values);
            break;

        case Sensors.PROXIMITY:
            var values = new Models.Proximity({
                device: json.device,
                timestamp: new Date(parseInt(json.timestamp)),
                proximity: json.proximity
            });
            // Insert data in the database
            InsertData(values);
            break;

        case Sensors.BATTERY:
            var values = new Models.Battery({
                device: json.device,
                timestamp: new Date(parseInt(json.timestamp)),
                level: json.level,
                scale: json.scale,
                voltage: json.voltage,
                temperature: json.temperature
            });
            // Insert data in the database
            InsertData(values);
            break;

        case Sensors.BAROMETER:
            var values = new Models.Barometer({
                device: json.device,
                timestamp: new Date(json.timestamp),
                value: json.value
            });
            // Insert data in the database
            InsertData(values);
            break;

        default:
            console.log("No existing behavior for this sensor: " + sensor);
    }
 }