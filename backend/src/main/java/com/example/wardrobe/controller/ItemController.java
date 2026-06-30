package com.example.wardrobe.controller;

import com.example.common.dto.ApiResponse;
import com.example.user.service.CloudinaryService;
import com.example.wardrobe.dto.request.ItemRequest;
import com.example.wardrobe.dto.response.ItemResponse;
import com.example.wardrobe.dto.response.PageResponse;
import com.example.wardrobe.dto.response.WardrobeStatisticsResponse;
import com.example.wardrobe.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ItemResponse> addItem(@RequestPart("data") @Valid ItemRequest itemRequest,
                                             @RequestPart("file") MultipartFile file) {
        var imageUrl = cloudinaryService.upload(file);
        itemRequest.setImageUrl(imageUrl);
        return ApiResponse.<ItemResponse>builder()
                .result(itemService.addItem(itemRequest))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ItemResponse> updateItem(@PathVariable UUID id,
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

    @DeleteMapping("/delete-item/{id}")
    public ApiResponse<String> deleteItem(@PathVariable UUID id) {
        return ApiResponse.<String>builder()
                .result(itemService.deleteItem(id))
                .build();
    }

    @GetMapping("/search-items")
    public ApiResponse<PageResponse<ItemResponse>> searchItems(@RequestParam(required = false) String name,
                                                               @RequestParam(required = false) String type,
                                                               @RequestParam(required = false) String color,
                                                               @RequestParam(required = false) String tag,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ItemResponse>>builder()
                .result(itemService.searchItems(name, type, color, tag, page, size))
                .build();
    }

    @GetMapping("/statistics")
    public ApiResponse<WardrobeStatisticsResponse> getStatistics() {
        return ApiResponse.<WardrobeStatisticsResponse>builder()
                .result(itemService.getWardrobeStatistics())
                .build();
    }

    @PostMapping("/restore/{id}")
    public ApiResponse<ItemResponse> restoreItem(@PathVariable UUID id) {
        return ApiResponse.<ItemResponse>builder()
                .result(itemService.restoreItem(id))
                .build();
    }

    @GetMapping("/trash")
    public ApiResponse<List<ItemResponse>> getTrashItems() {
        return ApiResponse.<List<ItemResponse>>builder()
                .result(itemService.getTrashItems())
                .build();
    }

    @DeleteMapping("/hard-delete/{id}")
    public ApiResponse<String> deleteHardItem(@PathVariable UUID id) {
        itemService.hardDeleteItem(id);
        return ApiResponse.<String>builder()
                .result("Item has been deleted")
                .build();
    }
}
