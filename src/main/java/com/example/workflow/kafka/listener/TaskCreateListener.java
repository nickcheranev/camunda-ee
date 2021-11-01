package com.example.workflow.kafka.listener;

import com.example.workflow.Constants;
import com.example.workflow.config.QBpmProperties;
import com.example.workflow.utils.ConvertUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.reactor.bus.CamundaEventBus;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Kornilov Oleg
 */
@Slf4j
@RequiredArgsConstructor
@CamundaSelector(type = "userTask", event = TaskListener.EVENTNAME_CREATE)
public class TaskCreateListener implements TaskListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final QBpmProperties qBpmProperties;
    private final CamundaEventBus eventBus;
    private final ObjectMapper objectMapper;

//    private DefaultTokenServices defaultTokenServices;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();

        // FIXME временный костыль для показа. Убрать!!!!
        Long userAccountId = Optional.ofNullable(ConvertUtils.convert(execution.getVariableLocal(Constants.CREATED_BY), Long.class))
                .orElse(getCurrentUserAccountId());

        TaskCreateEventDto userTask = TaskCreateEventDto.builder()
                .externalId(delegateTask.getId())
                .humanTaskTemplate(ConvertUtils.convert(execution.getVariableLocal(Constants.TEMPLATE), String.class))
                .processInstanceId(delegateTask.getProcessInstanceId())
                //.startDate(LocalDateTime.now())
                .objectId(ConvertUtils.convert(execution.getVariable(Constants.ID), String.class))
                .objectNumber(ConvertUtils.convert(execution.getVariableLocal(Constants.NUMBER), String.class))
                .correlationId(ConvertUtils.convert(execution.getVariable(Constants.CORRELATION_ID), String.class))
                .userAccountId(userAccountId)
                .details(ConvertUtils.convert(execution.getVariableLocal(Constants.DETAILS), String.class))
                .build();

        try {
            String jsonString = objectMapper.writeValueAsString(userTask);
            log.debug("TaskCreateEvent from activity: {} with params: {}", delegateTask.getName(), jsonString);
            kafkaTemplate.send(qBpmProperties.getTopics().getTaskCreateEvent(), jsonString);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Get current userAccountId
     *
     * @return
     */
    private Long getCurrentUserAccountId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
//            OAuth2AuthenticationDetails authenticationDetails = (OAuth2AuthenticationDetails) authentication.getDetails();
//            if (defaultTokenServices != null && authenticationDetails != null) {
//                OAuth2AccessToken oAuth2AccessToken = defaultTokenServices.readAccessToken(authenticationDetails.getTokenValue());
//                Map<String, Object> additionalInformation = oAuth2AccessToken.getAdditionalInformation();
//                return ConvertUtils.convert(additionalInformation.get(Constants.USER_ACCOUNT_ID), Long.class);
//            }
//        }
        return null;

    }

//    @Autowired(required = false)
//    public void setDefaultTokenServices(DefaultTokenServices defaultTokenServices) {
//        this.defaultTokenServices = defaultTokenServices;
//    }

    @Builder
    @AllArgsConstructor
    @Data
    private static class TaskCreateEventDto {
        private String humanTaskTemplate;
        private String objectId;
        private String objectNumber;
        private LocalDateTime startDate;
        private String externalId;
        private String processInstanceId;
        private String correlationId;
        private Long userAccountId;
        private String details;
    }

}
