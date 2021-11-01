package com.example.workflow.connect;

import org.camunda.connect.Connectors;
import org.camunda.connect.spi.Connector;

public class QBpmConnectors extends Connectors {

    public static void registerConnector(Connector<?> connector) {
        Connectors.registerConnector(connector);
    }
}
