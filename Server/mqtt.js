const mqtt = require('mqtt');
var client = mqtt.connect('tcp://localhost');

client.on('connect', () => {
    client.subscribe("sensors/np.#");
})