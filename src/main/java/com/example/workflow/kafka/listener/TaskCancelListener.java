package com.example.workflow.kafka.listener;

import com.example.workflow.config.QBpmProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.reactor.bus.CamundaEventBus;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author Kornilov Oleg
 */
@Slf4j
@RequiredArgsConstructor
@CamundaSelector(type = "userTask", event = TaskListener.EVENTNAME_DELETE)
public class TaskCancelListener implements TaskListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final QBpmProperties qBpmProperties;
    private final CamundaEventBus eventBus;
    private final ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        TaskCancelEventDto userTask = TaskCancelEventDto.builder()
                .externalId(delegateTask.getId())
                .build();
        try {
            String jsonString = objectMapper.writeValueAsString(userTask);
            log.debug("TaskCancelEvent from activity: {} with params: {}", delegateTask.getName(), jsonString);
            kafkaTemplate.send(qBpmProperties.getTopics().getTaskCancelEvent(), jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }

    }

    @Builder
    @AllArgsConstructor
    @Data
    private static class TaskCancelEventDto {
        protected String humanTaskId;
        protected String externalId;
        protected String cancelReason;
    }

}
