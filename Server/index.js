const express = require("express");
const bodyParser = require("body-parser");
const app = express();
const mqtt = require('./mqtt/mqtt');
const database = require("./database/database");


app.listen(3000, () => {
    console.log("Server is ON");
})

database.getLast("hola", 25);

var data = database.getLastRecordsInSeconds("accelerometer", 100);
data.then(result => {
    console.log(result);
});