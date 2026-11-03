
package com.example.projectmanagement;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public WorkflowService(
            RuntimeService runtimeService,
            TaskService taskService) {

        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    public ProcessInstance startProjectWorkflow(Project project) {

        Map<String, Object> variables = new HashMap<>();

        variables.put("projectId", project.getId());
        variables.put("projectName", project.getName());
        variables.put("projectManager", project.getManager());
        variables.put("teamLead", project.getTeamLead());
        variables.put("developer", project.getDeveloper());
        variables.put("qaTester", project.getQaTester());

        return runtimeService.startProcessInstanceByKey(
                "projectManagementProcess",
                String.valueOf(project.getId()),
                variables
        );
    }

    // Complete a workflow task with process variables
    public void completeTask(
            String taskId,
            Map<String, Object> variables) {

        taskService.complete(taskId, variables);
    }
}
