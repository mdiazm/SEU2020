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
  constructor(props) {
    super(props);
    this.state = {value: '', clients: [], sensors: []};

    fetch('http://178.62.241.158:3000/getAvailableClients', {
      headers: {
        withCredentials:true
      }
    })
      .then(res => res.json())
      .then((data) => {
        this.setState({ clients: data })
      })
      .catch(console.log)

    this.handleClick = this.handleClick.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleClick(e) {
    e.preventDefault();
    fetch('http://178.62.241.158:3000/getAvailableSensors?deviceId=s%3AS_-hcCGMhPWagoZjD0WUd7VVEhaRFw00.M8zDDgCpVSTL1LEIjU90RhvImg1JpuaEu6JwHwq2XdY', {
        headers: {
          //withCredentials:true
        }
      })
        .then(res => res.json())
        .then((data) => {
          this.setState({ sensors: data })
        })
        .catch(console.log)
  }
  
  handleSubmit(data) {
    data.preventDefault();

    (async () => {
      const rawResponse = await fetch('http://178.62.241.158:3000/chooseDevice', {
        method: 'POST',
        //withCredentials:true,
        body: JSON.stringify({deviceId: '00000000-5561-036d-0000-000075b319f8'})
      });
      const content = await rawResponse;
    
      console.log(content);
    })();
  }

  handleChange(event) {
    this.setState({value: event.target.value});
  }
  
  render() {
      return(
        <div class="login-dark">
            <form onSubmit={this.handleSubmit}>
                <h1 class="text-center">Sensor Monitoring</h1>
                <h2 class="sr-only">Login Form</h2>
                <ul>
                <Sensors sensors={this.state.sensors} />
                </ul>
                <div class="illustration">
                  <i class="icon ion-ios-pulse-strong"></i>
                </div>
                <div class="form-group">
                  <select class="form-control" value="{{label: this.state.value, value: this.state.value}}" onChange={this.handleChange}>
                      <option>Option</option>
                      <Clients clients={this.state.clients} />
                  </select>
                </div>
                <div class="form-group">
                  <button class="btn btn-primary btn-block" type="submit">Consultar</button>
                </div>
            </form>
            <button class="btn btn-primary btn-block" onClick={this.handleClick}>CARGAR</button>
        </div>
      )
  }
}

export default Login;