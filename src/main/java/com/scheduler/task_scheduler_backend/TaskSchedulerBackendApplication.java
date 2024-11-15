package com.scheduler.task_scheduler_backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class TaskSchedulerBackendApplication {

	public static void main(String[] args) {
		startSshTunnel();
		System.out.println("SSH Connection established successfully. Port forwarding enabled.");
		System.out.println("Starting Spring Boot Application...");		
		try {
            Thread.sleep(5000); // 10,000 milliseconds = 10 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.out.println("Sleep was interrupted: " + e.getMessage());
        }
		SpringApplication.run(TaskSchedulerBackendApplication.class, args);
	}
	static void startSshTunnel(){
		Thread sshTunnelThread = new Thread(() -> {
			String host = "13.201.52.170"; 
	        String user = "ec2-user"; // Default user for Amazon Linux
        	String privateKeyFilePath = "C:\\Users\\chait\\Downloads\\Free_Tier.pem"; // Path to your .pem file
    	    int localPort = 3307; // Port on localhost
	        String remoteHost = "database-1.cra04a2yok40.ap-south-1.rds.amazonaws.com"; 
        	int remotePort = 3306; // Port on EC2 instance
        	List<String> command = new ArrayList<>();
    	    command.add("cmd.exe");
	        command.add("/c");
        	command.add("ssh");
    	    command.add("-i");
	        command.add(privateKeyFilePath);
        	command.add("-L");
    	    command.add(localPort +":"+ remoteHost+":"+ remotePort);
	        command.add(user + "@" + host);
			System.out.println("Starting SSH Tunnel...");
        	ProcessBuilder processBuilder = new ProcessBuilder(command);
        	try {
            Process process = processBuilder.start();

            // Capture output and error streams
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // Print output
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);  // Print errors
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Port forwarding established successfully.");
            } else {
                System.out.println("Error: Port forwarding failed with exit code " + exitCode);
            }
        	} catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
		});
		sshTunnelThread.setDaemon(true);
		sshTunnelThread.start();
	}

}
