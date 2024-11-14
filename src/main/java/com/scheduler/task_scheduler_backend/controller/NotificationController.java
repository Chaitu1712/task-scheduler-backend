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
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Get all notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return new ResponseEntity<>(notificationRepository.findAll(), HttpStatus.OK);
    }

    // Get unread notifications
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        return new ResponseEntity<>(notificationRepository.findByStatus(NotificationStatus.UNREAD), HttpStatus.OK);
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
