package com.example.workflow.kafka.stream;

import java.io.IOException;

/**
 * @author Kornilov Oleg
 */
public interface MessageProcessor {
    void process(String topicName, String json) throws IOException;
}
