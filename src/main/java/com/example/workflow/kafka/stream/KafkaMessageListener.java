package com.example.workflow.kafka.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

import java.io.IOException;

/**
 * @author Kornilov Oleg
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageListener implements MessageListener<String, String> {

    private final MessageProcessor messageProcessor;

    //    @Auditable
    @Override
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        try {
            messageProcessor.process(consumerRecord.topic(), consumerRecord.value());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}