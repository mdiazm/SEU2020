import React from 'react';

const TableCoordinates = ({ data }) => {
		return (
			data.map((da) => 
			<tr>
				<td>{da["timestamp"]}</td>
				<td>{da["x"]}</td>
				<td>{da["y"]}</td>
				<td>{da["z"]}</td>
			</tr>
			)
		)
}

const TableLightValue = ({ data, value }) => {
	return (
		data.map((da) => 
		<tr>
			<td>{da["timestamp"]}</td>
			<td>{da[value]}</td>
		</tr>
		)
	)
}

export {
	TableCoordinates,
	TableLightValue
}
export default TableCoordinates;