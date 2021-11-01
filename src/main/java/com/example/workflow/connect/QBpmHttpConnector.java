package com.example.workflow.connect;

import org.camunda.connect.httpclient.HttpRequest;
import org.camunda.connect.spi.Connector;

public interface QBpmHttpConnector extends Connector<HttpRequest> {
    String ID = "rest-connector";
}
