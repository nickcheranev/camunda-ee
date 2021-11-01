package com.example.workflow.service;

import com.example.workflow.dto.KafkaOutputMessage;
import com.example.workflow.kafka.KafkaMessageEventManager;
import org.apache.kafka.common.header.Header;
import org.camunda.bpm.engine.impl.context.Context;

public class KafkaMessageServiceImpl implements KafkaMessageService {

    @Override
    public void sendMessage(String topic, Object payload) {
        sendMessage(topic, payload, new Header[]{});
    }

    @Override
    public void sendMessage(String topic, Object payload, Header... headers) {
        final KafkaMessageEventManager kafkaMessageEventManager = Context.getCommandContext()
                .getSession(KafkaMessageEventManager.class);

        kafkaMessageEventManager.addMessage(KafkaOutputMessage.builder()
                .topicName(topic)
                .payload(payload)
                .headers(headers)
                .build());
    }
}
