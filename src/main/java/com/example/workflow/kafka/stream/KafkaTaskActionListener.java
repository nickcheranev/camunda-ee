package com.example.workflow.kafka.stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.kafka.listener.MessageListener;

import java.io.IOException;
import java.util.Map;

/**
 * @author Kornilov Oleg
 */
@RequiredArgsConstructor
@Slf4j
public class KafkaTaskActionListener implements MessageListener<String, String> {

    private static final String CANCEL_BRIEF = "CANCEL";

    private final ProcessEngine camunda;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            HumanTaskTransition humanTask = objectMapper.readValue(record.value(), HumanTaskTransition.class);
            TaskService taskService = camunda.getTaskService();
            Task task = taskService.createTaskQuery().taskId(humanTask.getExternalID()).active().singleResult();
            if (task != null) {
                Map<String, Object> variables = objectMapper.readValue(objectMapper.writeValueAsString(humanTask),
                        new TypeReference<Map<String, Object>>() {
                        });
                /*if (CANCEL_BRIEF.equals(humanTask.getTransitionBrief()) || CANCEL_BRIEF.toLowerCase().equals(humanTask.getTransitionBrief())) {
                    taskService.deleteTask(humanTask.getExternalID(), humanTask.getCancelReason());
                    log.debug("Delete task. Variables: {}",variables);
                } else {*/
                taskService.complete(humanTask.getExternalID(), variables);
                log.debug("Task action. Transition: '{}'. Variables: {}",
                        humanTask.getTransitionBrief(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(variables));
                //}
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    @Data
    public static class HumanTaskTransition {
        protected Long humanTaskID;
        protected String transitionBrief;
        protected String processResult;
        protected String comment;
        protected String cancelReason;
        protected String externalID;
        protected String processInstanceID;
        protected String correlationID;
    }
}
