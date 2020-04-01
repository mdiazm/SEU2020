import React, { Component } from 'react';
import './App.css';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

import Main from './main.js';
import Login from './login.js';
import Graphic from './graphics.js';
import Light from './light.js';
import Battery from './battery.js';
import Proximity from './proximity.js';

import './assets/bootstrap/css/bootstrap.min.css';
import './assets/css/styles.min.css';
import './assets/css/custom.css';
import './assets/css/Features-Blue.css';
import './assets/css/Login-Form-Dark.css';
import './assets/fonts/ionicons.min.css';
import './assets/fonts/fontawesome-all.min.css';

class App extends Component {	
  render() {
    return(
      <Router>
        <Switch>
          <Route exact path="/">
            <Login />
          </Route>
          <Route exact path="/app">
            <Main />
          </Route>
          <Route exact path="/gyroscope">
            <Graphic sensor="/gyroscope" />
          </Route>
          <Route exact path="/proximity">
            <Proximity />
          </Route>
          <Route exact path="/Accelerometer">
            <Graphic />
          </Route>
          <Route exact path="/light">
            <Light />
          </Route>
          <Route exact path="/battery">
            <Battery />
          </Route>
        </Switch>
      </Router>
    )
  }
}

export default App;