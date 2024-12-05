package com.scheduler.task_scheduler_backend.repository;

import com.scheduler.task_scheduler_backend.model.Task;
import com.scheduler.task_scheduler_backend.model.Task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Custom query methods to find tasks by user ID and status
    List<Task> findByUserIdAndStatusOrderByDeadlineAscPriorityAsc(Long userId, TaskStatus status);

    // Custom query methods to find tasks by user ID and deadline
    List<Task> findByUserIdAndDeadlineBetweenOrderByDeadlineAscPriorityAsc(Long userId, LocalDateTime now, LocalDateTime deadline);
    
    // Custom query methods to find tasks by status
    List<Task> findByStatusOrderByDeadlineAscPriorityAsc(TaskStatus status);
    
    // Custom query methods to find tasks by ID and user ID
    Optional<Task> findByIdAndUserId(Long id, Long userId);
    void deleteByIdAndUserId(Long id, Long userId);

    // Custom query method to find all tasks by user ID
    List<Task> findAllByUserIdOrderByDeadlineAscPriorityAsc(Long userId);
}