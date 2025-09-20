package com.example.activiti.controller.deployment;

import lombok.RequiredArgsConstructor;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/api/deploy")
@RequiredArgsConstructor
public class ProcessDeploymentController {

    private final RepositoryService repositoryService;

    @PostMapping("/{processName}")
    public String deployProcess(@PathVariable String processName) {
        try {
            String resourcePath = "processes/" + processName + ".bpmn20.xml";
            InputStream bpmnStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

            if (bpmnStream == null) {
                return "BPMN file not found: " + resourcePath;
            }

            Deployment deployment = repositoryService.createDeployment()
                    .name(processName)
                    .addInputStream(processName + ".bpmn20.xml", bpmnStream)
                    .deploy();

            System.out.println("Process deployed with key: " + processName);

            return deployment.getId();

        } catch (Exception e) {
            return "Error deploying process: " + e.getMessage();
        }
    }
}
