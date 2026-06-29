package com.example.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketIORunner implements CommandLineRunner {
    private final SocketIOServer server;

    public SocketIORunner(@Qualifier("socialSocketIOServer") SocketIOServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
        log.info("SocketIO Server running success in port 9002!");
    }
}
