package com.example.activiti.controller.execution;

import com.example.activiti.generic.config.RequestToVariablesMapper;
import com.example.activiti.model.ClaimCompleteRequest;
import com.example.activiti.model.PdfMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/process")
@Slf4j
public class ProcessExecutionController {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final RequestToVariablesMapper requestToVariablesMapper;
    private final TaskService taskService;

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

    @PostMapping("/tasks/{processInstanceId}/claim-complete")
    public void claimAndComplete(@PathVariable String processInstanceId, @RequestBody ClaimCompleteRequest request) {

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();

        if (tasks.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tasks active don't exists for processInstanceId" + processInstanceId);
        }

        var taskId = tasks.get(0).getId();

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task does not exists");
        if (Boolean.TRUE.equals(task.isSuspended()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Task is suspended");

        // Validar que el grupo enviado sea realmente candidato de la tarea
        boolean groupIsCandidate = taskService.getIdentityLinksForTask(taskId).stream()
                .anyMatch(l -> "candidate".equals(l.getType()) && request.candidateGroup().equals(l.getGroupId()));
        if (!groupIsCandidate)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Group is not candidate for this task");

        // 2) Reclamar si aún no tiene assignee; si ya lo tiene:
        if (task.getAssignee() == null) {
            try {
                taskService.claim(taskId, request.userId());
            } catch (ActivitiTaskAlreadyClaimedException e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Task was claimed by other user");
            }
        } else if (!request.userId().equals(task.getAssignee())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Task was assigned to other user");
        }

        // 3) Completar
        Map<String, Object> vars = (request.variables() == null) ? Map.of() : request.variables();
        taskService.complete(taskId, vars);
    }

    @PostMapping(value = "/start/approve-rates", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startApproveRates(@RequestBody Object request) {
        var processKey = "approve-rates";
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
        if (!httpStatus.equals("201")) {
            if (error instanceof Map<?, ?> map) {
                int status = (map.get("status") instanceof Number n) ? n.intValue() : 400;
                return ResponseEntity.status(status).body(map);
            }
        }

        Map<String, Object> body = Map.of(
                "requestId", output.get("requestId"),
                "dueDate",   output.get("dueDate"),
                "processInstance", processInstance.getId()
        );

        return ResponseEntity.ok(body);

        //return ResponseEntity.ok(output.get("success"));
    }

    @PostMapping(value = "/start/async/{processKey}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> start_async(@PathVariable String processKey, @RequestBody Object request) {
        log.info("Si entró");
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

        Map<String, Object> variables = requestToVariablesMapper.map(processKey, request);

        var processInstance = runtimeService.startProcessInstanceByKey(processKey, variables);

        return ResponseEntity.status(200).body(Map.of(
                "timestamp", java.time.OffsetDateTime.now().toString(),
                "status", 200,
                "process-instance-id", processInstance.getProcessInstanceId()
        ));
    }

    /*@PostMapping("/pdf")
    public ResponseEntity<?> receivePdfGeneratedMessage(@RequestBody PdfMessageRequest request) {
        try {
            if (request == null || request.processInstanceId == null || request.processInstanceId.isBlank()) {
                return ResponseEntity.badRequest().body("processId is required");
            }

            runtimeService.messageEventReceived("PdfGenerated",request.processInstanceId, request.variables);

            return ResponseEntity.ok("Message delivered. Process resumed. businessKey=" + request.processInstanceId);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("No process found waiting for message PdfGenerated: " + e.getMessage());
        }
    }*/

    @PostMapping("/pdf")
    public ResponseEntity<?> onPdfGenerated(@RequestBody PdfMessageRequest request) {
        String messageName = "PdfGenerated";
        try {
            Execution execution = runtimeService.createExecutionQuery()
                    .messageEventSubscriptionName(messageName)
                    .processInstanceId(request.processInstanceId)
                    .singleResult();

            if (execution == null) {
                return ResponseEntity.status(404)
                        .body("No execution found waiting for message '" + messageName + "' for processId=" + request.processInstanceId);
            }

            runtimeService.messageEventReceived(messageName, execution.getId(), request.variables);

            return ResponseEntity.ok("Message delivered successfully to executionId=" + execution.getId());

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error delivering message: " + e.getMessage());
        }
    }


}


