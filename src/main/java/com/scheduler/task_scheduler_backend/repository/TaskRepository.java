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
    List<Task> findByUserIdAndStatusOrderByDeadlineAscPriorityDesc(Long userId, TaskStatus status);

    // Custom query methods to find tasks by user ID, priority, and deadline
    List<Task> findAllByUserIdOrderByDeadlineAscPriorityDesc(Long userId);
    List<Task> findAllByUserIdOrderByDeadlineAscPriorityAsc(Long userId);

    // Custom query methods to find tasks by user ID and deadline
    List<Task> findByUserIdAndDeadlineBetweenOrderByDeadlineAscPriorityAsc(Long userId, LocalDateTime now, LocalDateTime deadline);
    List<Task> findByUserIdAndDeadlineBetweenOrderByDeadlineAscPriorityDesc(Long userId, LocalDateTime now, LocalDateTime deadline);
    List<Task> findByStatus(TaskStatus status);
    // Custom query methods to find tasks by ID and user ID
    Optional<Task> findByIdAndUserId(Long id, Long userId);
    void deleteByIdAndUserId(Long id, Long userId);

}