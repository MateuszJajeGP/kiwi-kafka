package com.github.domwood.kiwi.kafka.resources;

import com.github.domwood.kiwi.kafka.task.admin.UpdateTopicConfiguration;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.requests.DeleteTopicsResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaAdminResource extends KafkaResource<AdminClient>{

    public KafkaAdminResource(Properties props) {
        super(props);
    }

    protected AdminClient createClient(Properties props){
        return AdminClient.create(props);
    }

    @Override
    protected void closeClient() throws Exception {
        this.getClient().close(10, TimeUnit.SECONDS);
    }

    public DescribeClusterResult describeCluster(){
        return this.getClient().describeCluster();
    }

    public DescribeLogDirsResult describeLogDirs(List<Integer> nodes){
        return this.getClient().describeLogDirs(nodes);
    }

    public DescribeConfigsResult describeConfigs(Collection<ConfigResource> resources) {
        return this.getClient().describeConfigs(resources);
    }

    public DescribeTopicsResult describeTopics(Collection<String> topicNames) {
        return this.getClient().describeTopics(topicNames);
    }

    public ListConsumerGroupsResult listConsumerGroups() {
        return this.getClient().listConsumerGroups();
    }

    public DescribeConsumerGroupsResult describeConsumerGroups(Collection<String> groupIds) {
        return this.getClient().describeConsumerGroups(groupIds);
    }

    public CreateTopicsResult createTopics(Collection<NewTopic> newTopics) {
        return this.getClient().createTopics(newTopics);
    }

    public ListTopicsResult listTopics() {
        return this.getClient().listTopics();
    }

    public ListConsumerGroupOffsetsResult listConsumerGroupOffsets(String groupId){
        return this.getClient().listConsumerGroupOffsets(groupId);
    }

    public DeleteTopicsResult deleteTopics(List<String> topics) {
        return this.getClient().deleteTopics(topics);
    }

    public DeleteConsumerGroupsResult deleteConsumerGroups(List<String> groupIds) {
        return this.getClient().deleteConsumerGroups(groupIds);
    }

    public AlterConfigsResult updateTopicConfiguration(Map<ConfigResource, Config> configs){
        return this.getClient().alterConfigs(configs);
    }
}
