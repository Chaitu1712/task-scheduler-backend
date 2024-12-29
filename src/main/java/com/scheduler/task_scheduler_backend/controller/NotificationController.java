package com.scheduler.task_scheduler_backend.controller;

import com.scheduler.task_scheduler_backend.model.Notification;
import com.scheduler.task_scheduler_backend.model.Notification.NotificationStatus;
import com.scheduler.task_scheduler_backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:3000", "https://task-scheduler-frontend-jade.vercel.app"})
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Get all notifications for a user
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getAllNotifications(@PathVariable Long userId) {
        return new ResponseEntity<>(notificationRepository.findByUserId(userId), HttpStatus.OK);
    }

    // Mark a notification as read
    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return notificationRepository.findById(id).map(notification -> {
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);
            return new ResponseEntity<>(notification, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
