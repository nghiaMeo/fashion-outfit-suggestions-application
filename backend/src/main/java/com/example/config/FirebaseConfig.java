package com.example.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            java.io.InputStream serviceAccountStream = null;
            String[] possiblePaths = {
                "serviceAccountKey.json",
                "wardrobe-services/serviceAccountKey.json",
                "../serviceAccountKey.json"
            };

            for (String path : possiblePaths) {
                try {
                    serviceAccountStream = new FileInputStream(path);
                    System.out.println("FirebaseConfig: Successfully loaded credentials from file path: " + path);
                    break;
                } catch (IOException ignored) {}
            }

            if (serviceAccountStream == null) {
                serviceAccountStream = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
                if (serviceAccountStream != null) {
                    System.out.println("FirebaseConfig: Successfully loaded credentials from classpath");
                }
            }

            if (serviceAccountStream == null) {
                throw new java.io.FileNotFoundException("Could not find serviceAccountKey.json in any checked paths or classpath");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
