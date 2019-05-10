package com.github.domwood.kiwi.api.ws;

import com.github.domwood.kiwi.data.input.ConsumerRequest;
import com.github.domwood.kiwi.data.output.OutboundResponse;
import com.github.domwood.kiwi.kafka.provision.KafkaResourceProvider;
import com.github.domwood.kiwi.kafka.provision.KafkaTaskProvider;
import com.github.domwood.kiwi.kafka.resources.KafkaConsumerResource;
import com.github.domwood.kiwi.kafka.task.consumer.ContinuousConsumeMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class KiwiWebSocketConsumerHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<String, ContinuousConsumeMessages> consumers;
    private final KafkaResourceProvider resourceProvider;
    private final KafkaTaskProvider taskProvider;

    @Autowired
    public KiwiWebSocketConsumerHandler(KafkaResourceProvider resourceProvider, KafkaTaskProvider taskProvider) {
        this.consumers = new ConcurrentHashMap<>();
        this.resourceProvider = resourceProvider;
        this.taskProvider = taskProvider;
    }


    public void addConsumerTask(String id,
                                ConsumerRequest request,
                                Consumer<OutboundResponse> consumer){
        if(consumers.get(id).isClosed()){
            logger.info("Consumer already present for session {}, but is closed, removing", id);
            this.consumers.remove(id);
        }

        if(!consumers.containsKey(id)){
            logger.info("Adding consumer for session {}", id);
            KafkaConsumerResource<String, String> resource = resourceProvider.kafkaStringConsumerResource(request.bootStrapServers());
            ContinuousConsumeMessages consumeMessages = taskProvider.continousConsumeMessages();
            this.consumers.put(id, consumeMessages);
            consumeMessages.registerConsumer(consumer);
            consumeMessages.execute(resource, request)
                    .whenComplete((voidValue, e) -> {
                        if(e != null) logger.error("Task for session "+id+" closed with error", e);
                        else logger.info("Task closed normally for session {}", id);
                    });
        }
        else{
            logger.info("Updating existing consumer for session {}", id);
            ContinuousConsumeMessages consumeMessages = consumers.get(id);
            consumeMessages.update(request);
        }
    }

    public void removeConsumerTask(String id){
        if(consumers.containsKey(id)){
            logger.info("Close and remove continuous consumer task for {} " + id);
            this.consumers.get(id).close();
            this.consumers.remove(id);
        }
    }



}