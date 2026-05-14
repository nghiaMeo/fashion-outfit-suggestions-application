package com.example.wardrobeservices.stocket;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Component;

@Component
public class SocketIOHandler {

    public SocketIOHandler(SocketIOServer server) {
        server.addConnectListener(socketIOClient -> {
            System.out.println("user connected: " + socketIOClient.getSessionId());
        });

        server.addDisconnectListener(socketIOClient -> {
            System.out.println("user disconnected: " + socketIOClient.getSessionId());
        });

        server.addEventListener("send_message", String.class, (client, data, ackSender) -> {
            System.out.println("message sent: " + data);
            server.getBroadcastOperations().sendEvent("receive_message", data);
        });

    }

}
