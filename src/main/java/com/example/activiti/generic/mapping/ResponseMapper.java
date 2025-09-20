package com.example.activiti.generic.mapping;

import com.example.activiti.generic.context.ExecutionContextAdapter;
import org.activiti.engine.delegate.DelegateExecution;

import java.util.Map;

public interface ResponseMapper {
    void map(Object responseBody, Map<String, Object> output, ExecutionContextAdapter context);
}

