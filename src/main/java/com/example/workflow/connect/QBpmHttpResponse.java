package com.example.workflow.connect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.camunda.connect.httpclient.HttpRequest;
import org.camunda.connect.httpclient.impl.HttpResponseImpl;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

public class QBpmHttpResponse extends HttpResponseImpl {
    private final ObjectMapper objectMapper;

    public QBpmHttpResponse(CloseableHttpResponse httpResponse, ObjectMapper objectMapper) {
        super(httpResponse);
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Override
    protected void collectResponseParameters(Map<String, Object> responseParameters) {
        super.collectResponseParameters(responseParameters);
        final Map<String, String> headers = (Map<String, String>) responseParameters.get(PARAM_NAME_RESPONSE_HEADERS);
        if (!CollectionUtils.isEmpty(headers)) {
            final String contentType = headers.get(HttpRequest.HEADER_CONTENT_TYPE);
            final String response = (String) responseParameters.get(PARAM_NAME_RESPONSE);
            if (MediaType.APPLICATION_JSON_VALUE.equals(contentType) && !StringUtils.isEmpty(response)) {
                responseParameters.put(PARAM_NAME_RESPONSE, objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
                }));
            }
        }

    }
}
