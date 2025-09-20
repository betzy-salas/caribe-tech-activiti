package com.example.activiti.processes.getdata;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GetDataFromCache implements JavaDelegate {

    private final Random random = new Random();

    @Override
    public void execute(DelegateExecution execution) {
        String documentType = (String) execution.getVariable("DOCUMENT_TYPE");
        String documentNumber = (String) execution.getVariable("DOCUMENT_NUMBER");

        System.out.println("-------------EXECUTION---------------" + execution.getProcessInstanceId());
        System.out.println("SERVICE TASK GetDataFromCache - Querying CACHE with type: " + documentType + ", document: "
                + documentNumber);

        boolean hasIncome = random.nextBoolean();

        if (hasIncome) {
            Map<String, Object> income = new HashMap<>();
            income.put("salary", 12000000);
            income.put("other", 900000);
            income.put("dateValidity","2025-05-30");
            execution.setVariable("income", income);
            System.out.println("✅ Income found IN CACHE: " + income);
        } else {
            execution.setVariable("income", null);
            System.out.println("❌ No income found IN CACHE");
        }
    }
}

