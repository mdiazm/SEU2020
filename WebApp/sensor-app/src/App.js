import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

import 'bootstrap/dist/css/bootstrap.min.css';
import CanvasJSReact from './assets/canvasjs.react';

var CanvasJS = CanvasJSReact.CanvasJS;
var CanvasJSChart = CanvasJSReact.CanvasJSChart;

class NavBarMenu extends Component {
  render() {
    return(
      <nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0">
        <a class="navbar-brand" href="#">
          <img src="/docs/4.4/assets/brand/bootstrap-solid.svg" width="30" height="30" class="d-inline-block align-top" alt=""/>
          Sensor-app
        </a>
      </nav>
    )
  }
}

class SideBar extends Component {
  render() {
    return(
      <div class="container-fluid">
        <div class="row">
          <nav class="col-md-2 d-none d-md-block bg-light sidebar">
            <div class="sidebar-sticky">
              <ul class="nav flex-column">
                <li class="nav-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-home"></svg>
                  <a class="nav-link active" href="#">
                    Inicio <span class="sr-only">(current)</span>
                  </a>
                </li>
              </ul>

              <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
                <span>Sensores</span>
              </h6>
              <ul class="nav flex-column mb-2">
                <li class="nav-item">
                  <a class="nav-link" href="#">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-home"></svg>
                    Acelerómetro
                  </a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="#">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-home"></svg>
                    Giroscopio
                  </a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="#">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-home"></svg>
                    Luminosidad
                  </a>
                </li>
              </ul>
              
            </div>
          </nav>
          <div class="col-md-10">
          <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom px-2 py-2">
            <h1 class="h2">Inicio</h1>
            <div class="btn-toolbar mb-2 mb-md-0">
              <div class="btn-group mr-2">
                <button class="btn btn-sm btn-outline-secondary">Compatir</button>
                <button class="btn btn-sm btn-outline-secondary">Exportar</button>
              </div>
            </div>
          </div>
            <Graphic />
            <Graphic2 />
          </div>
        </div>
      </div>
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
                    { label: "Grape",  y: 28  }
                ]
        }]
    }
    
    return (
      <div>
        <CanvasJSChart options = {options}
            /* onRef = {ref => this.chart = ref} */
        />
      </div>
    );
  }
}

class Graphic2 extends Component {
  render() {
    const options = {
      title: {
        text: "Basic Spline Chart in React"
      },
      data: [{				
                type: "spline",
                dataPoints: [
                    { label: "Apple",  y: 10  },
                    { label: "Orange", y: 15  },
                    { label: "Banana", y: 25  },
                    { label: "Mango",  y: 30  },
                    { label: "Grape",  y: 28  }
                ]
        }]
    }
    
    return (
      <div>
        <CanvasJSChart options = {options}
            /* onRef = {ref => this.chart = ref} */
        />
      </div>
    );
  }
}

class Main extends Component {
  render() {
    return(
      <main>
        <NavBarMenu />
        <SideBar />
      </main>
    )
  }
}

class App extends Component {	
  render() {
    return(
      <Main />
    )
  }
}

export default App;
