import React, { Component } from 'react';

import Sensors from './sensors.js';
import { Graphic } from './graphics.js';
import { TableCoordinates } from './tables.js';

import socketIOClient from "socket.io-client";

var datos = []
class BatteryInfo extends Component {
	constructor(props) {
			super(props);
			this.state = { battery: "", cadena: "prueba", response: false, endpoint: "http://178.62.241.158:3000" };
	}

	async componentDidMount() {
		var cadena = 'http://localhost/getLastRecordsInFrame?sensorName=battery&secondsFrame=120&deviceId=ffffffff-e16c-f9c0-0000-000075b319f8';

		let res = await fetch(cadena)
		let data = await res.json()

		this.setState({ battery: data[0] })

		const socket = socketIOClient(this.state.endpoint, {"forceNew": true});
      	socket.emit('subscribe', '00000000-5561-036d-0000-000075b319f8')
      	socket.on("message", data => this.state({ sensors: data} ));
	}
	render() {
			return (
				<div class="row no-gutters align-items-center">
					<div class="col-auto">
						<div class="text-dark font-weight-bold h5 mb-0 mr-3">
							<span>{this.state.battery["level"]}%</span>
						</div>
					</div>
				</div>
			)
	}
}

class LightInfo extends Component {
	constructor(props) {
			super(props);
			this.state = { light: "" };
	}

	async componentDidMount() {
			var cadena = 'http://localhost/getLastRecordsInFrame?sensorName=light&secondsFrame=1&deviceId=ffffffff-e16c-f9c0-0000-000075b319f8';

			let res = await fetch(cadena)
			let data = await res.json()

			this.setState({ light: data[0] })
	}
	render() {
			return (
				<div>
					<span>{this.state.light["lux"]}</span>
				</div>
			)
	}
}

class Status extends Component {
	constructor(props) {
			super(props);
			this.state = { status: "" };
	}

	async componentDidMount() {
			var cadena = 'http://localhost/getLastRecordsInFrame?sensorName=status&secondsFrame=1&deviceId=ffffffff-e16c-f9c0-0000-000075b319f8';

			let res = await fetch(cadena)
			let data = await res.json()

			this.setState({ status: data[0] })
	}

	render() {
		if ( this.state.status["value"] !== "discharging" ) {
			return (
				<span>Cargando</span>
			)
		} else {
			return (
				<span>No se está cargando</span>
			)
		}
	}
}

var fecha;
class History extends Component {
	constructor(props) {
			super(props);
			this.state = { status: "" };
	}

	async componentDidMount() {
			var cadena = 'http://localhost/getLastRecordsInFrame?sensorName=status&secondsFrame=1&deviceId=ffffffff-e16c-f9c0-0000-000075b319f8';

			let res = await fetch(cadena)
			let data = await res.json()

			this.setState({ status: data[0] })
			fecha = new Date(this.state.status["timestamp"])
			fecha = fecha.getHours() + ":" + fecha.getMinutes() + ":" + fecha.getSeconds() + " | " + fecha.getDate() + "/" + (fecha.getMonth()+1) + "/" + fecha.getFullYear()
	}

	render() {
		return (
			<span>{fecha}</span>
		)
	}
}

var device;
class Device extends Component {
	constructor(props) {
			super(props);
			this.state = { status: "" };
	}

	async componentDidMount() {
			var cadena = 'http://localhost/getLastRecordsInFrame?sensorName=status&secondsFrame=1&deviceId=ffffffff-e16c-f9c0-0000-000075b319f8';

			let res = await fetch(cadena)
			let data = await res.json()

			this.setState({ status: data[0] })
			device = this.state.status["device"].substring(24, this.state.status["device"].length)
	}

	render() {
		return (
			<span>{device}</span>
		)
	}
}

class Main extends Component {
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
				
		var cadena = 'http://localhost/getLastRecordsInFrame?sensorName=accelerometer&secondsFrame=60&deviceId=ffffffff-e16c-f9c0-0000-000075b319f8';
		let res = await fetch(cadena)
		let data = await res.json()

