package com.example.wardrobeservices.stocket;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.wardrobeservices.dto.response.UserStatusResponse;
import com.example.wardrobeservices.repository.ConversationMemberRepository;
import com.example.wardrobeservices.repository.UserRepository;
import com.example.wardrobeservices.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;


@Component
@Slf4j
public class SocketIOHandler {

    public SocketIOHandler(SocketIOServer server,
                           ConversationMemberRepository conversationMemberRepository,
                           JwtService jwtService,
                           UserRepository userRepository) {

        server.addConnectListener(client -> {
            var token = client.getHandshakeData().getSingleUrlParam("token");

            if (token != null && jwtService.isTokenValid(token)) {
                try {
                    var username = jwtService.extractUsername(token);
                    var user = userRepository.findByUsername(username).orElse(null);

                    if (user != null) {
                        client.set("userId", user.getId());
                        user.setOnline(true);
                        userRepository.save(user);
                        server.getBroadcastOperations().sendEvent("user_status",
                                new UserStatusResponse(user.getId(), true, null));
                        var memberships = conversationMemberRepository.findByUser(user);
                        for (var member : memberships) {
                            client.joinRoom(member.getConversation().getId().toString());
                        }
                        log.info("Member joined the conversation: {} and size: {}", user.getDisplayName(), memberships.size());
                    }
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
            if (userId != null){
                userRepository.findById((UUID) userId).ifPresent(user -> {
                    user.setOnline(false);
                    user.setLastSeen(Instant.now());
                    userRepository.save(user);
                    server.getBroadcastOperations().sendEvent("user_status",
                            new UserStatusResponse(user.getId(), false, null));
                });
            }

            log.info("client offline: {}", client.getSessionId());

        });

    }

}
