package com.example.auth.service;

public interface EmailService {
    void sendOtpEmail(String to, String otp);
}
