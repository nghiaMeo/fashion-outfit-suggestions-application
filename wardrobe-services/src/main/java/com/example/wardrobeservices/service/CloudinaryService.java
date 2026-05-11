package com.example.wardrobeservices.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    /**
 * Uploads the provided multipart file to Cloudinary.
 *
 * @param file the multipart file to upload
 * @return a String containing the uploaded resource's identifier or URL
 */
String upload(MultipartFile file);
}
