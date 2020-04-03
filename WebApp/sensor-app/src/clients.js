import React from 'react'

const Clients = ({ clients }) => {
    if (clients) {
        return (
        clients.map((client) =>
                <option value={ client.device }>{ client.device }</option>
            )
        )
    } else {
        return (
            <option>No existen dispositivos</option>
        )
    }
}

export default Clients;