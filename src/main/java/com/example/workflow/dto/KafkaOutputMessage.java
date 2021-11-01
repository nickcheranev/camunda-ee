package com.example.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Header;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class KafkaOutputMessage {
    private Header[] headers;
    private String topicName;
    private Object payload;
}
