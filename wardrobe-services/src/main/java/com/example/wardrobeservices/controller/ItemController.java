package com.example.wardrobeservices.controller;

import com.example.wardrobeservices.dto.request.ItemRequest;
import com.example.wardrobeservices.dto.response.ApiResponse;
import com.example.wardrobeservices.dto.response.ItemResponse;
import com.example.wardrobeservices.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    final ItemService itemService;

    @PostMapping("/add")
    public ApiResponse<ItemResponse> addItem(@RequestBody @Valid ItemRequest itemRequest) {

        return ApiResponse.<ItemResponse>builder()
                .result(itemService.addItem(itemRequest))
                .build();
    }

    @PostMapping("/update")
    public ApiResponse<ItemResponse> updateItem(@RequestBody @Valid UUID id,
                                                @RequestBody @Valid ItemRequest itemRequest) {

        return ApiResponse.<ItemResponse>builder()
                .result(itemService.updateItem(id, itemRequest))
                .build();
    }

    @GetMapping("/all-items")
    public ApiResponse<List<ItemResponse>> getAllItems() {
        return ApiResponse.<List<ItemResponse>>builder()
                .result(itemService.getAllItems()).build();
    }

    @PostMapping("/delete-item")
    public ApiResponse<String> deleteItem(@RequestBody @Valid UUID id) {
        return ApiResponse.<String>builder()
                .result(itemService.deleteItem(id))
                .build();
    }

}
