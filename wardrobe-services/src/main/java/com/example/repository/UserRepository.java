package com.example.repository;

import com.example.entity.User;
import com.example.entity.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.displayName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND u.id != :currentUserId")
    List<User> searchUsers(String query, UUID currentUserId);

    @Query("SELECT u FROM User u WHERE  u.id != :currentUserId AND u.isPrivateProfile = false " +
            "AND u.id NOT IN (SELECT f.receiverId FROM Friendship f WHERE f.requesterId = :currentUserId)" +
            "AND u.id NOT IN (select f.requesterId FROM Friendship f WHERE f.receiverId = :currentUserId)")
    List<User> findPublicUsersToSuggest(@Param("currentUserId") UUID currentUserId);


}
