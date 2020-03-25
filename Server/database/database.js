/**
 * Script to wrap communication with mongoDB database.
 */

const mongoose = require("mongoose");

// Connect to Database
mongoose.connect("mongodb://mdmedina:123456@localhost:27017/sensorsDatabase", {useNewUrlParser: true, useUnifiedTopology: true});

// Flag to indicate if database is ready.
var databaseReady = false;
module.exports.databaseReady = databaseReady;

// Array of available sensors
var availableSensors = new Array();

// Check errors
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error: '));
db.once('open', () => {
    console.log("Database is now connected");
    getSensorsIdentifiers();
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
    Status
};

/**
 * Function to store data in mongoDB
 * @param {*} data to store in mongoDB
 */
function insertData(data){
    data.save((err, item) => {
        if(err) return console.error(err);
    })
}

// Export function to insert data
module.exports.insertData = insertData;

/**
 * Get last 'number' records from the specified sensor. 
 * @param MongooseModel sensor to obtain data from
 * @param Integer number of records to query in the database. 
 */
async function getLast(sensor, number){

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
        var value = await sensorModel.find()
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
 */
async function getLastRecordsInSeconds(sensor, seconds){
    
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
        var interval = await sensorModel.findOne().sort({"timestamp": -1});

        interval = new Date(interval.timestamp - seconds * 1000);

        const data = await sensorModel.find({"timestamp" : {$gt: interval.toISOString()}}, {}, {sort: {"timestamp": -1}}, function(error, data){
            return data == null ? data : null;
        });

        return data;
    } else {
        return null;
    }
}

module.exports.getLastRecordsInSeconds = getLastRecordsInSeconds;


function getSensorsIdentifiers(){
    db.db.listCollections().toArray(function(err, names){
        names.forEach(function(value, index, array){
            var sensorName = value.name.toLowerCase();
            var sensorsInitials = sensorName.slice(0, 4);
            
            switch(sensorsInitials){
                case Sensors.ACCELEROMETER.slice(0, 4):
                    availableSensors.push(Sensors.ACCELEROMETER);
                    break;
                case Sensors.GYROSCOPE.slice(0, 4):
                    availableSensors.push(Sensors.GYROSCOPE);
                    break;
                case Sensors.GPS.slice(0, 4):
                    availableSensors.push(Sensors.GPS);
                    break;
                case Sensors.LIGHT.slice(0, 4):
                    availableSensors.push(Sensors.LIGHT);
                    break;
                case Sensors.PROXIMITY.slice(0, 4):
                    availableSensors.push(Sensors.PROXIMITY);
                    break;
                case Sensors.BATTERY.slice(0, 4):
                    availableSensors.push(Sensors.BATTERY);
                    break;
                case Sensors.BAROMETER.slice(0, 4):
                    availableSensors.push(Sensors.BAROMETER);
                    break;
                case Sensors.STATUS.slice(0, 4):
                    availableSensors.push(Sensors.STATUS);
                    break;                                                  
            }
        });
        module.exports.availableSensors = availableSensors;
    });
}