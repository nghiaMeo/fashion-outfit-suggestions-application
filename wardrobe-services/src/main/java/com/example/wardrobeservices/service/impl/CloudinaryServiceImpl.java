package com.example.wardrobeservices.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.wardrobeservices.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Uploads the given multipart file to Cloudinary and obtains the uploaded asset's secure URL.
     *
     * @param file the multipart file to upload
     * @return the uploaded asset's `secure_url` as a string
     * @throws RuntimeException if the file bytes cannot be read or the upload cannot be completed
     */
    public String upload(MultipartFile file) {
        try {
            var uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            var url = uploadResult.get("secure_url").toString();

            log.info("Upload to Cloudinary Successfully");

            return url;
        } catch (IOException e) {
            log.error("Error while uploading file {}", e.getMessage());
            throw new RuntimeException("Cannot upload file, please try again");
        }
    }
}
