import React from "react";
import "./MetricsSummaryTab.css";
import MetricsSummaryPanel from '../MetricsSummaryPanel';

class MetricsSummaryTab extends React.Component {

    state = {
        data: []
    };

    componentDidMount() {

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