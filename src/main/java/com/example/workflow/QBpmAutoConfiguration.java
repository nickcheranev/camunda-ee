package com.example.workflow;

import com.example.workflow.config.*;
import com.example.workflow.connect.QBpmConnectors;
import com.example.workflow.connect.QBpmHttpConnector;
import com.example.workflow.connect.QBpmHttpConnectorImpl;
import com.example.workflow.kafka.KafkaMessageSender;
import com.example.workflow.kafka.KafkaMessageSessionFactory;
import com.example.workflow.kafka.listener.TaskCancelListener;
import com.example.workflow.kafka.listener.TaskCreateListener;
import com.example.workflow.kafka.stream.*;
import com.example.workflow.service.KafkaMessageService;
import com.example.workflow.service.KafkaMessageServiceImpl;
import com.example.workflow.service.ProcessDeployer;
import com.example.workflow.service.ProcessDeployerImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.cfg.CompositeProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.extension.reactor.bus.CamundaEventBus;
import org.camunda.bpm.spring.boot.starter.util.CamundaSpringBootUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@Configuration
@Import({QBpmDelegateConfig.class, KafkaConsumerConfig.class, KafkaProducerConfig.class})
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertyLoaderFactory.class)
//@EnableFeignClients({"ru.diasoft.micro.mdp.feign"})
//@ComponentScan(value = {"ru.diasoft.micro.mdp.feign", "ru.diasoft.micro.mdpauditlib"}, basePackageClasses = DelegateController.class)
//@AutoConfigureAfter(DsTraceKafkaAutoConfiguration.class)
@EnableConfigurationProperties
public class QBpmAutoConfiguration {

    @Bean
    public SpringProcessEngineConfiguration processEngineConfigurationImpl(List<ProcessEnginePlugin> processEnginePlugins,
//                                                                         BusinessSensorSessionFactory businessSensorSessionFactory,
//                                                                         ProcessActivitySessionFactory processActivitySessionFactory,
//                                                                         ProcessDefinitionSessionFactory processDefinitionSessionFactory,
//                                                                         ProcessInstanceSessionFactory processInstanceSessionFactory,
                                                                           KafkaMessageSessionFactory kafkaMessageSessionFactory) {
        SpringProcessEngineConfiguration configuration = CamundaSpringBootUtil.springProcessEngineConfiguration();
        configuration.getProcessEnginePlugins().add(new CompositeProcessEnginePlugin(processEnginePlugins));

        final List<HistoryEventHandler> customHistoryEventHandlers = configuration.getCustomHistoryEventHandlers();
//        customHistoryEventHandlers.add(new BusinessSensorHistoryEventHandler());
//        customHistoryEventHandlers.add(new ProcessActivityEventHandler());
//        customHistoryEventHandlers.add(new ProcessInstanceEventHandler());

        final List<SessionFactory> sessionFactories = configuration.getCustomSessionFactories();
//        sessionFactories.add(businessSensorSessionFactory);
//        sessionFactories.add(processActivitySessionFactory);
//        sessionFactories.add(processDefinitionSessionFactory);
//        sessionFactories.add(processInstanceSessionFactory);
        sessionFactories.add(kafkaMessageSessionFactory);

//        configuration.getCustomPostBPMNParseListeners().add(new ProcessDefinitionListener());

        return configuration;
    }

