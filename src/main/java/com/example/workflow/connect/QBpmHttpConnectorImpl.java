package com.example.workflow.connect;

import com.example.workflow.config.QBpmProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.camunda.connect.httpclient.HttpRequest;
import org.camunda.connect.httpclient.HttpResponse;
import org.camunda.connect.httpclient.impl.AbstractHttpConnector;

public class QBpmHttpConnectorImpl extends AbstractHttpConnector<HttpRequest, HttpResponse> implements QBpmHttpConnector {

    //    private final ActualToken actualToken;
    private final ObjectMapper objectMapper;
    //    private final LoadBalancerClient loadBalancer;
    private final QBpmProperties qBpmProperties;
    private final boolean securityEnabled;

    public QBpmHttpConnectorImpl(/*LoadBalancerClient loadBalancer,*/ ObjectMapper objectMapper/*, ActualToken actualToken*/, QBpmProperties qBpmProperties, boolean securityEnabled) {
        super(QBpmHttpConnector.ID);
//        this.loadBalancer = loadBalancer;
        this.objectMapper = objectMapper;
//        this.actualToken = actualToken;
        this.qBpmProperties = qBpmProperties;
        this.securityEnabled = securityEnabled;
    }

    @Override
    protected HttpResponse createResponse(CloseableHttpResponse response) {
        return new QBpmHttpResponse(response, objectMapper);
    }

    @Override
    public HttpRequest createRequest() {
        return new QBpmHttpRequest(this, objectMapper/*, loadBalancer, actualToken*/, qBpmProperties, securityEnabled);
    }

}
