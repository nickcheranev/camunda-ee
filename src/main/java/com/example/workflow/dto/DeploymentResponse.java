package com.example.workflow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class DeploymentResponse {
    String id;
    Integer version;
    Integer errorCode;
    String errorMessage;
    String correlationId;
    String eventCorrelationId;
}