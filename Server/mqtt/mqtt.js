/**
 * Script to handle communication with the Android client using MQTT protocol.
 */

const mqtt = require('mqtt');
const database = require('../database/database')
var client = mqtt.connect('tcp://localhost'); // MQTT Broker url (via TCP)

// Possible actions
const Actions = {
    SEND: 'send',
    REQUEST: 'request',
    REGISTER: 'register', 
    COLLECTION: 'collection'
};

// Configure caller from index script.
var emitMessage = null;

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

    if(elements.length == 3){
        var sensor = elements[2];
        if(action === Actions.SEND){
            // Code to store user data
            parseReceivedRegister(sensor, message.toString());
        } else if(action === Actions.REQUEST){
            // Code to send requested information to Android Client
        } else if(action === Actions.COLLECTION) {
            // Receive a collection of registers.
            parseReceivedCollection(sensor, message.toString());
        }
    } else {
        if(action === Actions.REGISTER) {
            // Register device 
            registerDevice(message);
        }
    }
});

/**
 * Functions to parse messages received by MQTT protocol
 * 
 * @param sensor which sent the information
 * @param data string formatted as json with the information from each sensor
 */
 function parseReceivedRegister(sensor, data){

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

        case Sensors.STATUS:
            var values = new Models.Status({
                device: json.device,
                value: json.value
            });

            // Insert data in the database
            InsertData(values);
            break;

        default:
            console.log("No existing behavior for this sensor: " + sensor);
    }

    // Emit message
    distributeDataRealTime(json.device, sensor, data);
}

/**
 * Functions to parse messages received by MQTT protocol
 * 
 * @param sensor which sent the information
 * @param data string formatted as json with the information from each sensor
 */
function parseReceivedCollection(sensor, data){

    // Split text into lines (each line is a different record.)
    data = '[' + data.replace(/}{/g, '},{') + ']';
    var values = JSON.parse(data);
    console.log(values.length);

    // Store each line as a record independently.
    values.forEach((value, index, array) => {
        var json = value;

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

            case Sensors.STATUS:
                var values = new Models.Status({
                    device: json.device,
                    value: json.value
                });

                // Insert data in the database
                InsertData(values);
                break;

            default:
                console.log("No existing behavior for this sensor: " + sensor);
        }

        // Distribute data
        distributeDataRealTime(json.device, sensor, json);
    });
}

/**
 * Function to register a mobile phone within the system. This will be called when the mobile connects to MQTT broker.
 * @param json message 
 */
function registerDevice(message){

    var json = JSON.parse(message);

    var values = new Models.Device({
        device: json.device
    });

    InsertData(values);
}

/**
 * Function to configure emitter. This is, every times that a mqtt message is received, emitMessage() function,
 * which is defined in index.js, will be called to distribute new content in real time across the users which
 * are requesting information about.
 * @param emitter callback associated to WebSocket that will distribute the data.
 */
function configureEmitter(emitter){
    emitMessage = emitter;
}

module.exports.configureEmitter = configureEmitter;

/**
 * Function to send information in realtime via websockets
 * @param {} device owner of the information
 * @param {*} sensor generator of the information
 * @param {*} data to distribute
 */
function distributeDataRealTime(device, sensor, data){

    var dataJSON = JSON.parse(data);

    var values = {
        "sensor": sensor, 
        "data": data,
        "timestamp": new Date(parseInt(dataJSON.timestamp)).toString()
    }

    emitMessage(device, values);
}