package com.example.workflow.kafka;

import com.example.workflow.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;


@Slf4j
@RequiredArgsConstructor
public class KafkaMessageSender {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String producer;


    public void send(String topic, Object object) {
        send(topic, object, new Header[]{});
    }

    //    @Auditable
    public void send(String topic, Object object, Header... headers) {
        try {
            final String jsonMessage = objectMapper.writeValueAsString(object);
            final ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonMessage);

            if (headers != null) {
                for (Header header : headers) {
                    record.headers().add(header);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Send message to {}, body: {}", topic, jsonMessage);
            }

            record.headers().add(new RecordHeader(Constants.PRODUCER, producer.getBytes()));
            kafkaTemplate.send(record);
        } catch (Exception e) {
            log.error("Could not transform and send message due to: " + e.getMessage(), e);
        }
    }
}