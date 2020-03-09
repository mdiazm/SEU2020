import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

import 'bootstrap/dist/css/bootstrap.min.css';
import CanvasJSReact from './assets/canvasjs.react';

var CanvasJS = CanvasJSReact.CanvasJS;
var CanvasJSChart = CanvasJSReact.CanvasJSChart;

class App extends Component {	
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

export default App;
