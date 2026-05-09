package com.example.wardrobeservices.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    @NotBlank(message = "Old password is mandatory")
    private String oldPassword;

    @NotBlank(message = "New password is mandatory")
    @Size(min = 8, message = "New password must be at least 8 characters")
    private String newPassword;
}
