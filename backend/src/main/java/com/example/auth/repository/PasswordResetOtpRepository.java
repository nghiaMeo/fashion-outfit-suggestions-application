package com.example.auth.repository;

import com.example.auth.entity.PasswordResetOtp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends CrudRepository<PasswordResetOtp, String> {

    Optional<PasswordResetOtp> findByEmail(String email);

    void deleteByEmail(String email);
}
