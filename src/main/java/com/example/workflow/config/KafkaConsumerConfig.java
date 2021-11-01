package com.example.workflow.config;

import com.example.workflow.kafka.stream.KafkaMessageListener;
import com.example.workflow.kafka.stream.KafkaTaskActionListener;
import com.example.workflow.kafka.stream.ProcessDeployListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Kornilov Oleg
 */
@RequiredArgsConstructor
@Slf4j
@Configuration
public class KafkaConsumerConfig {

    private ConcurrentMessageListenerContainer<String, String> addListener(
            String topic, String groupId, MessageListener<?, ?> listener,
            ConsumerFactory<String, String> consumerFactory) {

        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setGroupId(groupId);
        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);

        container.setupMessageListener(listener);
        container.start();
        log.info("Created and started kafka consumer for topic {}", topic);
        return container;
    }

    @Bean
    ConcurrentMessageListenerContainer<String, String> messageListenerContainer(
            QBpmProperties qBpmProperties, KafkaMessageListener kafkaMessageListener,
            KafkaTaskActionListener taskActionListener, ProcessDeployListener processDeployListener,
            ConsumerFactory<String, String> consumerFactory, ApplicationContext applicationContext) {

        final String serviceName = applicationContext.getEnvironment().getProperty("spring.application.name");
        final String inputTopics = qBpmProperties.getTopics().getProcessInputTopics();
        Stream.of(Optional.ofNullable(inputTopics).orElse("").split(";"))
                .filter(s -> !StringUtils.isEmpty(s))
                .forEach(topicName -> addListener(topicName, serviceName, kafkaMessageListener, consumerFactory));
        // подписка на обновление процесса от админки
        addListener(qBpmProperties.getTopics().getProcessDeployEvent(), serviceName, processDeployListener, consumerFactory);
        // подписка на изменение пользовательской задачи в камунде
        return addListener(qBpmProperties.getTopics().getTaskActionEvent(), serviceName, taskActionListener,
                consumerFactory);
    }

    @Bean
    public KafkaListenerContainerFactory<? extends AbstractMessageListenerContainer<String, String>>
    kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties/*, @Autowired(required = false) DsTraceKafkaBean dsTraceKafkaBean*/) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(kafkaProperties/*, dsTraceKafkaBean*/));
    }

    private Map<String, Object> consumerConfigs(KafkaProperties kafkaProperties/*, DsTraceKafkaBean dsTraceKafkaBean*/) {
        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
        // указываем вручную, так как spring cloud stream kafka переопределяет эти свойства
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

//        if (dsTraceKafkaBean != null) {
//            // значит включена трасировка (dstrace.enabled: true)
//            /* DsTrace start */
//            consumerProperties.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, DsTraceKafkaConsumerInterceptor.class.getName());
//            consumerProperties.put(DsTraceKafkaConfiguration.TRACE_BEAN, dsTraceKafkaBean);
//            /* DsTrace end */
//        }

        return consumerProperties;
    }
}
