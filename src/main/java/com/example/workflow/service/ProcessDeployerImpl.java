package com.example.workflow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;

import java.nio.charset.Charset;

@Slf4j
@RequiredArgsConstructor
public class ProcessDeployerImpl implements ProcessDeployer {
    private final RepositoryService repositoryService;

    //    @Auditable
    @Override
    public Deployment deploy(String resourceName, String deploymentName, String diagram) {
        if (log.isDebugEnabled()) {
            log.debug("Start deploying camunda process");
        }
        final Deployment deployment = repositoryService.createDeployment()
                .addInputStream(resourceName, IOUtils.toInputStream(diagram, Charset.defaultCharset()))
                .name(deploymentName)
                .deploy();
        if (log.isDebugEnabled()) {
            log.debug("Finished deploying camunda process. DeploymentId: {}", deployment.getId());
        }
        return deployment;
    }
}
