# SEU2020
Embedded and Ubiquitous Systems practices for MSc in Computer Sciences @ University of Jaén 2020

## Description
This repository contains code to create a Fusion Context Network. The system is build onto three basic modules: web app, mobile app, and server. The role of each is shown as following:

 * Server: it enables a MQTT and HTTP server, which are respectively used by the phone (to receive information from) and by the web client (to send the information). As the phone
 sends information via MQTT, the server is responsible from capturing it and storing in a Mongo database.
 
 * Mobile app: is installed on an Android device, and captures information from each sensor in the device. This information is then sent via MQTT to a main server.
 
 * Web app: read the information stored in the main server and show all the information in a structured way as a web page (monitor).
