package com.example.workflow.service;

import org.camunda.bpm.engine.repository.Deployment;

public interface ProcessDeployer {
    Deployment deploy(String resourceName, String deploymentName, String diagram);
}
