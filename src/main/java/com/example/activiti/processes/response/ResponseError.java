package com.example.activiti.processes.response;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;


public class ResponseError implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("Response Error");
    }
}