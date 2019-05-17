import React, {Component} from "react";
import PropTypes from "prop-types";
import {Button, Modal, ModalBody, ModalFooter, ModalHeader} from "reactstrap";
import * as ApiService from "../../../services/ApiService";
import {toast} from "react-toastify";

class DeleteTopic extends Component {
    constructor(props) {
        super(props);
        this.state = {
            modal: false
        }
    }


    open = () => {
        this.setState({
            modal: true
        })
    };

    close = () => {
        this.setState({
            modal: false
        })
    };

    deleteTopic = () => {
        ApiService.deleteTopic(this.props.topic, () =>{
            toast.success("Topic Deletion Successful");
            this.setState({
                modal: false
            }, this.props.onComplete)
        }, (err) => toast.error(`Failed to delete topic ${err.message}`))
    };

    render() {
        return (
            <div>
                <Button color="danger" onClick={() => this.open()}>Delete Topic</Button>

                <Modal isOpen={this.state.modal} toggle={this.close} >
                    <ModalHeader toggle={this.close}>Delete Kafka Topic</ModalHeader>
                    <ModalBody>
                        <p>
                            Note: This will only have an effect if the broker has <i>delete.topic.enable</i> set to true.
                        </p>
                        <p>
                            <b>
                                Deleting the topic will lead to a loss of all data on that topic. Are you sure?
                            </b>
                        </p>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="danger" onClick={this.deleteTopic}>Yes</Button>{' '}
                        <Button color="success" onClick={this.close}>Cancel</Button>
                    </ModalFooter>
                </Modal>

            </div>
        )
    }
}

DeleteTopic.propTypes = {
    topic: PropTypes.string.isRequired,
    onComplete: PropTypes.func.isRequired
};


export default DeleteTopic;