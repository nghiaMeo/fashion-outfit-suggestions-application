package com.example.wardrobe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OutfitCommentRequest {
    @NotBlank(message = "Content comment can't null")
    private String content;
}
