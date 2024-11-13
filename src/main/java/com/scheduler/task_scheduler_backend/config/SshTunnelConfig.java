package com.scheduler.task_scheduler_backend.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
public class SshTunnelConfig {

    private static final Logger logger = LoggerFactory.getLogger(SshTunnelConfig.class);
    private Session session;

    @Bean
    public void connectToDatabaseViaSsh() throws Exception {
        String sshUser = "ec2-user"; // SSH username
        String sshHost = "13.201.52.170"; // SSH host (EC2 instance or bastion host)
        int sshPort = 22; // Default SSH port
        String sshKeyPath = "./././././resources/Free_Tier.pem"; // Path to the .pem file
        int localPort = 3307; // Local port to forward
        String remoteHost = "database-1.cra04a2yok40.ap-south-1.rds.amazonaws.com"; // RDS database endpoint
        int remotePort = 3306; // MySQL port on the RDS instance

        // Setup SSH connection
        JSch jsch = new JSch();
        jsch.addIdentity(sshKeyPath);
        session = jsch.getSession(sshUser, sshHost, sshPort);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        // Forward the local port to the remote database port
        session.setPortForwardingL(localPort, remoteHost, remotePort);

        logger.info("SSH Tunnel established on localhost:{}", localPort);
    }

    @PreDestroy
    public void disconnectSsh() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            logger.info("SSH Tunnel disconnected.");
        }
    }
}
