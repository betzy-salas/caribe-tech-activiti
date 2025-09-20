package com.example.activiti.processes.hello;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

//@Component
public class HelloTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String message = "¡Hola! Se ejecutó el proceso BPMN desde el controlador.";
        execution.setVariable("httpStatus", 200);
        execution.setVariable("success", "¡Hola! Se ejecutó el proceso BPMN desde el controlador.");
        System.out.println(message);
    }
}

