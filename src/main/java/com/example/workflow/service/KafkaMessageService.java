package com.example.workflow.service;

import org.apache.kafka.common.header.Header;

public interface KafkaMessageService {
    void sendMessage(String topic, Object payload);

    void sendMessage(String topic, Object payload, Header... headers);
}
