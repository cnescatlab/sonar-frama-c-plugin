import React from "react";
import template from "./MetricsSummaryPanel.css";
import MetricsSummary from '../MetricsSummary';
import MetricsBulletChart from '../MetricsBulletChart';

class MetricsSummaryPanel extends React.Component {

    render() {
        return ( <div className = "MetricsSummaryPanel" >
            <h3>{ this.props.label }</h3>
            <table>
                <tr>
                    <th id="empty-cell"></th>
                    <th>Total</th>
                    <th>Min</th>
                    <th>Mean</th>
                    <th>Max</th>
                </tr>
                <tbody>
                {
                    this.props.data.map(
                        (item) =>
                        <MetricsSummary item = { item }/>
                    )
                }
                </tbody>
            </table>
            {
                this.props.data.map(
                    (item) =>
                    <MetricsBulletChart item = { item } />
                )
            }
            </div>
        );
    };
}

export default MetricsSummaryPanel;