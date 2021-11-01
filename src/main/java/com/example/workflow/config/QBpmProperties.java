package com.example.workflow.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QBpmProperties {

    private Topics topics;
    private RestConnector restConnector;

    @Getter
    @Setter
    public static class Topics {
        private String taskActionEvent;
        private String taskCreateEvent;
        private String taskCancelEvent;
        private String processDeployEvent;
        private String processDeployPublishEvent;
        private String processMonitorEvent;
        private String processDefinitionEvent;
        private String processActivityEvent;
        private String processInstanceEvent;

        private String processInputTopics;
        private String processOutputTopics;
    }

    @Getter
    @Setter
    public static class RestConnector {
        private Integer connectionTimeout;
        private Integer connectionRequestTimeout;
        private Integer socketTimeout;
    }
}