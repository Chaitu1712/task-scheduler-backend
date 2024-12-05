# **Task Scheduler Backend**

This is the backend service for the Task Scheduler application, built using Spring Boot. It provides APIs for managing tasks, handling rescheduling, and sending notifications for overdue tasks. The project uses Amazon RDS MySQL as the database with an SSH tunnel for private access.

* * *

## **Features**

- **Task Management**: Create, read, update, and delete tasks.
- **Rescheduling**: Automatically reschedule overdue tasks to the next day and mark them as "Overdue."
- **Notifications**: Track and notify users about overdue or rescheduled tasks.
- **Auto Cleanup**: Automatically delete completed tasks daily.
- **Task Prioritization**: Adjust task priorities based on deadlines daily.

* * *

## **Technologies Used**

- **Java**: Programming language for backend logic.
- **Spring Boot**: Framework for rapid development.
- **MySQL**: Amazon RDS for database storage.
- **SSH Tunnel**: Secured database access via private SSH connection.
- **Maven**: Build and dependency management.

* * *

### **Dependencies**

The project uses the following key dependencies:
- `Spring Boot Starter Data JPA`
- `Spring Boot Starter Web`
- `Spring Boot Starter Security`
- `MySQL Connector/J`
- `JSCH` for SSH connections

* * *

## **Project Setup**

### **1. Clone the Repository**

   git clone https://github.com/your-username/task-scheduler-backend.gitcd 
   cd task-scheduler-backend

### **2. Configure the Application**

- **Database Configuration**:Edit the `application.properties` file in the `src/main/resources` folder:

        spring.datasource.url=jdbc:mysql://localhost:3306/task_scheduler
        spring.datasource.username=your_username
        spring.datasource.password=your_password
        spring.jpa.hibernate.ddl-auto=update
- **SSH Tunnel**:Ensure the SSH tunnel is configured to connect to the database. You can use tools like `SshTunnelConfig` in the project.

### **3. Build and Run**

- Build the project:

        mvn clean install
- Run the application:

        mvn spring-boot:run

### **4. Security Configuration**

The application uses Spring Security for basic security configuration. The `SecurityConfig` class disables CORS and CSRF and permits all requests. Passwords are encoded using `BCryptPasswordEncoder`.


* * *

## **API Endpoints**

### **Task Endpoints**

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/tasks/{userId}` | Get all tasks for a user |
| GET | `/api/tasks/{userId}/{id}` | Get task by ID and user ID |
| POST | `/api/tasks/{userId}` | Create a new task for a user |
| PUT | `/api/tasks/{userId}/{id}` | Update task details by ID and user ID |
| DELETE | `/api/tasks/{userId}/{id}` | Delete a task by ID and user ID |
| GET | `/api/tasks/{userId}/deadline/{deadline}` | Get tasks by deadline for a user |
| GET | `/api/tasks/{userId}/status` | Get tasks by status for a user |
| PATCH | `/api/tasks/{userId}/{id}/status` | Update the status of a task for a user |

### **Notification Endpoint**

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/notifications/{userId}` | Get all notifications for a user |
| PATCH | `/api/notifications/{id}/read` | Mark a notification as read |

* * *

## **Development Notes**


### **Scheduled Tasks**

The application includes scheduled tasks that run hourly:
- Reschedule overdue tasks.
- Auto-delete completed tasks.
- Update task priorities based on deadlines.
- Auto-delete read notifications

### **Validation**

The `Task` entity uses validation annotations to ensure data integrity:

- `@NotBlank` for required fields.
- `@FutureOrPresent` for deadlines.
- `@Email` for valid email addresses in `User`.
- Enum for task status with a default value of `PENDING`.

* * *



## **Contributing**

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature-name`).
3. Commit your changes (`git commit -m "Add feature"`).
4. Push to the branch (`git push origin feature-name`).
5. Open a Pull Request.

* * *

## **License**

This project is licensed under the MIT License. See the `LICENSE` file for details.