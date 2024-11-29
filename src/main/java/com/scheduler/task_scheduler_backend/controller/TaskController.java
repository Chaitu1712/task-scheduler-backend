package com.scheduler.task_scheduler_backend.controller;

import com.scheduler.task_scheduler_backend.model.Task;
import com.scheduler.task_scheduler_backend.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Create a new task for a user
    @PostMapping("/{userId}")
    public ResponseEntity<Task> createTask(@PathVariable Long userId, @RequestBody Map<String, Object> taskData) {
        String t= (String) taskData.get("title");
        String d= (String) taskData.get("description");
        int p= (int) taskData.get("priority");
        String dl= (String) taskData.get("deadline");
        LocalDateTime deadline = LocalDateTime.parse(dl);
        Task task = new Task(t, d, p, deadline);
        Task createdTask = taskService.createTask(task, userId);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // Get a task by ID and user ID
    @GetMapping("/{userId}/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long userId, @PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id, userId);
        return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a task by ID and user ID
    @PutMapping("/{userId}/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long userId, @PathVariable Long id, @RequestBody Map<String, Object> taskData) {
        try {
            Task temp=taskService.getTaskById(id, userId).get();
            String t= (String) taskData.get("title")==null?temp.getTitle():(String) taskData.get("title");
            String d= (String) taskData.get("description")==null?temp.getDescription():(String) taskData.get("description");
            int p= (int) taskData.get("priority")==0?temp.getPriority():(int) taskData.get("priority");
            String dl= (String) taskData.get("deadline")==null?temp.getDeadline().toString():(String) taskData.get("deadline");
            LocalDateTime deadline = LocalDateTime.parse(dl);
            Task task = new Task(t, d, p, deadline);
            Task updatedTask = taskService.updateTask(id, task, userId);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a task by ID and user ID
    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long userId, @PathVariable Long id) {
        taskService.deleteTask(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get tasks by deadline for a user
    @GetMapping("/{userId}/deadline/{deadline}")
    public ResponseEntity<List<Task>> getTasksByDeadline(@PathVariable Long userId, @PathVariable String deadline) {
        List<Task> tasks = taskService.getTasksByDeadline(userId, deadline, false);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Get tasks by status for a user
    @GetMapping("/{userId}/status")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable Long userId, @RequestParam String status) {
        List<Task> tasks = taskService.getTasksByStatus(userId, status, false);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Get all tasks for a user, optionally filtered by status and/or deadline, sorted by priority and deadline
    @GetMapping("/{userId}")
    public ResponseEntity<List<Task>> getAllTasksSorted(@PathVariable Long userId, @RequestParam(required = false, defaultValue="false") String desc, @RequestParam(required = false) String status, @RequestParam(required = false) String deadline) {
        List<Task> tasks;
        boolean descOrder = desc.equals("true");
        if (status != null && deadline != null) {
            tasks = taskService.getTasksByStatusAndDeadline(userId, status, deadline, descOrder);
        } else if (status != null) {
            tasks = taskService.getTasksByStatus(userId, status, descOrder);
        } else if (deadline != null) {
            tasks = taskService.getTasksByDeadline(userId, deadline, descOrder);
        } else {
            tasks = taskService.getAllTasksSorted(userId, descOrder);
        }
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Update the status of a task for a user
    @PatchMapping("/{userId}/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long userId, @PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            Task updatedTask = taskService.updateTaskStatus(id, status, userId);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}