package com.scheduler.task_scheduler_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskSchedulerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskSchedulerBackendApplication.class, args);
	}

}
