package com.example.activiti.generic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "process-input")
public class ProcessInputConfig {

    private Map<String, Map<String, String>> mappings = new LinkedHashMap<>();

    public Map<String, String> forProcess(String processKey) {
        return mappings.getOrDefault(processKey, Map.of());
    }
}

