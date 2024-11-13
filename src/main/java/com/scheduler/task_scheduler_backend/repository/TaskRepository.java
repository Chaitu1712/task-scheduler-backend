package com.scheduler.task_scheduler_backend.repository;


import com.scheduler.task_scheduler_backend.model.Task;
import com.scheduler.task_scheduler_backend.model.Task.TaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Custom query method to find tasks by status
    List<Task> findByStatus(TaskStatus status);

    // Custom query method to find tasks ordered by priority and deadline
    List<Task> findAllByOrderByPriorityAscDeadlineAsc();
}