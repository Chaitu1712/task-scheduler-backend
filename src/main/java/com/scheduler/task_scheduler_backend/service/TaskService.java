package com.scheduler.task_scheduler_backend.service;

import com.scheduler.task_scheduler_backend.model.Notification;
import com.scheduler.task_scheduler_backend.repository.NotificationRepository;
import com.scheduler.task_scheduler_backend.model.Notification.NotificationStatus;
import com.scheduler.task_scheduler_backend.model.Task;
import com.scheduler.task_scheduler_backend.model.Task.TaskStatus;
import com.scheduler.task_scheduler_backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final NotificationRepository notificationRepository;

   @Autowired
    public TaskService(TaskRepository taskRepository, NotificationRepository notificationRepository) {
        this.taskRepository = taskRepository;
        this.notificationRepository = notificationRepository;
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
    public List<Task> getAllTasksSorted(boolean desc) {
        return desc?taskRepository.findAllByOrderByDeadlineAscPriorityDesc():taskRepository.findAllByOrderByDeadlineAscPriorityAsc();
    }

    // Find tasks by deadline (e.g., today, tomorrow, this week)
    public List<Task> getTasksByDeadline(String deadline,boolean desc) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.withHour(23).withMinute(59).withSecond(59);
        LocalDateTime endOfTomorrow = endOfDay.plusDays(1);
        LocalDateTime endOfThisWeek = endOfDay.plusDays(7);
        if(desc){
            switch (deadline) {
                case "TODAY":
                    return taskRepository.findByDeadlineBetweenOrderByDeadlineAscPriorityDesc(now, endOfDay);
                case "TOMORROW":
                    return taskRepository.findByDeadlineBetweenOrderByDeadlineAscPriorityDesc(endOfDay, endOfTomorrow);
                case "THIS_WEEK":
                    return taskRepository.findByDeadlineBetweenOrderByDeadlineAscPriorityDesc(endOfDay, endOfThisWeek);
                default:
                    return List.of();
            }
        }
        else{

            switch (deadline) {
                case "TODAY":
                    return taskRepository.findByDeadlineBetweenOrderByDeadlineAscPriorityAsc(now, endOfDay);
                case "TOMORROW":
                    return taskRepository.findByDeadlineBetweenOrderByDeadlineAscPriorityAsc(endOfDay, endOfTomorrow);
                case "THIS_WEEK":
                    return taskRepository.findByDeadlineBetweenOrderByDeadlineAscPriorityAsc(endOfDay, endOfThisWeek);
                default:
                    return List.of();
            }
        }
    }

    // Find tasks by status and deadline
    public List<Task> getTasksByStatusAndDeadline(String status, String deadline,boolean desc) {
        List<Task> tasksByStatus = getTasksByStatus(status,desc);
        List<Task> tasksByDeadline = getTasksByDeadline(deadline,desc);
        tasksByStatus.retainAll(tasksByDeadline);
        
        return tasksByStatus;
    }

    // Find tasks by status (e.g., PENDING, COMPLETED)
    public List<Task> getTasksByStatus(String status,boolean desc) {
        if(desc){
            return taskRepository.findByStatusOrderByDeadlineAscPriorityDesc(TaskStatus.valueOf(status));
        }
        else{
            return taskRepository.findByStatusOrderByDeadlineAscPriorityAsc(TaskStatus.valueOf(status));
        }
    }
    // Update the status of a task
    public Task updateTaskStatus(Long id, String status) {
        return taskRepository.findById(id).map(task -> {
            task.setStatus(status);
            return taskRepository.save(task);
        }).orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found"));
    }
    // Reschedule a task to the next day and set status to OVERDUE
    public void rescheduleOverdueTasks() {
        List<Task> overdueTasks = taskRepository.findAll().stream()
                .filter(task -> task.getDeadline().isBefore(LocalDateTime.now()) && task.getStatus() != "COMPLETED")
                .toList();

        for (Task task : overdueTasks) {
                task.setDeadline(task.getDeadline().plusDays(1));
                task.setStatus(TaskStatus.OVERDUE);
                taskRepository.save(task); // Save the updated task
                createNotification("Task '" + task.getTitle() + "' was rescheduled because it became overdue.");
        }
    }
    // Delete all tasks that have a status of COMPLETED
    public void deleteCompletedTasks() {
       List<Task> completedTasks = taskRepository.findByStatusOrderByDeadlineAscPriorityAsc(TaskStatus.COMPLETED);
        for (Task task : completedTasks) {
            taskRepository.delete(task);
            createNotification("Task '" + task.getTitle() + "' was removed because it was completed.");
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
    //delete notifications marked read
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
            if (daysUntilDeadline <=  1) {
                createNotification("Task '" + task.getTitle() + "' is due tomorrow.");
            } else if (daysUntilDeadline == 3) {
                createNotification("Task '" + task.getTitle() + "' is due in three days.");
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
    private void createNotification(String message) {
        Notification notification = new Notification(message, LocalDateTime.now(),NotificationStatus.UNREAD);
        notificationRepository.save(notification);
    }
}