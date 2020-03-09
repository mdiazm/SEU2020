const express = require("express");
const bodyParser = require("body-parser");
const app = express();
const mqtt = require('./mqtt/mqtt');
const database = require("./database/database");


app.listen(3000, () => {
    console.log("Server is ON");
})