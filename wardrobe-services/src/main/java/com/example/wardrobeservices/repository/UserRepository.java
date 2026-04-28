package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    /**
 * Checks whether a User with the given email exists.
 *
 * @param email the email address to look up
 * @return `true` if a user with the given email exists, `false` otherwise
 */
boolean existsByEmail(String email);
    /**
 * Determines whether a User with the given username exists.
 *
 * @param username the username to check for existence
 * @return true if a User with the given username exists, false otherwise
 */
boolean existsByUsername(String username);
}
