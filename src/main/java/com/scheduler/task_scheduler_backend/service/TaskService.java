package com.scheduler.task_scheduler_backend.service;

import com.scheduler.task_scheduler_backend.repository.NotificationRepository;
import com.scheduler.task_scheduler_backend.model.Notification;
import com.scheduler.task_scheduler_backend.model.Task;
import com.scheduler.task_scheduler_backend.model.Notification.NotificationStatus;
import com.scheduler.task_scheduler_backend.model.Task.TaskStatus;
import com.scheduler.task_scheduler_backend.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final NotificationRepository notificationRepository;

    public TaskService(TaskRepository taskRepository, NotificationRepository notificationRepository) {
        this.taskRepository = taskRepository;
        this.notificationRepository = notificationRepository;
    }

    // Create a new task for a user
    public Task createTask(Task task, Long userId) {
        task.setUserId(userId);
        return taskRepository.save(task);
    }

    // Get a task by ID and user ID
    public Optional<Task> getTaskById(Long id, Long userId) {
        return taskRepository.findByIdAndUserId(id, userId);
    }

    // Update an existing task for a user
    public Task updateTask(Long id, Task updatedTask, Long userId) {
        return taskRepository.findByIdAndUserId(id, userId).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setDeadline(updatedTask.getDeadline());
            task.setPriority(updatedTask.getPriority());
            task.setStatus(updatedTask.getStatus());
            return taskRepository.save(task);
        }).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found"));
    }

    // Delete a task by ID
    public void deleteTask(Long id, Long userId) {
        taskRepository.deleteByIdAndUserId(id, userId);
    }

    // List all tasks for a user, sorted by priority and deadline
    public List<Task> getAllTasksSorted(Long userId, boolean desc) {
        return desc ? taskRepository.findAllByUserIdOrderByDeadlineAscPriorityDesc(userId) : taskRepository.findAllByUserIdOrderByDeadlineAscPriorityAsc(userId);
    }

    // Find tasks by deadline for a user
    public List<Task> getTasksByDeadline(Long userId, String deadline, boolean desc) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.withHour(23).withMinute(59).withSecond(59);
        LocalDateTime endOfTomorrow = endOfDay.plusDays(1);
        LocalDateTime endOfThisWeek = endOfDay.plusDays(7);
        if (desc) {
            switch (deadline) {
                case "TODAY":
                    return taskRepository.findByUserIdAndDeadlineBetweenOrderByDeadlineAscPriorityDesc(userId, now, endOfDay);
                case "TOMORROW":
                    return taskRepository.findByUserIdAndDeadlineBetweenOrderByDeadlineAscPriorityDesc(userId, endOfDay, endOfTomorrow);
                case "THIS_WEEK":
                    return taskRepository.findByUserIdAndDeadlineBetweenOrderByDeadlineAscPriorityDesc(userId, endOfDay, endOfThisWeek);
                default:
                    return List.of();
            }
        } else {
            switch (deadline) {
                case "TODAY":
                    return taskRepository.findByUserIdAndDeadlineBetweenOrderByDeadlineAscPriorityAsc(userId, now, endOfDay);
                case "TOMORROW":
                    return taskRepository.findByUserIdAndDeadlineBetweenOrderByDeadlineAscPriorityAsc(userId, endOfDay, endOfTomorrow);
                case "THIS_WEEK":
                    return taskRepository.findByUserIdAndDeadlineBetweenOrderByDeadlineAscPriorityAsc(userId, endOfDay, endOfThisWeek);
                default:
                    return List.of();
            }
        }
    }

    // Find tasks by status and deadline for a user
    public List<Task> getTasksByStatusAndDeadline(Long userId, String status, String deadline, boolean desc) {
        List<Task> tasksByStatus = getTasksByStatus(userId, status, desc);
        List<Task> tasksByDeadline = getTasksByDeadline(userId, deadline, desc);
        tasksByStatus.retainAll(tasksByDeadline);
        return tasksByStatus;
    }

    // Find tasks by status for a user
    public List<Task> getTasksByStatus(Long userId, String status, boolean desc) {
        return desc ? taskRepository.findByUserIdAndStatusOrderByDeadlineAscPriorityDesc(userId, TaskStatus.valueOf(status)) : taskRepository.findByUserIdAndStatusOrderByDeadlineAscPriorityAsc(userId, TaskStatus.valueOf(status));
    }

    // Update the status of a task for a user
    public Task updateTaskStatus(Long id, String status, Long userId) {
        return taskRepository.findByIdAndUserId(id, userId).map(task -> {
            task.setStatus(TaskStatus.valueOf(status));
            return taskRepository.save(task);
        }).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found"));
    }

    // Reschedule a task to the next day and set status to OVERDUE
    public void rescheduleOverdueTasks() {
        List<Task> overdueTasks = taskRepository.findAll().stream()
                .filter(task -> task.getDeadline().isBefore(LocalDateTime.now()) && !task.getStatus().equals(TaskStatus.COMPLETED))
                .toList();

        for (Task task : overdueTasks) {
            task.setDeadline(task.getDeadline().plusDays(1));
            task.setStatus(TaskStatus.OVERDUE);
            taskRepository.save(task); // Save the updated task
            createNotification("Task '" + task.getTitle() + "' was rescheduled because it became overdue.", task.getUserId());
        }
    }

    // Delete all tasks that have a status of COMPLETED for a user
    public void deleteCompletedTasks() {
        List<Task> completedTasks = taskRepository.findByStatus(TaskStatus.COMPLETED);
        for (Task task : completedTasks) {
            taskRepository.delete(task);
            createNotification("Task '" + task.getTitle() + "' was removed because it was completed.", task.getUserId());
        }
    }

    // Adjust the priority of tasks based on the days remaining until the deadline
    public void adjustPriorityBasedOnDeadline() {
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            long daysUntilDeadline = java.time.Duration.between(LocalDateTime.now(), task.getDeadline()).toDays();
            if (daysUntilDeadline <= 1) {
                task.setPriority(1); // Highest priority
            } else if (daysUntilDeadline <= 3) {
                task.setPriority(3);
            }
            taskRepository.save(task); // Save the updated task
        }
    }

    // Delete notifications marked as read
    public void deleteReadNotifications() {
        List<Notification> readNotifications = notificationRepository.findByStatus(NotificationStatus.READ);
        for (Notification notification : readNotifications) {
            notificationRepository.delete(notification);
        }
    }

    // Notify the user of upcoming deadlines
    public void notifyUpcomingDeadlines() {
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            long daysUntilDeadline = java.time.Duration.between(LocalDateTime.now(), task.getDeadline()).toDays();
            if (daysUntilDeadline <= 1) {
                createNotification("Task '" + task.getTitle() + "' is due tomorrow.", task.getUserId());
            } else if (daysUntilDeadline == 3) {
                createNotification("Task '" + task.getTitle() + "' is due in three days.", task.getUserId());
            }
        }
    }

    // Scheduled task to run at intervals of one hour
    @Scheduled(cron = "0 */15 * * * ?")
    public void performDailyTaskManagement() {
        deleteCompletedTasks();
        rescheduleOverdueTasks();
        adjustPriorityBasedOnDeadline();
        deleteReadNotifications();
        notifyUpcomingDeadlines();
    }

    private void createNotification(String message, Long userId) {
        Notification notification = new Notification(message, LocalDateTime.now(), NotificationStatus.UNREAD, userId);
        notificationRepository.save(notification);
    }
}