package com.example.activiti.generic.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "external-services")
public class ExternalServicesConfig {

    private Map<String, ServicesDefinition> services = new LinkedHashMap<>();

    public Map<String, ServicesDefinition> getServices() { return services; }
    public void setServices(Map<String, ServicesDefinition> services) {
        this.services = (services != null ? services : new LinkedHashMap<>());
    }

    public ServicesDefinition getByServiceKey(String key) {
        return services.get(key);
    }

    public ServicesDefinition getOrThrow(String key) {
        ServicesDefinition servicesDefinition = services.get(key);
        if (servicesDefinition == null) throw new IllegalArgumentException("Services doesn't exist in config: " + key);
        return servicesDefinition;
    }

    public ServicesDefinition findByName(String name) {
        if (name == null) return null;
        return services.values().stream()
                .filter(sd -> name.equals(sd.getName()))
                .findFirst().orElse(null);
    }

    @PostConstruct
    public void logServices() {
        System.out.println("ExternalServicesConfig bean: " + System.identityHashCode(this));
        if (services.isEmpty()) {
            System.out.println("Services were not loaded from config");
        } else {
            System.out.println("Loaded services:");
            services.forEach((key, val) ->
                    System.out.printf(" - %s → %s (%s)%n", key, val.getUrl(), val.getMethod()));
        }
    }

    public static class ServicesDefinition {
        private String name;
        private String url;
        private String method;
        private Map<String, String> headers = new LinkedHashMap<>();
        private String bodyTemplate;
        private Map<String, String> responseMapping = new LinkedHashMap<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public Map<String, String> getHeaders() { return headers; }
        public void setHeaders(Map<String, String> headers) {
            this.headers = (headers != null ? headers : new LinkedHashMap<>());
        }
        public String getBodyTemplate() { return bodyTemplate; }
        public void setBodyTemplate(String bodyTemplate) { this.bodyTemplate = bodyTemplate; }
        public Map<String, String> getResponseMapping() { return responseMapping; }
        public void setResponseMapping(Map<String, String> responseMapping) {
            this.responseMapping = (responseMapping != null ? responseMapping : new LinkedHashMap<>());
        }
    }
}