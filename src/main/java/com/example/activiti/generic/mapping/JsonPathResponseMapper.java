package com.example.activiti.generic.mapping;

import com.example.activiti.generic.context.ExecutionContextAdapter;
import com.jayway.jsonpath.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonPathResponseMapper implements ResponseMapper {

    private final Map<String, String> responseMapping;
    private final Map<String, JsonPath> compiled;

    private static final Configuration JSONPATH_CONF = Configuration.builder()
            .options(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS)
            .build();

    public JsonPathResponseMapper(Map<String, String> responseMapping) {
        this.responseMapping = responseMapping != null ? responseMapping : Collections.emptyMap();

        this.compiled = this.responseMapping.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> JsonPath.compile(e.getValue()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    @Override
    public void map(Object responseBody, Map<String, Object> output, ExecutionContextAdapter context) {
        String activityId = context.getActivityId();

        if (responseBody == null) {
            System.out.printf("[%s] Response is null. Setting nulls.%n", activityId);
            responseMapping.keySet().forEach(k -> output.put(k, null));
            return;
        }

        DocumentContext documentContext;
        try {
            if (responseBody instanceof String s) {
                if (s.isBlank()) {
                    System.out.printf("[%s] Response body is empty. Setting nulls.%n", activityId);
                    responseMapping.keySet().forEach(k -> output.put(k, null));
                    return;
                }
                documentContext = JsonPath.using(JSONPATH_CONF).parse(s);
            } else {
                documentContext = JsonPath.using(JSONPATH_CONF).parse(responseBody);
            }
        } catch (Exception exception) {
            System.out.printf("[%s] Could not parse response. Setting nulls. Reason: %s%n", activityId, exception.getMessage());
            responseMapping.keySet().forEach(k -> output.put(k, null));
            return;
        }

        compiled.forEach((key, path) -> {
            try {
                Object value = documentContext.read(path);   // con la conf, si falta algo → null
                output.put(key, value);
                System.out.printf("[%s] %s = %s%n", activityId, key, value);
            } catch (Exception e) {
                System.out.printf("[%s] Could not extract '%s'. Setting null. Reason: %s%n",
                        activityId, key, e.getMessage());
                output.put(key, null);
            }
        });
    }
}