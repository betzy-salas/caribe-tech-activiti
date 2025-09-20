package com.example.activiti.processes.getdata;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;

public class GetDataFromExternalService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("Consulting external service...");

        Map<String, Object> ingresos = new HashMap<>();
        ingresos.put("salary", 1500000);
        ingresos.put("other", 300000);
        execution.setVariable("income", ingresos);
    }
}
