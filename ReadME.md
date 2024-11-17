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

## **Project Setup**

### **1. Clone the Repository**

    bashCopy codegit clone https://github.com/your-username/task-scheduler-backend.gitcd task-scheduler-backend

### **2. Configure the Application**

- **Database Configuration**:Edit the `application.properties` file in the `src/main/resources` folder:

        propertiesCopy codespring.datasource.url=jdbc:mysql://localhost:3306/task_schedulerspring.datasource.username=your_usernamespring.datasource.password=your_passwordspring.jpa.hibernate.ddl-auto=update
- **SSH Tunnel**:Ensure the SSH tunnel is configured to connect to the database. You can use tools like `SshTunnelConfig` in the project.

### **3. Build and Run**

- Build the project:

        bashCopy codemvn clean install
- Run the application:

        bashCopy codemvn spring-boot:run

* * *

## **API Endpoints**

### **Task Endpoints**

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks/{id}` | Get task by ID |
| POST | `/api/tasks` | Create a new task |
| PUT | `/api/tasks/{id}` | Update task details |
| DELETE | `/api/tasks/{id}` | Delete a task by ID |

### **Notification Endpoint**

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/notifications` | Get all notifications |

* * *

## **Development Notes**

### **Scheduled Tasks**

- Automatic functions are scheduled to run daily at midnight:
    - Rescheduling overdue tasks.
    - Auto-deletion of completed tasks.
    - Day-based priority updates.

### **Validation**

The `Task` entity uses validation annotations to ensure data integrity:

- `@NotBlank` for required fields.
- `@FutureOrPresent` for deadlines.
- Enum for task status with a default value of `PENDING`.

* * *

## **Testing**

### **Unit Tests**

Unit tests are available for service and repository layers. To run tests:

    bashCopy codemvn test

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