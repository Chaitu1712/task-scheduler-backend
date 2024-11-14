package com.scheduler.task_scheduler_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private LocalDateTime timestamp;
    public enum NotificationStatus {
        UNREAD,
        READ
    }
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    public Notification() {
    }

    public Notification(String message, LocalDateTime timestamp, NotificationStatus status) {
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status.toString(); }
    public void setStatus(NotificationStatus status) { this.status= status; }
}