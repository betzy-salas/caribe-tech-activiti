package com.example.activiti.controller.execution;

import com.example.activiti.generic.config.RequestToVariablesMapper;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricVariableInstance;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/process")
public class ProcessExecutionController {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final RequestToVariablesMapper requestToVariablesMapper;

    @PostMapping(value = "/start/{processKey}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> start(@PathVariable String processKey, @RequestBody Object request) {
        var processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .latestVersion()
                .singleResult();
        if (processDefinition == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "timestamp", java.time.OffsetDateTime.now().toString(),
                    "status", 404,
                    "error", "Process not found: " + processKey
            ));
        }

        Map<String,Object> variables = requestToVariablesMapper.map(processKey, request);

        var processInstance = runtimeService.startProcessInstanceByKey(processKey, variables);

        Map<String,Object> output = getVariablesSafe(processInstance.getId());

        Object error = output.get("error");
        String httpStatus = output.get("httpStatus").toString();
        if (!httpStatus.equals("200")) {
            if (error instanceof Map<?, ?> map) {
                int status = (map.get("status") instanceof Number n) ? n.intValue() : 400;
                return ResponseEntity.status(status).body(map);
            }
        }

        return ResponseEntity.ok(output.get("success"));
    }

    private Map<String,Object> getVariablesSafe(String processInstanceId) {
            return getHistoricVariables(processInstanceId);
    }

    private Map<String,Object> getHistoricVariables(String processInstanceId) {
        var list = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        Map<String,Object> variables = new HashMap<>();
        for (HistoricVariableInstance v : list) {
            variables.put(v.getVariableName(), v.getValue());
        }
        return variables;
    }
}


