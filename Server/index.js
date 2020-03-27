const express = require("express");
const bodyParser = require("body-parser");
const app = express();
const mqtt = require('./mqtt/mqtt');
const database = require("./database/database");
const server = require('http').Server(app);
const session = require("express-session");

// Configure sessions
app.use(session({secret: "cookie", saveUninitialized: true, resave: true}));
app.use(bodyParser.json());

// API GET Methods

/**
 * GET to obtain every record in a specified time frame. 
 * EXAMPLE Format of the request: ip:3000/getLastInFrame?sensorName=accelerometer&secondsFrame=5
 */
app.get('/getLastRecordsInFrame', function(req, res){
    // Get session variable
    var sess = req.session;

    // Check if user set a device-id. 
    if(sess.deviceId){
        if(database.databaseReady){
            if (!req.query.sensorName || !req.query.secondsFrame){
                return res.send("Missing parameters. Example: /getLastInFrame?sensorName=accelerometer&secondsFrame=5");
            }

            var sensorName = req.query.sensorName;
            var secondsFrame = Number(req.query.secondsFrame);
    
            var data = database.getLastRecordsInSeconds(sensorName, secondsFrame, sess.deviceId);
    
            // Send data in JSON format
            data.then(result => {
                return res.json(result);
            });
    
        } else {
            var result = "Database is starting";
            return res.send(result);
        }
    } else {
        return res.send(null);
    }
});

/**
 * GET to obtain available sensors
 */
app.get("/getAvailableSensors", function(req, res){
    // Get session variable.
    var sess = req.session;

    // If session variable is defined.
    if(sess.deviceId){
        if(database.databaseReady){
            var sensorNames = database.getSensorsIdentifiers(sess.deviceId);
            return res.json(sensorNames);
        } else {
            var result = "Database is starting";
            return res.send(result);
        }
    } else {
        return res.send(null);
    }
})

/**
 * GET to obtain a specified number of records starting from the last stored.
 * EXAMPLE Format of the request: ip:3000/getLastRecords?sensorName=accelerometer&recordsNumber=5
 */
app.get('/getLastRecords', function(req, res){
    // Get session variable
    var sess = req.session;

    // Check if user set a device-id. 

    if (sess.deviceId){
        if(database.databaseReady){
            // Check if parameters are written in a correct way.
            if (!req.query.sensorName || !req.query.recordsNumber){
                return res.send("Missing parameters. Example: /getLastRecords?sensorName=accelerometer&recordsNumber=5");
            }

            var sensorName = req.query.sensorName;
            var recordsNumber = Number(req.query.recordsNumber);
    
            var data = database.getLast(sensorName, recordsNumber, sess.deviceId);
    
            // Send data in JSON format
            data.then(result => {
                return res.json(result);
            });
    
        } else {
            var result = "Database is starting";
            return res.send(result);
        }
    } else {
        return res.send(null);
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

    if(sess.deviceId){
        
        var startDate;
        var endDate;
        var sensorName = req.query.sensorName;
        var device = sess.deviceId;

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
                return result;
            })

            return res.json(data);
        } else {
            // endDate was defined so we should call a different method.
            var data = database.getDataInInterval(device, sensorName, startDate, endDate).then((result) =>{
                return result;
            })

            return res.json(data);
        }

    } else {
        return res.sendStatus(404);
    }
})

// Start server
server.listen(3000, () => {
    console.log("Server is running right now");
})