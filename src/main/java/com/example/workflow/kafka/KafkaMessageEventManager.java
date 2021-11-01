package com.example.workflow.kafka;

import com.example.workflow.dto.KafkaOutputMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.impl.interceptor.Session;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class KafkaMessageEventManager implements Session {

    private final KafkaMessageSender kafkaMessageSender;
    private final List<KafkaOutputMessage> messageList = new LinkedList<>();

    public void addMessage(KafkaOutputMessage kafkaOutputMessage) {
        messageList.add(kafkaOutputMessage);
    }

    @Override
    public void flush() {
        if (log.isDebugEnabled()) {
            log.debug("KafkaMessageEventManager: Flush message list");
        }
        messageList.forEach(kafkaOutputMessage ->
                kafkaMessageSender.send(kafkaOutputMessage.getTopicName(), kafkaOutputMessage.getPayload(), kafkaOutputMessage.getHeaders())
        );
    }

    @Override
    public void close() {
        if (log.isDebugEnabled()) {
            log.debug("MessageEventManager: Clear message list");
        }
        messageList.clear();
    }
}
