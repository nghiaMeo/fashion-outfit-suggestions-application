package com.example.notification.repository;

import com.example.notification.entity.Notification;
import com.example.notification.entity.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);

    long countByRecipientIdAndIsReadFalse(UUID recipientId);

    void deleteByRecipientIdAndActorIdAndType(UUID recipientId, UUID actorId, NotificationType type);

    List<Notification> findByRecipientIdAndTypeNotOrderByCreatedAtDesc(UUID recipientId, NotificationType type);

    long countByRecipientIdAndIsReadFalseAndTypeNot(UUID recipientId, NotificationType type);
}
