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
public class ItemRequest {

    @NotBlank(message = "Tên món đồ không được để trống")
    private String name;
    @NotBlank(message = "Loại đồ (áo, quần...) không được để trống")
    private String type;
    @NotBlank(message = "Màu sắc không được để trống")
    private String color;
    private String season;
    private String brand;
    private String occasion;
    private String imageUrl;
    private String tags; // Danh sách tag, ví dụ: "casual,summer,trending"
}
