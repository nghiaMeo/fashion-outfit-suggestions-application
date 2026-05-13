package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.Friendship;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.entity.enums.FriendshipStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends CrudRepository<Friendship, UUID> {

    @Query("SELECT COUNT(f) FROM Friendship f WHERE (f.requester = :user OR f.receiver = :user) AND f.status = 'ACCEPTED'")
    long countAcceptedFriends(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE (f.requester = :user1 AND f.receiver = :user2)" +
            "OR (f.requester = :user2 AND f.receiver = :user1)")
    Optional<Friendship> findRelation(@Param("user1") User user1, @Param("user2") User user2);

    List<Friendship> findByReceiverAndStatus(User receiver, FriendshipStatus status);

    @Query("SELECT f FROM Friendship  f WHERE (f.requester = :user OR f.receiver = :user)" +
            "AND f.status = 'ACCEPTED' ")
    List<Friendship> findAllAcceptedFriendships(@Param("user") User user);


}

