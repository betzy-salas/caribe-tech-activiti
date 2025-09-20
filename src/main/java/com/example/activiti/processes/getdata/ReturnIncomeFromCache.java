package com.example.activiti.processes.getdata;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;

public class ReturnIncomeFromCache implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        Object incomeObj = execution.getVariable("income");

        if (incomeObj instanceof Map<?, ?> incomes) {
            System.out.println("📦 SERVICE TASK ReturnIncomeFromCache - Returning income from CACHE:");
            incomes.forEach((key, value) ->
                    System.out.println("🧾 " + key + ": " + value)
            );
        } else {
            System.out.println("⚠️ SERVICE TASK ReturnIncomeFromCache - There is no income to return from CACHE.");
        }
    }
}

