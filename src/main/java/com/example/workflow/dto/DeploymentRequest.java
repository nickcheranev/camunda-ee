package com.example.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeploymentRequest {
    String id;
    Integer version;
    String fileName;
    String deploymentName;
    String diagram;
    String correlationId;
    String eventCorrelationId;
}