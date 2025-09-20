package com.example.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ActivitiIntegrationTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @BeforeEach
    void deployManually() {
        repositoryService.createDeployment()
                .addClasspathResource("buy-income-test.bpmn20.xml")
                .deploy();
    }

    @Test
    @Deployment(resources = "buy-income-test.bpmn20.xml")
    void testProcessExecution() {
        Map<String, Object> variables = Map.of(
                "TIPO_DOCUMENTO", "CC",
                "NUMERO_DOCUMENTO", "12345678"
        );

        System.out.println("Procesos desplegados:");
        repositoryService.createProcessDefinitionQuery()
                .list()
                .forEach(p -> System.out.println("🔍 " + p.getKey()));

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("buy-income-test", variables);

        assertThat(processInstance).isNotNull();
        assertThat(processInstance.isEnded()).isTrue();
    }
}

