/**
 * Script to wrap communication with mongoDB database.
 */

const mongoose = require("mongoose");

// Disable deprecations alerts
mongoose.set('useCreateIndex', true);

// Connect to Database
mongoose.connect("mongodb://mdmedina:123456@localhost:27017/sensorsDatabase", {useNewUrlParser: true, useUnifiedTopology: true});

// Flag to indicate if database is ready.
var databaseReady = false;
module.exports.databaseReady = databaseReady;

// Check errors
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error: '));
db.once('open', () => {
    console.log("Database is now connected");
    databaseReady = true;
    module.exports.databaseReady = databaseReady;
});

// Create models for the objects and data which is going to be stored in the database.
var Schema = mongoose.Schema;

// Gyroscope
var GyroscopeSchema = new Schema({
    device: String,
    timestamp: {type: Date, default: Date.now},
    x: Number, 
    y: Number,
    z: Number 
});

var Gyroscope = mongoose.model('Gyroscope', GyroscopeSchema);

// Accelerometer
var AccelerometerSchema = new Schema({
    device: String,
    timestamp: {type: Date, default: Date.now},
    x: Number, 
    y: Number,
    z: Number 
});

var Accelerometer = mongoose.model('Accelerometer', AccelerometerSchema);

// GPS
var GpsSchema = new Schema({
    device: String,
    timestamp: {type: Date, default: Date.now},
    latitude: Number, // Degrees
    longitude: Number, // Degrees
    bearing: Number, // Degrees
    speed: Number, // In meters/second (if available)
    altitude: Number, // Meters
    provider: String,
    accuracy: Number
});

var GPS = mongoose.model('GPS', GpsSchema);

// Light
var LightSchema = new Schema({
    device: String,
    timestamp: {type: Date, default: Date.now},
    lux: Number // Lux
});

var Light = mongoose.model('Light', LightSchema);

// Proximity
var ProximitySchema = new Schema({
    device: String, 
    timestamp: {type: Date, default: Date.now},
    proximity: Number // Distance to an object in front of the device or binary presence
});

var Proximity = mongoose.model('Proximity', ProximitySchema);

// Battery
var BatterySchema = new Schema({
    device: String, 
    timestamp: {type: Date, default: Date.now},
    level: Number, // [0, scale]
    scale: Number,
    voltage: Number,
    temperature: Number
});

var Battery = mongoose.model('Battery', BatterySchema);

// Barometer
var BarometerSchema = new Schema({
    device: String, 
    timestamp: {type: Date, default: Date.now},
    value: Number // mBar or hPa, depending on the hardware
});

var Barometer = mongoose.model('Barometer', BarometerSchema);

// Phone status (shutdown, charging, discharging...)
var StatusSchema = new Schema({
    device: String,
    timestamp: {type: Date, default: Date.now},
    value: String // Status of the sender's phone.
});

var Status = mongoose.model('Status', StatusSchema);

// Devices identifiers
var DeviceSchema = new Schema({
    device: {type: String, unique: true}
});

var Device = mongoose.model('Device', DeviceSchema);

/** Functions to store and load data in/from the database */

// Sensors enum
const Sensors = {
    ACCELEROMETER: "accelerometer",
    GYROSCOPE: "gyroscope", 
    GPS: "gps",
    LIGHT: "light",
    PROXIMITY: "proximity",
    BATTERY: "battery",
    BAROMETER: "barometer",
    STATUS: "status"
};

module.exports.Sensors = Sensors;

// Export models
module.exports.Models = {
    Accelerometer, 
    Gyroscope,
    GPS,
    Light,
    Proximity,
    Battery,
    Barometer,
    Status,
    Device
};

/**
 * Function to store data in mongoDB
 * @param {*} data to store in mongoDB
 */
function insertData(data){
    data.save((err, item) => {
        if(err) return console.error(err);
    })

    // Update available collections.
    getSensorsIdentifiers();
}

// Export function to insert data
module.exports.insertData = insertData;

/**
 * Get last 'number' records from the specified sensor. 
 * @param MongooseModel sensor to obtain data from
 * @param Integer number of records to query in the database.
 * @param {*} device id of the client
 */
async function getLast(sensor, number, device){
    var sensorModel = null;

    // Get model to find records.
    switch(sensor){
        case Sensors.ACCELEROMETER:
            sensorModel = Accelerometer;
            break;
        case Sensors.GYROSCOPE:
            sensorModel = Gyroscope;
            break;
        case Sensors.GPS:
            sensorModel = GPS;
            break;
        case Sensors.LIGHT:
            sensorModel = Light;
            break;
        case Sensors.PROXIMITY:
            sensorModel = Proximity;
            break;
        case Sensors.BATTERY:
            sensorModel = Battery;
            break;
        case Sensors.BAROMETER:
            sensorModel = Barometer;
            break;
        case Sensors.STATUS:
            sensorModel = Status;
            break;
    }

    // Perform query in the database on the specified model
    if(sensorModel != null){
        var value = await sensorModel.find({"device": device})
        .sort("-timestamp")
        .limit(number);
        return value;
    } else {
        return null;
    }
}

// Export this function to use it from code.
module.exports.getLast = getLast;

/**
 * Get all records in time frame indicated in seconds parameter. The system returns each record from the
 * last saved until specified seconds in the past.
 * @param {*} sensor id of the sensor to recover information from.
 * @param {*} seconds size of the time frame.
 * @param {*} device id of the client
 */
