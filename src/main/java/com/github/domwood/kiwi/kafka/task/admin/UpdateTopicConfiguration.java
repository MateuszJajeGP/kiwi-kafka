package com.github.domwood.kiwi.kafka.task.admin;

import com.github.domwood.kiwi.data.input.UpdateTopicConfig;
import com.github.domwood.kiwi.kafka.resources.KafkaAdminResource;
import com.github.domwood.kiwi.kafka.task.KafkaTask;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.common.config.ConfigResource;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.github.domwood.kiwi.utilities.FutureUtils.toCompletable;

public class UpdateTopicConfiguration implements KafkaTask<UpdateTopicConfig, Void, KafkaAdminResource> {
    @Override
    public CompletableFuture<Void> execute(KafkaAdminResource resource, UpdateTopicConfig input) {
        return toCompletable(resource.updateTopicConfiguration(asConfigs(input)).all());
    }

    private Map<ConfigResource, Config> asConfigs(UpdateTopicConfig topicConfig){
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topicConfig.topic());
        Config config = new Config(topicConfig.config().entrySet()
                .stream()
                .map(kv -> new ConfigEntry(kv.getKey(), kv.getValue()))
                .collect(Collectors.toList()));
        return ImmutableMap.of(resource, config);
    }
}
