package com.example.configuration;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class SocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer(JwtUtils jwtUtils){
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("localhost");
        config.setPort(9003);
        
        SocketIOServer server = new SocketIOServer(config);
        
        server.addConnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            if (token != null && jwtUtils.isTokenValid(token)) {
                try {
                    UUID userId = jwtUtils.extractUserId(token);
                    client.set("userId", userId);
                    client.joinRoom(userId.toString());
                    System.out.println("Notification SocketIO client connected: " + userId);
                } catch (Exception e) {
                    client.disconnect();
                }
            } else {
                client.disconnect();
            }
        });

        server.addDisconnectListener(client -> {
            Object userId = client.get("userId");
            if (userId != null) {
                System.out.println("Notification SocketIO client disconnected: " + userId);
            }
        });
        
        return server;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer){
        return new SpringAnnotationScanner(socketIOServer);
    }

    @Bean
    public CommandLineRunner socketIOServerRunner(SocketIOServer server) {
        return args -> {
            server.start();
            System.out.println("SocketIO Server running success in port 9003!");
        };
    }
}
