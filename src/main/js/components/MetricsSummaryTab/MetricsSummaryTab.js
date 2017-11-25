import React from "react";
import "./MetricsSummaryTab.css";
import MetricsSummaryPanel from '../MetricsSummaryPanel';
import {getJSON} from 'sonar-request';

class MetricsSummaryTab extends React.Component {
	
	const metricKeys = ['framac-cyclomatic-complexity-min','framac-cyclomatic-complexity-mean','framac-cyclomatic-complexity-max','framac-sloc','framac-sloc-min','framac-sloc-mean','framac-sloc-max'];

    state = {
        data: []
    };

    findMeasures() {
/*     fetch('http://192.168.1.25:9000/api/measures/component?metricKeys=framac-sloc-mean&component=fr.cnes.framac:framac-metrics-sq-scanner').then(function (response) {
      console.log(response.measures);
    return response.measures;
   }); */

    	return getJSON('/api/measures/component', {
    		metricKeys:'framac-sloc-mean',
    		component:'fr.cnes.framac:framac-metrics-sq-scanner'
    	}).then(function (response) {
    		return response.component.measures;
    	});
    }

    constructor(){
    	super();
    	this.findMeasures = this.findMeasures.bind(this);
    }

    componentDidMount() {
        
    	this.findMeasures().then((valuesReturnedByAPI) => {
    		this.setState({
    			data: valuesReturnedByAPI
    		});
    	});

        this.setState({
            data: [
                { name: 'Complexity Simplified', total: '-', min: 3, mean: 3.5, max: 4 },
                { name: 'Line Of Code', total: 35, min: 12, mean: 13.5, max: 15 }
            ]
        });
    }

    render() {
        return (
            <div className = "MetricsSummaryTab" >

                <MetricsSummaryPanel label='' data={this.state.data}/>

            </div>
        );
    };
}

export default MetricsSummaryTab;
