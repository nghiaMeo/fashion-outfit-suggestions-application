package com.example.wardrobeservices.stocket;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.wardrobeservices.repository.ConversationMemberRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SocketIOHandler {

    public SocketIOHandler(SocketIOServer server, ConversationMemberRepository conversationMemberRepository) {

        server.addConnectListener(socketIOClient -> {
            System.out.println("user connected: " + socketIOClient.getSessionId());
        });

        server.addEventListener("join_room", String.class, (client, conversationId, ackSender) -> {
            try {
                UUID convId = UUID.fromString(conversationId);
                client.joinRoom(conversationId);
                System.out.println("user joined room: " + convId);
                if (ackSender.isAckRequested()) {
                    ackSender.sendAckData(convId + "success join in room success");
                }
            } catch (Exception e) {
                System.out.println("user joined room failed: " + e);
            }
        });

        server.addEventListener("leave_room", String.class, (client, conversationId, ackSender) -> {
            client.leaveRoom(conversationId);
            System.out.println("client: " + client.getSessionId() + " leaved room: " + conversationId);
        });

        server.addDisconnectListener(socketIOClient -> {
            System.out.println("user disconnected: " + socketIOClient.getSessionId());
        });

    }

}
