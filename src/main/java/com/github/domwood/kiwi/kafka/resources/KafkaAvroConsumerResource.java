package com.github.domwood.kiwi.kafka.resources;

//import com.github.domwood.kiwi.exceptions.KafkaResourceClientCloseException;
//import com.github.domwood.kiwi.kafka.task.MyAvroFormatter;
//import org.apache.commons.lang3.tuple.Pair;
//import org.apache.kafka.clients.consumer.*;
//import org.apache.kafka.common.TopicPartition;
//import org.apache.kafka.common.serialization.ByteArrayDeserializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.Duration;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;
//
//import static java.util.stream.Collectors.toMap;
//
//public class KafkaAvroConsumerResource extends AbstractKafkaResource<KafkaConsumer<String, String>> {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    private Properties properties;
//    private MyAvroFormatter formatter;
//
//    public KafkaAvroConsumerResource(Properties config) {
//        super(config);
//        formatter = new MyAvroFormatter(config.getProperty("schema-registry-url"));
//    }
//
//    @Override
//    protected KafkaConsumer<String, byte[]> createClient(Properties props) {
//        //TODO improve passing these values
//        String groupIdPrefix = props.getProperty("groupIdPrefix", "");
//        String groupIdSuffix = props.getProperty("groupIdSuffix", "");
//        String groupId = String.format("%s%s%s", groupIdPrefix, Thread.currentThread().getName(), groupIdSuffix);
//        props.remove("groupIdPrefix");
//        props.remove("groupIdSuffix");
//        props.remove(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG);
//
//        this.properties = new Properties();
//        properties.putAll(props);
//        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, groupId);
//        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getCanonicalName());
//
//
//        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, groupId);
//        return new KafkaConsumer<>(properties);
//    }
//
//    @Override
//    protected void closeClient() throws KafkaResourceClientCloseException {
//        //TODO sort out concurrent modification issue
//        logger.info("Kafka consumer client closing...");
//        try {
//            this.getClient().unsubscribe();
//        } catch (Exception e) {
//            logger.warn("Failed to cleanly unsubscribe");
//        }
//        try {
//            this.getClient().close();
//            logger.info("Kafka consumer closed for groupId: " + this.getGroupId());
//        } catch (Exception e) {
//            throw new KafkaResourceClientCloseException("Failed to cleanly close WebSocketService, due to " + e.getMessage(), e);
//        }
//    }
//
//    public boolean isCommittingConsumer() {
//        return Optional.ofNullable(this.properties.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG))
//                .orElse("true")
//                .equals("true");
//    }
//
//    public void subscribe(List<String> topics) {
//        this.getClient().subscribe(topics);
//    }
//
//    public Set<TopicPartition> assignment() {
//        return this.getClient().assignment();
//    }
//
//    public ConsumerRecords<String, String> poll(Duration timeout) {
//        ConsumerRecords<String, byte[]> poll = this.getClient().poll(timeout);
//
//        Map<TopicPartition, List<ConsumerRecord<String, String>>> records4 = poll.partitions()
//                .stream()
//                .map(tp ->
//                        Map.entry(
//                                tp,
//                                poll.records(tp).stream()
//                                        .map(elt -> new ConsumerRecord<>(elt.topic(), elt.partition(), elt.offset(), elt.timestamp(), elt.timestampType(), elt.checksum(), elt.serializedKeySize(), elt.serializedValueSize(), elt.key(), formatter.format(elt.value()), elt.headers()))
//                                        .collect(Collectors.toList())
//                        )
//                ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//        return new ConsumerRecords<>(records4);
//    }
//
//    public void seekToBeginning(Set<TopicPartition> topicPartitions) {
//        this.getClient().seekToBeginning(topicPartitions);
//    }
//
//    public void seek(Map<TopicPartition, Long> topicPartitions) {
//        topicPartitions.forEach((k, v) -> this.getClient().seek(k, v));
//    }
//
//    public Map<TopicPartition, Long> endOffsets(Set<TopicPartition> topicPartitions) {
//        return this.getClient().endOffsets(topicPartitions);
//    }
//
//    public Map<TopicPartition, Long> currentPosition(Set<TopicPartition> topicPartitions) {
//        return topicPartitions.stream()
//                .map(tp -> Pair.of(tp, getClient().position(tp)))
//                .collect(toMap(Pair::getLeft, Pair::getRight));
//    }
//
//    public void commitAsync(Map<TopicPartition, OffsetAndMetadata> offsets, OffsetCommitCallback callback) {
//        this.getClient().commitAsync(offsets, callback);
//    }
//
//    public void unsubscribe() {
//        try {
//            this.getClient().unsubscribe();
//            logger.info("Unsubscribing client from topics");
//        } catch (Exception e) {
//            logger.warn("Failed to unsubscribe client", e);
//        }
//
//    }
//
//    public String getGroupId() {
//        return this.properties.getProperty(ConsumerConfig.GROUP_ID_CONFIG);
//    }
//}
