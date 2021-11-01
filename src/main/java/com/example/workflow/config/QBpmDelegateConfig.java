package com.example.workflow.config;

import com.example.workflow.delegate.MessageDelegate;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QBpmDelegateConfig {

    @Bean
    public MessageDelegate messageDelegate(RuntimeService runtimeService, QBpmProperties qBpmProperties/*, MdpAuditLib mdpAuditLib*/) {
        return new MessageDelegate(runtimeService, qBpmProperties/*, mdpAuditLib*/);
    }
}
