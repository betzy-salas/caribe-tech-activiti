package com.example.activiti.processes.response;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;


public class WriteHello implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("Hello. I'm going to wait a message from external service");
    }
}