    @Bean
    @ConfigurationProperties(prefix = "q-bpm")
    public QBpmProperties bpmPlayerKafkaProperties() {
        return new QBpmProperties();
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskCancelListener taskCancelListener(KafkaTemplate<String, String> kafkaTemplate, QBpmProperties qBpmProperties, CamundaEventBus eventBus, ObjectMapper objectMapper) {
        return new TaskCancelListener(kafkaTemplate, qBpmProperties, eventBus, objectMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskCreateListener taskCreateListener(KafkaTemplate<String, String> kafkaTemplate, QBpmProperties qBpmProperties, CamundaEventBus eventBus, ObjectMapper objectMapper) {
        return new TaskCreateListener(kafkaTemplate, qBpmProperties, eventBus, objectMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaMessageSender messageSender(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
                                            @Value("${spring.application.name:none}") String applicationName) {
        return new KafkaMessageSender(kafkaTemplate, objectMapper, applicationName);
    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaMessageService kafkaMessageService() {
        return new KafkaMessageServiceImpl();
    }

    @ConditionalOnMissingBean
    @Bean
    public MessageProcessor messageProcessor(RuntimeService runtimeService, RepositoryService repositoryService, ObjectMapper objectMapper) {
        return new MessageProcessorImpl(runtimeService, repositoryService, objectMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaMessageListener kafkaMessageListener(MessageProcessor messageProcessor) {
        return new KafkaMessageListener(messageProcessor);
    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaTaskActionListener kafkaTaskActionListener(ProcessEngine camunda, ObjectMapper objectMapper) {
        return new KafkaTaskActionListener(camunda, objectMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public ProcessDeployListener processDeployListener(KafkaTemplate<String, String> kt, QBpmProperties p, ObjectMapper om, ProcessDeployer pd) {
        return new ProcessDeployListener(kt, p, om, pd);
    }

//    @ConditionalOnMissingBean
//    @Bean
//    public BusinessSensorSessionFactory businessSensorSessionFactory(KafkaMessageSender businessSensorEventKafkaMessageSender, QBpmProperties kafkaProperties) {
//        return new BusinessSensorSessionFactory(businessSensorEventKafkaMessageSender, kafkaProperties);
//    }
//
//    @ConditionalOnMissingBean
//    @Bean
//    public ProcessDefinitionSessionFactory processDeploySessionFactory(KafkaMessageSender kafkaMessageSender, QBpmProperties qBpmProperties, Environment environment) {
//        return new ProcessDefinitionSessionFactory(kafkaMessageSender, environment, qBpmProperties);
//    }
//
//    @ConditionalOnMissingBean
//    @Bean
//    public ProcessActivitySessionFactory processActivitySessionFactory (KafkaMessageSender kafkaMessageSender, QBpmProperties qBpmProperties) {
//        return new ProcessActivitySessionFactory(kafkaMessageSender, qBpmProperties);
//    }
//
//    @ConditionalOnMissingBean
//    @Bean
//    public ProcessInstanceSessionFactory processInstanceSessionFactory (KafkaMessageSender kafkaMessageSender, QBpmProperties qBpmProperties) {
//        return new ProcessInstanceSessionFactory(kafkaMessageSender, qBpmProperties);
//    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaMessageSessionFactory kafkaMessageSessionFactory(KafkaMessageSender kafkaMessageSender) {
        return new KafkaMessageSessionFactory(kafkaMessageSender);
    }

    @ConditionalOnMissingBean
    @Bean
    public ProcessDeployer processDeployer(RepositoryService repositoryService) {
        return new ProcessDeployerImpl(repositoryService);
    }

    @ConditionalOnMissingBean
    @Bean
    public QBpmHttpConnector httpConnector(/*LoadBalancerClient loadBalancerClient,*/ ObjectMapper objectMapper
            /*ActualToken actualToken*/, @Value("${security.enabled:true}") boolean securityEnabled,
                                                                                      QBpmProperties qBpmProperties) {
        QBpmHttpConnectorImpl httpConnector = new QBpmHttpConnectorImpl(/*loadBalancerClient,*/ objectMapper/*, actualToken*/, qBpmProperties, securityEnabled);
        QBpmConnectors.registerConnector(httpConnector);
        return httpConnector;
    }

//    @ConditionalOnMissingBean
//    @Bean
//    public QBpmSortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver() {
//        return new QBpmSortHandlerMethodArgumentResolver();
//    }
//
//    @ConditionalOnMissingBean
//    @Bean
//    public QBpmPageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver(QBpmSortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver) {
//        return new QBpmPageableHandlerMethodArgumentResolver(sortHandlerMethodArgumentResolver);
//    }
//
//    @ConditionalOnMissingBean
//    @Bean
//    public QBpmQuerydslPredicateBuilder predicateBuilder(@Qualifier("mvcConversionService") ObjectFactory<ConversionService> conversionService,
//                                                         QuerydslBindingsFactory querydslBindingsFactory) {
//        return new QBpmQuerydslPredicateBuilder(conversionService, querydslBindingsFactory);
//    }
}
