import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

import Sensors from './sensors.js';
import Clients from './clients.js';
import Main from './main.js';
import Login from './login.js';

import './assets/bootstrap/css/bootstrap.min.css';
import './assets/css/styles.min.css';
import './assets/css/custom.css';
import './assets/css/Features-Blue.css';
import './assets/css/Login-Form-Dark.css';
import './assets/fonts/ionicons.min.css';
import './assets/fonts/fontawesome-all.min.css';

import { Container, Col, Navbar, Nav, Row } from 'react-bootstrap';
import CanvasJSReact from './assets/canvasjs.react';

var CanvasJS = CanvasJSReact.CanvasJS;
var CanvasJSChart = CanvasJSReact.CanvasJSChart;

class NavBarMenu extends Component {
  render() {
    return(
      <Navbar bg="dark" className="sticky-top flex-md-nowrap p-0">
        <Navbar.Brand href="/">
          <img src="/docs/4.4/assets/brand/bootstrap-solid.svg" width="30" height="30" class="d-inline-block align-top" alt=""/>
          Sensor Monitoring
        </Navbar.Brand>
      </Navbar>
    )
  }
}

class SideBar extends Component {
  state = {
    sensors: []
  }

  componentDidMount() {
    fetch('https://cors-anywhere.herokuapp.com/http://178.62.241.158:3000/getAvailableSensors')
    .then(res => res.json())
    .then((data) => {
      this.setState({ sensors: data})
    })
    .catch(console.log)
  }

  render() {
    return(
      <Nav defaultActiveKey="#" className="flex-column">
        <Sensors sensors={this.state.sensors} />
      </Nav>
    )
  }
}

class Graphic extends Component {
  render() {
    const options = {
      title: {
        text: "Basic Column Chart in React"
      },
      data: [{				
                type: "column",
                dataPoints: [
                    { label: "Apple",  y: 10  },
                    { label: "Orange", y: 15  },
                    { label: "Banana", y: 25  },
                    { label: "Mango",  y: 30  },
                    { label: "Grape",  y: 28  },
                    { label: "Grape",  y: 28  }
                ]
        }]
    }
    
    return (
      <div>
        {this.props.name}
        <CanvasJSChart options = {options}
            /* onRef = {ref => this.chart = ref} */
        />
      </div>
    );
  }
}

class Gyroscope extends Component {
  render() {
    return(
      <main>
        <NavBarMenu />
        <Container>
          <Row>
            <SideBar />
            <Col sm={10}>
              <Graphic/>
            </Col>
          </Row>
        </Container>
      </main>
    )
  }
}

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
            <Gyroscope />
          </Route>
          <Route exact path="/proximity">
            <Gyroscope />
          </Route>
          <Route exact path="/Accelerometer">
            <Gyroscope />
          </Route>
          <Route exact path="/light">
            <Gyroscope />
          </Route>
        </Switch>
      </Router>
    )
  }
}

export default App;