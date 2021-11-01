package com.example.workflow.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;


@Getter
public class MessageMap extends HashMap<String, Object> {

    private final String businessKey;
    private final String sender;
    private final String correlationId;
    private final String eventCorrelationId;

    @JsonCreator
    public MessageMap(@JsonProperty("businessKey") String businessKey,
                      @JsonProperty("sender") String sender,
                      @JsonProperty("correlationId") String correlationId,
                      @JsonProperty("eventCorrelationId") String eventCorrelationId) {
        this.businessKey = businessKey;
        this.sender = sender;
        this.correlationId = correlationId;
        this.eventCorrelationId = eventCorrelationId;
    }
}
