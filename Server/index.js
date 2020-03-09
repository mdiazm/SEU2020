const express = require("express");
const bodyParser = require("body-parser");
const app = express();
const mqtt = require('./mqtt');
const database = require("./database");


app.listen(3000, () => {
    console.log("Server is ON");
})