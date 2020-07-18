import React, {Component} from "react";
import {Progress, Tooltip} from "reactstrap";
import "./heatmap-red.css";
import "./heatmap-blue.css";
import PropTypes from "prop-types";

class TopicDensityBar extends Component {
    constructor(props) {
        super(props);
        this.state = {
            toolTipToggle: false
        }
    }

    toggleToolTip = (value) => this.setState({toolTipToggle: !value})

    render() {
        return (
            <Progress key={"progress_" + this.props.number} multi max={this.props.data.rangeLength + 20}>
                <Progress barClassName={"bg-heatmap-white"} bar key={"preamble"}
                          value={10}>{this.props.startBar}</Progress>
                {
                    this.props.data.ranges.map((range, index) =>
                        (
                            <Progress bar
                                      key={"pg_" + this.props.number + "_" + index}
                                      id={"pg_" + this.props.number + "_" + index}
                                      value={range.density}
                                      barClassName={"bg-heatmap-" + this.props.color + "-" + range.density}
                                      aria-label={range.label}
                            />
                        ))

                }
                {
                    // this.props.data.ranges.map((range, index) => (
                    //     <Tooltip key={"ttp_" + this.props.number + "_" + index} placement="bottom"
                    //     isOpen={this.state.toolTipToggle} toggle={this.toggleToolTip} target={"pg_"+ this.props.number + "_" + index} />
                    // )
                }
                <Progress barClassName={"bg-heatmap-white"} bar key={"post"} value={10}>{this.props.stopBar}</Progress>
            </Progress>
        )
    }
}

TopicDensityBar.propTypes = {
    number: PropTypes.number.isRequired,
    data: PropTypes.object.isRequired,
    color: PropTypes.string.isRequired,
    startBar: PropTypes.string.isRequired,
    stopBar: PropTypes.string.isRequired,
};


export default TopicDensityBar;