package com.example.activiti.generic.processor;

import com.example.activiti.generic.config.ExternalServicesConfig;
import com.example.activiti.generic.invoker.ExternalServicesInvoker;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("genericServiceTask")
public class GenericServiceTask extends ServiceTaskProcessor {

    @Autowired
    private ExternalServicesConfig config;

    @Autowired
    private ExternalServicesInvoker invoker;

    private FixedValue serviceName;

    public void setServiceName(FixedValue serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    protected String resolveServiceName(DelegateExecution execution) {
        return (String) serviceName.getValue(execution);
    }

    @Override
    protected ExternalServicesConfig getConfig() {
        return config;
    }

    @Override
    protected ExternalServicesInvoker getInvoker() {
        return invoker;
    }
}
