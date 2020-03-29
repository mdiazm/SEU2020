import React, { Component } from 'react';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

import Sensors from './sensors.js';

class Main extends Component {
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
        <main>
          <div id="wrapper">
          <nav class="navbar navbar-dark align-items-start sidebar sidebar-dark accordion bg-gradient-primary p-0">
              <div class="container-fluid d-flex flex-column p-0">
                  <a class="navbar-brand d-flex justify-content-center align-items-center sidebar-brand m-0" href="#">
                      <div class="sidebar-brand-icon rotate-n-15"></div>
                      <div class="sidebar-brand-text mx-3"><span>Sensor Monitoring</span></div>
                  </a>
                  <hr class="sidebar-divider my-0"/>
                  <ul class="nav navbar-nav text-light" id="accordionSidebar">
                      <Sensors />
                      <li class="nav-item" role="presentation"><a class="nav-link active" href="index.html"><i class="fas fa-home"></i><span>Inicio</span></a></li>
                      <li class="nav-item" role="presentation"><a class="nav-link" href="profile.html"><i class="fas fa-user fas fa-asterisk"></i><span>Gyroscope</span></a></li>
                      <li class="nav-item" role="presentation"><a class="nav-link" href="table.html"><i class="fas fa-ruler-combined"></i><span>Proximity</span></a></li>
                      <li class="nav-item" role="presentation"><a class="nav-link" href="blank.html"><i class="fab fa-cloudscale"></i><span>Accelerometer</span></a></li>
                      <li class="nav-item" role="presentation"><a class="nav-link" href="blank.html"><i class="fas fa-lightbulb"></i><span>Light</span></a></li>
                  </ul>
                  <div class="text-center d-none d-md-inline"><button class="btn rounded-circle border-0" id="sidebarToggle" type="button"></button></div>
              </div>
          </nav>
          <div class="d-flex flex-column" id="content-wrapper">
              <div id="content">
                  <nav class="navbar navbar-light navbar-expand bg-white shadow mb-4 topbar static-top">
                      <div class="container-fluid"><button class="btn btn-link d-md-none rounded-circle mr-3" id="sidebarToggleTop" type="button"><i class="fas fa-bars"></i></button>
                          <ul class="nav navbar-nav flex-nowrap ml-auto">
                              <li class="nav-item dropdown d-sm-none no-arrow"><a class="dropdown-toggle nav-link" data-toggle="dropdown" aria-expanded="false" href="#"><i class="fas fa-search"></i></a>
                                  <div class="dropdown-menu dropdown-menu-right p-3 animated--grow-in" role="menu" aria-labelledby="searchDropdown">
                                      <form class="form-inline mr-auto navbar-search w-100">
                                          <div class="input-group"><input class="bg-light form-control border-0 small" type="text" placeholder="Search for ..."/>
                                              <div class="input-group-append"><button class="btn btn-primary py-0" type="button"><i class="fas fa-search"></i></button></div>
                                          </div>
                                      </form>
                                  </div>
                              </li>
                              <li class="nav-item dropdown no-arrow mx-1" role="presentation"></li>
                              <li class="nav-item dropdown no-arrow mx-1" role="presentation">
                                  <div class="shadow dropdown-list dropdown-menu dropdown-menu-right" aria-labelledby="alertsDropdown"></div>
                              </li>
                              <div class="d-none d-sm-block topbar-divider"></div>
                              <li class="nav-item dropdown no-arrow" role="presentation"></li>
                          </ul>
                      </div>
                  </nav>
                  <div class="container-fluid">
                      <div class="d-sm-flex justify-content-between align-items-center mb-4">
                          <h3 class="text-dark mb-0">Inicio</h3>
                      </div>
                      <div class="row">
                          <div class="col-md-6 col-xl-3 mb-4">
                              <div class="card shadow border-left-primary py-2">
                                  <div class="card-body">
                                      <div class="row align-items-center no-gutters">
                                          <div class="col mr-2">
                                              <div class="text-uppercase text-primary font-weight-bold text-xs mb-1"></div>
                                          </div>
                                          <div class="col-auto">
                                              nada
                                          </div>
                                      </div>
                                  </div>
                              </div>
                          </div>
                          <div class="col-md-6 col-xl-3 mb-4">
                              <div class="card shadow border-left-success py-2">
                                  <div class="card-body">
                                      <div class="row align-items-center no-gutters">
                                          <div class="col mr-2">
                                              <div class="text-uppercase text-success font-weight-bold text-xs mb-1"><span>REGISTRO BATERÍA</span></div>
                                              <div class="text-dark font-weight-bold h5 mb-0"><span>58%</span></div>
                                          </div>
                                          <div class="col-auto"><i class="fas fa-battery-half fa-2x text-gray-300"></i></div>
                                      </div>
                                      <div class="row align-items-center no-gutters space-margin">
                                          <div class="col mr-2">
                                              <div class="text-uppercase text-success font-weight-bold text-xs mb-1"><span>Media luminosidad</span></div>
                                              <div class="text-dark font-weight-bold h5 mb-0"><span>352</span></div>
                                          </div>
                                          <div class="col-auto"><i class="far fa-lightbulb fa-2x text-gray-300"></i></div>
                                      </div>
                                  </div>
                              </div>
                          </div>
                          <div class="col-md-6 col-xl-3 mb-4">
                              <div class="card shadow border-left-info py-2">
                                  <div class="card-body">
                                      <div class="row align-items-center no-gutters">
                                          <div class="col mr-2">
                                              <div class="text-uppercase text-info font-weight-bold text-xs mb-1"><span>Tasks</span></div>
                                              <div class="row no-gutters align-items-center">
                                                  <div class="col-auto">
                                                      <div class="text-dark font-weight-bold h5 mb-0 mr-3"><span>50%</span></div>
                                                  </div>
                                                  <div class="col">
                                                      <div class="progress progress-sm">
                                                          nada
                                                      </div>
                                                  </div>
                                              </div>
                                          </div>
                                          <div class="col-auto"><i class="fas fa-clipboard-list fa-2x text-gray-300"></i></div>
                                      </div>
                                      <div class="row align-items-center no-gutters space-margin">
                                          <div class="col mr-2">
                                              <div class="text-uppercase text-info font-weight-bold text-xs mb-1"><span>Tasks</span></div>
                                              <div class="row no-gutters align-items-center">
                                                  <div class="col-auto">
                                                      <div class="text-dark font-weight-bold h5 mb-0 mr-3"><span>50%</span></div>
                                                  </div>
                                                  <div class="col">
                                                      <div class="progress progress-sm">
                                                          nada
                                                      </div>
                                                  </div>
                                              </div>
                                          </div>
                                          <div class="col-auto"><i class="fas fa-clipboard-list fa-2x text-gray-300"></i></div>
                                      </div>
                                  </div>
                              </div>
                          </div>
                          <div class="col-md-6 col-xl-3 mb-4">
                              <div class="card shadow border-left-warning py-2">
                                  <div class="card-body">
                                      <div class="row align-items-center no-gutters">
                                          <div class="col mr-2">
                                              <div class="text-uppercase text-warning font-weight-bold text-xs mb-1"><span>Pending Requests</span></div>
                                              <div class="text-dark font-weight-bold h5 mb-0"><span>18</span></div>
                                          </div>
                                          <div class="col-auto"><i class="fas fa-comments fa-2x text-gray-300"></i></div>
                                      </div>
                                      <div class="row align-items-center no-gutters space-margin">
                                          <div class="col mr-2">
                                              <div class="text-uppercase text-warning font-weight-bold text-xs mb-1"><span>Pending Requests</span></div>
                                              <div class="text-dark font-weight-bold h5 mb-0"><span>18</span></div>
                                          </div>
                                          <div class="col-auto"><i class="fas fa-comments fa-2x text-gray-300"></i></div>
                                      </div>
                                  </div>
                              </div>
                          </div>
                      </div>
                      <div class="row">
                          <div class="col-lg-7 col-xl-8">
                              <div class="card shadow mb-4">
                                  <div class="card-header d-flex justify-content-between align-items-center">
                                      <h6 class="text-primary font-weight-bold m-0">Gyroscope</h6>
                                      <div class="dropdown no-arrow"><button class="btn btn-link btn-sm dropdown-toggle" data-toggle="dropdown" aria-expanded="false" type="button"><i class="fas fa-ellipsis-v text-gray-400"></i></button>
                                          <div class="dropdown-menu shadow dropdown-menu-right animated--fade-in" role="menu">
                                              <p class="text-center dropdown-header">dropdown header:</p><a class="dropdown-item" role="presentation" href="#">&nbsp;Action</a><a class="dropdown-item" role="presentation" href="#">&nbsp;Another action</a>
                                              <div class="dropdown-divider"></div><a class="dropdown-item" role="presentation" href="#">&nbsp;Something else here</a></div>
                                      </div>
                                  </div>
                                  <div class="card-body"></div>
                              </div>
                          </div>
                          <div class="col-lg-5 col-xl-4">
                              <div class="card shadow mb-4">
                                  <div class="card-header d-flex justify-content-between align-items-center">
                                      <h6 class="text-primary font-weight-bold m-0">Datos</h6>
                                      <div class="dropdown no-arrow"><button class="btn btn-link btn-sm dropdown-toggle" data-toggle="dropdown" aria-expanded="false" type="button"><i class="fas fa-ellipsis-v text-gray-400"></i></button>
                                          <div class="dropdown-menu shadow dropdown-menu-right animated--fade-in"
                                              role="menu">
                                              <p class="text-center dropdown-header">dropdown header:</p><a class="dropdown-item" role="presentation" href="#">&nbsp;Action</a><a class="dropdown-item" role="presentation" href="#">&nbsp;Another action</a>
                                              <div class="dropdown-divider"></div><a class="dropdown-item" role="presentation" href="#">&nbsp;Something else here</a></div>
                                      </div>
                                  </div>
                                  <div class="card-body"></div>
                              </div>
                          </div>
                      </div>
                      <div class="row">
                          <div class="col-lg-6 mb-4"></div>
                          <div class="col">
                              <div class="row"></div>
                          </div>
                      </div>
                  </div>
              </div></div></div>
              <script src="./assets/js/jquery.min.js" type="text/babel"></script>
              <script src="./assets/bootstrap/js/bootstrap.min.js"></script>
              <script src="./assets/js/chart.min.js"></script>
              <script src="./assets/js/bs-init.js"></script>
              <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.4.1/jquery.easing.js"></script>
              <script src="./assets/js/theme.js"></script>
          </main>
      )
    }
  }

export default Main;