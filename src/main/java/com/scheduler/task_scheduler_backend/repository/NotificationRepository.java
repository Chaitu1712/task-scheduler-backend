package com.scheduler.task_scheduler_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.task_scheduler_backend.model.Notification;
import com.scheduler.task_scheduler_backend.model.Notification.NotificationStatus;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Custom query method to find notifications by user ID
    List<Notification> findByUserId(Long userId);
    List<Notification> findByStatus(NotificationStatus status);
    // Custom query method to find notifications by status
    List<Notification> findByUserIdByStatus(Long userId,NotificationStatus status);
}
