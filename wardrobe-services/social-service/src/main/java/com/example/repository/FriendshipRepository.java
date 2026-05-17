package com.example.repository;

import com.example.entity.Friendship;
import com.example.entity.enums.FriendshipStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends CrudRepository<Friendship, UUID> {

    @Query("SELECT COUNT(f) FROM Friendship f WHERE (f.requesterId = :userId OR f.receiverId = :userId) AND f.status = 'ACCEPTED'")
    long countAcceptedFriends(@Param("userId") UUID userId);

    @Query("SELECT f FROM Friendship f WHERE (f.requesterId = :user1 AND f.receiverId = :user2) " +
            "OR (f.requesterId = :user2 AND f.receiverId = :user1)")
    Optional<Friendship> findRelation(@Param("user1") UUID user1, @Param("user2") UUID user2);

    List<Friendship> findByReceiverIdAndStatus(UUID receiverId, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.requesterId = :userId OR f.receiverId = :userId) " +
            "AND f.status = 'ACCEPTED' ")
    List<Friendship> findAllAcceptedFriendships(@Param("userId") UUID userId);
}

