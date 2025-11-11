package com.example.activiti.generic.processor;

import com.example.activiti.generic.config.ExternalServicesConfig;
import com.example.activiti.generic.context.ExecutionContextAdapter;
import com.example.activiti.generic.exception.ExternalServicesException;
import com.example.activiti.generic.factory.MapperFactory;
import com.example.activiti.generic.invoker.ExternalServicesInvoker;
import com.example.activiti.generic.mapping.ResponseMapper;
import com.example.activiti.generic.model.ExternalServicesResponse;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;

public abstract class ServiceTaskProcessor implements JavaDelegate {

    protected abstract String resolveServiceName(DelegateExecution execution);
    protected abstract ExternalServicesConfig getConfig();
    protected abstract ExternalServicesInvoker getInvoker();

    @Override
    public void execute(DelegateExecution execution) {
        ExecutionContextAdapter context = new ExecutionContextAdapter(execution);
        String name = resolveServiceName(execution);
        ExternalServicesConfig.ServicesDefinition definition = getConfig().getByServiceKey(name);

        if (definition == null) {
            throw new ExternalServicesException("Missing config for service: " + name, null);
        }

        System.out.printf("Process ID '%s'%n", context.getProcessInstanceId());

        Map<String, Object> input = new HashMap<>(context.getVariables());
        ExternalServicesResponse<String> response = getInvoker().invoke(
                definition.getUrl(),
                definition.getMethod(),
                definition.getHeaders(),
                definition.getBodyTemplate(),
                input
        );

        int status = (response != null ? response.getStatusCode() : 0);
        boolean ok = (response != null && response.is2xx());
        String body = (response != null ? response.getBody() : null);
        String errorBody = (response != null ? response.getErrorBody() : null);

        context.setVariable("httpStatus", status);
        context.setVariable("httpOk", ok);
        context.setVariable("httpBody", body);
        context.setVariable("httpErrorBody", errorBody);

        if (ok) {
            handleResponseMapping(definition, response, context);
        } else {
            System.out.printf("Activity '%s' non-2xx (status=%d). Skipping mapping.%n",
                    context.getActivityId(), status);
        }
    }

    protected void handleResponseMapping(ExternalServicesConfig.ServicesDefinition definition,
                                         ExternalServicesResponse<String> response,
                                         ExecutionContextAdapter context) {

        String activityId = context.getActivityId();
        Map<String, Object> mapped = new HashMap<>();
        ResponseMapper mapper = MapperFactory.getMapper(definition);

        if (mapper != null) {
            try {
                if (response == null || response.getBody() == null || response.getBody().isBlank()) {
                    System.out.printf("Activity '%s' received an empty or null response. Mapping variables with null values.%n", activityId);
                    definition.getResponseMapping().keySet().forEach(key -> mapped.put(key, null));
                } else {
                    mapper.map(response.getBody(), mapped, context);
                }
                mapped.forEach(context::setVariable);
                //System.out.printf("Activity '%s' mapped and set variables: %s%n", activityId, mapped);
            } catch (Exception e) {
                System.err.printf("Activity '%s' error during response mapping: %s%n", activityId, e.getMessage());
            }
        } else {
            context.setVariable("responseBody", response != null ? response.getBody() : null);
            System.out.printf("Activity '%s' has no mapper configured. Stored raw response as 'responseBody'.%n", activityId);
        }
    }
}