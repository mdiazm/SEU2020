const express = require("express");
const bodyParser = require("body-parser");
const app = express();
const mqtt = require('./mqtt/mqtt');
const database = require("./database/database");

// API GET Methods

/**
 * GET to obtain every record in a specified time frame. 
 * EXAMPLE Format of the request: ip:3000/getLastInFrame?sensorName=accelerometer&secondsFrame=5
 */
app.get('/getLastInFrame', function(req, res){

    if(database.databaseReady){
        var sensorName = req.query.sensorName;
        var secondsFrame = req.query.secondsFrame;

        var data = database.getLastRecordsInSeconds("accelerometer", 100);
        data.then(result => {
            console.log(result);
            res.json(result);
        });
    } else {
    var result = "Database is starting";
    res.send(result);
}
});

/**
 * GET to obtain available sensors
 */
app.get("/getAvailableSensors", function(req, res){
    if(database.databaseReady){
        var sensorNames = database.availableSensors;
        res.json(sensorNames);
    } else {
        var result = "Database is starting";
        res.send(result);
    }
})

// Start server
app.listen(3000, () => {
    console.log("Server is ON");
})