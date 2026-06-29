package com.example.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationSocketIORunner implements CommandLineRunner {
    private final SocketIOServer server;
    private final JwtUtils jwtUtils;

    public NotificationSocketIORunner(
            @Qualifier("notificationSocketIOServer") SocketIOServer server,
            JwtUtils jwtUtils) {
        this.server = server;
        this.jwtUtils = jwtUtils;

        // Configure basic connection handling for notification client so it can join its room
        this.server.addConnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            if (token != null && jwtUtils.isTokenValid(token)) {
                try {
                    var userId = jwtUtils.extractUserId(token);
                    client.joinRoom(userId.toString());
                    log.info("Notification client associated with user: {}", userId);
                } catch (Exception e) {
                    log.error("Notification connect error: {}", e.getMessage());
                }
            } else {
                log.warn("Notification connect attempted with invalid token");
            }
        });
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
        log.info("Notification SocketIO Server running success in port 9003!");
    }
}
