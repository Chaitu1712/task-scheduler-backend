package com.scheduler.task_scheduler_backend.repository;


import com.scheduler.task_scheduler_backend.model.Task;
import com.scheduler.task_scheduler_backend.model.Task.TaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Custom query method to find tasks by status
    List<Task> findByStatusOrderByDeadlineAscPriorityAsc(TaskStatus status);
    // Custom query method to find tasks by status in descending order
    List<Task> findByStatusOrderByDeadlineAscPriorityDesc(TaskStatus status);
    // Custom query method to find tasks by descending priority and ascending deadline
    List<Task> findAllByOrderByDeadlineAscPriorityDesc();
    // Custom query method to find tasks ordered by priority and deadline
    List<Task> findAllByOrderByDeadlineAscPriorityAsc();
    // Custom query method to find tasks between now and a deadline
    List<Task> findByDeadlineBetweenOrderByDeadlineAscPriorityAsc(LocalDateTime now, LocalDateTime deadline);
    // Custom query method to find tasks between now and a deadline in descending order
    List<Task> findByDeadlineBetweenOrderByDeadlineAscPriorityDesc(LocalDateTime now, LocalDateTime deadline);
}