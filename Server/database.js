const mongoose = require("mongoose");

// Connect to Database
mongoose.connect("mongodb://mdmedina:123456@localhost:27017/sensorsDatabase");

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

module.exports = mongoose.model('Gyroscope', GyroscopeSchema);

// Accelerometer
var AccelerometerSchema = new Schema({
    device: String,
    timestamp: {type: Date, default: Date.now},
    x: Number, 
    y: Number,
    z: Number 
});

module.exports = mongoose.model('Accelerometer', AccelerometerSchema);

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

module.exports = mongoose.model('GPS', GpsSchema);

// Light
var LightSchema = new Schema({
    device: String,
    timestamp: {type: Date, default: Date.now},
    lux: Number // Lux
});

module.exports = mongoose.model('Light', LightSchema);

// Proximity
var ProximitySchema = new Schema({
    device: String, 
    timestamp: {type: Date, default: Date.now},
    proximity: Number // Distance to an object in front of the device or binary presence
});

module.exports = mongoose.model('Proximity', ProximitySchema);

// Battery
var BatterySchema = new Schema({
    device: String, 
    timestamp: {type: Date, default: Date.now},
    level: Number, // [0, scale]
    scale: Number,
    voltage: Number,
    temperature: Number
});

module.exports = mongoose.model('Battery', BatterySchema);

// Barometer
var BarometerSchema = new Schema({
    device: String, 
    timestamp: {type: Date, default: Date.now},
    value: Number // mBar or hPa, depending on the hardware
});

module.exports = mongoose.model('Barometer', BarometerSchema);

/** Functions to store and load data in/from the database */

