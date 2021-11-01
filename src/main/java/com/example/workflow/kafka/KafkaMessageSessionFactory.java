package com.example.workflow.kafka;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;

@RequiredArgsConstructor
public class KafkaMessageSessionFactory implements SessionFactory {
    private final KafkaMessageSender kafkaMessageSender;

    @Override
    public Class<?> getSessionType() {
        return KafkaMessageEventManager.class;
    }

    @Override
    public Session openSession() {
        return new KafkaMessageEventManager(kafkaMessageSender);
    }
}
