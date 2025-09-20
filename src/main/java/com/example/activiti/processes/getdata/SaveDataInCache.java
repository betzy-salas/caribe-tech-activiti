package com.example.activiti.processes.getdata;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;

public class SaveDataInCache implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String documentType = (String) execution.getVariable("DOCUMENT_TYPE");
        String documentNumber = (String) execution.getVariable("DOCUMENT_NUMBER");

        Object income = execution.getVariable("income");

        System.out.println("SERVICE TASK SaveDataInCache - Saving data in CACHE with type: "
                + documentType + ", document: "
                + documentNumber + ", income: "
                + income);
    }
}

