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
      <li class="nav-item" role="presentation">
        <a class="nav-link active" href={sensor}>
          <span>{sensor}</span>
        </a>
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