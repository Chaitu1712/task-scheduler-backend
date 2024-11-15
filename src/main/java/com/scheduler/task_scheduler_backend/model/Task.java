package com.scheduler.task_scheduler_backend.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.security.PublicKey;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @Column(nullable = false)
    @Future(message = "Deadline must be a future date")
    private LocalDateTime deadline;
    
    @Column(nullable = false)
    @Min(1)
    @Max(10)
    private int priority;
    
    public enum TaskStatus {
        PENDING, COMPLETED, OVERDUE
    }
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
    public Task(){}
    // Constructors
    public Task(String title, String description, int priority, LocalDateTime deadline) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = TaskStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(String status) {
        this.status = TaskStatus.valueOf(status);
    }
    public void setStatus(TaskStatus status) {
        this.status =status;
    }

    // toString Method
    @Override
    public String toString() {
        return "Task" + id +
                ":- \nTitle='" + title + '\'' +
                "\nDescription='" + description + '\'' +
                "\nDeadline=" + deadline +
                "\nPriority=" + priority +
                "\nStatus='" + status + '\'' +
                '\n';
    }
}