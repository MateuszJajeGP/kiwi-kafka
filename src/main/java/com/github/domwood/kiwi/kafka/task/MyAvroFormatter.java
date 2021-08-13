package com.github.domwood.kiwi.kafka.task;

import io.confluent.kafka.formatter.AvroMessageFormatter;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class MyAvroFormatter extends AvroMessageFormatter implements Deserializer<String> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public MyAvroFormatter() {
    }

    public MyAvroFormatter(String srurl) {
        Properties avroFormatterConfig = new Properties();
        avroFormatterConfig.put("schema.registry.url", srurl);
        this.init(avroFormatterConfig);
    }

    public String format(byte[] payload) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String utf8 = StandardCharsets.UTF_8.name();
            PrintStream ps = new PrintStream(baos, true, utf8);
            this.writeTo(payload, ps);
            return baos.toString(utf8);
        } catch (Exception e) {
            logger.error("Got exception during deserializing", e);
            return new String(payload);
        }
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
        Properties avroFormatterConfig = new Properties();
        avroFormatterConfig.put("schema.registry.url", configs.get("schema-registry-url"));
        this.init(avroFormatterConfig);
    }

    @Override
    public String deserialize(String s, byte[] bytes) {
        return format(bytes);
    }

    @Override
    public String deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }
}
