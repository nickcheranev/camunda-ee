package com.example.workflow.kafka.stream;

import com.example.workflow.config.QBpmProperties;
import com.example.workflow.dto.DeploymentRequest;
import com.example.workflow.dto.DeploymentResponse;
import com.example.workflow.service.ProcessDeployer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.camunda.bpm.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.example.workflow.Constants.CONSUMER;
import static com.example.workflow.Constants.PRODUCER;

@Slf4j
@RequiredArgsConstructor
public class ProcessDeployListener implements MessageListener<String, String> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final QBpmProperties qBpmProperties;
    private final ObjectMapper objectMapper;
    private final ProcessDeployer processDeployer;

    @Value("${spring.application.name}")
    private String applicationName;

    @SneakyThrows
    @Override
    public void onMessage(ConsumerRecord<String, String> record) {
        final String consumer = getHeaderValue(record, CONSUMER);
        final String producer = getHeaderValue(record, PRODUCER);

        if (applicationName.equals(consumer)) {
            DeploymentRequest deploymentRequest = objectMapper.readValue(record.value(), DeploymentRequest.class);

            DeploymentResponse response = new DeploymentResponse();
            response.setId(deploymentRequest.getId());
            response.setId(deploymentRequest.getId());
            response.setVersion(deploymentRequest.getVersion());
            response.setCorrelationId(deploymentRequest.getCorrelationId());
            response.setEventCorrelationId(deploymentRequest.getEventCorrelationId());

            try {
                Deployment deploy = processDeployer.deploy(deploymentRequest.getFileName(),
                        deploymentRequest.getDeploymentName(), deploymentRequest.getDiagram());
                response.setErrorCode(0);
                log.debug("Deploy success. Request: {}, DeploymentId: {}", deploymentRequest, deploy.getId());
            } catch (Exception exception) {
                response.setErrorCode(500);
                response.setErrorMessage(exception.getMessage());
                log.error("Deploy error. Request: {}. Error: {}", deploymentRequest, exception.getMessage());
            } finally {
                ProducerRecord<String, String> message = new ProducerRecord<>(qBpmProperties.getTopics().getProcessDeployPublishEvent(),
                        objectMapper.writeValueAsString(response));
                message.headers().add(PRODUCER, applicationName.getBytes());
                message.headers().add(CONSUMER, producer != null ? producer.getBytes() : null);
                kafkaTemplate.send(message);
                log.debug("Sent message: " + message);
            }
        }
    }

    private String getHeaderValue(ConsumerRecord<String, String> record, String headerName) {
        Optional<Header> header = Optional.ofNullable(record.headers().lastHeader(headerName));
        return header.map(value -> new String(value.value(), StandardCharsets.UTF_8).replaceAll("\"", "")).orElse(null);
    }

}
