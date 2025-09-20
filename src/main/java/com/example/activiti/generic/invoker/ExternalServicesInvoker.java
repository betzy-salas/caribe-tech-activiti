package com.example.activiti.generic.invoker;

import com.example.activiti.generic.model.ExternalServicesResponse;
import com.example.activiti.generic.util.TemplatesResolver;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ExternalServicesInvoker {

    private final RestTemplate restTemplate = new RestTemplate();

    public ExternalServicesResponse<String> invoke(String url,
                                                   String method,
                                                   Map<String, String> headersMap,
                                                   String bodyTemplate,
                                                   Map<String, Object> variables) {

        String resolvedUrl = TemplatesResolver.resolve(url, variables);
        String resolvedBody = TemplatesResolver.resolve(bodyTemplate, variables);

        HttpHeaders headers = new HttpHeaders();
        if (headersMap != null) {
            headersMap.forEach((k, v) -> headers.set(k, TemplatesResolver.resolve(v, variables)));
        }

        if (!headers.containsKey(HttpHeaders.ACCEPT)) {
            headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.ALL));
        }

        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase(Locale.ROOT));
        HttpEntity<String> entity = (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE)
                ? new HttpEntity<>(headers)
                : new HttpEntity<>(resolvedBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(resolvedUrl, httpMethod, entity, String.class);
            String body = response.getStatusCode() == HttpStatus.NO_CONTENT ? null : response.getBody();
            return ExternalServicesResponse.success(
                    response.getStatusCodeValue(),
                    toSimpleHeaders(response.getHeaders()),
                    body
            );

        } catch (HttpStatusCodeException e) {
            return ExternalServicesResponse.error(
                    e.getRawStatusCode(),
                    toSimpleHeaders(e.getResponseHeaders()),
                    e.getMessage(),
                    e
            );

        } catch (RestClientException e) {
            return ExternalServicesResponse.error(
                    -1,
                    Map.of(),
                    null,
                    e
            );
        }
    }

    private static Map<String, String> toSimpleHeaders(HttpHeaders httpHeaders) {
        if (httpHeaders == null || httpHeaders.isEmpty()) return Map.of();
        Map<String, String> flat = new LinkedHashMap<>();
        httpHeaders.forEach((k, v) -> flat.put(k, String.join(",", v)));
        return flat;
    }

    private static String safeBody(HttpStatusCodeException httpStatusCodeException) {
        try {
            return httpStatusCodeException.getResponseBodyAsString();
        } catch (Exception ignore) {
            return null;
        }
    }
}

