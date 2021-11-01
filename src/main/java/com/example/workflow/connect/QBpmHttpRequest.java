package com.example.workflow.connect;

import com.example.workflow.config.QBpmProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.camunda.connect.httpclient.HttpBaseRequest;
import org.camunda.connect.httpclient.HttpRequest;
import org.camunda.connect.httpclient.HttpResponse;
import org.camunda.connect.httpclient.impl.AbstractHttpRequest;
import org.camunda.connect.httpclient.impl.RequestConfigOption;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QBpmHttpRequest extends AbstractHttpRequest<HttpRequest, HttpResponse> implements HttpRequest {
    private final static String SERVICE = "service";
    private final static String RESOURCE = "resource";
    private final static String AUTHORIZATION_TYPE = "Bearer";
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String SLASH = "/";

    private final ObjectMapper objectMapper;
    //    private final LoadBalancerClient loadBalancer;
//    private final ActualToken actualToken;
    private final QBpmProperties qBpmProperties;
    private final boolean securityEnabled;

    public QBpmHttpRequest(QBpmHttpConnector connector, ObjectMapper objectMapper/*, LoadBalancerClient loadBalancer, ActualToken actualToken*/, QBpmProperties qBpmProperties, boolean securityEnabled) {
        super(connector);
        this.objectMapper = objectMapper;
//        this.loadBalancer = loadBalancer;
//        this.actualToken = actualToken;
        this.qBpmProperties = qBpmProperties;
        this.securityEnabled = securityEnabled;
    }

    HttpRequest service(String service) {
        setRequestParameter(SERVICE, service);
        return this;
    }

    String getService() {
        return getRequestParameter(SERVICE);
    }

    HttpRequest resource(String resource) {
        setRequestParameter(RESOURCE, resource);
        return this;
    }

    String getResource() {
        return getRequestParameter(RESOURCE);
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }

    @Override
    public Map<String, String> getHeaders() {
//        if (securityEnabled) {
//            header(AUTHORIZATION_HEADER, String.format("%s %s", AUTHORIZATION_TYPE, actualToken.getActualTechToken()));
//        }
        header(HttpRequest.HEADER_CONTENT_TYPE, getContentType());
        return super.getHeaders();
    }

    @Override
    public Map<String, Object> getConfigOptions() {
        Map<String, Object> configOptions = Optional.ofNullable(super.getConfigOptions()).orElseGet(HashMap::new);
        QBpmProperties.RestConnector restConnector = qBpmProperties.getRestConnector();
        if (restConnector != null) {
            configOptions.put(RequestConfigOption.CONNECTION_TIMEOUT.getName(), restConnector.getConnectionTimeout());
            configOptions.put(RequestConfigOption.CONNECTION_REQUEST_TIMEOUT.getName(), restConnector.getConnectionRequestTimeout());
            configOptions.put(RequestConfigOption.SOCKET_TIMEOUT.getName(), restConnector.getSocketTimeout());
        }
        return configOptions;
    }

    @Override
    public String getUrl() {
//        final ServiceInstance serviceInstance = loadBalancer.choose(getService());
//
//        if (serviceInstance == null) {
//            throw new ConnectorRequestException(String.format("Service name «%s» is not registered in discovery", getService()));
//        }

        final String resource = getResource();
        final StringBuilder sb = new StringBuilder();

        if (!resource.startsWith(SLASH)) {
            sb.append(SLASH);
        }
        sb.append(resource);

        return "http://localhost:8080/" + // String.format("%s://%s:%s%s", Optional.ofNullable(serviceInstance.getScheme()).orElse("http"),
                // serviceInstance.getHost(), serviceInstance.getPort(),
                sb.toString();
    }

    @SneakyThrows
    @Override
    public String getPayload() {
        Object requestParameter = getRequestParameter(HttpBaseRequest.PARAM_NAME_REQUEST_PAYLOAD);
        return objectMapper.writeValueAsString(requestParameter);
    }
}