async function getLastRecordsInSeconds(sensor, seconds, device){
    var sensorModel = null;

    // Get model to find records.
    switch(sensor){
        case Sensors.ACCELEROMETER:
            sensorModel = Accelerometer;
            break;
        case Sensors.GYROSCOPE:
            sensorModel = Gyroscope;
            break;
        case Sensors.GPS:
            sensorModel = GPS;
            break;
        case Sensors.LIGHT:
            sensorModel = Light;
            break;
        case Sensors.PROXIMITY:
            sensorModel = Proximity;
            break;
        case Sensors.BATTERY:
            sensorModel = Battery;
            break;
        case Sensors.BAROMETER:
            sensorModel = Barometer;
            break;
        case Sensors.STATUS:
            sensorModel = Status;
            break;
    }

    // Perform query in the database on the specified model
    if(sensorModel != null){
        var interval = await sensorModel.findOne({"device": device}).sort({"timestamp": -1});

        interval = new Date(interval.timestamp - seconds * 1000);

        const data = await sensorModel.find({"timestamp" : {$gt: interval.toISOString()}, "device": device}, {}, {sort: {"timestamp": -1}}, function(error, data){
            return data == null ? data : null;
        });

        return data;
    } else {
        return null;
    }
}

module.exports.getLastRecordsInSeconds = getLastRecordsInSeconds;

/**
 * Get available sensors for the given device.
 * @param {*} device id of the device. This pre-configured on the web app.
 */
async function getSensorsIdentifiers(device){
    var availableSensors = [];

    var models = mongoose.models;

    for (var obj in models){
        var data = await models[obj].find({"device": device}, (err, data) => {
            return data;
        });

        if(data.length > 0 && obj.toLowerCase() != "device"){
            availableSensors.push(obj.toLowerCase());
        }
    }

    return availableSensors;
}

module.exports.getSensorsIdentifiers = getSensorsIdentifiers;

/**
 * Function to get available clients from the database. Those clients will be shown in the user interface (or web client).
 */
async function getAvailableClients(){
    var devices = await Device.find((error, data) => {
        return data;
    });

    return devices;
}

module.exports.getAvailableClients = getAvailableClients;

/**
 * Retrieve data from the specified sensor to the specified user in a time interval.
 * @param {*} device MAC address of the client
 * @param {*} sensor sensor to retrieve data from
 * @param {*} start start date of the interval
 * @param {*} end end date of the interval.
 */
async function getDataInInterval(device, sensor, start, end){

    // Set granularity of the interval
    var startDate = new Date(start);
    startDate.setHours(00, 00, 00);

    var endDate = new Date(end);
    endDate.setHours(23, 59, 59);

    // Perform query on the database
    var sensorModel = null;

    // Get model to find records.
    switch(sensor){
        case Sensors.ACCELEROMETER:
            sensorModel = Accelerometer;
            break;
        case Sensors.GYROSCOPE:
            sensorModel = Gyroscope;
            break;
        case Sensors.GPS:
            sensorModel = GPS;
            break;
        case Sensors.LIGHT:
            sensorModel = Light;
            break;
        case Sensors.PROXIMITY:
            sensorModel = Proximity;
            break;
        case Sensors.BATTERY:
            sensorModel = Battery;
            break;
        case Sensors.BAROMETER:
            sensorModel = Barometer;
            break;
        case Sensors.STATUS:
            sensorModel = Status;
            break;
    }

    // Perform query in the database on the specified model
    if(sensorModel != null){

        const data = await sensorModel.find({"timestamp" : {$gte: startDate.toISOString(), $lte: endDate.toISOString()}, 
        "device": device}, {}, {sort: {"timestamp": -1}}, function(error, data){
            return data == null ? data : null;
        });

        return data;
    } else {
        return null;
    }
}

module.exports.getDataInInterval = getDataInInterval;

/**
 * Retrieve all records in day specified in day parameter.
 * @param {*} device MAC address of the client
 * @param {*} sensor sensor to retrieve data from
 * @param {*} day to retrieve data from.
 */
async function getDataInDay(device, sensor, day){

    // Set granularity of the interval
    var startDate = new Date(day);
    startDate.setHours(00, 00, 00);

    var endDate = new Date(day);
    endDate.setHours(23, 59, 59);

    // Perform query on the database
    var sensorModel = null;

    // Get model to find records.
    switch(sensor){
        case Sensors.ACCELEROMETER:
            sensorModel = Accelerometer;
            break;
        case Sensors.GYROSCOPE:
            sensorModel = Gyroscope;
            break;
        case Sensors.GPS:
            sensorModel = GPS;
            break;
        case Sensors.LIGHT:
            sensorModel = Light;
            break;
        case Sensors.PROXIMITY:
            sensorModel = Proximity;
            break;
        case Sensors.BATTERY:
            sensorModel = Battery;
            break;
        case Sensors.BAROMETER:
            sensorModel = Barometer;
            break;
        case Sensors.STATUS:
            sensorModel = Status;
            break;
    }

    // Perform query in the database on the specified model
    if(sensorModel != null){

        const data = await sensorModel.find({"timestamp" : {$gte: startDate.toISOString(), $lte: endDate.toISOString()}, 
        "device": device}, {}, {sort: {"timestamp": -1}}, function(error, data){
            return data == null ? data : null;
        });

        return data;
    } else {
        return null;
    }
}

module.exports.getDataInDay = getDataInDay;