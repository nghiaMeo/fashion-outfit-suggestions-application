package com.example.auth.service;

import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.common.exception.ErrorCode;
import com.example.common.exception.ResourceNotFoundException;
import com.example.common.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom UserDetailsService for Spring Security
 * Loads user details by email for authentication
 */
@Slf4j
@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user details for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        boolean enabled = user.getStatus() == User.UserStatus.ACTIVE;

        return new CustomUserDetails(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                enabled
        );
    }
}