		datos = data
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
				  <div class="container-fluid">
					  <div class="d-sm-flex justify-content-between align-items-center mb-4">
					  </div>
					  <div class="row">
						  <div class="col-md-6 col-xl-4 mb-4">
							  <div class="card shadow border-left-success py-2">
								  <div class="card-body">
									  <div class="row align-items-center no-gutters">
										  <div class="col mr-2">
											  <div class="text-uppercase text-success font-weight-bold text-xs mb-1">
													<span>BATERÍA ACTUAL</span>
												</div>
												<BatteryInfo />
										  </div>
										  <div class="col-auto"><i class="fas fa-battery-half fa-2x text-gray-300"></i></div>
									  </div>
									  <div class="row align-items-center no-gutters space-margin">
										  <div class="col mr-2">
											  <div class="text-uppercase text-success font-weight-bold text-xs mb-1">
													<span>ESTADO DEL DISPOSITIVO</span>
												</div>
											  <div class="text-dark font-weight-bold h5 mb-0">
													<Status />
												</div>
										  </div>
										  <div class="col-auto"><i class="fas fa-charging-station fa-2x text-gray-300"></i></div>
									  </div>
								  </div>
							  </div>
						  </div>
						  <div class="col-md-6 col-xl-4 mb-4">
							  <div class="card shadow border-left-info py-2">
								  <div class="card-body">
									  <div class="row align-items-center no-gutters">
										  <div class="col mr-2">
											  <div class="text-uppercase text-info font-weight-bold text-xs mb-1">
													<span>LUZ AMBIENTE ACTUAL</span>
												</div>
											  <div class="row no-gutters align-items-center">
												  <div class="col-auto">
													  <div class="text-dark font-weight-bold h5 mb-0 mr-3"><LightInfo /></div>
												  </div>
											  </div>
										  </div>
										  <div class="col-auto"><i class="fas fa-lightbulb fa-2x text-gray-300"></i></div>
									  </div>
									  <div class="row align-items-center no-gutters space-margin">
										  <div class="col mr-2">
											  <div class="text-uppercase text-info font-weight-bold text-xs mb-1">
													<span>LUZ MEDIA AMBIENTE</span>
												</div>
											  <div class="row no-gutters align-items-center">
												  <div class="col-auto">
													  <div class="text-dark font-weight-bold h5 mb-0 mr-3">
															<span>Suficiente</span>
														</div>
												  </div>
											  </div>
										  </div>
										  <div class="col-auto"><i class="fas fa-sun fa-2x text-gray-300"></i></div>
									  </div>
								  </div>
							  </div>
						  </div>
						  <div class="col-md-6 col-xl-4 mb-4">
							  <div class="card shadow border-left-warning py-2">
								  <div class="card-body">
									  <div class="row align-items-center no-gutters">
										  <div class="col mr-2">
											  <div class="text-uppercase text-warning font-weight-bold text-xs mb-1">
													<span>Última actualización</span>
												</div>
											  <div class="text-dark font-weight-bold h5 mb-0">
													<History/>
												</div>
										  </div>
										  <div class="col-auto"><i class="fas fa-info-circle fa-2x text-gray-300"></i></div>
									  </div>
									  <div class="row align-items-center no-gutters space-margin">
										  <div class="col mr-2">
											  <div class="text-uppercase text-warning font-weight-bold text-xs mb-1">
												<span>ID de dispositivo</span>
											</div>
											  <div class="text-dark font-weight-bold h5 mb-0">
												<Device />
											</div>
										  </div>
										  <div class="col-auto"><i class="fas fa-id-badge fa-2x text-gray-300"></i></div>
									  </div>
								  </div>
							  </div>
						  </div>
					  </div>
					  <div class="row">
						  <div class="col-lg-7 col-xl-8">
							  <div class="card shadow mb-4">
								  <div class="card-header d-flex justify-content-between align-items-center">
									  <h6 class="text-primary font-weight-bold m-0">Accelerometer</h6>
								  </div>
								  <div class="card-body">
									<Graphic sensor={"/accelerometer"} />
								</div>
							  </div>
						  </div>
						  <div class="col-lg-5 col-xl-4">
							  <div class="card shadow mb-4">
								  <div class="card-header d-flex justify-content-between align-items-center">
									  <h6 class="text-primary font-weight-bold m-0">Datos</h6>
								  </div>
								  <div class="card-body">
									<table class="table table-responsive">
										<thead>
											<tr>
												<th>Hora</th>
												<th>X</th>
												<th>Y</th>
												<th>Z</th>
											</tr>
										</thead>
										<tbody>
											<TableCoordinates data={datos} />
										</tbody>
									</table>
								</div>
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