package com.example.wardrobe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class OutfitCommentRequest {

    private UUID parentId;

    @NotBlank(message = "Content comment can't null")
    private String content;
}
