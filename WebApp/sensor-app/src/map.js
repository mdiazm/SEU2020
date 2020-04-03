import React, { Component } from 'react';

import Sensors from './sensors.js';
import { Graphic } from './graphics.js';
import { TableCoordinates } from './tables.js';

class Map extends Component {
	constructor(props) {
		super(props);
		this.state = { sensors: [] };
	}

	async componentDidMount() {
		fetch('http://localhost/getAvailableSensors?deviceId=ffffffff-e16c-f9c0-0000-000075b319f8')
		.then(res => res.json())
		.then((data) => {
		  this.setState({ sensors: data })
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
					  <div class="sidebar-brand-text mx-2"><span>Sensor Monitoring</span></div>
				  </a>
				  <hr class="sidebar-divider my-0"/>
				  <ul class="nav navbar-nav text-light" id="accordionSidebar">
				  <li class="nav-item" role="presentation"><a class="nav-link active" href="/app"><span>Inicio</span></a></li>
					  <Sensors sensors={this.state.sensors}/>
				  </ul>
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
			  </div>
				</div>
				<div class="row">
					<div id="map"></div>
				</div>
				</div>
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

export default Map;