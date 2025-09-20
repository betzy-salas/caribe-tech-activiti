package com.example.activiti.processes.response;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;


public class ReturnError implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        Object st = execution.getVariable("httpStatus");
        System.out.println("st=" + st + " (" + (st!=null ? st.getClass() : "null") + ")");
        System.out.println(st);
        int status = (st instanceof Number) ? ((Number) st).intValue() : 500;

        Object errorText = execution.getVariable("httpErrorBody");
        System.out.println(execution.getCurrentActivityId());
        Object p = execution.getVariable("property");
        System.out.println("property=" + p + " (" + (p!=null ? p.getClass() : "null") + ")");

        Map<String,Object> error= new LinkedHashMap<>();
        error.put("timestamp", OffsetDateTime.now(ZoneOffset.UTC).toString());
        error.put("status", status);
        error.put("error", errorText);

        execution.setVariable("error", error);
        execution.setVariable("success", false);
    }
}