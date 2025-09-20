package com.example.activiti.generic.model;

import java.util.Map;

public class ExternalServicesResponse<T> {
    private final int statusCode;
    private final Map<String, String> headers;
    private final T body;
    private final String errorBody;
    private final Throwable error;

    private ExternalServicesResponse(int statusCode, Map<String, String> headers, T body, String errorBody, Throwable error) {
        this.statusCode = statusCode;
        this.headers = headers != null ? headers : Map.of();
        this.body = body;
        this.errorBody = errorBody;
        this.error = error;
    }

    public static <T> ExternalServicesResponse<T> success(int statusCode, Map<String,String> headers, T body) {
        return new ExternalServicesResponse<>(statusCode, headers, body, null, null);
    }

    public static <T> ExternalServicesResponse<T> error(int statusCode, Map<String,String> headers, String errorBody, Throwable error) {
        return new ExternalServicesResponse<>(statusCode, headers, null, errorBody, error);
    }

    public int getStatusCode() { return statusCode; }
    public Map<String, String> getHeaders() { return headers; }
    public T getBody() { return body; }
    public String getErrorBody() { return errorBody; }
    public Throwable getError() { return error; }
    public boolean is2xx() { return statusCode >= 200 && statusCode < 300; }
}



