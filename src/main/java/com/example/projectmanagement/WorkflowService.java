package com.example.projectmanagement;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.HistoryService;

import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricActivityInstance;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    public WorkflowService(
            RuntimeService runtimeService,
            TaskService taskService,
            HistoryService historyService) {

        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
    }

    // 1. CREATE PROCESS INSTANCE
    @Transactional
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

    // 2. FIND ALL RUNNING PROCESS INSTANCES
    public List<ProcessInstance> getAllProcessInstances() {

        return runtimeService
                .createProcessInstanceQuery()
                .processDefinitionKey("projectManagementProcess")
                .list();
    }

    // 3. FIND PROCESS INSTANCE BY ID
    public ProcessInstance getProcessInstanceById(String processInstanceId) {

        return runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
    }

    // 4. GET ALL ACTIVE PMS TASKS
    public List<Task> getAllTasks() {

        return taskService
                .createTaskQuery()
                .processDefinitionKey("projectManagementProcess")
                .active()
                .list();
    }

    // 5. GET TASKS ASSIGNED TO A USER
    public List<Task> getTasksByAssignee(String userId) {

        return taskService
                .createTaskQuery()
                .processDefinitionKey("projectManagementProcess")
                .taskAssignee(userId)
                .active()
                .list();
    }

    // 6. GET UNASSIGNED TASKS
    public List<Task> getUnassignedTasks() {

        return taskService
                .createTaskQuery()
                .processDefinitionKey("projectManagementProcess")
                .taskUnassigned()
                .active()
                .list();
    }

    // 7. CLAIM TASK
    @Transactional
    public void claimTask(String taskId, String userId) {

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        if (task.getAssignee() != null) {
            throw new IllegalStateException("Task is already assigned to: "
                    + task.getAssignee());
        }

        taskService.claim(taskId, userId);
    }

    // 8. COMPLETE TASK WITH PROCESS VARIABLES
    @Transactional
    public void completeTask(
            String taskId,
            Map<String, Object> variables) {

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }

        taskService.complete(taskId, variables);
    }

    // 9. SET PROCESS VARIABLES
    @Transactional
    public void setProcessVariables(
            String processInstanceId,
            Map<String, Object> variables) {

        ProcessInstance instance = getProcessInstanceById(processInstanceId);

        if (instance == null) {
            throw new IllegalArgumentException(
                    "Running process instance not found: " + processInstanceId);
        }

        runtimeService.setVariables(processInstanceId, variables);
    }

    // 10. GET PROCESS VARIABLES
    public Map<String, Object> getProcessVariables(String processInstanceId) {

        return runtimeService.getVariables(processInstanceId);
    }

    // 11. GET COMPLETED PROCESS INSTANCES
    public List<HistoricProcessInstance> getCompletedProcessInstances() {

        return historyService
                .createHistoricProcessInstanceQuery()
                .processDefinitionKey("projectManagementProcess")
                .finished()
                .list();
    }

    // 12. GET PROCESS HISTORY
    public List<HistoricActivityInstance> getProcessHistory(
            String processInstanceId) {

        return historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();
    }
}
