package com.example.activiti.generic.context;

import org.activiti.engine.delegate.DelegateExecution;

import java.util.Map;

public class ExecutionContextAdapter {
    private final DelegateExecution execution;

    public ExecutionContextAdapter(DelegateExecution execution) {
        this.execution = execution;
    }

    public Map<String, Object> getVariables() {
        return execution.getVariables();
    }

    public void setVariable(String name, Object value) {
        execution.setVariable(name, value);
    }

    public String getActivityId() {
        return execution.getCurrentActivityId();
    }

    public String getProcessInstanceId() {
        return execution.getProcessInstanceId();
    }

    public String getActivityName() {return  execution.getCurrentFlowElement().getName();}
}

