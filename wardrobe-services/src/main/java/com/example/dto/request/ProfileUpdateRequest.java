package com.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileUpdateRequest {
    @NotBlank(message = "displayName can't empty")
    private String displayName;

    private String bio;

    private String avatarUrl;

    private boolean isPrivateProfile;

}
