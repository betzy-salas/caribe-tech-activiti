package com.example.activiti.processes.getdata;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GetDataFromMareigua implements JavaDelegate {

    private final Random random = new Random();

    @Override
    public void execute(DelegateExecution execution) {
        String documentType = (String) execution.getVariable("DOCUMENT_TYPE");
        String documentNumber = (String) execution.getVariable("DOCUMENT_NUMBER");

        System.out.println("SERVICE TASK GetDataFromMareigua - Querying MAREIGUA with type: " + documentType + ", document: "
                + documentNumber);

        boolean hasIncome = random.nextBoolean();

        if (hasIncome) {
            Map<String, Object> income = new HashMap<>();
            income.put("salary", 3500000);
            income.put("other", 0);
            income.put("dateValidity","2025-05-30");
            execution.setVariable("income", income);
            System.out.println("✅ Income found IN MAREIGUA: " + income);
        } else {
            execution.setVariable("income", null);
            System.out.println("❌ No Income found IN MAREIGUA");
        }
    }
}

