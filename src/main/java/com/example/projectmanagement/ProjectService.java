package com.example.projectmanagement;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WorkflowService workflowService;

    public ProjectService(
            ProjectRepository projectRepository,
            WorkflowService workflowService) {

        this.projectRepository = projectRepository;
        this.workflowService = workflowService;
    }

    // Create project and start workflow
    @Transactional
    public Project createProject(Project project) {

        Project savedProject = projectRepository.save(project);

        workflowService.startProjectWorkflow(savedProject);

        return savedProject;
    }

    // Save or update project
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    // Get all projects
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // Get project by ID
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    // Delete project
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}
