import React, { Component } from 'react';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

import Sensors from './sensors.js';
import Clients from './clients.js';

class Login extends Component {
    state = {
      clients: []
    }
  
    componentDidMount() {
      fetch('https://cors-anywhere.herokuapp.com/http://178.62.241.158:3000/getAvailableClients')
      .then(res => res.json())
      .then((data) => {
        this.setState({ clients: data })
      })
      .catch(console.log)
    }
  
      render() {
          return(
        <div class="login-dark">
          <form method="post" action="https://cors-anywhere.herokuapp.com/http://178.62.241.158:3000/chooseDevice">
              <h1 class="text-center">Sensor Monitoring</h1>
              <h2 class="sr-only">Login Form</h2>
              <div class="illustration"><i class="icon ion-ios-pulse-strong"></i></div>
              <div class="form-group">
                <select class="form-control">
                  <optgroup label="Seleccionar usuario">
                    <Clients clients={this.state.clients} />
                  </optgroup>
                </select>
              </div>
              <div class="form-group"><button class="btn btn-primary btn-block" type="submit">Consultar</button></div>
          </form>
      </div>
          )
      }
  }

export default Login;