package com.example.wardrobeservices.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String upload(MultipartFile file);
}
