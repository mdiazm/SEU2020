import React from 'react'

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