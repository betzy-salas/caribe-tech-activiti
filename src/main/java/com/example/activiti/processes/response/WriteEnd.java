package com.example.activiti.processes.response;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;


public class WriteEnd implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("End. I executed my tasks after received message from external service");
    }
}