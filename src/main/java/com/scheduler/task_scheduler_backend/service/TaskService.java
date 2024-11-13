package com.scheduler.task_scheduler_backend.service;

import com.scheduler.task_scheduler_backend.model.Task;
import com.scheduler.task_scheduler_backend.model.Task.TaskStatus;
import com.scheduler.task_scheduler_backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Create a new task
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Read or get a task by ID
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Update an existing task
    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setDeadline(updatedTask.getDeadline());
            task.setPriority(updatedTask.getPriority());
            task.setStatus(updatedTask.getStatus());
            return taskRepository.save(task);
        }).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found"));
    }

    // Delete a task by ID
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // List all tasks, sorted by priority and deadline
    public List<Task> getAllTasksSorted() {
        return taskRepository.findAllByOrderByPriorityAscDeadlineAsc();
    }

    // Find tasks by status (e.g., PENDING, COMPLETED)
    public List<Task> getTasksByStatus(String s) {
        TaskStatus status = TaskStatus.valueOf(s.toUpperCase());
        return taskRepository.findByStatus(status);
    }

    // Update the status of a task
    public Task updateTaskStatus(Long id, String status) {
        return taskRepository.findById(id).map(task -> {
            task.setStatus(status);
            return taskRepository.save(task);
        }).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found"));
    }
    // Reschedule a task to the next day and set status to OVERDUE
    public Task rescheduleTask(Long id) {
        return taskRepository.findById(id).map(task -> {
            // Set the deadline to the next day
            task.setDeadline(task.getDeadline().plusDays(1));
            // Set the status to OVERDUE
            task.setStatus("OVERDUE");
            return taskRepository.save(task);
        }).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found"));
    }
}