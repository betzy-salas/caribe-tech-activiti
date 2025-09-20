package com.example.activiti.processes.response;

import com.example.activiti.model.ProcessResponse;
import com.example.activiti.model.response.SourceResponse;
import com.example.activiti.model.response.TargetResponse;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.math.BigDecimal;

public class ReturnSuccess implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {

        ProcessResponse processResponse = new ProcessResponse(createSourceResponse(execution),
                createTargetResponse(execution)
                );

        execution.setVariable("success", processResponse);
    }

    private SourceResponse createSourceResponse(DelegateExecution execution){

        Object newSourceBalance = execution.getVariable("newSourceBalance");
        Object oldSourceBalance = execution.getVariable("previousSourceBalance");
        Object accountType = execution.getVariable("ACCOUNT_SOURCE_TYPE");
        Object accountNumber = execution.getVariable("ACCOUNT_SOURCE_NUMBER");

        System.out.println("newSourceBalance=" + newSourceBalance + " (" + (newSourceBalance!=null ? newSourceBalance.getClass() : "null") + ")");

        BigDecimal amount = (newSourceBalance instanceof BigDecimal)
                ? (BigDecimal) newSourceBalance
                : BigDecimal.ZERO;

        return SourceResponse.builder()
                .accountNumber((accountNumber instanceof String) ? ((String) accountNumber).toString() : "Empty")
                .accountType((accountType instanceof String) ? ((String) accountType).toString() : "Empty")
                .newBalance((newSourceBalance instanceof Double) ? ((Double) newSourceBalance).doubleValue() : 0)
                .oldBalance((oldSourceBalance instanceof Double) ? ((Double) oldSourceBalance).doubleValue() : 0)
                .build();
    }

    private TargetResponse createTargetResponse(DelegateExecution execution){

        Object newTargetBalance = execution.getVariable("newTargetBalance");
        Object oldTargetBalance = execution.getVariable("previousTargetBalance");
        Object accountType = execution.getVariable("ACCOUNT_TARGET_TYPE");
        Object accountNumber = execution.getVariable("ACCOUNT_TARGET_NUMBER");

        return TargetResponse.builder()
                .accountNumber((accountNumber instanceof String) ? ((String) accountNumber).toString() : "Empty")
                .accountType((accountType instanceof String) ? ((String) accountType).toString() : "Empty")
                .newBalance((newTargetBalance instanceof Double) ? ((Double) newTargetBalance).doubleValue() : 0)
                .oldBalance((oldTargetBalance instanceof Double) ? ((Double) oldTargetBalance).doubleValue() : 0)
                .build();
    }
}