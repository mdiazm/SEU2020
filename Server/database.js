/**
 * Script to wrap communication with mongoDB database.
 */

const mongoose = require("mongoose");

// Connect to Database
mongoose.connect("mongodb://mdmedina:123456@localhost:27017/sensorsDatabase", {useNewUrlParser: true, useUnifiedTopology: true});

// Check errors
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error: '));
db.once('open', () => {
    console.log("Database is now connected");
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

/** Functions to store and load data in/from the database */

// Sensors enum
const Sensors = {
    ACCELEROMETER: "accelerometer",
    GYROSCOPE: "gyroscope", 
    GPS: "gps",
    LIGHT: "light",
    PROXIMITY: "proximity",
    BATTERY: "battery",
    BAROMETER: "barometer"
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
    Barometer
};

/**
 * Function to store data in mongoDB
 * @param {*} data to store in mongoDB
 */
function insertData(data){
    data.save((err, item) => {
        if(err) return console.error(err);
        console.log(item);
    })
}

// Export function to insert data
module.exports.insertData = insertData;
