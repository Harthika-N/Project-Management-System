
package com.example.projectmanagement;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    // Complete a workflow task
    @PostMapping("/tasks/{taskId}/complete")
    public String completeTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables) {

        workflowService.completeTask(taskId, variables);

        return "Workflow task completed successfully";
    }
}
