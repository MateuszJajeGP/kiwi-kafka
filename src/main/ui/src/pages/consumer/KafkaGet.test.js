import React from 'react';
import ReactDOM from 'react-dom';
import KafkaGet from './KafkaGet';
import * as ApiService from "../../services/ApiService";
import WebSocketService from "../../services/WebSocketService";
import DataStore from "../../services/GlobalStore";
import {mount} from "enzyme/build";
import { waitForState, wait } from 'enzyme-async-helpers';

jest.mock("../../services/ApiService");
jest.mock("../../services/WebSocketService");
jest.mock("../../services/GlobalStore");

const testDataResponse = {
    responseType:".ImmutableConsumerResponse",
    messages:[
        {
            responseType:".ImmutableConsumedMessage",
            timestamp:1557561292030,
            partition:7,
            offset:30,
            message:"testData1",
            key:"testKey1",
            headers:{}
        },
        {
            responseType:".ImmutableConsumedMessage",
            timestamp:1557561192030,
            partition:8,
            offset:45,
            message:"testData2",
            key:"testKey2",
            headers:{}
        }
    ]
};


const topicList = [
    "exampleTestTopicOne", "exampleTestTopicTwo"
];

beforeEach(() => {
    ApiService.getTopics.mockClear();
    ApiService.consume.mockClear();
    WebSocketService.consume.mockClear();
    DataStore.get.mockImplementation((topics) => [])
});

it('renders without crashing', () => {
    const div = document.createElement('div');
    ReactDOM.render(<KafkaGet />, div);
    ReactDOM.unmountComponentAtNode(div);
});

it('renders via enzyme', () => {
    const wrapper = mount(<KafkaGet />);
    const title = <h1>Get Data From Kafka</h1>
    expect(wrapper.contains(title)).toEqual(true);
});

it('check kafka messages from rest', async () => {

    ApiService.getTopics.mockImplementation((cb, eb) => {
        cb(topicList);
    });

    ApiService.consume.mockImplementation((topics, limit, fromStart, filters, cb, eb) => {
        cb(testDataResponse)
    });

    const wrapper = mount(<KafkaGet />);

    wrapper.find('#topicInput').at(0)
        .simulate('change', { target: { value: 'testDataTopic' } })

    wrapper.find("#messageLimitInput").at(0)
        .simulate('change', { target: { value: 2 } });

    wrapper.find('#consumeViaRestButton').at(0)
        .simulate('click');

    await waitForState(wrapper, state => state.messages && state.messages.length > 0);

    expect(wrapper.exists(`#record_row_${testDataResponse.messages[0].partition}_${testDataResponse.messages[0].offset}`)).toBeTruthy();
    expect(wrapper.exists(`#record_row_${testDataResponse.messages[1].partition}_${testDataResponse.messages[1].offset}`)).toBeTruthy();

    expect(ApiService.getTopics).toHaveBeenCalledTimes(1);
    expect(ApiService.consume).toHaveBeenCalledTimes(1);
    expect(ApiService.consume).toHaveBeenCalledWith(
        ['testDataTopic'],
        2,
        expect.any(Boolean),
        [],
        expect.any(Function),
        expect.any(Function)
    );
});

it('check kafka messages from websocket', async () => {

    ApiService.getTopics.mockImplementation((cb, eb) => {
        cb(topicList);
    });

    WebSocketService.consume.mockImplementation((topics, filters, cb, eb, close) => {
        cb(testDataResponse)
    });

    const wrapper = mount(<KafkaGet />);

    wrapper.find('#topicInput').at(0)
        .simulate('change', { target: { value: 'testDataTopic' } })

    wrapper.find("#messageLimitInput").at(0)
        .simulate('change', { target: { value: 2 } });

    wrapper.find('#consumeViaWebSocketButton').at(0)
        .simulate('click');

    await waitForState(wrapper, state => state.messages && state.messages.length > 0);

    expect(wrapper.exists(`#record_row_${testDataResponse.messages[0].partition}_${testDataResponse.messages[0].offset}`)).toBeTruthy();
    expect(wrapper.exists(`#record_row_${testDataResponse.messages[1].partition}_${testDataResponse.messages[1].offset}`)).toBeTruthy();

    expect(ApiService.getTopics).toHaveBeenCalledTimes(1);
    expect(WebSocketService.consume).toHaveBeenCalledTimes(1);

    expect(WebSocketService.consume).toHaveBeenCalledWith(
        ['testDataTopic'],
        [],
        expect.any(Function),
        expect.any(Function),
        expect.any(Function)
    );
});
