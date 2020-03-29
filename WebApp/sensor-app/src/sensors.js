import React from 'react'
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

const Sensors = ({ sensors }) => {
  if (sensors) {
    return (
      sensors.map((sensor) =>
        <li class="nav-item">
          <Link to={ sensor } class="nav-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-home"></svg>
            { sensor }
          </Link>
        </li>
      )
    )
  } else {
    return (
      <span>No se han encontrado sensores</span>
    )
  }
}

export default Sensors;