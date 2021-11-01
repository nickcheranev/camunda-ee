package com.example.workflow.delegate;

import com.example.workflow.Constants;
import com.example.workflow.config.QBpmProperties;
import com.example.workflow.dto.KafkaOutputMessage;
import com.example.workflow.kafka.KafkaMessageEventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.context.Context;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class MessageDelegate extends AbstractJavaDelegate {

    private final RuntimeService runtimeService;
    private final QBpmProperties qBpmProperties;
    private final Set<String> topicSet = new HashSet<>();

    @PostConstruct
    public void init() {
        final String outputTopics = qBpmProperties.getTopics().getProcessOutputTopics();
        Optional.ofNullable(outputTopics).ifPresent(s -> Arrays.stream(s.split(";"))
                .filter(s1 -> !StringUtils.isEmpty(s))
                .forEach(topicSet::add));
    }

    @Override
    protected void executeDelegate(DelegateExecution execution) {
//        try {
//            mdpAuditLib.addInfoEvent("APPLICATION"
//                    , "MessageDelegate.executeDelegate()"
//                    , execution.getVariablesLocal()
//                    , Collections.emptyMap()
//                    , "Отправка сообщения в Kafka через MessageDelegate");
//        } catch (Exception e) {
//            log.warn("Ошибка аудита", e);
//        }

        final String topic = getVariable(execution, Constants.TOPIC, String.class);

        if (!topicSet.contains(topic)) {
            throw new ProcessEngineException(String.format("Output topic «%s» not registered", topic));
        }

        final KafkaMessageEventManager kafkaMessageEventManager = Context.getCommandContext().getSession(KafkaMessageEventManager.class);
        final String businessKey = execution.getProcessBusinessKey();
        final String eventCorrelationId = UUID.randomUUID().toString();
        final String sender = execution.getProcessEngine().getRepositoryService().getProcessDefinition(execution.getProcessDefinitionId()).getKey();

        final String correlationId = Optional.ofNullable(getProcessVariable(execution, Constants.CORRELATION_ID, String.class)).orElseGet(() -> {
            String uuid = UUID.randomUUID().toString();
            execution.setVariable(Constants.CORRELATION_ID, uuid);
            return uuid;
        });

        final Map<String, Object> variablesLocal = execution.getVariablesLocal();
        variablesLocal.put(Constants.CORRELATION_ID, correlationId);
        variablesLocal.put(Constants.BUSINESS_KEY, businessKey);
        variablesLocal.put(Constants.SENDER, sender);
        variablesLocal.remove(Constants.TOPIC);

        // добавляем сообщение в очередь
        kafkaMessageEventManager.addMessage(KafkaOutputMessage.builder()
                .topicName(topic)
                .payload(variablesLocal)
                .build());

        // устанавливаем переменную в локальный скоп процесса (требутся для вложенных процессов)
        runtimeService.setVariableLocal(execution.getParentId(), Constants.EVENT_CORRELATION_ID, eventCorrelationId);

        if (log.isDebugEnabled()) {
            log.debug("Add message to kafka message event manager. Sender: {}, Topic: {}, BusinessKey: {}, CorrelationId: {}",
                    sender, topic, businessKey, correlationId);
        }

    }

}
