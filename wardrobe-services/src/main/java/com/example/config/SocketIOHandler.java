package com.example.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.security.JwtUtils;
import com.example.dto.UserStatusResponse;
import com.example.repository.ConversationMemberRepository;
import com.example.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class SocketIOHandler {

    public SocketIOHandler(@Qualifier("socialSocketIOServer") SocketIOServer server,
                           ConversationMemberRepository conversationMemberRepository,
                           JwtUtils jwtUtils,
                           UserService userService) {

        server.addConnectListener(client -> {
            var token = client.getHandshakeData().getSingleUrlParam("token");

            if (token != null && jwtUtils.isTokenValid(token)) {
                try {
                    var userId = jwtUtils.extractUserId(token);

                    client.set("userId", userId);
                    try {
                        userService.updatePresence(userId, true);
                    } catch (Exception ex) {
                        log.error("Failed to update presence: {}", ex.getMessage());
                    }
                    server.getBroadcastOperations().sendEvent("user_status",
                            new UserStatusResponse(userId, true, null));

                    client.joinRoom(userId.toString());

                    var memberships = conversationMemberRepository.findByUserId(userId);
                    for (var member : memberships) {
                        client.joinRoom(member.getConversation().getId().toString());
                    }
                    log.info("Member joined the conversation: {} and size: {}", userId, memberships.size());
                } catch (Exception e) {
                    log.error("Error when connected: {}", e.getMessage());
                }
            } else {
                log.error("Invalid token: {}", token);
                client.disconnect();
            }
        });

        server.addEventListener("join_room", String.class, (client, conversationId, ackSender) -> {
            client.joinRoom(conversationId);
            log.info("Member: {} joined the conversation: {} ", client.getSessionId(), conversationId);
        });

        server.addEventListener("leave_room", String.class, (client, conversationId, ackSender) -> {
            client.leaveRoom(conversationId);
            log.info("Member: {} leave the conversation: {} ", client.getSessionId(), conversationId);
        });

        server.addDisconnectListener(client -> {
            var userId = client.get("userId");
            if (userId != null) {
                var userUuid = (UUID) userId;
                try {
                    userService.updatePresence(userUuid, false);
                } catch (Exception ex) {
                    log.error("Failed to update presence: {}", ex.getMessage());
                }
                server.getBroadcastOperations().sendEvent("user_status",
                        new UserStatusResponse(userUuid, false, null));
            }

            log.info("client offline: {}", client.getSessionId());
        });
    }
}
