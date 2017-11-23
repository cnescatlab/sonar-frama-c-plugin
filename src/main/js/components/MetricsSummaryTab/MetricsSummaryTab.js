import "./MetricsSummaryTab.css";
import MetricsSummary from '../MetricsSummary';
import MetricsBulletChart from '../MetricsBulletChart';
import React from "react";

class MetricsSummaryTab extends React.Component {

    state = {
        data: []
    };

    componentDidMount() {

        this.setState({
            data: [
                { name: 'Nesting', total: '-', min: '-', mean: '-', max: '-' },
                { name: 'Ratio Comment', total: '-', min: '-', mean: '-', max: '-' },
                { name: 'Complexity Simplified', total: '-', min: 3, mean: 3.5, max: 4 },
                { name: 'Line Of Code', total: 27, min: 12, mean: 13.5, max: 15 }
            ]
        });
    }

    render() {
        return (
        	    <div className="App">

        	    <table>
        	      <tr>
        	      <th></th>
        	      <th>Total</th>
        	      <th>Min</th>
        	      <th>Mean</th>
        	      <th>Max</th>
        	      </tr>
        	      <tbody>
        	        {this.state.data.map(
        	            (item) =>
        	            <MetricsSummary
        	                item={item}
        	              />
        	          )
        	        }
        	        </tbody>
        	    </table>
        	    <MetricsBulletChart/>
        	        </div>
        	 
        		);
    }
}

export default MetricsSummaryTab;