import React from "react";
import "./MetricsSummaryTab.css";
import MetricsSummaryPanel from '../MetricsSummaryPanel';
import {getJSON} from 'sonar-request';

const metricKeysTab = [
	'framac-cyclomatic-complexity-min',
	'framac-cyclomatic-complexity-mean',
	'framac-cyclomatic-complexity-max',
	'framac-sloc',
	'framac-sloc-min',
	'framac-sloc-mean',
	'framac-sloc-max'];

function findMeasures(componentName) {
	return new Promise(function(resolve, reject) {
		
	let metricKeys = metricKeysTab.reduce((accumulator, currentValue) => accumulator + ',' + currentValue);

	let measure_component = getJSON('/api/measures/component', {
		metricKeys:metricKeys,
		component:componentName
	}).then(function (measure_component) {
		console.log(measure_component);
		
		// Build map 'metric:value'
		let allMetrics = new Map();
		measure_component.component.measures.forEach(element => {
		    for (let index = 0; index < metricKeysTab.length; index++) {
		      const key = metricKeysTab[index];
		      if(element.metric === key){
		    	  allMetrics.set(element.metric, element.value);
		      }      
		    }
		});
		console.log(allMetrics);
		
		// Build result table
		let res=[
/*			{name:'Nesting', total: '-', min: '-', mean: '-', max: '-'},
			{name:'Ratio Comment', total: '-', min: '-', mean: '-', max: '-'},
*/			{name:'Complexity Simplified', 
				total: '-', 
				min: allMetrics.get('framac-cyclomatic-complexity-min'), 
				mean: allMetrics.get('framac-cyclomatic-complexity-mean'), 
				max: allMetrics.get('framac-cyclomatic-complexity-max')
			},
			{name:'Line Of Code', 
				total: allMetrics.get('framac-sloc'),
				min: allMetrics.get('framac-sloc-min'), 
				mean: allMetrics.get('framac-sloc-mean'), 
				max: allMetrics.get('framac-sloc-max')
			}
		];	
		console.log(res);

		resolve(res);
	}).catch(function(error) {
	    console.log('No measures found: ' + error.message); 
	    reject(null);
	});
});
}

class MetricsSummaryTab extends React.Component {
	
    state = {
        data: []
    };

    constructor(){
    	super();
    }

    componentDidMount() {
    	console.log(this.props);
        console.log('MetricsSummaryTab:'+this.props.project);
    	findMeasures(this.props.project.key).then((item) => {
    	    this.setState({
    	      data: item
    	    });
    	});

/*        this.setState({
            data: [
                { name: 'Complexity Simplified', total: '-', min: 3, mean: 3.5, max: 4 },
                { name: 'Line Of Code', total: 35, min: 12, mean: 13.5, max: 15 }
            ]
        });*/
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
