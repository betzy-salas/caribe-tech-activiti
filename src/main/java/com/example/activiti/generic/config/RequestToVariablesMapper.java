package com.example.activiti.generic.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RequestToVariablesMapper {

    private final ProcessInputConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Configuration JP_CONF = Configuration.builder()
            .options(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS)
            .build();

    public RequestToVariablesMapper(ProcessInputConfig config) {
        this.config = config;
    }

    public Map<String,Object> map(String processKey, Object request) {
        Map<String,String> mapping = config.forProcess(processKey);
        String json = (request instanceof String s) ? s : toJson(request);

        if (mapping.isEmpty()) {
            return objectMapper.convertValue(request, new TypeReference<>() {});
        }

        DocumentContext documentContext = JsonPath.using(JP_CONF).parse(json);
        Map<String,Object> variables = new LinkedHashMap<>();
        mapping.forEach((variableName, jsonPath) ->
                variables.put(variableName, documentContext.read(jsonPath)));
        return variables;
    }

    private String toJson(Object o) {
        try { return objectMapper.writeValueAsString(o); }
        catch (Exception e) { throw new IllegalArgumentException("The request could not be serialized.", new Exception()); }
    }
}

