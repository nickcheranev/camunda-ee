package com.example.workflow.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

/**
 * @author Kornilov Oleg
 */
@Configuration
@RequiredArgsConstructor
@EnableKafka
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, String> producerFactory(KafkaProperties kafkaProperties/*, @Autowired(required = false) DsTraceKafkaBean dsTraceKafkaBean*/) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(kafkaProperties/*, dsTraceKafkaBean*/));
    }

    private Map<String, Object> producerConfigs(KafkaProperties kafkaProperties/*, DsTraceKafkaBean dsTraceKafkaBean*/) {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
        // указываем вручную, так как spring cloud stream kafka переопределяет эти свойства
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

//        if (dsTraceKafkaBean != null) {
//            // значит включена трасировка (dstrace.enabled: true)
//            /* DsTrace start */
//            producerProperties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, DsTraceKafkaProducerInterceptor.class.getName());
//            producerProperties.put(DsTraceKafkaConfiguration.TRACE_BEAN, dsTraceKafkaBean);
//            /* DsTrace end */
//        }

        return producerProperties;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
