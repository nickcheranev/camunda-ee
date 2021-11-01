package com.example.workflow;

/**
 * @author Kornilov Oleg
 */
public final class Constants {

    public interface Messages {
        String CREATE_OBJECT = "CreateObject";
        String UPDATE_OBJECT = "UpdateObject";
    }


    public static final String CORRELATION_ID = "correlationId";
    public static final String EVENT_CORRELATION_ID = "eventCorrelationId";
    public static final String BUSINESS_KEY = "businessKey";
    public static final String SENDER = "sender";
    public static final String TOPIC = "topic";
    public final static String ID = "id";
    public final static String IS_VALID = "isValid";
    public final static String ATTRIBUTES = "attributes";
    public final static String SCHEMA_VERSION = "schemaVersion";
    public final static String USER_NAME = "userName";
    public final static String EXTERNAL_USER_ID = "externalUserId";
    public final static String SYS_NAME = "sysName";
    public final static String NUMBER = "number";
    public final static String DETAILS = "details";
    public final static String TEMPLATE = "template";
    public final static String USER_ACCOUNT_ID = "user_account_id";
    public final static String CREATED_BY = "createdBy";
    public final static String PRODUCER = "producer";
    public final static String CONSUMER = "consumer";

    // UpdateRequestID
    public final static String UPDATE_REQUEST_ID = "UpdateRequestID";
    public final static String UPDATE_REQUEST_MESSAGE = "UpdateRequestMessage";
}
