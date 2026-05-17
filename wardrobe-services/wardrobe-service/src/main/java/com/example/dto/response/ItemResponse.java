package com.example.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemResponse {

    private UUID id;
    private String name;
    private String type;
    private String color;
    private String season;
    private String brand;
    private String occasion;
    private String imageUrl;
    private Instant createdAt;

}
