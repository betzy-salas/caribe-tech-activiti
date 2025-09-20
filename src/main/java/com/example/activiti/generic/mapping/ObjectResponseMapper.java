package com.example.activiti.generic.mapping;

import com.example.activiti.generic.context.ExecutionContextAdapter;

import java.util.Map;

public class ObjectResponseMapper implements ResponseMapper {
    private final String variableName;

    public ObjectResponseMapper(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public void map(Object responseBody, Map<String, Object> output, ExecutionContextAdapter contextAdapter) {
        output.put(variableName, responseBody);
    }
}


