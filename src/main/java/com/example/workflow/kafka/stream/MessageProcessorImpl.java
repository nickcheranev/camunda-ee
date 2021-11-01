package com.example.workflow.kafka.stream;

import com.example.workflow.Constants;
import com.example.workflow.dto.MessageMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Kornilov Oleg
 */
@Slf4j
@RequiredArgsConstructor
public class MessageProcessorImpl implements MessageProcessor {

    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final ObjectMapper objectMapper;

    @Override
    public void process(String topicName, String json) throws IOException {
        MessageMap message = objectMapper.readValue(json, new TypeReference<MessageMap>() {
        });

        if (log.isDebugEnabled()) {
            log.debug("Input message.\n\tSender: {}\n\tTopic: {}\n\tBusinessKey: {}\n\tCorrelationId: {}",
                    message.getSender(), topicName, message.getBusinessKey(), message.getCorrelationId());
            log.debug("Variables: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message));
        }

        final ExecutionQuery executionQuery = runtimeService.createExecutionQuery();
        final boolean businessKeyExist = message.getBusinessKey() != null;

        if (businessKeyExist) {
            executionQuery.processInstanceBusinessKey(message.getBusinessKey());
        }

        Execution execution = executionQuery
                .messageEventSubscriptionName(topicName)
                .processVariableValueEquals(Constants.CORRELATION_ID, message.getCorrelationId())
                .singleResult();

        if (execution == null) {
            // запрашиваем процесс, который ожидает такое сообщения
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .messageEventSubscriptionName(topicName)
                    .singleResult();
            if (processDefinition != null && !isAlreadyRun(message)) {
                message.put(Constants.CORRELATION_ID, Optional.ofNullable(message.getCorrelationId()).orElseGet(() -> UUID.randomUUID().toString()));
                ProcessInstance processInstance =
                        businessKeyExist ? runtimeService.startProcessInstanceByMessage(topicName, message.getBusinessKey(), message) :
                                runtimeService.startProcessInstanceByMessage(topicName, message);
                log.debug("Started process by message:\n\tTopic: {}\n\tProcessInstanceId: {}\n\tBusinessKey: {}",
                        topicName, processInstance.getProcessInstanceId(), processInstance.getBusinessKey());
                log.debug("Variables: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message));
            } else {
                log.debug("Skip message. Process definition does not found or already running instance");
            }

        } else {
            // возобновляем процесс, которой висит в runtime и ожидает такое сообщение
            final MessageCorrelationBuilder messageCorrelation = runtimeService.createMessageCorrelation(topicName);

            if (businessKeyExist) {
                messageCorrelation.processInstanceBusinessKey(message.getBusinessKey());
            }

            if (message.getEventCorrelationId() != null) {
                messageCorrelation.localVariableEquals(Constants.EVENT_CORRELATION_ID, message.getEventCorrelationId());
            }

            final ProcessInstance processInstance = messageCorrelation
                    .processInstanceVariableEquals(Constants.CORRELATION_ID, message.getCorrelationId())
                    .setVariablesLocal(message)
                    .correlateWithResult()
                    .getProcessInstance();

            log.debug("Resuming process by message:\n\tTopic: {}\n\tProcessInstanceId: {}\n\tBusinessKey: {}",
                    topicName, processInstance != null ? processInstance.getProcessInstanceId() : null,
                    processInstance != null ? processInstance.getBusinessKey() : null);
            log.debug("Variables: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message));
        }
    }

    private boolean isAlreadyRun(MessageMap message) {
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        if (message.getBusinessKey() != null) {
            processInstanceQuery = processInstanceQuery.processInstanceBusinessKey(message.getBusinessKey());
        }

        List<ProcessInstance> processInstances = processInstanceQuery
                .variableValueEquals(Constants.CORRELATION_ID, message.getCorrelationId())
                .active()
                .list();

        if (log.isDebugEnabled()) {
            log.debug("Found process instances: {}", processInstances);
        }

        return processInstances.size() > 0;
    }
}
