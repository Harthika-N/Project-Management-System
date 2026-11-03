package com.example.projectmanagement;

import org.springframework.web.bind.annotation.*;

import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricActivityInstance;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    // 1. GET ALL RUNNING PROCESS INSTANCES
    @GetMapping("/instances")
    public List<Map<String, Object>> getAllProcessInstances() {

        List<Map<String, Object>> result = new ArrayList<>();

        for (ProcessInstance instance :
                workflowService.getAllProcessInstances()) {

            Map<String, Object> data = new HashMap<>();

            data.put("id", instance.getId());
            data.put("processDefinitionKey",
                    instance.getProcessDefinitionKey());
            data.put("businessKey", instance.getBusinessKey());
            data.put("ended", instance.isEnded());

            result.add(data);
        }

        return result;
    }

    // 2. GET PROCESS INSTANCE BY ID
    @GetMapping("/instances/{processInstanceId}")
    public Map<String, Object> getProcessInstance(
            @PathVariable String processInstanceId) {

        ProcessInstance instance =
                workflowService.getProcessInstanceById(processInstanceId);

        if (instance == null) {
            throw new RuntimeException("Process instance not found");
        }

        Map<String, Object> data = new HashMap<>();

        data.put("id", instance.getId());
        data.put("processDefinitionKey",
                instance.getProcessDefinitionKey());
        data.put("businessKey", instance.getBusinessKey());
        data.put("ended", instance.isEnded());

        return data;
    }

    // 3. GET ALL ACTIVE TASKS
    @GetMapping("/tasks")
    public List<Map<String, Object>> getAllTasks() {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Task task : workflowService.getAllTasks()) {
            result.add(convertTaskToMap(task));
        }

        return result;
    }

    // 4. GET TASKS BY ASSIGNEE
    @GetMapping("/tasks/assignee/{userId}")
    public List<Map<String, Object>> getTasksByAssignee(
            @PathVariable String userId) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Task task :
                workflowService.getTasksByAssignee(userId)) {

            result.add(convertTaskToMap(task));
        }

        return result;
    }

    // 5. GET UNASSIGNED TASKS
    @GetMapping("/tasks/unassigned")
    public List<Map<String, Object>> getUnassignedTasks() {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Task task : workflowService.getUnassignedTasks()) {
            result.add(convertTaskToMap(task));
        }

        return result;
    }

    // 6. CLAIM TASK
    @PostMapping("/tasks/{taskId}/claim/{userId}")
    public String claimTask(
            @PathVariable String taskId,
            @PathVariable String userId) {

        workflowService.claimTask(taskId, userId);

        return "Task claimed successfully";
    }

    // 7. COMPLETE TASK
    @PostMapping("/tasks/{taskId}/complete")
    public String completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false)
            Map<String, Object> variables) {

        if (variables == null) {
            variables = new HashMap<>();
        }

        workflowService.completeTask(taskId, variables);

        return "Workflow task completed successfully";
    }

    // 8. GET PROCESS VARIABLES
    @GetMapping("/instances/{processInstanceId}/variables")
    public Map<String, Object> getProcessVariables(
            @PathVariable String processInstanceId) {

        return workflowService.getProcessVariables(processInstanceId);
    }

    // 9. SET PROCESS VARIABLES
    @PutMapping("/instances/{processInstanceId}/variables")
    public String setProcessVariables(
            @PathVariable String processInstanceId,
            @RequestBody Map<String, Object> variables) {

        workflowService.setProcessVariables(
                processInstanceId, variables);

        return "Process variables updated successfully";
    }

    // 10. GET COMPLETED PROCESS INSTANCES
    @GetMapping("/instances/completed")
    public List<Map<String, Object>> getCompletedProcessInstances() {

        List<Map<String, Object>> result = new ArrayList<>();

        for (HistoricProcessInstance instance :
                workflowService.getCompletedProcessInstances()) {

            Map<String, Object> data = new HashMap<>();

            data.put("id", instance.getId());
            data.put("processDefinitionKey",
                    instance.getProcessDefinitionKey());
            data.put("businessKey", instance.getBusinessKey());
            data.put("startTime", instance.getStartTime());
            data.put("endTime", instance.getEndTime());

            result.add(data);
        }

        return result;
    }

    // 11. GET PROCESS HISTORY
    @GetMapping("/instances/{processInstanceId}/history")
    public List<Map<String, Object>> getProcessHistory(
            @PathVariable String processInstanceId) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (HistoricActivityInstance activity :
                workflowService.getProcessHistory(processInstanceId)) {

            Map<String, Object> data = new HashMap<>();

            data.put("activityId", activity.getActivityId());
            data.put("activityName", activity.getActivityName());
            data.put("activityType", activity.getActivityType());
            data.put("startTime", activity.getStartTime());
            data.put("endTime", activity.getEndTime());

            result.add(data);
        }

        return result;
    }

    // COMMON METHOD: CONVERT TASK TO SIMPLE JSON DATA
    private Map<String, Object> convertTaskToMap(Task task) {

        Map<String, Object> data = new HashMap<>();

        data.put("id", task.getId());
        data.put("name", task.getName());
        data.put("assignee", task.getAssignee());
        data.put("processInstanceId", task.getProcessInstanceId());
        data.put("taskDefinitionKey", task.getTaskDefinitionKey());
        data.put("createTime", task.getCreateTime());

        return data;
    }
}
