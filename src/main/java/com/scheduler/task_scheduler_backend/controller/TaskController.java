package com.scheduler.task_scheduler_backend.controller;

import com.scheduler.task_scheduler_backend.model.Task;
import com.scheduler.task_scheduler_backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Create a new task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Map<String, Object> taskData) {
        String t= (String) taskData.get("title");
        String d= (String) taskData.get("description");
        int p= (int) taskData.get("priority");
        String dl= (String) taskData.get("deadline");
        LocalDateTime deadline = LocalDateTime.parse(dl);
        Task task = new Task(t, d, p, deadline);
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // Get a task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a task by ID
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,@RequestBody Map<String, Object> taskData) {
        try {
            Task temp=taskService.getTaskById(id).get();
            String t= (String) taskData.get("title")==null?temp.getTitle():(String) taskData.get("title");
            String d= (String) taskData.get("description")==null?temp.getDescription():(String) taskData.get("description");
            int p= (int) taskData.get("priority")==0?temp.getPriority():(int) taskData.get("priority");
            String dl= (String) taskData.get("deadline")==null?temp.getDeadline().toString():(String) taskData.get("deadline");
            LocalDateTime deadline = LocalDateTime.parse(dl);
            Task task = new Task(t, d, p, deadline);
            Task updatedTask = taskService.updateTask(id, task);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a task by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //get tasks by deadline
    @GetMapping("/deadline/{deadline}")
    public ResponseEntity<List<Task>> getTasksByDeadline(@PathVariable String deadline) {
        List<Task> tasks = taskService.getTasksByDeadline(deadline,false);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Get tasks by status
    @GetMapping("/status")
    public ResponseEntity<List<Task>> getTasksByStatus(@RequestParam String status) {
        List<Task> tasks = taskService.getTasksByStatus(status,false);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    // Get all tasks, optionally filtered by status and/or deadline, sorted by priority and deadline
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasksSorted(@RequestParam(required = false, defaultValue="false") String desc,@RequestParam(required = false) String status, @RequestParam(required = false) String deadline) {
        List<Task> tasks;
        boolean descOrder = desc.equals("true");
        if (status != null && deadline != null) {
            tasks = taskService.getTasksByStatusAndDeadline(status, deadline,descOrder);
        } else if (status != null) {
            tasks = taskService.getTasksByStatus(status,descOrder);
        } else if (deadline != null) {
            tasks = taskService.getTasksByDeadline(deadline,descOrder);
        } else {
            tasks = taskService.getAllTasksSorted(descOrder);
        }
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Update the status of a task
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            Task updatedTask = taskService.updateTaskStatus(id, status);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}