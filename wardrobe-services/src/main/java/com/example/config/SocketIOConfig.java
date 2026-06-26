package com.example.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SocketIOConfig {

    @Bean("socialSocketIOServer")
    public SocketIOServer socketIOServer(){
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("localhost");
        config.setPort(9002);
        return new SocketIOServer(config);
    }

    @Bean("notificationSocketIOServer")
    public SocketIOServer notificationSocketIOServer(){
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(9003);
        return new SocketIOServer(config);
    }

    @Bean("socialSpringAnnotationScanner")
    public SpringAnnotationScanner springAnnotationScanner(@Qualifier("socialSocketIOServer") SocketIOServer socialSocketIOServer){
        return new SpringAnnotationScanner(socialSocketIOServer);
    }
}
