import React from 'react'
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

const Clients = ({ clients }) => {
    if (clients) {
        return (
        clients.map((client) =>
                <option value={ client._id }>{ client.device }</option>
            )
        )
    } else {
        return (
            <option>No existen dispositivos</option>
        )
    }
}

export default Clients;