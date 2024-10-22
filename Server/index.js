const express = require("express");
const bodyParser = require("body-parser");
const app = express();
const mqtt = require('./mqtt/mqtt');
const database = require("./database/database");
const server = require('http').Server(app);
const session = require("express-session");
const io = require('socket.io')(server);
const cors = require('cors');

// Configure sessions
app.use(session({secret: "cookie", saveUninitialized: true, resave: true}));
app.use(bodyParser.json());
app.use(cors()); // Cross-origin

// API GET Methods

/**
 * GET to obtain every record in a specified time frame. 
 * EXAMPLE Format of the request: ip:3000/getLastInFrame?sensorName=accelerometer&secondsFrame=5
 */
app.get('/getLastRecordsInFrame', function(req, res){
    // Get session variable
    var sess = req.session;

    if(database.databaseReady){
        if (!req.query.sensorName || !req.query.secondsFrame){
            return res.send("Missing parameters. Example: /getLastInFrame?sensorName=accelerometer&secondsFrame=5");
        }

        var sensorName = req.query.sensorName;
        var secondsFrame = Number(req.query.secondsFrame);
        var deviceId = req.query.deviceId;

        var data = database.getLastRecordsInSeconds(sensorName, secondsFrame, deviceId);

        // Send data in JSON format
        data.then(result => {
            return res.json(result);
        });

    } else {
        var result = "Database is starting";
        return res.send(result);
    }

});

/**
 * GET to obtain available sensors
 */
app.get("/getAvailableSensors", function(req, res){
    // Get session variable.
    var sess = req.session;

    if(database.databaseReady){
        var deviceId = req.query.deviceId;
        var sensorNames = database.getSensorsIdentifiers(deviceId);


        sensorNames.then((result) => {
            return res.json(result);
        });

    } else {
        var result = "Database is starting";
        return res.send(result);
    }

})

/**
 * GET to obtain a specified number of records starting from the last stored.
 * EXAMPLE Format of the request: ip:3000/getLastRecords?sensorName=accelerometer&recordsNumber=5
 */
app.get('/getLastRecords', function(req, res){
    // Get session variable
    var sess = req.session;

    if(database.databaseReady){
        // Check if parameters are written in a correct way.
        if (!req.query.sensorName || !req.query.recordsNumber){
            return res.send("Missing parameters. Example: /getLastRecords?sensorName=accelerometer&recordsNumber=5");
        }

        var sensorName = req.query.sensorName;
        var recordsNumber = Number(req.query.recordsNumber);
        var deviceId = req.query.deviceId;

        var data = database.getLast(sensorName, recordsNumber, deviceId);

        // Send data in JSON format
        data.then(result => {
            return res.json(result);
        });

    } else {
        var result = "Database is starting";
        return res.send(result);
    }
});

/**
 * Get physical directions of each client registered in the database.
 */
app.get("/getAvailableClients", (req, res) =>{

    var values = database.getAvailableClients();
    values.then((result) => {
        return res.send(result);        
    });
});

/**
 * This method is to select device to collect data from the database.
 */
app.post("/chooseDevice", (req, res) => {

    // Get device identifier from HTTP header
    var device = req.body.deviceId;

    // Get session var
    var sess = req.session;

    // Store device identifier in cookie
    sess.deviceId = device;

    return res.sendStatus(200);
});

/**
 * Get chosen device.
 */
app.get("/chosenDevice", (req, res) => {
    // Get session variable
    var sess = req.session;

    if(sess.deviceId){
        return res.send(sess.deviceId);
    } else {
        return res.sendStatus(404);
    }
})

app.get("/getDataOnDate", (req, res) => {
    var sess = req.session;

        
    var startDate;
    var endDate;
    var sensorName = req.query.sensorName;
    var device = req.query.deviceId;

    // Check if parameters were written in a correct way.
    if(!req.query.startDate && !req.query.endDate){
        return res.send(404);
    } else if (!req.query.startDate){
        return res.send(404);
    } else if (!req.query.endDate){
        endDate = null;
    }

    // Call to one method or another depending on endDate == null

    if(endDate == null){
        // endDate was not defined
        var data = database.getDataInDay(device, sensorName, startDate).then((result) =>{
            return res.json(result);
        })
    } else {
        // endDate was defined so we should call a different method.
        var data = database.getDataInInterval(device, sensorName, startDate, endDate).then((result) =>{
            return res.json(result);
        })
    }
})

/**
 * Create sockets to receive updates in real time.
 *  */ 
io.on('connection', (socket) =>{
    console.log("Someone connected to this socket: " + socket.id);

    socket.on('subscribe', (room) =>{
        console.log(socket.id + " subscribed to room of device: " + room);
        socket.join(room);
    });

    socket.on("unsubscribe", (room) => {
        console.log(socket.id + " subscribed to room of device: " + room);
        socket.leave(room);
    })
});

/**
 * This function will put the data object in room (from websocket) specified in device parameter.
 * @param {*} device room to put the data in.
 * @param {*} data data to put in the room, containing information about the sensors in realtime.
 */
function distributeData(device, data){
    console.log("Data is being distributed in room: " + device);
    io.in(device).emit("message", data);
}

mqtt.configureEmitter(distributeData);

// Start server
server.listen(3000, () => {
    console.log("Server is running right now");
})