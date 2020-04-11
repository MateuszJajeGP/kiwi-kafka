import React, {Component} from "react";
import PropTypes from "prop-types";
import "../../App.css";
import {Dropdown, DropdownItem, DropdownMenu, DropdownToggle} from "reactstrap";
import * as ApiService from "../../services/ApiService";
import {toast} from "react-toastify";
import SessionStore from "../../services/SessionStore";

class ClusterChooser extends Component {

    constructor(props) {
        super(props);

        this.state = {
            clusterDropDownOpen: false,
            clusters: []
        };

    }
    componentDidMount() {
        this.mounted = true;

        ApiService.getKafkaConfiguration((kafkaConfig) => {
            let activeCluster = Object.keys(kafkaConfig || {"default":null})[0];
            if(kafkaConfig[SessionStore.getActiveCluster()]){
                activeCluster = SessionStore.getActiveCluster();
            }
            else{
                SessionStore.setActiveCluster(activeCluster);
            }
            if(this.mounted){
                this.setState({
                    kafkaConfig: kafkaConfig,
                    clusters: Object.keys(kafkaConfig),
                    activeCluster: activeCluster
                })
            }

        }, () => toast.error("No connection to server"));
    }

    toggleCluster = () => {
        this.setState({
            clusterDropDownOpen: !this.state.clusterDropDownOpen
        });
    };

    setActiveCluster = (cluster) => {
        this.setState({
            activeCluster: cluster
        }, () => SessionStore.setActiveCluster(cluster));
    };

    render() {
        return (
            <div>
                <Dropdown nav inNavbar size="sm" isOpen={this.state.clusterDropDownOpen} toggle={this.toggleCluster}>
                    <DropdownToggle nav caret>
                        Active Cluster: {this.state.activeCluster}
                    </DropdownToggle>
                    <DropdownMenu>
                        {
                            this.state.clusters.map((cluster) => (<DropdownItem key={cluster} onClick={() => this.setActiveCluster(cluster)}>{cluster}</DropdownItem>))
                        }
                    </DropdownMenu>
                </Dropdown>
            </div>
        )
    }
}

ClusterChooser.propTypes = {
    onUpdate: PropTypes.func
};

export default ClusterChooser;